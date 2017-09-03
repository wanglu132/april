package stack;

public class StackHeapTest {

	public static void main(String[] args) {
		StackHeapTest t = new StackHeapTest();
		// t.heap();
		t.stack(0);
	}

	private void stack(int i) {
		System.out.println(i);
		stack(++i);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void heap() {
		for (int t = 0;; t++) {
			System.out.println(t);
			new Thread() {
				@Override
				public void run() {
					while (true)
						;
				}
			}.start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
