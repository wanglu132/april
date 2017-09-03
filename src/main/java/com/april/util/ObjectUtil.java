/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.april.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.lang.reflect.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * UtilObject
 * 
 */
public class ObjectUtil {

	private static final Log log = LogFactory.getLog(ObjectUtil.class);

	public static byte[] getBytes(InputStream is) {
		byte[] buffer = new byte[4 * 1024];
		ByteArrayOutputStream bos = null;
		byte[] data = null;
		try {
			bos = new ByteArrayOutputStream();

			int numBytesRead;
			while ((numBytesRead = is.read(buffer)) != -1) {
				bos.write(buffer, 0, numBytesRead);
			}
			data = bos.toByteArray();
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				log.error(e);
			}
		}

		return data;
	}

	/** Serialize an object to a byte array */
	public static byte[] getBytes(Object obj) {
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		byte[] data = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			data = bos.toByteArray();
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				log.error(e);
			}
		}

		return data;
	}

	public static long getByteCount(Object obj) {
		OutputStreamByteCount osbc = null;
		ObjectOutputStream oos = null;
		try {
			osbc = new OutputStreamByteCount();
			oos = new ObjectOutputStream(osbc);
			oos.writeObject(obj);
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
				if (osbc != null) {
					osbc.close();
				}
			} catch (IOException e) {
				log.error(e);
			}
		}
		if (osbc != null) {
			return osbc.getByteCount();
		} else {
			return 0;
		}
	}

	/** Deserialize a byte array back to an object */
	public static Object getObject(byte[] bytes) {
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		Object obj = null;

		try {
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis, Thread.currentThread()
					.getContextClassLoader());
			obj = ois.readObject();
		} catch (ClassNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				log.error(e);
			}
		}

		return obj;
	}

	public static boolean equalsHelper(Object o1, Object o2) {
		if (o1 == o2) {
			// handles same-reference, or null
			return true;
		} else if (o1 == null || o2 == null) {
			// either o1 or o2 is null, but not both
			return false;
		} else {
			return o1.equals(o2);
		}
	}

	public static <T> int compareToHelper(Comparable<T> o1, T o2) {
		if (o1 == o2) {
			// handles same-reference, or null
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			// either o1 or o2 is null, but not both
			return 1;
		} else {
			return o1.compareTo(o2);
		}
	}

	public static int doHashCode(Object o1) {
		if (o1 == null)
			return 0;
		return o1.hashCode();
	}
	
	public static void main(String[] args) {
		String a = "aaaaaaaaaaaa";
		System.out.println(getByteCount(a));
	}
}

class OutputStreamByteCount extends OutputStream {

	protected long byteCount = 0;

	public OutputStreamByteCount() {
		super();
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int arg0) throws IOException {
		byteCount++;
	}

	public void write(byte[] b) throws IOException {
		byteCount += b.length;
	}

	public void write(byte[] b, int off, int len) throws IOException {
		byteCount += len;
	}

	public long getByteCount() {
		return this.byteCount;
	}
}

class ObjectInputStream extends java.io.ObjectInputStream {

	private ClassLoader classloader;

	public ObjectInputStream(InputStream in, ClassLoader loader)
			throws IOException {
		super(in);
		this.classloader = loader;
	}

	/**
	 * @see java.io.ObjectInputStream#resolveClass(java.io.ObjectStreamClass)
	 */
	protected Class resolveClass(ObjectStreamClass classDesc)
			throws IOException, ClassNotFoundException {
		return loadClass(classDesc.getName(), classloader);
	}

	public static Class<?> loadClass(String className, ClassLoader loader)
			throws ClassNotFoundException {
		// small block to speed things up by putting using preloaded classes for
		// common objects, this turns out to help quite a bit...
		Class<?> theClass = null;

		if (loader == null)
			loader = Thread.currentThread().getContextClassLoader();

		try {
			theClass = loader.loadClass(className);
		} catch (Exception e) {
		}

		return theClass;
	}

	/**
	 * @see java.io.ObjectInputStream#resolveProxyClass(java.lang.String[])
	 */
	protected Class resolveProxyClass(String[] interfaces) throws IOException,
			ClassNotFoundException {
		Class[] cinterfaces = new Class[interfaces.length];
		for (int i = 0; i < interfaces.length; i++)
			cinterfaces[i] = classloader.loadClass(interfaces[i]);

		try {
			return Proxy.getProxyClass(classloader, cinterfaces);
		} catch (IllegalArgumentException e) {
			throw new ClassNotFoundException(null, e);
		}

	}
}
