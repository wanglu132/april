package com.april.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesUtil {
	private static Log log = LogFactory.getLog(PropertiesUtil.class);

	public static Properties getProperties(String path) {

		Properties prop = new Properties();
		try {
			InputStream input = new FileInputStream(path);
			prop.load(input);
			input.close();
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
		return prop;
	}

}

