package dp;

public class Yb {
	
	public static void main(String[] args) {
		int d[] = dp(23);
		for(int n : d) {
			System.out.println(n);
		}
	}

	public static int[] dp(int y) {
		
		int a = 1, b = 3, c = 5;
		int d[] = new int[y+1];
		
		for(int i = 0; i <= y; i++) {
			
			int t = 0;
			if(i >= c) {
				t = c;
			}else if(i >= b) {
				t = b;
			}else if(i >= a) {
				t = a;
			}
			
			if(t == 0) {
				d[i] = 0;
			}else {
				d[i] = d[i - t] + 1;
			}
			
		}
		
		return d;
	}

}
