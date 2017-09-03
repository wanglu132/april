package com.april.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.april.util.ReflectUtil;

public class ReloadableObjectsPool{

	private Map<String, ClassInfo> objectPools = new HashMap<String, ClassInfo>();

	private List<Class<?>> reloadableInterfaces = new ArrayList<Class<?>>();
	
	private static ReloadableObjectsPool op = new ReloadableObjectsPool();

	public static ReloadableObjectsPool getInstance() {
		return op;
	}

	void addObject(Class<?> clazz) {
		Object o = null;
		try {
			o = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String className = clazz.getName();
		String absoluteClassFilePath = ReflectUtil.classNameToAbsoluteClassFilePath(className);
		long lastMObject = new File(absoluteClassFilePath).lastModified();
		ClassInfo ci = new ClassInfo(o, lastMObject);
		objectPools.put(className, ci);
	}
	
	void removeObject(Class<?> clazz){
		String className = clazz.getName();
		objectPools.remove(className);
	}
	
	public <T> T getObject(Class<?> clazz) {
		
		String className = clazz.getName();
		return (T)objectPools.get(className).getClazz();
	}
	
	Map<String, ClassInfo> getObjects(){
		return objectPools;
	}

	public void addReloadableInterface(Class<?> interfaceClass){
		reloadableInterfaces.add(interfaceClass);
	}
	
	List<Class<?>> getReloadableInterfaces(){
		return reloadableInterfaces;
	}
	
    class ClassInfo {
		private Object clazz;

		private long lastMt;

		ClassInfo(Object clazz, long lastMt) {
			this.clazz = clazz;
			this.lastMt = lastMt;
		}

		Object getClazz() {
			return clazz;
		}

		long getLastMt() {
			return lastMt;
		}

		void setLastMt(long lastMt) {
			this.lastMt = lastMt;
		}

	}
    
    public static void main(String[] args) {
	}
}
