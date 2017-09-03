package com.april.nserver;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.april.nserver.spliter.Spliter;
import com.april.nserver.handler.Handler;

public class SingleServerConfig {

	private SocketAddress socketAddress;
	private Handler handler;
	private Spliter spliter;
	private int readBufferSize = 600;

	private ExecutorService splitWorkerExecutor;
	private Map<String, ByteBuffer> cache;

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Spliter getSpliter() {
		return spliter;
	}

	public void setSpliter(Spliter spliter) {
		this.spliter = spliter;
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}

	ExecutorService getSplitWorkerExecutor() {
		return splitWorkerExecutor;
	}

	Map<String, ByteBuffer> getCache() {
		return cache;
	}

	public SingleServerConfig() {
	}

	public SingleServerConfig(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
		this.splitWorkerExecutor = Executors.newSingleThreadExecutor();
		this.cache = new HashMap<String, ByteBuffer>();
	}

}
