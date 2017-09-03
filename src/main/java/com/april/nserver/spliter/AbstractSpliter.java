package com.april.nserver.spliter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.april.util.ByteUtil;


public abstract class AbstractSpliter implements Spliter {

	public List<Packet> split(ByteBuffer buffer, String cacheKey, Map<String, ByteBuffer> cache) {
		
		int hlen = setHeadLength();
		
		ByteBuffer bf = cache.remove(cacheKey);
		if(bf != null){
			if (bf.hasRemaining()) {
				ByteBuffer buf = ByteUtil.combineByteBuffer(bf, buffer);
				buffer = buf;
			}
		}
		
		List<Packet> packets = new ArrayList<Packet>();
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
					cache.put(cacheKey, buffer);
					break;
				}
			} else {
				cache.put(cacheKey, buffer);
				break;
			}
		}

		return packets;
	}
	
	public abstract int setHeadLength();
	
	public abstract int getBodyLength(byte[] head);
	
	
	
	

}
