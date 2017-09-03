package com.april.client;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.april.client.common.CustomThreadFactory;
import com.april.client.common.Packet;
import com.april.client.common.Transceiver;
import com.april.util.ByteUtil;
import com.april.util.SleepUtil;

/**
 * <b>异步, 单工, 全双工, 长连接</b><br>
 * asynchrony, singlex, full-duplex, keep-alive connection
 * 
 * @author WL
 * 
 */
public abstract class AbstractAsynKeepAliveSession {

	private static Log log = LogFactory
			.getLog(AbstractAsynKeepAliveSession.class);

	// ---------------配置属性-----------------

	private String host;

	private int port;

	private String sessionName;

	private boolean isSinglex;

	private boolean isSendActive;

	private long activeIntervalMil;

	private long connectIntervalSec;

	private int headLength;

	private int receiveBufferSize = 0;

	private int sendBufferSize = 0;

	private int soTimeoutMil = 0;

	// ---------------------------------------

	private volatile long lastSendTM;

	private InetSocketAddress isa;

	private Socket sock = null;

	private DataInputStream in;

	private OutputStream out;
	
	private static ByteBuffer cache = null;

	private AtomicBoolean isConnectRunning = new AtomicBoolean(false);

	private AtomicBoolean isActiveRunning = new AtomicBoolean(false);

	private AtomicBoolean isReceiveRunning = new AtomicBoolean(false);

	// --------------线程池, 队列----------------

	private ExecutorService packetProcessorExecutor;

	private LinkedBlockingQueue byteQueue = new LinkedBlockingQueue();

	// ----------------------------------

	// --------------统计使用---------------

	private AtomicInteger activeCount = new AtomicInteger(0);

	private AtomicInteger receiveCount = new AtomicInteger(0);

	// ------------------------------------

	public AbstractAsynKeepAliveSession() {
	}

	public final void startup() {
		
		if(sessionName == null){
			throw new IllegalArgumentException("sessionName must be set!");
		}
		if(activeIntervalMil == 0){
			throw new IllegalArgumentException("activeIntervalSec must be set!");
		}
		if(connectIntervalSec == 0){
			throw new IllegalArgumentException("connectIntervalSec must be set!");
		}
		
		isa = new InetSocketAddress(host, port);

		if (!isSinglex) {

			Thread spliter = new Thread(
					new ControllerThreadGroup("controller"), new Spliter(),
					sessionName + "-spliter");
			spliter.start();

			log.info("新建报文分割线程 " + "name: " + spliter.getName());

			packetProcessorExecutor = new ThreadPoolExecutor(2, 3, 60L,
					TimeUnit.SECONDS, new LinkedBlockingQueue(),
					new CustomThreadFactory(this.sessionName
							+ "-packetProcessor"), new CustomPolicy());
		}

		this.connect();
	}

	private void connect() {

		isConnectRunning.set(true);

		Thread connector = new Thread(new ControllerThreadGroup("controller"),
				new Connector(), this.sessionName + "-connector");
		connector.start();
	}

	private synchronized void reConnect() {
		if (!isConnectRunning.get()) {
			this.closeLink();
			SleepUtil.sleepMil(1);
			this.connect();
		}
	}

	private void closeLink() {
		try {
			if (sock != null) {
				if (sock.isConnected() && !sock.isClosed()) {
					sock.close();
				}
			}

			log.info("关闭连接成功." + getServerInfo());
		} catch (IOException e) {
			log.error("关闭连接失败: " + getServerInfo() + e.getMessage(), e);
		}
	}

	protected final void sendBytes(byte[] bs) throws IOException {
		if (sock != null && sock.isConnected() && !sock.isClosed()) {
			out.write(bs);
			out.flush();
		} else {
			throw new IOException("连接还未建立或已经被关闭!" + getServerInfo());
		}
	}

