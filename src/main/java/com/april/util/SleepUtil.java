package com.april.util;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SleepUtil {

	private static Log log = LogFactory.getLog(SleepUtil.class);

	/**
	 * 秒
	 * 
	 * @param timeout
	 */
	public static void sleepSec(long timeout) {
		try {
			TimeUnit.SECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 毫秒
	 * 
	 * @param timeout
	 */
	public static void sleepMil(long timeout) {
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 微秒
	 * 
	 * @param timeout
	 */
	public static void sleepMic(long timeout) {
		try {
			TimeUnit.MICROSECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 纳秒
	 * 
	 * @param timeout
	 */
	public static void sleepNan(long timeout) {
		try {
			TimeUnit.NANOSECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

}
