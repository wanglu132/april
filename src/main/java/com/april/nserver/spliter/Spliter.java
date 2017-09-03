package com.april.nserver.spliter;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;



public interface Spliter {

	public List<Packet> split(ByteBuffer buffer, String cacheKey, Map<String, ByteBuffer> cache);
}
