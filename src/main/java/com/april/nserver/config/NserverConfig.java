package com.april.nserver.config;

import com.april.config.Config;
import com.april.config.ConfigFileObserverAdaptor;


public abstract class NserverConfig extends ConfigFileObserverAdaptor implements Config {

	public static int corePoolSize = 3;
	
	public static int maximumPoolSize = 10;
	
	public static long keepAliveTime = 30;
	
	public static int queueLength = 0;
	
	public void config(String xml) {
		
		
		config();
	}
	
	public abstract void config();
}
