package forkJoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Fib extends RecursiveTask<Integer> {

	private static final long serialVersionUID = 1L;
	
	static final int threshold = 13;
	int number;

	Fib(int n) {
		number = n;
	}

	int seqFib(int n) {
		if (n <= 1)
			return n;
		else
			return seqFib(n - 1) + seqFib(n - 2);
	}

	@Override
	protected Integer compute() {
		if (number <= threshold) // granularity ctl
			return seqFib(number);
		else {
			return new Fib(number - 1).compute() + new Fib(number - 2).fork().join();
		}
	}

	public static void main(String[] args) {

		int n = 48, result = 0;
		long before = 0, use = 0;
		Fib f = new Fib(n);
		
		before = System.currentTimeMillis();
		result = f.seqFib(n);
		use = System.currentTimeMillis() - before;
		System.out.println(String.format("result: %d. use: %d", result, use));
		
		ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
	             new DebugForkJoinWorkerThreadFactory(), null, false); 
		
		before = System.currentTimeMillis();
		result = forkJoinPool.invoke(f);
		use = System.currentTimeMillis() - before;
		System.out.println(String.format("result: %d. use: %d", result, use));
		
//		ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
//	             new DebugForkJoinWorkerThreadFactory(), null, false);  
//		
//		before = System.currentTimeMillis();
//		result = forkJoinPool.invoke(new Fibonacci(n));
//		use = System.currentTimeMillis() - before;
//		System.out.println(String.format("result: %d. use: %d", result, use));
		
	}

}
