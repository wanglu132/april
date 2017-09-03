package dp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Solution {

	private static final Scanner scan = new Scanner(System.in);

	public static void main(String args[]) throws Exception {
		
		String xs = scan.nextLine();
		
		int x = Integer.valueOf(xs);
		
		int y = 0;
		for(int i = 1; i < Integer.MAX_VALUE; i++) {
			y = x * i;
			String ys = String.valueOf(y);
			char[] yc = ys.toCharArray();
			boolean b_1 = true;
			for(char c : yc) {
				if(c != '4' && c != '0') {
					b_1 = false;
				}
			}
			
			if(b_1) {
				char[] one = ys.substring(0, ys.indexOf('0') - 1).toCharArray();
				char[] two = ys.substring(ys.indexOf('0')).toCharArray();
				
				boolean flag = true;
				for(char c : one) {
					if(c != '4') {
						flag = false;
					}
				}
				
				flag = true;
				if(flag) {
					for(char c : two) {
						if(c != '0') {
							flag = false;
						}
					}
				}
				
				if(flag) {
					int a = one.length;
					int b = two.length;
					int z = 2 * a + b;
					System.out.println(z);
				}
			}
		}

	}

}
