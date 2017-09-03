package forkJoin;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TimedEventSupport {
	private static final Timer timer = new Timer();

	/**
	 * Build a future to return the value after a delay.
	 * 
	 * @param delay
	 * @param value
	 * @return future
	 */
	public static <T> CompletableFuture<T> delayedSuccess(int delay, T value) {
		CompletableFuture<T> future = new CompletableFuture<T>();
		TimerTask task = new TimerTask() {
			public void run() {
				future.complete(value);
			}
		};
		timer.schedule(task, delay * 1000);
		return future;
	}

	/**
	 * Build a future to return a throwable after a delay.
	 * 
	 * @param delay
	 * @param t
	 * @return future
	 */
	public static <T> CompletableFuture<T> delayedFailure(int delay, Throwable t) {
		CompletableFuture<T> future = new CompletableFuture<T>();
		TimerTask task = new TimerTask() {
			public void run() {
				future.completeExceptionally(t);
			}
		};
		timer.schedule(task, delay * 1000);
		return future;
	}

	// task definitions
	private static CompletableFuture<Integer> task1(int input) {
		return TimedEventSupport.delayedSuccess(1, input + 1);
	}

	private static CompletableFuture<Integer> task2(int input) {
		return TimedEventSupport.delayedSuccess(2, input + 2);
	}

	private static CompletableFuture<Integer> task3(int input) {
		return TimedEventSupport.delayedSuccess(3, input + 3);
	}

	private static CompletableFuture<Integer> task4(int input) {
		return TimedEventSupport.delayedSuccess(1, input + 4);
	}

	private static CompletableFuture<Integer> runBlocking() {
		Integer i1 = task1(1).join();
		CompletableFuture<Integer> future2 = task2(i1);
		CompletableFuture<Integer> future3 = task3(i1);
		Integer result = task4(future2.join() + future3.join()).join();
		return CompletableFuture.completedFuture(result);
	}

	private static CompletableFuture<Integer> runNonblocking() {
		return task1(1)
				.thenCompose(i1 -> ((CompletableFuture<Integer>) task2(i1).thenCombine(task3(i1), (i2, i3) -> i2 + i3)))
				.thenCompose(i4 -> task4(i4));
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		long before = 0, use = 0;
//		before = System.currentTimeMillis();
//		CompletableFuture<Integer> cf = runBlocking();
//		System.out.println(cf.get());
//		use = System.currentTimeMillis() - before;
//		System.out.println(use);
//		timer.cancel();
		
		before = System.currentTimeMillis();
		CompletableFuture.supplyAsync(() -> { 
				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return 8;
			}).handleAsync((r, t) -> r + 6);
		use = System.currentTimeMillis() - before;
		System.out.println(use);
	}
}