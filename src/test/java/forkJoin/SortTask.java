package forkJoin;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class SortTask extends RecursiveAction {

	private static final long serialVersionUID = 1L;

	final int[] array;
	final int lo, hi;

	SortTask(int[] array, int lo, int hi) {
		this.array = array;
		this.lo = lo;
		this.hi = hi;
	}

	SortTask(int[] array) {
		this(array, 0, array.length);
	}

	@Override
	protected void compute() {
		if (hi - lo < THRESHOLD)
			sortSequentially(lo, hi);
		else {
			int mid = (lo + hi) >>> 1;
			invokeAll(new SortTask(array, lo, mid), new SortTask(array, mid, hi));
			merge(lo, mid, hi);
		}
	}

	// implementation details follow:
	static final int THRESHOLD = 1000;

	void sortSequentially(int lo, int hi) {
		Arrays.sort(array, lo, hi);
	}

	void merge(int lo, int mid, int hi) {
		int[] buf = Arrays.copyOfRange(array, lo, mid);
		for (int i = 0, j = lo, k = mid; i < buf.length; j++)
			array[j] = (k == hi || buf[i] < array[k]) ? buf[i++] : array[k++];
	}
	
	public static void main(String[] args) {
		int n = 0b1000000000000000000000000000000, j = n >>> 3;
		int a[] = new int[j];
		Random r = new Random();
		
		for(int i = 0; i < j ; i++) {
			a[i] = r.nextInt(n);
		}
		
		long before = 0, use = 0;
		SortTask f = new SortTask(a);
		
		before = System.currentTimeMillis();
		Arrays.parallelSort(a);
		use = System.currentTimeMillis() - before;
		System.out.println(String.format("use: %d", use));
		
//		ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
//	             new DebugForkJoinWorkerThreadFactory(), null, false); 
//		
//		before = System.currentTimeMillis();
//		forkJoinPool.invoke(f);
//		use = System.currentTimeMillis() - before;
//		System.out.println(String.format("use: %d", use));
	}
}
