package com.april.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * MD5生成的摘要长度为16个字节, 128位<br>
 * SHA-1生成的摘要长度为20个字节, 160位 支持算法有:MD2, MD5, SHA-1
 * 
 * @author Administrator
 * 
 */
public class MessageDigestUtil {
	/**
	 * 使用指定的消息摘要算法对指定的字符串做一个摘要，返回摘要的内容<br>
	 * (将摘要字节数组转为16进制字符串的形式返回)
	 * 
	 * @param origin
	 * @param algorithm
	 *            如 "MD5", "SHA-1" 等
	 * @return
	 */
	public static String doMessageDigestHex(String message, String algorithm) {
		return CodingUtil.bytes2hex(doMessageDigest(message,
				algorithm));
	}

	public static String doMessageDigestHex(byte[] bytes, String algorithm) {
		return CodingUtil.bytes2hex(doMessageDigest(bytes,
				algorithm));
	}

	/**
	 * 使用指定的消息摘要算法对指定的字符串做一个摘要，返回摘要的内容<br>
	 * (以摘要字节数组的形式返回)
	 * 
	 * @param origin
	 * @param algorithm
	 *            如 "MD5", "SHA-1" 等
	 * @return
	 */
	public static byte[] doMessageDigest(String message, String algorithm) {
		return doMessageDigest(message.getBytes(), algorithm);
	}

	/**
	 * 使用指定的消息摘要算法对指定的字节数组做一个摘要，返回摘要的内容<br>
	 * (以摘要字节数组的形式返回)
	 * 
	 * @param bytes
	 * @param algorithm
	 *            如 "MD5", "SHA-1" 等
	 * @return
	 */
	public static byte[] doMessageDigest(byte[] bytes, String algorithm) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
			return md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}

	}

	public static String md5Hex(String message) {
		return CodingUtil.bytes2hex(md5(message));
	}

	public static byte[] md5(String message) {
		return md5(message.getBytes());
	}

	public static String md5Hex(byte[] bytes) {
		return CodingUtil.bytes2hex(md5(bytes));
	}

	public static byte[] md5(byte[] bytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			return md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}

	}

	public static String shaHex(String message) {
		return CodingUtil.bytes2hex(sha(message));
	}

	public static byte[] sha(String message) {
		return sha(message.getBytes());
	}

	public static String shaHex(byte[] bytes) {
		return CodingUtil.bytes2hex(sha(bytes));
	}

	public static byte[] sha(byte[] bytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			return md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}

	}

	public static void main(String args[]) {
		System.out.println(MessageDigestUtil.doMessageDigestHex("abc", "SHA"));
		System.out
				.println(MessageDigestUtil.doMessageDigestHex("abc", "SHA-1"));
	}
}
