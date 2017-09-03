package com.april.cache;

import java.util.Map;


/**
 * 字典数据缓存接口<br>
 * 实现此接口的类，数据会被放入 ServletContext
 * @author WL
 *
 */
public interface DicCache extends RefreshableCache{
	
	/**
	 * 
	 * @return 缓存对象
	 */
	public Map<String, Object> getData();
}
