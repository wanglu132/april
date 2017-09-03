package com.april.nserver.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Session {
	
	private static Log log = LogFactory.getLog(Session.class);

	private SocketChannel ch;

	public Session(SocketChannel ch) {
		this.ch = ch;
	}

	public String toString() {
		return ch.toString();
	}

	public void send(byte[] data) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		ch.write(buffer);
	}

}
