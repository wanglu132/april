package com.april.nserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.april.nserver.handler.Handler;
import com.april.nserver.session.Session;
import com.april.nserver.spliter.Packet;
import com.april.nserver.spliter.Spliter;

public class NServer {

	private static Log log = LogFactory.getLog(NServer.class);
	
	private String name;

	private Selector selector;

	private ExecutorService readServiceExecutor = null;

	public NServer(String name) {
		this.name = name;
		try {
//			System.setProperty("java.nio.channels.spi.SelectorProvider",
//					"sun.nio.ch.EPollSelectorProvider");
			selector = Selector.open();
			log.info(selector.provider());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void bind(SingleServerConfig singleServerConfig,
			SingleServerConfig... singleServerConfigs) throws IOException {
		SingleServerConfig[] ssconfigs = new SingleServerConfig[singleServerConfigs.length + 1];
		ssconfigs[0] = singleServerConfig;
		System.arraycopy(singleServerConfigs, 0, ssconfigs, 1,
				singleServerConfigs.length);
		for (SingleServerConfig ssconfig : ssconfigs) {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);
			ServerSocket ss = ssc.socket();
			// ss.setReceiveBufferSize(65536);
			SocketAddress localAddress = ssconfig.getSocketAddress();
			ss.bind(localAddress);

			Attachment atta = new Attachment(ssconfig);
			ssc.register(selector, SelectionKey.OP_ACCEPT, atta);
			log.info("listening on: [" + localAddress + "]");
		}

		Thread listener = new Thread(new ServerListenler(), "ServerListenler");
		listener.start();

		ReadExecutors readExecutors = new ReadExecutors();
		readServiceExecutor = readExecutors.newReadThreadPool();
	}

	// -----------------统计使用-----------------------------
	private AtomicLong onRead_count = new AtomicLong(0);
	private AtomicLong total_bytes = new AtomicLong(0);
	private AtomicLong total_count = new AtomicLong(0);

	private static long start = 0;
	private static boolean flag = true;

	// ------------------------------------------------------

	private class ServerListenler implements Runnable {

		public void run() {
			try {
				int n = 0;
				while ((n = selector.select()) > 0) {
					if (flag) {
						start = System.currentTimeMillis();
					}
					flag = false;
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					// Set<SelectionKey> keys = selector.keys();
					// System.out.println("keys: " + keys.size());
					// System.out.println("selectedKeys: " +
					// selectedKeys.size());
					Iterator<SelectionKey> its = selectedKeys.iterator();
					while (its.hasNext()) {
						SelectionKey selectedKey = its.next();
						its.remove();
						if (selectedKey.isAcceptable()) {
							onAccepted(selectedKey);
						} else if (selectedKey.isReadable()) {
							onRead(selectedKey);
							onRead_count.addAndGet(1);
						} else if (selectedKey.isWritable()) {
							try {
								onWrite(selectedKey);
							} catch (NotYetConnectedException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (n == 0) {
					log.info("服务器关闭");
				}
			} catch (IOException e) {
				log.error(e.getMessage());
				log.info("服务器关闭");
				e.printStackTrace();
			}
		}

		private void onAccepted(SelectionKey key) {
			Attachment atta = (Attachment) key.attachment();
			Handler handler = atta.getSingleServerConfig().getHandler();

			ServerSocketChannel scc = (ServerSocketChannel) key.channel();
			try {
				SocketChannel sc = scc.accept();
				sc.configureBlocking(false);
				Socket socket = sc.socket();
				// System.out.println("getReceiveBufferSize: "
				// + socket.getReceiveBufferSize());
				// System.out.println("getSendBufferSize: "
				// + socket.getSendBufferSize());
				ServerSocket ss = scc.socket();

				log.info("[" + ss.getLocalSocketAddress() + "] 获取客户端的连接: ["
						+ socket.getRemoteSocketAddress() + "]");
				sc.register(selector, SelectionKey.OP_READ, atta);

				Session session = new Session(sc);
				handler.sessionCreated(session);
			} catch (IOException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}

		private void onRead(SelectionKey key) {

			Attachment atta = (Attachment) key.attachment();

			int readBufferSize = atta.getSingleServerConfig()
					.getReadBufferSize();

			ByteBuffer readBuffer = ByteBuffer.allocate(readBufferSize);

			SocketChannel sc = (SocketChannel) key.channel();

			int counter = 0;

			readBuffer.clear();
			while (true) {
				int r = 0;
				try {
					r = sc.read(readBuffer);
				} catch (IOException e) {
					counter = 0;
					log.error(e.getMessage());
					// e.printStackTrace();
					break;
				}
				if (readBuffer.position() == readBufferSize) {
					readBuffer.flip();
					readBufferSize += readBufferSize;
					ByteBuffer buffer = ByteBuffer.allocate(readBufferSize);
					buffer.put(readBuffer);
					readBuffer = buffer;
				}
				if (r <= 0) {
					break;
				}
				counter += r;
			}
			Session session = new Session(sc);
			if (counter > 0) {
				readBuffer.flip();
				Attachment attach = new Attachment(
						atta.getSingleServerConfig(), session);
				attach.setBuffer(readBuffer);
				atta.getSingleServerConfig().getSplitWorkerExecutor().execute(
						new SplitWorker(attach));
			} else {

				Handler handler = atta.getSingleServerConfig().getHandler();
				try {
					handler.sessionClosed(session);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Socket socket = sc.socket();
				log.info("[" + socket.getRemoteSocketAddress()
						+ "] 断开了与服务器的连接.");
				try {
					sc.finishConnect();
					sc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				key.cancel();

			}
		}

		private void onWrite(SelectionKey key) throws NotYetConnectedException,
				IOException, Exception {
			Attachment atta = (Attachment) key.attachment();
			ByteBuffer packet = (ByteBuffer) atta.getBuffer();

			SocketChannel sc = (SocketChannel) key.channel();
			sc.write(packet);
			key.interestOps(SelectionKey.OP_READ);

			// writePacketQueue.put(atta);
		}
	}

	private class SplitWorker implements Runnable {

		private Attachment attachment;

		public SplitWorker(Attachment attachment) {
			this.attachment = attachment;
		}

		public void run() {

			Spliter spliter = attachment.getSingleServerConfig().getSpliter();
			ByteBuffer buffer = attachment.getBuffer();
			Session session = attachment.getSession();
			String cacheKey = session.toString();
			List<Packet> packets = spliter.split(buffer, cacheKey, attachment
					.getSingleServerConfig().getCache());
			for (Packet packet : packets) {
				Attachment attach = new Attachment(attachment
						.getSingleServerConfig(), session);
				attach.setPacket(packet);
				readServiceExecutor.execute(new ReadWorker(attach));
			}
		}
	}

	private class ReadWorker implements Runnable {

		private Attachment attachment;

		public ReadWorker(Attachment atta) {
			this.attachment = atta;
		}

		public void run() {

			Packet packet = attachment.getPacket();

			Session session = attachment.getSession();
			Handler handler = attachment.getSingleServerConfig().getHandler();


			try {
				handler.messageReceived(session, packet);
			} catch (Exception e) {
				e.printStackTrace();
			}

			total_count.addAndGet(1);
			total_bytes.addAndGet(packet.getPacket().length);

//			if (total_count.longValue() == Client.BN * Client.WK) {
//				long use = System.currentTimeMillis() - start;
//				BigDecimal buse = new BigDecimal(use);
//				BigDecimal sec = buse.divide(new BigDecimal(1000));
//				BigDecimal bto = new BigDecimal(total_count.get());
//				int pj = bto.divide(sec, BigDecimal.ROUND_DOWN).intValue();
//				log.info("\n---------------统计---------------\n" + "收到总字节数: "
//						+ total_bytes.get() + "\n" + "onRead_count: "
//						+ onRead_count.get() + "\n" + "总花费: " + use + "毫秒\n"
//						+ "总报文笔数: " + total_count.get() + "\n" + "平均: " + pj
//						+ "笔/秒\n" + "----------------------------------");
//			}
		}
	}

	private class Attachment {

		private SingleServerConfig singleServerConfig;

		private Session session;

		private Packet packet;

		private ByteBuffer buffer;

		public Attachment(SingleServerConfig singleServerConfig) {
			this.singleServerConfig = singleServerConfig;
		}

		public Attachment(SingleServerConfig singleServerConfig, Session session) {
			this.singleServerConfig = singleServerConfig;
			this.session = session;
		}

		public SingleServerConfig getSingleServerConfig() {
			return singleServerConfig;
		}

		public void setSingleServerConfig(SingleServerConfig singleServerConfig) {
			this.singleServerConfig = singleServerConfig;
		}

		public ByteBuffer getBuffer() {
			return buffer;
		}

		public void setBuffer(ByteBuffer buffer) {
			this.buffer = buffer;
		}

		public Packet getPacket() {
			return packet;
		}

		public void setPacket(Packet packet) {
			this.packet = packet;
		}

		public Session getSession() {
			return session;
		}

		public void setSession(Session session) {
			this.session = session;
		}

	}
}