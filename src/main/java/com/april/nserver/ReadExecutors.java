package com.april.nserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.april.nserver.config.NserverConfig;

class ReadExecutors {

	private final int corePoolSize = NserverConfig.corePoolSize;

	private final int maximumPoolSize = NserverConfig.maximumPoolSize;

	private final long keepAliveTime = NserverConfig.keepAliveTime;
	
	private final int queueLength = NserverConfig.queueLength;

	private BlockingQueue<Runnable> workQueue;

	private final RejectedExecutionHandler handler = new CustomPolicy();

	private final ThreadFactory threadFactory = new CustomThreadFactory();

	ExecutorService newReadThreadPool() {
		if(queueLength == 0)
		    workQueue = new LinkedBlockingQueue<Runnable>();
		else
			workQueue = new LinkedBlockingQueue<Runnable>(queueLength);
		
        return new CustomThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory, handler);
    }

	private class CustomThreadPoolExecutor extends ThreadPoolExecutor {

		public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
				long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
				RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
					threadFactory, handler);
		}
		
		@Override
		protected void beforeExecute(Thread t, Runnable r) {

		}
		
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
		}

		@Override
		protected void terminated() {
		}
	}
	
	private class CustomThreadFactory implements ThreadFactory {
		private final AtomicInteger poolNumber = new AtomicInteger(1);
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		CustomThreadFactory() {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
					.getThreadGroup();
			namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, namePrefix
					+ threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
	
	private class CustomPolicy implements RejectedExecutionHandler {

		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println("丢弃: " + r.toString());
		}

	}
	
}