	/**
	 * 对于发送时出现链路异常, 没有发送出去的报文, 抛出异常, 依赖于前置框架处理 <br>
	 * 1, 对于单工链路, 可能服务端已将链路断开, 但是客户端因为没有阻塞读, 所以发现不了, <br>
	 * 必须在发出心跳, 或者交易报文的时候报链路异常才能发现链路已关闭, <br>
	 * 所以对于单工链路, 发送报文的时候报链路异常则重建连接, 并重发该笔报文. <br>
	 * 2, 对于双工链路, 服务端将链路断开, 则客户端阻塞读的地方立刻报出链路异常, 此时则关闭链接, 重建连接. 在连接还没有建立起来之前,
	 * 发送报文, 则会报出链路异常, 此时应该等待一会在重发此报文.<br>
	 * 3, 如果再次重发仍然没有发送成功, 则这笔报文记为失败.
	 * 
	 */
	public final void sendMessage(Object o) throws Exception {
		try {
			this.send(o);
			this.updateLastSendTM();
		} catch (SocketException e) {
			log.error("发送时链路异常! 关闭连接, 重建连接, 休眠3秒重新发送..." + getConnectInfo()
					+ ": " + e.getMessage(), e);
			reConnect();

			SleepUtil.sleepSec(3);
			try {
				this.send(o);
				this.updateLastSendTM();
			} catch (SocketException e1) {
				throw new Exception("向对方发送报文失败 " + getConnectInfo() + ": "
						+ e.getMessage());
			} catch (IOException e2) {
				throw new Exception("向对方发送报文失败 " + getConnectInfo() + ": "
						+ e.getMessage());
			}

		} catch (IOException e) {
			log.error("发送时链路异常! 关闭连接, 重建连接, 休眠3秒重新发送..." + getConnectInfo()
					+ ": " + e.getMessage(), e);
			reConnect();

			SleepUtil.sleepSec(3);
			try {
				this.send(o);
				this.updateLastSendTM();
			} catch (SocketException e1) {
				throw new Exception("向对方发送报文失败 " + getConnectInfo() + ": "
						+ e.getMessage());
			} catch (IOException e2) {
				throw new Exception("向对方发送报文失败 " + getConnectInfo() + ": "
						+ e.getMessage());
			}
		} catch (Exception e) {
			log.error("向对方发送报文失败 " + getConnectInfo() + ": " + e.getMessage(),
					e);
			reConnect();
			throw new Exception("向对方发送报文失败 " + getConnectInfo() + ": "
					+ e.getMessage());
		}
	}

	private void updateLastSendTM() {
		this.lastSendTM = System.currentTimeMillis();
	}

	// ---------------------实现类需要实现的方法---------------------

	protected abstract void send(Object o) throws Exception;

	/**
	 * 在连接刚建立好的时候做一些初始化工作, 例如签到等等<br>
	 * 方法报出异常或者返回false, 都会导致关闭连接, 重建连接, 重新执行init方法<br>
	 * 在init方法内部可以调用sendBytes()向对方发送报文, 也可以调用readFully方法原链路读取对方响应报文
	 * 
	 * @return
	 * @throws Exception
	 */
	protected boolean init(Transceiver sw) throws Exception {
		return true;
	}

	/**
	 * 如果isSendActive为true, 实现类必须覆盖此方法
	 * 
	 * @throws Exception
	 */
	protected void sendActive() throws Exception {
	}

	/**
	 * 对于双工, 实现类必须覆盖此方法
	 * 
	 * @param packet
	 */
	protected void receive(Packet packet) {
	}

	/**
	 * 对于双工, 实现类必须覆盖此方法
	 * 
	 * @param head
	 * @return
	 * @throws Exception
	 */
	protected int getBodyLength(byte[] head) throws Exception {
		return 0;
	}

	// -------------------------------------------------------------

	/**
	 * 可以调用sendMessage方法重发失败报文 | 目前没有用到
	 * 
	 * @param o
	 */
	// protected void failToSend(Object o) throws Exception {
	// }
	private class Connector implements Runnable {

