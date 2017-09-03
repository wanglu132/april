package com.april.config;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Files {

	private List<FileInfo> configFiles = new ArrayList<FileInfo>();

	private List<FileInfo> classesFiles = new LinkedList<FileInfo>();
	
	private ConcreteFileSubject cfs = new ConcreteFileSubject();
	
	private static Files cf = new Files();

	public static Files getInstance() {
		return cf;
	}

	public void addConfigFileObserver(ConfigFileObserver o) {
		this.cfs.addConfigFileObserver(o);
	}
	
	ConcreteFileSubject getConcreteFileSubject(){
		return cfs;
	}
	
	List<FileInfo> getConfigFiles() {
		return configFiles;
	}

	public void addConfigFile(String configFilePath) {
		FileInfo info = new FileInfo(configFilePath);
		configFiles.add(info);
	}
	
	void addClassFile(String classFilePath) {
		FileInfo info = new FileInfo(classFilePath);
		classesFiles.add(info);
	}
	
	void removeClassFile(FileInfo classFile) {
		classesFiles.remove(classFile);
	}
	
	List<FileInfo> getClassesFiles() {
		return classesFiles;
	}

	class FileInfo {
		private long lastMT;
		private String filePath;

		FileInfo(String filePath) {
			File file = new File(filePath);
			this.lastMT = file.lastModified();
			this.filePath = filePath;
		}

		long getLastMT() {
			return lastMT;
		}

		void setLastMT(long lastMT) {
			this.lastMT = lastMT;
		}

		String getFilePath() {
			return filePath;
		}

		void setFilePath(String filePath) {
			this.filePath = filePath;
		}

	}

}
