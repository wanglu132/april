package com.april.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CodingUtil {

	private static Log log = LogFactory.getLog(CodingUtil.class);

	public static String bytes2hex(byte[] bytes) {
		int n = bytes.length;
		StringBuffer buffer = new StringBuffer(n);
		for (int i = 0; i < n; i++) {
			int bn = (int) bytes[i];
			if (bn < 0) {
				bn = 256 + bn;
			}
			buffer.append(Integer.toHexString(bn));
		}
		return buffer.toString();
	}
	
	public static String bytesTohex(byte[] bytes){
		BigInteger bi = new BigInteger(1, bytes);
		return bi.toString(16);
	}

	public static String byte2hex(byte bye) {
		int bn = (int) bye;
		if (bn < 0) {
			bn = 256 + bn;
		}
		return Integer.toHexString(bn);
	}

	public static byte[] hex2bytes(String hex) {

		byte[] bytes = new byte[hex.length() / 2];
		String[] hexs = groupString(hex, 2);
		for (int i = 0; i < hexs.length; i++) {
			bytes[i] = hex2byte(hexs[i]);
		}
		return bytes;
	}
	
	public static byte[] hexTobytes(String hex) {
		BigInteger bi = new BigInteger(hex, 16);
		System.out.println(bi.signum());
		return bi.toByteArray();
	}

	private static byte hex2byte(String hex) {
		int value = hex2int(hex.substring(0, 1)) * 16
				+ hex2int(hex.substring(1, 2));
		if (value > 127)
			value = value - 256;
		return (byte) value;
	}

	private static int hex2int(String hex) {
		return "0123456789abcdef".indexOf(hex);
	}

	/**
	 * UCS2转汉字
	 * 
	 * @param ucs2
	 * @return
	 */
	public static String UCS2ToUTF8(String ucs2) {
		String utf = null;
		try {
			utf = URLDecoder.decode(ucs2, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e);
			e.printStackTrace();
		}
		return utf;
	}

	/**
	 * 汉字转UCS2
	 * 
	 * @param utf8
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String UTF8ToUCS2(String utf8) {
		StringBuffer buffer = new StringBuffer();
		int n = utf8.length();
		for (int i = 0; i < n; i++) {
			String c = String.valueOf(utf8.charAt(i));
			if (isANP(c)) {
				try {
					buffer.append("\\u00" + byte2hex(c.getBytes("UTF-8")[0]));
				} catch (UnsupportedEncodingException e) {
					log.error(e);
					e.printStackTrace();
				}
			} else {
				String chinese = "";
				try {
					chinese = bytes2hex(c.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e);
					e.printStackTrace();
				}
				String[] bytes = groupString(chinese, 2);
				StringBuffer sb = new StringBuffer();
				sb.append(Integer
						.toBinaryString(Integer.parseInt(bytes[0], 16))
						.substring(4));
				sb.append(Integer
						.toBinaryString(Integer.parseInt(bytes[1], 16))
						.substring(2));
				sb.append(Integer
						.toBinaryString(Integer.parseInt(bytes[2], 16))
						.substring(2));
				buffer.append("\\u"
						+ Integer.toHexString(Integer
								.parseInt(sb.toString(), 2)));
			}
		}
		return buffer.toString();
	}

	private static boolean isANP(String a) {
		int n = 1;
		try {
			n = a.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			log.error(e);
			e.printStackTrace();
		}
		if (n == 1)
			return true;
		else
			return false;
	}

	// public static String UTF8ToUCS2(String utf8) {
	// String ub = null;
	// try {
	// ub = URLEncoder.encode(utf8, "UTF-8").replace("%", "");
	// } catch (UnsupportedEncodingException e) {
	// log.error(e);
	// e.printStackTrace();
	// }
	// StringBuffer ucs2 = new StringBuffer();
	// String[] chineses = groupString(ub, 6);
	// for (String chinese : chineses) {
	// String[] bytes = groupString(chinese, 2);
	// StringBuffer buffer = new StringBuffer();
	// buffer.append(Integer
	// .toBinaryString(Integer.parseInt(bytes[0], 16))
	// .substring(4));
	// buffer.append(Integer
	// .toBinaryString(Integer.parseInt(bytes[1], 16))
	// .substring(2));
	// buffer.append(Integer
	// .toBinaryString(Integer.parseInt(bytes[2], 16))
	// .substring(2));
	// ucs2.append("\\u"
// + Integer.toHexString(Integer
	// .parseInt(buffer.toString(), 2)));
	// }
	//
	// return ucs2.toString();
	// }

	public static String[] groupString(String str, int interval) {
		int n = str.length() / interval;
		String[] group = new String[n];
		int m = 0;
		for (int i = 0; i < n; i++) {
			group[i] = str.substring(m, m + interval);
			m += interval;
		}
		return group;
	}
	
	public static String paddingString(String hex, int len, char padding,
			boolean left) {
		StringBuffer buffer = new StringBuffer(hex);
		int l = hex.length();
		if (l <= len) {
			int r = len - l;
			for (int i = 0; i < r; i++) {
				if (left) {
					buffer.insert(0, padding);
				} else {
					buffer.append(padding);
				}
			}
		}
		return buffer.toString();
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		// System.out.println(UTF8ToUCS2("对不起，处理失败，请稍后在试"));
		// System.out.println(new String(hex2bytes("00584d4f")));
		// System.out.println(bytes2hex("CA".getBytes()));
//		String s = "交易失败，发\"KH#姓名#证件号码#支付密码#证件类型\"到10658008开户后再试，证件类型默认为1身份证。询4006125880";
//		// System.out.println(URLEncoder.encode("KH", "UTF-8"));
//		// System.out.println("K".getBytes("UTF-8").length);
//		// System.out.println(UCS2ToUTF8("\u007e\u0048"));
//		System.out.println(UTF8ToUCS2("汉H"));
//		System.out.println(bytesTohex("中".getBytes()));
//		System.out.println(bytes2hex("中".getBytes()));
//		System.out.println(new String(hexTobytes("d6d0")));
		System.out.println(paddingString("1234", 6, ' ', true));
	}
}