		public void run() {

			try {

				while (true) {// 确保连接上对方，同时初始化成功

					do {
						try {
							sock = new Socket();
							sock.setReuseAddress(true);
							if (receiveBufferSize != 0) {
								sock.setReceiveBufferSize(receiveBufferSize);
							}
							if (sendBufferSize != 0) {
								sock.setSendBufferSize(sendBufferSize);
							}
							sock.connect(isa);
						} catch (IOException e) {
							log.error("新建连接失败" + getServerInfo() + ": "
									+ e.getMessage());
							log.debug("sleep " + connectIntervalSec
									+ "s... reconnect");
							SleepUtil.sleepSec(connectIntervalSec);
						}
					} while (!sock.isConnected() || sock.isClosed());

					if (isSinglex) {
						try {
							sock.shutdownInput();
							out = sock.getOutputStream();
						} catch (IOException e) {
							log.error("关闭,重建连接" + getConnectInfo() + e.getMessage(), e);
							closeLink();
							continue;
						}
					} else {
						try {
							in = new DataInputStream(sock.getInputStream());
							out = sock.getOutputStream();
						} catch (IOException e) {
							log.error("关闭,重建连接" + getConnectInfo()  + e.getMessage(), e);
							closeLink();
							continue;
						}
					}

					log.info("与服务端连接建立成功" + getConnectInfo()
							+ getConnectParams());

					boolean initRes;
					try {
						if (isSinglex) {
							initRes = init(new Transceiver(out, sock));
						} else {
							initRes = init(new Transceiver(out,
									new DataInputStream(in), sock));
						}
					} catch (Exception e) {
						log.error("初始化init报异常, 关闭,重建连接" + getConnectInfo()
								+ e.getMessage(), e);
						closeLink();
						continue;
					}

					if (initRes) {
						log.info("初始化init成功" + getConnectInfo());
						try {
							if (isSinglex) {
								sock.setSoTimeout(0);
							} else {
								sock.setSoTimeout(soTimeoutMil);
							}
						} catch (SocketException e) {
							log.error(e.getMessage(), e);
							closeLink();
							continue;
						}
						break;
					} else {
						log.info("初始化init失败, 关闭,重建连接" + getConnectInfo());
						closeLink();
						continue;
					}
				}

				if (!isSinglex && !isReceiveRunning.get()) {

					isReceiveRunning.set(true);

					Receiver receiveThread = new Receiver(
							new ControllerThreadGroup("controller"),
							sessionName + "-receiver");
					receiveThread.start();

					log.info("新建接收线程 " + "name: " + receiveThread.getName()
							+ " NO." + receiveCount.addAndGet(1));
				}

				if (isSendActive && !isActiveRunning.get()) {

					isActiveRunning.set(true);

					ActiveSender activeThread = new ActiveSender(
							new ControllerThreadGroup("controller"),
							sessionName + "-activeSender");
					activeThread.start();

					log.info("新建心跳线程 " + "name: " + activeThread.getName()
							+ " NO." + activeCount.addAndGet(1));
				}

				isConnectRunning.set(false);

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				isConnectRunning.set(false);
				reConnect();
			}
		}
	}

	private class ActiveSender extends Thread {

		private ActiveSender(ThreadGroup group, String name) {
			super(group, name);
			updateLastSendTM();
		}

		public void run() {
			while (true) {
				long currentTM = System.currentTimeMillis();
				long interval = currentTM - lastSendTM;
				if (interval >= activeIntervalMil) {
					try {
						sendActive();
					} catch (Exception e) {
						log.error("向服务端发心跳失败" + getConnectInfo()
								+ e.getMessage(), e);
						isActiveRunning.set(false);
						log.info("心跳线程正常运行结束 " + "name: " + this.getName()
								+ " NO." + activeCount.get());
						reConnect();
						return;
					}
					updateLastSendTM();
				} else {
					long ca = activeIntervalMil - interval;
					log.debug("sleep: " + ca + "ms...");
					SleepUtil.sleepMil(ca);
				}
			}
		}
	}

	private class Receiver extends Thread {

		private Receiver(ThreadGroup group, String name) {
			super(group, name);
		}

