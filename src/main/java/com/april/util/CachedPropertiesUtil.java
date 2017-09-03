package com.april.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CachedPropertiesUtil {
	private static Log log = LogFactory.getLog(CachedPropertiesUtil.class);
	private static Map<String, FileInfo> map = Collections
			.synchronizedMap(new HashMap<String, FileInfo>());

	public static Properties getProperties(String classpath) {

		classpath = Thread.currentThread().getContextClassLoader().getResource(classpath)
				.getPath();
		long last = new File(classpath).lastModified();
		if (map.containsKey(classpath)) {
			if (map.get(classpath).getLm() == last) {
				log.info("从map中获取属性文件");
				return map.get(classpath).getProps();
			} else {
				log.info("文件已经被修改, 从文件流中读取属性文件，文件路径:" + classpath);
				return readProps(last, classpath);
			}
		} else {
			log.info("文件第一次被读取, 从文件流中读取属性文件，文件路径:" + classpath);

			return readProps(last, classpath);
		}
	}

	private static Properties readProps(long last, String path) {
		Properties prop = new Properties();
		try {
			InputStream input = new FileInputStream(path);
			prop.load(input);
			input.close();
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
		FileInfo fi = new FileInfo(last, prop);
		map.put(path, fi);
		return prop;
	}
	
	public static int getInt(String classpath, String key){
		Properties props = getProperties(classpath);
		return Integer.valueOf(props.getProperty(key));
	}
	
	public static String getString(String classpath, String key){
		Properties props = getProperties(classpath);
		return props.getProperty(key);
	}

}

class FileInfo {
	private long lm;

	private Properties props;

	public FileInfo(long lm, Properties props) {
		super();
		this.lm = lm;
		this.props = props;
	}

	public long getLm() {
		return lm;
	}

	public Properties getProps() {
		return props;
	}
}
