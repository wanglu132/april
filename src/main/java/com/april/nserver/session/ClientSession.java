package com.april.nserver.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClientSession {

	private static Log log = LogFactory.getLog(ClientSession.class);

	private SocketChannel sh;

	public ClientSession(String host, int port) {
		InetSocketAddress isa = new InetSocketAddress(host, port);
		try {
			sh = SocketChannel.open();
			sh.connect(isa);
			sh.configureBlocking(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Socket socket = getSocket();
		log.info("与服务端连接建立成功: server[" + isa.getHostName() + ":" + isa.getPort() + "] client[" + socket.getLocalPort() + "]");
	}
	
	public void configureBlocking(boolean block){
		try {
			sh.configureBlocking(block);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		try {
			sh.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public byte[] receive(Receiver receiver) {
		return receiver.receive(sh);
	}

	public void close() {
		try {
			sh.finishConnect();
			sh.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket getSocket(){
		return sh.socket();
	}
	
	public void setSoTimeout(int sec){
		try {
			getSocket().setSoTimeout(sec * 1000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

}
