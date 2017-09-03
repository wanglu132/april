package com.april.util;

import java.io.*;

public class SequenceUtil {

	private static SequenceUtil su = null;

	private SequenceUtil() {
	}

	public static synchronized SequenceUtil getInstance() {
		if (su == null)
			su = new SequenceUtil();
		return su;
	}

	public synchronized long getSequence(String name) {
		long seq = 0;
		try {
			File f = new File(name + ".seq");
			if (!f.exists()) { 
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeLong(seq);
			} else { 
				FileInputStream fis = new FileInputStream(f);
				DataInputStream dis = new DataInputStream(fis);
				seq = dis.readLong() + 1;
				if (seq < 0)
					seq = 0;
				FileOutputStream fos = new FileOutputStream(f);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeLong(seq);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return seq;
	}

	public static String formatSequence(long seq, int width) {
		Long lg = new Long(seq);
		String is = lg.toString();
		if (is.length() < width) {
			while (is.length() < width)
				is = "0" + is;
		} else {
			is = is.substring(is.length() - width, is.length());
		}
		return is;
	}

	public static void main(String[] args) {
		System.out.println(SequenceUtil.formatSequence(SequenceUtil
				.getInstance().getSequence("wan"), 1));
	}
}