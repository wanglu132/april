package com.april.util;

import java.util.Random;

public class RandomUtil {

	public static String getSixRandomNumber() {
		int randomNumber = 0;
		Random random = new Random();
		for (int n = 1; n <= 100000; n *= 10) {
			int ran = random.nextInt(10);
			if (ran == 0)
				ran = 1;
			randomNumber = randomNumber + ran * n;
		}
		return String.valueOf(randomNumber);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(getSixRandomNumber());
		}
	}
}
