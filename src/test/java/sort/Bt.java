package sort;

import java.math.BigDecimal;
import java.util.*;

class Bt {

	public static void main(String[] args) {
		// Input
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		String[] s = new String[n + 2];
		for (int i = 0; i < n; i++) {
			s[i] = sc.next();
		}
		sc.close();

		String temp = null;
		for (int i = 0; i < s.length - 3; i++) {
			for (int j = 0; j < s.length - 3 - i; j++) {
				if (new BigDecimal(s[j]).compareTo(new BigDecimal(s[j + 1])) < 0) {
					temp = s[j];
					s[j] = s[j + 1];
					s[j + 1] = temp;
				}
			}
		}

		// Output
		for (int i = 0; i < n; i++) {
			System.out.println(s[i]);
		}
	}

}