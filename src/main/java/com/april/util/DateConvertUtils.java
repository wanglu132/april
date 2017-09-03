package com.april.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class DateConvertUtils {
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    public static final String TIME_FORMAT = "HH:mm:ss";
    
    public static final String DATE_TIME_S_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static final String DATE_TIME_S_NY_FORMAT = "MM-dd HH:mm:ss";
    
    public static final String DATE_TIME_M_FORMAT = "yyyy-MM-dd HH:mm";
    
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
	
	public static java.util.Date parse(String dateString,String dateFormat) {
		return parse(dateString, dateFormat,java.util.Date.class);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends java.util.Date> T parse(String dateString,String dateFormat,Class<T> targetResultType) {
		if(StringUtils.isEmpty(dateString))
			return null;
		DateFormat df = new SimpleDateFormat(dateFormat);
		try {
			long time = df.parse(dateString).getTime();
			java.util.Date t = targetResultType.getConstructor(long.class).newInstance(time);
			return (T)t;
		} catch (ParseException e) {
			String errorInfo = "cannot use dateformat:"+dateFormat+" parse datestring:"+dateString;
			throw new IllegalArgumentException(errorInfo,e);
		} catch (Exception e) {
			throw new IllegalArgumentException("error targetResultType:"+targetResultType.getName(),e);
		}
	}
	
	public static String format(Date date,String dateFormat) {
		 if(date == null)
			 return null;
		 return new SimpleDateFormat(dateFormat).format(date);
	}
	
	/**
	 * 将 mysql时间戳 转为 Date
	 * @param unixTimestamp
	 * @return
	 */
	public static Date unixTimestamp2date(int unixTimestamp)
	{
		return new Date(unixTimestamp * 1000L);
	}
	
	public static String unixTimestamp2FormatDate(int unixTimestamp, String dateFormat) {
		return format(unixTimestamp2date(unixTimestamp), dateFormat);
	}
	
	/**
	 * 将 Date 转为 mysql时间戳
	 * @param date
	 * @return
	 */
	public static int date2unixTimestamp(Date date)
	{
		return (int)(date.getTime() / 1000L);
	}
	
	/**
	 * 返回当前时间 格式：mysql时间戳
	 * @return
	 */
	public static int currentUnixTimestamp()
	{
		return (int)(System.currentTimeMillis() / 1000L);
	}
	
	private static final int QUARTER[] = {0, 15, 30, 45};
	
	public static long currentQuarter() {
		int qm = -1;
		int minuteOfHour = DateTime.now().getMinuteOfHour();
		for(int q : QUARTER) {
			int c = minuteOfHour - q;
			if(c >= 0 && c < 15) {
				qm = q;
				break;
			}
		}
		
		if(qm == -1) {
			throw new IllegalArgumentException("quarter is wrong");
		}
		
		long currentQuarter = DateTime.now().minuteOfHour().setCopy(qm).secondOfMinute().setCopy(0).millisOfSecond().setCopy(0).getMillis();
		return currentQuarter;
	}
	
	public static long currentHour() {
		long currentHour = DateTime.now().minuteOfHour().setCopy(0).secondOfMinute().setCopy(0).millisOfSecond().setCopy(0).getMillis();
		return currentHour;
	}
	
	public static long currentDay() {
		long currentDay = DateTime.now().hourOfDay().setCopy(0).minuteOfHour().setCopy(0).secondOfMinute().setCopy(0).millisOfSecond().setCopy(0).getMillis();
		return currentDay;
	}
	
	public static Instant truncatedDownQuarter(long time_sec) {
		Instant instant = Instant.ofEpochSecond(time_sec);
		return instant.truncatedTo(QuarterUnit.QUARTERS);
	}
	
	public static Instant truncatedUpQuarter(long time_sec) {
		return truncatedDownQuarter(time_sec).plus(QuarterUnit.QUARTERS.getDuration());
	}
	
	public static Instant truncatedDownHour(long time_sec) {
		Instant instant = Instant.ofEpochSecond(time_sec);
		return instant.truncatedTo(QuarterUnit.HOURS);
	}
	
	public static Instant truncatedUpHour(long time_sec) {
		return truncatedDownHour(time_sec).plus(QuarterUnit.HOURS.getDuration());
	}
	
	public static Instant truncatedDownDay(long time_sec) {
		Instant instant = Instant.ofEpochSecond(time_sec);
		return instant.truncatedTo(QuarterUnit.DAYS);
	}
	
	public static Instant truncatedUpDay(long time_sec) {
		return truncatedDownDay(time_sec).plus(QuarterUnit.DAYS.getDuration());
	}
	
	public static void main(String[] args) {
		Instant instant = Instant.now();
		System.out.println(instant.toString());
		System.out.println(instant.plus(QuarterUnit.QUARTERS.getDuration()).getEpochSecond());
		System.out.println((int)instant.plus(QuarterUnit.QUARTERS.getDuration()).getEpochSecond());
	}
	
}