		public void run() {

			while (true) {

				try {
					// way 1
//					 byte[] head = new byte[getHeadLength()];
//					 in.readFully(head);
//					 byte[] body = new byte[getBodyLength(head)];
//					 in.readFully(body);
//					
//					 packetProcessorExecutor.execute(new PacketProcessor(
//					 new Packet(head, body)));

					// way 2
					// int readLength = 300;
					// ByteBuffer buffer = ByteBuffer.allocate(readLength);
					//
					// int c = in.read();
					// if (c == -1) {
					// throw new EOFException("到达流末尾");
					// }
					// buffer.put((byte) c);
					//
					// int i = 1;
					// while (true) {
					// for (; i < readLength; i++) {
					// c = in.read();
					// if (c == -1) {
					// log.debug("c == -1");
					// break;
					// }
					// buffer.put((byte) c);
					// }
					//						
					// if (buffer.hasRemaining()) {
					// buffer.flip();
					// byteQueue.put(buffer);
					// // log.debug("remaining1: " + buffer.remaining());
					// break;
					// } else {
					// buffer.flip();
					// if(buffer.remaining() >= 2048){
					// byteQueue.put(buffer);
					// // log.debug("remaining2: " + buffer.remaining());
					// break;
					// }else{
					// readLength = readLength * 2;
					// ByteBuffer buffer2 = ByteBuffer
					// .allocate(readLength);
					// buffer2.put(buffer);
					// buffer = buffer2;
					// // log.debug("remaining3: " + buffer.remaining());
					// }
					// }
					// }

					// log.debug("available" + in.available());
					// SleepUtil.sleepMil(100);

					// way 3
					int readLen = 1000;
					int total = 0, off = 0, len = readLen;
					byte[] b = new byte[readLen];

					while (true) {

//						log.debug("readLen: " + readLen + " off: " + off
//								+ " len: " + len + " total: " + total);
						int r = in.read(b, off, len);

						total += r;
						if (r == len) {
							readLen = readLen * 2;
							byte[] b2 = new byte[readLen];
							System.arraycopy(b, 0, b2, 0, b.length);
							off = b.length;
							len = readLen / 2;
							b = b2;
						} else {
							ByteBuffer buffer = ByteBuffer.wrap(b, 0, total);
							byteQueue.put(buffer);
							break;
						}

					}

					// ret.addAndGet(1);
					// log.debug("i" + ret.get() + ": " + i);

				} catch (SocketTimeoutException e) {
					log.error(e.getMessage(), e);
					isReceiveRunning.set(false);
					log.info("接收线程正常运行结束 " + "name: " + this.getName() + " NO."
							+ receiveCount.get());
					reConnect();
					break;
				} catch (EOFException e) {
					log.error(e.getMessage(), e);
					isReceiveRunning.set(false);
					log.info("接收线程正常运行结束 " + "name: " + this.getName() + " NO."
							+ receiveCount.get());
					reConnect();
					break;
				} catch (SocketException e) {
					log.error(e.getMessage(), e);
					isReceiveRunning.set(false);
					log.info("接收线程正常运行结束 " + "name: " + this.getName() + " NO."
							+ receiveCount.get());
					reConnect();
					break;
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					isReceiveRunning.set(false);
					log.info("接收线程正常运行结束 " + "name: " + this.getName() + " NO."
							+ receiveCount.get());
					reConnect();
					break;
				} catch (Exception e) {// 一般为getBodyLength()方法报出
					log.error(e.getMessage(), e);
					isReceiveRunning.set(false);
					log.info("接收线程正常运行结束 " + "name: " + this.getName() + " NO."
							+ receiveCount.get());
					reConnect();
					break;
				}
			}

		}
	}

	private List split(ByteBuffer buffer) throws Exception {

		int hlen = this.headLength;

		if (cache != null) {
			if (cache.hasRemaining()) {
				ByteBuffer buf = ByteUtil.combineByteBuffer(cache, buffer);
				buffer = buf;
			}
		}

		List packets = new ArrayList();
		while (buffer.hasRemaining()) {
			byte[] head = new byte[hlen];
			if (buffer.remaining() >= hlen) {
				buffer.get(head);

				int blen = getBodyLength(head);
				if (blen <= buffer.remaining()) {
					byte[] body = new byte[blen];
					buffer.get(body);
					packets.add(new Packet(head, body));
				} else {
					buffer.position(buffer.position() - hlen);
					cache = buffer;
					break;
				}
			} else {
				cache = buffer;
				break;
			}
		}

		return packets;
	}

	private class Spliter implements Runnable {

		public void run() {

			while (true) {
				try {
					
					ByteBuffer buffer = (ByteBuffer) byteQueue.take();
					List packets = split(buffer);
					for (Iterator its = packets.iterator(); its.hasNext();) {
						Packet pack = (Packet) its.next();
						packetProcessorExecutor.execute(new PacketProcessor(
								pack));
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					closeLink();
					continue;
				}
			}
		}
	}

	private class PacketProcessor implements Runnable {

