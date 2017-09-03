package forkJoin;

import java.util.concurrent.RecursiveAction;

public class IncrementTask extends RecursiveAction {
	
	private static final long serialVersionUID = 1L;
	
	static final int THRESHOLD = 1000;
	
	final long[] array;
	final int lo, hi;

	IncrementTask(long[] array, int lo, int hi) {
		this.array = array;
		this.lo = lo;
		this.hi = hi;
	}

	protected void compute() {
		if (hi - lo < THRESHOLD) {
			for (int i = lo; i < hi; ++i)
				array[i]++;
		} else {
			int mid = (lo + hi) >>> 1;
			invokeAll(new IncrementTask(array, lo, mid), new IncrementTask(array, mid, hi));
		}
	}
}
