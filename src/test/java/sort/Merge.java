package sort;

public class Merge {

	public void merge_sort(int[] a, int p, int r) {
		if (p < r) {
			int q = (p + r) / 2;
			merge_sort(a, p, q);
			merge_sort(a, q + 1, r);
		}
	}

	public static void merge(int a[], int p, int q, int r) {
		int[] la = new int[q - p + 1];
		int[] ra = new int[r - q];

		for (int i = 0; i < la.length; i++) {
			la[i] = a[p + i];
		}

		for (int i = 0; i < ra.length; i++) {
			ra[i] = a[q + i + 1];
		}

		for (int k = p, i = 0, j = 0; k <= r; k++) {
			if (j > (ra.length - 1) || la[i] <= ra[j]) {
				a[k] = la[i];
				i++;
			} else {
				a[k] = ra[j];
				j++;
			}
		}
	}

	public static void main(String[] args) {
		int[] a = { 3, 5, 7, 10, 15, 2, 6, 8, 9, 13 };
		merge(a, 0, 4, 9);

		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
