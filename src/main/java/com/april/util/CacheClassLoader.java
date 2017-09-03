package com.april.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.april.config.ConcreteFileSubject;
import com.april.config.Files;
import com.april.config.Updater;

public class CacheClassLoader extends ClassLoader {

	private static Log log = LogFactory.getLog(CacheClassLoader.class);

	private Map<String, ClassInfo> classCache = new ConcurrentHashMap<String, ClassInfo>();

	private static CacheClassLoader cacheClassLoader = new CacheClassLoader();

	private CacheClassLoader() {
		super(Thread.currentThread().getContextClassLoader());
	}

	public static CacheClassLoader getInstance() {
		return cacheClassLoader;
	}

	private Class<?> load_class(String className) {
		String classFile = className.replace(".", "/") + ".class";
		String path = this.getResource(classFile).getPath();
		byte[] cbs = null;
		InputStream in = null;
		BufferedInputStream bf = null;
		try {
			in = new FileInputStream(path);
			bf = new BufferedInputStream(in);

			cbs = new byte[bf.available()];
			bf.read(cbs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(in != null)
				in.close();
				if(bf != null)
				bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.defineClass(className, cbs, 0, cbs.length);
	}

	public Class loadByClassName(String className) {
		String classFile = className.replace(".", "/") + ".class";
		String path = Thread.currentThread().getContextClassLoader()
				.getResource(classFile).getPath();
		long lm = new File(path).lastModified();
		if (classCache.containsKey(className)) {
			if (classCache.get(className).getLm() == lm) {
				log.info("从map中获取Class.");
				return classCache.get(className).getClazz();
			} else {
				log.info("类已经被修改, 实例化一个新的classLoader, 重新加载类.");
				CacheClassLoader ccl = new CacheClassLoader();
				Class clazz = ccl.load_class(className);
				ClassInfo ci = new ClassInfo(lm, clazz);

				classCache.put(className, ci);
				return clazz;
			}
		} else {
			log.info("该类第一次被加载,使用一个固定的classLoader, 加载此类.");
			Class clazz = CacheClassLoader.getInstance().load_class(className);
			ClassInfo ci = new ClassInfo(lm, clazz);

			classCache.put(className, ci);
			return clazz;
		}
	}

	public Map<String, ClassInfo> getClassCache() {
		return classCache;
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		CacheClassLoader ccl = new CacheClassLoader();
		Class clazz = ccl.loadByClassName("com.april.config.ConfigFile");
//		ConcreteFileSubject up = ConcreteFileSubject.class.cast(clazz.newInstance());
		
		Object configfile = clazz.getDeclaredMethod("getInstance").invoke(clazz.newInstance());
		if(configfile instanceof Object){
			System.out.println(true);
		}
//		System.out.println(clazz.isInstance(clazz.newInstance()));
	}
}

class ClassInfo implements Serializable {
	private long lm;

	private Class clazz;

	public ClassInfo(long lm, Class clazz) {
		super();
		this.lm = lm;
		this.clazz = clazz;
	}

	public long getLm() {
		return lm;
	}

	public Class getClazz() {
		return clazz;
	}
}