		private Packet packet;

		private PacketProcessor(Packet packet) {
			this.packet = packet;
		}

		public void run() {

			try {
				receive(packet);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				closeLink();
			}
		}
	}

	private class ControllerThreadGroup extends ThreadGroup {

		private ControllerThreadGroup(String name) {
			super(name);
		}

		public void uncaughtException(Thread t, Throwable e) {

			String name = t.getName();
			if (name.equals(sessionName + "-connector")) {
				isConnectRunning.set(false);
				log.error(name + "因为一个未捕获的异常而停止", e);
				reConnect();
			} else if (name.equals(sessionName + "-receiver")) {
				isReceiveRunning.set(false);
				log.error(name + "因为一个未捕获的异常而停止", e);
				reConnect();
			} else if (name.equals(sessionName + "-activeSender")) {
				isActiveRunning.set(false);
				log.error(name + "因为一个未捕获的异常而停止", e);
				reConnect();
			} else if (name.equals(sessionName + "-spliter")) {
				log.error(name + "因为一个未捕获的异常而停止, 重建该线程...", e);
				t.start();
			}
		}
	}

	private class CustomPolicy implements RejectedExecutionHandler {

		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			log.info("丢弃: " + r.toString());
		}

	}

	protected final String getConnectInfo() {
		if (sock != null) {
			return " [L:" + sock.getLocalPort() + "]->[" + host + ":" + port
					+ "] ";
		} else {
			return " [L:null" + "]->[" + host + ":" + port + "] ";
		}
	}

	protected final String getServerInfo() {

		return " [" + host + ":" + port + "] ";
	}

	/**
	 * 获取连接参数配置
	 * 
	 * @return
	 */
	protected final String getConnectParams() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n------------连接参数------------");
		try {
			buffer.append("\nreceiveBufferSize: " + sock.getReceiveBufferSize())
				  .append("\nsendBufferSize: " + sock.getSendBufferSize())
				  .append("\nreuseAddress: " + sock.getReuseAddress())
				  .append("\nsoTimeout: " + sock.getSoTimeout());
		} catch (SocketException e) {
			log.error(e.getMessage(), e);
		}
		buffer.append("\n--------------------------------");
		return buffer.toString();
	}

	protected final int getHeadLength() {
		return headLength;
	}

	// -----------------注入--------------------

	public final void setHost(String host) {
		this.host = host;
	}

	public final void setPort(int port) {
		this.port = port;
	}

	public final void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public final void setSinglex(boolean isSinglex) {
		this.isSinglex = isSinglex;
	}

	/**
	 * 0则使用默认值
	 * 
	 * @param receiveBufferSize_KB
	 */
	public final void setReceiveBufferSize_KB(int receiveBufferSize_KB) {
		if (receiveBufferSize_KB == 64) {
			this.receiveBufferSize = receiveBufferSize_KB * 1024 - 1;
		} else {
			this.receiveBufferSize = receiveBufferSize_KB * 1024;
		}
	}

	/**
	 * 0则使用默认值
	 * 
	 * @param sendBufferSize_KB
	 */
	public final void setSendBufferSize_KB(int sendBufferSize_KB) {
		if (sendBufferSize_KB == 64) {
			this.sendBufferSize = sendBufferSize_KB * 1024 - 1;
		} else {
			this.sendBufferSize = sendBufferSize_KB * 1024;
		}
	}

	/**
	 * 设置双工时的链路超时时间<br>
	 * 单工时链路超时时间为无穷大(0)<br>
	 * 如果在链路上超过此时间没有收到任何数据的话就把链接断开, 重新建立连接.
	 * 
	 * @param soTimeoutSec
	 */
	public final void setSoTimeoutSec(int soTimeoutSec) {
		this.soTimeoutMil = soTimeoutSec * 1000;
	}

	public final void setSendActive(boolean isSendActive) {
		this.isSendActive = isSendActive;
	}

	public final void setActiveIntervalSec(long activeIntervalSec) {
		this.activeIntervalMil = activeIntervalSec * 1000;
	}

	public final void setConnectIntervalSec(long connectIntervalSec) {
		this.connectIntervalSec = connectIntervalSec;
	}

	public final void setHeadLength(int headLength) {
		this.headLength = headLength;
	}

}
