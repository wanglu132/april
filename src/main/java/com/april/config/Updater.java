package com.april.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.april.config.Files.FileInfo;
import com.april.config.ReloadableObjectsPool.ClassInfo;
import com.april.util.FileUtil;
import com.april.util.PropertiesUtil;
import com.april.util.ReflectUtil;
import com.april.util.XmlValidator;

public class Updater {

	private Log log = LogFactory.getLog(Updater.class);

	private Files cf = null;
	
	private ReloadableObjectsPool rop = null;
	
	public Updater(Files cf) {
		this.cf = cf;
	}
	
	public Updater(ReloadableObjectsPool rop) {
		this.rop = rop;
		this.loadAllClassFilesInClassPath();
		this.loadAllReloadableObjects(rop);
	}
	
	public Updater(Files cf, ReloadableObjectsPool rop) {
		this.cf = cf;
		this.rop = rop;
		this.loadAllClassFilesInClassPath();
		this.loadAllReloadableObjects(rop);
	}

	public void start() {
		ScheduledExecutorService sec = Executors
				.newSingleThreadScheduledExecutor();
		sec.scheduleWithFixedDelay(new UpdateTimer(), 2, 10, TimeUnit.SECONDS);
	}
	
	public void start(long initialDelay, long delay) {
		ScheduledExecutorService sec = Executors
				.newSingleThreadScheduledExecutor();
		sec.scheduleWithFixedDelay(new UpdateTimer(), initialDelay, delay,
				TimeUnit.SECONDS);
	}
	
	private void loadAllClassFilesInClassPath(){
		
        List<String> classFiles = ReflectUtil.getAllClassFilesInClassPath();
		Files files = Files.getInstance();
		for(String classFile : classFiles){
			files.addClassFile(classFile);
		}
	}
	
	private void loadAllReloadableObjects(ReloadableObjectsPool rop){
		List<Class<?>> classes = ReflectUtil.getAllClassesInClassPath();
		for(Class<?> clazz : classes){
				if(this.isInstanceofSpecInterfaces(clazz)){
					rop.addObject(clazz);
				}
		}
	}
	
	private boolean isInstanceofSpecInterfaces(Class<?> clazz){
		List<Class<?>> interfaces = rop.getReloadableInterfaces();
		for(Class<?> interf : interfaces){
			try {
				if(interf.isInstance(clazz.newInstance())){
					return true;
				}
			} catch (InstantiationException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
		}
		return false;
	}

	private class UpdateTimer implements Runnable {

		public void run() {
			if(cf != null){
				this.updateConfigFile();
			}
			if(rop != null){
				this.updateClass();
			}
		}
		
		private void updateConfigFile() {
			List<FileInfo> cfs = cf.getConfigFiles();
			for (FileInfo fi : cfs) {
				String filePath = fi.getFilePath();
				File file = new File(filePath);
				long lastMT = file.lastModified();
				if (lastMT != fi.getLastMT()) {
					fi.setLastMT(lastMT);
					log.info("更新文件: " + filePath);
					cf.getConcreteFileSubject().setChanged();
					String name = file.getName();
					String reg = name.substring(name.indexOf(".") + 1)
							.toLowerCase();
					if (reg.equals("xml")) {
						FileUtil fu = new FileUtil();
						if (name.equals("april.xml")) {
							String xsdPath = Thread.currentThread()
									.getContextClassLoader().getResource(
											"april.xsd").getPath();
							XmlValidator validator = new XmlValidator();
							if (validator.validate(filePath, xsdPath)) {
								String xml = fu.readFileToString(filePath);
								cf.getConcreteFileSubject().notifyConfigFileObserver(xml);
							} else {
								log.info("验证april.xml文件失败: " + filePath);
							}
						} else {
							String xml = fu.readFileToString(filePath);
							cf.getConcreteFileSubject().notifyConfigFileObserver(xml);
						}
					} else if (reg.equals("properties")) {
						Properties prop = PropertiesUtil
								.getProperties(filePath);
						cf.getConcreteFileSubject().notifyConfigFileObserver(prop);
					}
					cf.getConcreteFileSubject().clearChanged();
				}
			}
		}

		private void updateClass() {
			List<String> currClassFiles = ReflectUtil.getAllClassFilesInClassPath();
			Files files = Files.getInstance();
			List<FileInfo> cachedClassesFiles = files.getClassesFiles();
			
			int currClassFilesSize = currClassFiles.size();
			int cachedClassesFilesSize = cachedClassesFiles.size();
			boolean flag = false;
			if(currClassFilesSize > cachedClassesFilesSize){
				for(String currClassFile : currClassFiles){
					flag = false;
					for(FileInfo cachedClassesFile : cachedClassesFiles){
						if(cachedClassesFile.getFilePath().equals(currClassFile)){
							flag = true;
							break;
						}else{
							continue;
						}
					}
					if(flag == false){
						files.addClassFile(currClassFile);
						log.debug("新增了class文件: " + currClassFile);
						
						String className = ReflectUtil.absoluteClassFilePathToClassName(currClassFile);
						Class<?> currClass = ReflectUtil.getClass(className);
						if(isInstanceofSpecInterfaces(currClass)){
							rop.addObject(currClass);
							log.info("装载class类: " + className);
						}
					}
				}
			}else if(currClassFilesSize < cachedClassesFilesSize){
				List<FileInfo> deletedCachedClassFile = new ArrayList<FileInfo>();
				for(FileInfo cachedClassFile : cachedClassesFiles){
					String cachedClassesFilePath = cachedClassFile.getFilePath();
					flag = false;
					for(String currClassFile : currClassFiles){
						if(currClassFile.equals(cachedClassesFilePath)){
							flag = true;
							break;
						}else{
							continue;
						}
					}
					if(flag == false){
						deletedCachedClassFile.add(cachedClassFile);
						
						String className = ReflectUtil.absoluteClassFilePathToClassName(cachedClassesFilePath);
						Class<?> deletedClass = ReflectUtil.getClass(className);
						if(isInstanceofSpecInterfaces(deletedClass)){
							rop.removeObject(deletedClass);
							log.info("卸载class类: " + className);
						}
					}
				}
				for(FileInfo cachedClassFile : deletedCachedClassFile){
					files.removeClassFile(cachedClassFile);
					log.debug("删除了class文件: " + cachedClassFile.getFilePath());
				}
			}else{
				Map<String, ClassInfo> objects = rop.getObjects();
				Set<String> classNames = objects.keySet();
				for(String className : classNames){
					String absoluteClassFilePath = ReflectUtil.classNameToAbsoluteClassFilePath(className);
					File classFile = new File(absoluteClassFilePath);
					long lastMT = classFile.lastModified();
					ClassInfo ci = objects.get(className);
					if(ci.getLastMt() != lastMT){
						rop.addObject(ReflectUtil.reloadClass(className));
						log.info("重新装载class类: " + className);
					}
				}
			}
		}
		
	}

}
