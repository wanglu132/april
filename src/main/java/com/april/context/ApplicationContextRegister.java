package com.april.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextRegister implements ApplicationContextAware {

	private final Logger log = LoggerFactory.getLogger(ApplicationContextRegister.class);

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ContextUtils.setApplicationContext(applicationContext);
		log.debug("ApplicationContext registed");
	}

}