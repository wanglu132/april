package heap;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] a = new int[n];
        for(int a_i=0; a_i < n; a_i++){
            a[a_i] = in.nextInt();
            
            if(a_i == 0) {
            	System.out.println(String.format("%.1f", (double)a[a_i]));
            	continue;
            }
            
            for(int i = 0; i < a_i; i++) {
            	if(a[a_i] < a[i]) {
            		int tmp = a[a_i];
            		System.arraycopy(a, i, a, i+1, a_i - i);
            		a[i] = tmp;
            	}
            }
            
            if((a_i + 1) % 2 == 0) {
            	int t = (a_i + 1) / 2;
            	double m = ((double)a[t] + (double)a[t - 1]) / 2;
            	System.out.println(String.format("%.1f", (double)m));
            }else{
            	System.out.println(String.format("%.1f", (double)a[a_i / 2]));
            }
        }
    }
}