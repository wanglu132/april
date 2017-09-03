package forkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {
	
	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		return new DebugForkJoinWorkThread(pool);
	}
	
	private static class DebugForkJoinWorkThread extends ForkJoinWorkerThread {
		
		final Logger log = LoggerFactory.getLogger(DebugForkJoinWorkThread.class);

		protected DebugForkJoinWorkThread(ForkJoinPool pool) {
			super(pool);
		}
		
		@Override
		protected void onStart() {
			super.onStart();
			log.debug("{} onStart.", getName());
		}
		
		@Override
		protected void onTermination(Throwable exception) {
			super.onTermination(exception);
			log.debug("{} onTermination.", getName());
		}
		
	}

}
