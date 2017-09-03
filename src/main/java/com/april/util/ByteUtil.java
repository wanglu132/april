package com.april.util;

import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ByteUtil {

	private static Log log = LogFactory.getLog(ByteUtil.class);

	public static void printDetail(ByteBuffer buffer) {
		log.info("position: " + buffer.position() + ", limit: "
				+ buffer.limit() + ", capacity: " + buffer.capacity());

	}

	public static void printDetail(ByteBuffer buffer, String name) {
		log.info(name + " - position: " + buffer.position() + ", limit: "
				+ buffer.limit() + ", capacity: " + buffer.capacity());

	}

	public static byte[] combineBytes(byte[] front, byte[] behind) {
		byte[] together = new byte[front.length + behind.length];
		System.arraycopy(front, 0, together, 0, front.length);
		System.arraycopy(behind, 0, together, front.length, behind.length);
		return together;
	}
	
	public static ByteBuffer combineByteBuffer(ByteBuffer front, ByteBuffer behind) {
		ByteBuffer together = ByteBuffer.allocate(front.remaining() + behind.remaining());
		together.put(front);
		together.put(behind);
		together.flip();
		return together;
	}

	public static ByteBuffer deepCopy(ByteBuffer buffer) {
		ByteBuffer buf = ByteBuffer.allocate(buffer.remaining());
		buf.put(buffer);
		buf.flip();
		return buf;
	}

	public static byte[] copyRemainingBytes(ByteBuffer buffer) {
		byte[] b = new byte[buffer.remaining()];
		buffer.get(b);
		return b;
	}
	
}
