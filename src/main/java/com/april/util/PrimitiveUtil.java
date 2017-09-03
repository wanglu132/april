
package com.april.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

public class PrimitiveUtil {

	private static final char[]	digits	= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String bytes2hex(byte[] bs)
	{

		char[] buf = new char[bs.length << 1];
		int i = bs.length, j = buf.length;
		while (i > 0)
		{
			buf[--j] = digits[bs[--i] & 0xF];
			buf[--j] = digits[bs[i] >>> 4 & 0xF];
		}
		return new String(buf, j, buf.length - j);
	}

	private static char c2c(char c)
	{

		if (c >= '0' && c <= '9')
		{
			c -= '0';
		}
		else if (c >= 'A' && c <= 'F')
		{
			c -= '7';
		}
		else
		{
			c -= 'W';
		}
		return c;
	}

	public static byte[] hex2bytes(String hex)
	{

		byte[] buf = new byte[hex.length() >>> 1];
		char[] chars = hex.toCharArray();
		for (int i = 0, j = 0; i < chars.length; i++)
		{
			buf[j++] = (byte) (c2c(chars[i]) << 4 | c2c(chars[++i]));
		}
		return buf;
	}
	
	public static int byteArrayToInt(byte[] b){  
	    byte[] a = new byte[4];  
	    int i = a.length - 1,j = b.length - 1;  
	    for (; i >= 0 ; i--,j--) {//从b的尾部(即int值的低位)开始copy数据  
	        if(j >= 0)  
	            a[i] = b[j];  
	        else  
	            a[i] = 0;//如果b.length不足4,则将高位补0  
	  }  
	    int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位  
	    int v1 = (a[1] & 0xff) << 16;  
	    int v2 = (a[2] & 0xff) << 8;  
	    int v3 = (a[3] & 0xff) ;  
	    return v0 + v1 + v2 + v3;  
	}

	public static String firstCharToLower(String str)
	{

		char[] cArray = str.toCharArray();
		cArray[0] = Character.toLowerCase(cArray[0]);
		return new String(cArray);
	}
	
	public static String firstCharToUpper(String str)
	{

		char[] cArray = str.toCharArray();
		cArray[0] = Character.toUpperCase(cArray[0]);
		return new String(cArray);
	}
	
	public static String[] split(String str, String regex)
	{
		List<String> sArray = new ArrayList<String>();
		StringBuilder sb = new StringBuilder(str.trim());
		
		for(int idx = sb.indexOf(regex); idx != -1; idx = sb.indexOf(regex))
		{
			String s = sb.substring(0, idx);
			if(s.length() > 0)
			{
				sArray.add(s);
			}
			sb.delete(0, idx + 1);
		}
		String rest = sb.toString();
		if(rest.length() != 0)
		{
			sArray.add(rest);
		}
		return sArray.toArray(new String[0]);
	}
	
	public static void copyFields(Object src, Object dst)
	{
		Field[] srcFields = src.getClass().getDeclaredFields();
		for(Field srcField : srcFields)
		{
			if(!srcField.isAnnotationPresent(XmlTransient.class))
			{
				try
				{
					Object value = src.getClass().getDeclaredMethod("get" + firstCharToUpper(srcField.getName())).invoke(src);
					dst.getClass().getDeclaredMethod("set" + firstCharToUpper(srcField.getName()), srcField.getType()).invoke(dst, value);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 表列名转对象属性名<br>
	 * 如：trade_variety_id转为tradeVarietyId
	 * @param columnName
	 * @return
	 */
	public static String getPropertyName(String columnName) {
		
		StringBuilder sb = new StringBuilder();
		String name = columnName.toLowerCase();
		
		String[] segs = name.split("_");
		sb.append(segs[0]);
		for(int i = 1; i < segs.length; i++) {
			char[] ca = segs[i].toCharArray();
			ca[0] = Character.toUpperCase(ca[0]);
			sb.append(new String(ca));
		}
		
		return sb.toString();
	}
	
	/**
	 * 表列名转对象set方法名<br>
	 * 如：trade_variety_id转为setTradeVarietyId
	 * @param columnName
	 * @return
	 */
	public static String getSetMethodName(String columnName) {
		
		StringBuilder sb = new StringBuilder("set");
		String name = columnName.toLowerCase();
		
		String[] segs = name.split("_");
		for(int i = 0; i < segs.length; i++) {
			char[] ca = segs[i].toCharArray();
			ca[0] = Character.toUpperCase(ca[0]);
			sb.append(new String(ca));
		}
		
		return sb.toString();
	}
	
	/**
	 * 根据对象属性名设置对象属性值
	 * @param obj
	 * @param propertyName
	 * @param propertyValue
	 * @throws Exception
	 */
	public static void setPropertyValue(Object obj, String propertyName, String propertyValue) throws Exception {
		
		Field property = obj.getClass().getDeclaredField(propertyName);
		Class<?> type = property.getType();
		if(type == Integer.class) {
			property.set(obj, Integer.parseInt(propertyValue));
		}else if(type == Long.class) {
			property.set(obj, Long.parseLong(propertyValue));
		}else if(type == Boolean.class) {
			property.set(obj, Integer.parseInt(propertyValue) > 0);
		}else if(type == BigDecimal.class) {
			property.set(obj, new BigDecimal(propertyValue));
		}
	}
	
	public static void main(String[] args) {
		System.out.println(PrimitiveUtil.getSetMethodName("trade_variety_id"));
	}

}
