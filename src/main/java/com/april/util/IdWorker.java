package com.april.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 生成唯一编号
 * @author WL
 *
 */
public abstract class IdWorker {

	private static final long TIME_BEGIN = 1397619257861L;
	
	/**
	 * 根据用户UID，生成唯一编号<br>
	 * 从 TIME_BEGIN 开始，一年内时间位不会重复
	 * @param uid 不能大于2097151
	 * @return
	 */
	public static long next(long uid)
	{
		if(uid > 2097151 || uid < 0)
		{
			throw new IllegalArgumentException("uid can't be greater than 2097151 or less than 0");
		}
		return (uid << 42) | ((System.currentTimeMillis() - TIME_BEGIN) << 7) | ThreadLocalRandom.current().nextInt(128);
	}
	
	/**
	 * 根据用户ID，获取表名
	 * @param uid
	 * @param baseTableName
	 * @param tableNum
	 * @return
	 */
	public static String getTableName(int uid, String baseTableName, int tableNum)
	{
		int h = uid;
		h ^= (h >>> 20) ^ (h >>> 12);
		h ^= (h >>> 7) ^ (h >>> 4);

		int i = h & (tableNum - 1);

		return baseTableName + "_" + i;
	}
}
