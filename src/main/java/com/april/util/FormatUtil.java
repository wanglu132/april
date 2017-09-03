package com.april.util;

public class FormatUtil {

	public static String removeZero(String input){
		String xiao = input.substring(10, 12);
		int n = input.length();
		for(int i = 0; i < n - 2; i++){
			if(input.charAt(i) != '0'){
				String v = input.substring(i, 10);
				String res = v + "." + xiao;
				return res;
			}
		}
		return "0." + xiao;
	}
	
	public static String removeLast4Zero(String input){
		return input.substring(0, input.indexOf(".") + 3);
	}
	
	public static String leftAlign_Blank(String input, int lenth){
		int n = lenth - input.length();
		for(int i = 0; i < n; i++){
			input += " ";
		}
		return input;
	}
	
	public static void main(String[] args) {
//		System.out.println(removeLast4Zero("0.000000"));
		System.out.println(leftAlign_Blank("aa", 8));
		System.out.println(leftAlign_Blank("aa", 8));
		System.out.println(removeZero("000000111101"));
	}
}
