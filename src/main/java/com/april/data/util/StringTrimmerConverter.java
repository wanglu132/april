package com.april.data.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * 去除请求参数首尾空格
 * @author WL
 *
 */
public class StringTrimmerConverter implements Converter<String, String> {

	@Override
	public String convert(String source) 
	{
		if(StringUtils.hasLength(source))
		{
			return source.trim();
		}
		return null;
	}

}
