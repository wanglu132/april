package com.april.client.common;

import com.april.util.ByteUtil;

public class Packet {

	private byte[] head;

	private byte[] body;

	public Packet(byte[] head, byte[] body) {
		this.head = head;
		this.body = body;
	}

	public byte[] getHead() {
		return head;
	}

	void setHead(byte[] head) {
		this.head = head;
	}

	public byte[] getBody() {
		return body;
	}

	void setBody(byte[] body) {
		this.body = body;
	}

	public byte[] getPacket() {
		return ByteUtil.combineBytes(getHead(), getBody());
	}
}
