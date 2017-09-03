package com.april.config;

import java.util.Properties;

public interface ConfigFileObserver {

	public void update(String xml);
	
	public void update(Properties prop);
}
