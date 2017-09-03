package com.april.client.common;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class Transceiver {

	private final DataInputStream in;

	private final OutputStream out;

	private final Socket sock;

	public Transceiver(OutputStream out, Socket sock) {
		this.in = null;
		this.out = out;
		this.sock = sock;
	}

	public Transceiver(OutputStream out, DataInputStream in, Socket sock) {
		this.in = in;
		this.out = out;
		this.sock = sock;
	}

	public final void sendBytes(byte[] bs) throws IOException {
		out.write(bs);
		out.flush();
	}

	public final void readFully(byte[] bs) throws IOException {
		if(in == null){
			throw new IllegalArgumentException("为单工, 不允许从对方输入流中读取数据!");
		}
		in.readFully(bs);
	}

	/**
	 * 设置超时时间, 单位秒
	 * 
	 * @param timeout
	 * @throws SocketException
	 */
	public final void setSoTimeoutSec(int timeout) throws SocketException {
		sock.setSoTimeout(timeout * 1000);
	}
}
