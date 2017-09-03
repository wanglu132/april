package com.april.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogUtil {

	private static Log logMpsp = LogFactory.getLog("MPSP");

	public static void mpsp(Object... os) {
		StringBuffer sb = new StringBuffer();
		sb.append(unull(os[0]));
		int n = os.length;
		for(int i = 1; i < n; i++){
			sb.append(", ").append(unull(os[i]));
		}
		synchronized (logMpsp) {
			logMpsp.info("# " + sb.toString());
		}
	}

	private static Object unull(Object obj) {
		return obj == null ? "" : obj;
	}
	
}
