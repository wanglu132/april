package forkJoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Fibonacci extends RecursiveTask<Integer> {

	private static final long serialVersionUID = 1L;

	final int n;

	Fibonacci(int n) {
		this.n = n;
	}

	public Integer compute() {
		if (n <= 1)
			return n;
		Fibonacci f1 = new Fibonacci(n - 1);
		f1.fork();
		Fibonacci f2 = new Fibonacci(n - 2);
		return f2.compute() + f1.join();
	}
	
	public static void main(String[] args) {
		
		ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
	             new DebugForkJoinWorkerThreadFactory(), null, false);  
		ForkJoinTask<Integer> result = forkJoinPool.submit(new Fibonacci(25));
		
		try {
			System.out.println(result.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
