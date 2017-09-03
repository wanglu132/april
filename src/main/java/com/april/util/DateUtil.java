package com.april.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

	/**
	 * yyyyMMdd
	 * 
	 * @return
	 */
	public static String getReqDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(new Date());
	}

	/**
	 * HHmmss
	 * 
	 * @return
	 */
	public static String getReqTime() {
		SimpleDateFormat format = new SimpleDateFormat("HHmmss");
		return format.format(new Date());
	}

	/**
	 * yyMMdd HHmmss
	 * 
	 * @param format
	 * @return
	 */
	public static String getFormatedDT(String format) {
		SimpleDateFormat ft = new SimpleDateFormat(format);
		return ft.format(new Date());
	}
	
	public static String addAroundSpecField(int field, int amount){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_MONTH, amount);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		return format.format(calendar.getTime());
	}
	
	public static void main(String[] args) {
		System.out.println(addAroundSpecField(Calendar.DAY_OF_MONTH, -7));
	}
}
