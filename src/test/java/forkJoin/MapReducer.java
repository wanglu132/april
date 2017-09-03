package forkJoin;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountedCompleter;

@FunctionalInterface
interface MyMapper<E> {
	E apply(E e);
}

@FunctionalInterface
interface MyReducer<E> {
	E apply(E x, E y);
}

public class MapReducer<E> extends CountedCompleter<E> {
	final E[] array;
	final MyMapper<E> mapper;
	final MyReducer<E> reducer;
	final int lo, hi;
	MapReducer<E> sibling;
	E result;

	MapReducer(CountedCompleter<?> p, E[] array, MyMapper<E> mapper, MyReducer<E> reducer, int lo, int hi) {
		super(p);
		this.array = array;
		this.mapper = mapper;
		this.reducer = reducer;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	public void compute() {
		if (hi - lo >= 2) {
			int mid = (lo + hi) >>> 1;
			MapReducer<E> left = new MapReducer(this, array, mapper, reducer, lo, mid);
			MapReducer<E> right = new MapReducer(this, array, mapper, reducer, mid, hi);
			left.sibling = right;
			right.sibling = left;
			setPendingCount(1); // only right is pending
			right.fork();
			left.compute(); // directly execute left
		} else {
			if (hi > lo)
				result = mapper.apply(array[lo]);
			tryComplete();
		}
	}

	@Override
	public void onCompletion(CountedCompleter<?> caller) {
		if (caller != this) {
			MapReducer<E> child = (MapReducer<E>) caller;
			MapReducer<E> sib = child.sibling;
			if (sib == null || sib.result == null)
				result = child.result;
			else
				result = reducer.apply(child.result, sib.result);
		}
	}

	@Override
	public E getRawResult() {
		return result;
	}

	public static <E> E mapReduce(E[] array, MyMapper<E> mapper, MyReducer<E> reducer) {
		return new MapReducer<E>(null, array, mapper, reducer, 0, array.length).invoke();
	}
	
	public static void main(String[] args) {
		
		ConcurrentHashMap<String, String> c = new ConcurrentHashMap<String, String>();
		c.put("a", "a");
		c.put("b", "b");
		c.put("c", "c");
		c.put("d", "d");
		c.put("e", "e");
		
		int r = c.reduceEntries(1, e -> e.getKey().length(), (a,b)-> a + b);
		
		System.out.println(r);
	}
}
