package com.april.config;

interface ConfigFileSubject {

	void addConfigFileObserver(ConfigFileObserver o);
	
	void deleteConfigFileObserver(ConfigFileObserver o);
	
	void notifyConfigFileObserver();
	
	void notifyConfigFileObserver(Object arg);
	
	void deleteConfigFileObservers();
	
	int countConfigFileObserver();
	
	boolean hasChanged();
	
}
