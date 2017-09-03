package com.april.nserver.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.april.util.ByteUtil;


public abstract class Receiver {
	
	public byte[] receive(SocketChannel socketChannel){
		ByteBuffer head = ByteBuffer.allocate(setHeadLength());
		ByteBuffer body = null;
		try {
			socketChannel.read(head);
			body = ByteBuffer.allocate(getBodyLength(head.array()));
			socketChannel.read(body);
			head.flip();
			body.flip();
			return ByteUtil.combineByteBuffer(head, body).array();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract int setHeadLength();
	
	public abstract int getBodyLength(byte[] head);
}
