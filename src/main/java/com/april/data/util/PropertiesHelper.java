package com.april.data.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.util.StringUtils;

public class PropertiesHelper
{
  public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;
  public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;
  public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
  private int systemPropertiesMode = 0;
  private Properties p;
  
  public PropertiesHelper(Properties p)
  {
    setProperties(p);
  }
  
  public PropertiesHelper(Properties p, int systemPropertiesMode)
  {
    setProperties(p);
    if ((systemPropertiesMode != 0) && (systemPropertiesMode != 1) && (systemPropertiesMode != 2)) {
      throw new IllegalArgumentException("error systemPropertiesMode mode:" + systemPropertiesMode);
    }
    this.systemPropertiesMode = systemPropertiesMode;
  }
  
  public Properties getProperties()
  {
    return this.p;
  }
  
  public void setProperties(Properties props)
  {
    if (props == null) {
      throw new IllegalArgumentException("properties must be not null");
    }
    this.p = props;
  }
  
  public String getRequiredProperty(String key)
    throws IllegalStateException
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      throw new IllegalStateException("required property is blank by key=" + key);
    }
    return value;
  }
  
  public String getNullIfBlank(String key)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return null;
    }
    return value;
  }
  
  public String getNullIfEmpty(String key)
  {
    String value = getProperty(key);
    if ((value == null) || ("".equals(value))) {
      return null;
    }
    return value;
  }
  
  public String getAndTryFromSystem(String key)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      value = getSystemProperty(key);
    }
    return value;
  }
  
  private String getSystemProperty(String key)
  {
    String value = System.getProperty(key);
    if (isBlankString(value)) {
      value = System.getenv(key);
    }
    return value;
  }
  
  public Integer getInteger(String key)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return null;
    }
    return Integer.valueOf(Integer.parseInt(value));
  }
  
  public int getInt(String key, int defaultValue)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return defaultValue;
    }
    return Integer.parseInt(value);
  }
  
  public int getRequiredInt(String key)
    throws IllegalStateException
  {
    return Integer.parseInt(getRequiredProperty(key));
  }
  
  public Long getLong(String key)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return null;
    }
    return Long.valueOf(Long.parseLong(value));
  }
  
  public long getLong(String key, long defaultValue)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return defaultValue;
    }
    return Long.parseLong(value);
  }
  
  public long getRequiredLong(String key)
    throws IllegalStateException
  {
    return Long.parseLong(getRequiredProperty(key));
  }
  
  public Boolean getBoolean(String key)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return null;
    }
    return Boolean.valueOf(Boolean.parseBoolean(value));
  }
  
  public boolean getBoolean(String key, boolean defaultValue)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return defaultValue;
    }
    return Boolean.parseBoolean(value);
  }
  
  public boolean getRequiredBoolean(String key)
    throws IllegalStateException
  {
    return Boolean.parseBoolean(getRequiredProperty(key));
  }
  
  public Float getFloat(String key)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return null;
    }
    return Float.valueOf(Float.parseFloat(value));
  }
  
  public float getFloat(String key, float defaultValue)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return defaultValue;
    }
    return Float.parseFloat(value);
  }
  
  public float getRequiredFloat(String key)
    throws IllegalStateException
  {
    return Float.parseFloat(getRequiredProperty(key));
  }
  
  public Double getDouble(String key)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return null;
    }
    return Double.valueOf(Double.parseDouble(value));
  }
  
  public double getDouble(String key, double defaultValue)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return defaultValue;
    }
    return Double.parseDouble(value);
  }
  
  public double getRequiredDouble(String key)
    throws IllegalStateException
  {
    return Double.parseDouble(getRequiredProperty(key));
  }
  
  public URL getURL(String key)
    throws IllegalArgumentException
  {
    try
    {
      return new URL(getProperty(key));
    }
    catch (MalformedURLException e)
    {
      throw new IllegalArgumentException("Property " + key + " must be a valid URL (" + getProperty(key) + ")");
    }
  }
  
  public Object getClassInstance(String key)
    throws IllegalArgumentException
  {
    String s = getProperty(key);
    if ((s == null) || ("".equals(s.trim()))) {
      throw new IllegalArgumentException("Property " + key + " must be a valid classname  : " + key);
    }
    try
    {
      return Class.forName(s).newInstance();
    }
    catch (ClassNotFoundException nfe)
    {
      throw new IllegalArgumentException(s + ": invalid class name for key " + key, nfe);
    }
    catch (InstantiationException e)
    {
      throw new IllegalArgumentException(s + ": class could not be reflected " + s, e);
    }
    catch (IllegalAccessException e)
    {
      throw new IllegalArgumentException(s + ": class could not be reflected " + s, e);
    }
  }
  
  public Object getClassInstance(String key, Object defaultinstance)
    throws IllegalArgumentException
  {
    return containsKey(key) ? getClassInstance(key) : defaultinstance;
  }
  
  public String[] getStringArray(String key)
  {
    String v = getProperty(key);
    if (v == null) {
      return new String[0];
    }
    return StringUtils.tokenizeToStringArray(v, ", \t\n\r\f");
  }
  
  public int[] getIntArray(String key)
  {
    return toIntArray(getStringArray(key));
  }
  
  public Properties getStartsWithProperties(String prefix)
  {
    if (prefix == null) {
      throw new IllegalArgumentException("'prefix' must be not null");
    }
    Properties props = getProperties();
    
    Properties result = new Properties();
    for (Map.Entry<Object, Object> entry : props.entrySet())
    {
      String key = (String)entry.getKey();
      if ((key != null) && (key.startsWith(prefix))) {
        result.put(key.substring(prefix.length()), entry.getValue());
      }
    }
    return result;
  }
  
  public Object setProperty(String key, int value)
  {
    return setProperty(key, String.valueOf(value));
  }
  
  public Object setProperty(String key, long value)
  {
    return setProperty(key, String.valueOf(value));
  }
  
  public Object setProperty(String key, float value)
  {
    return setProperty(key, String.valueOf(value));
  }
  
  public Object setProperty(String key, double value)
  {
    return setProperty(key, String.valueOf(value));
  }
  
  public Object setProperty(String key, boolean value)
  {
    return setProperty(key, String.valueOf(value));
  }
  
  public String getProperty(String key, String defaultValue)
  {
    String value = getProperty(key);
    if (isBlankString(value)) {
      return defaultValue;
    }
    return value;
  }
  
  public String getProperty(String key)
  {
    String propVal = null;
    if (this.systemPropertiesMode == 2) {
      propVal = getSystemProperty(key);
    }
    if (propVal == null) {
      propVal = this.p.getProperty(key);
    }
    if ((propVal == null) && (this.systemPropertiesMode == 1)) {
      propVal = getSystemProperty(key);
    }
    return propVal;
  }
  
  public Object setProperty(String key, String value)
  {
    return this.p.setProperty(key, value);
  }
  
  public void clear()
  {
    this.p.clear();
  }
  
  public Set<Map.Entry<Object, Object>> entrySet()
  {
    return this.p.entrySet();
  }
  
  public Enumeration<?> propertyNames()
  {
    return this.p.propertyNames();
  }
  
  public boolean contains(Object value)
  {
    return this.p.contains(value);
  }
  
  public boolean containsKey(Object key)
  {
    return this.p.containsKey(key);
  }
  
  public boolean containsValue(Object value)
  {
    return this.p.containsValue(value);
  }
  
  public Enumeration<Object> elements()
  {
    return this.p.elements();
  }
  
  public Object get(Object key)
  {
    return this.p.get(key);
  }
  
  public boolean isEmpty()
  {
    return this.p.isEmpty();
  }
  
  public Enumeration<Object> keys()
  {
    return this.p.keys();
  }
  
  public Set<Object> keySet()
  {
    return this.p.keySet();
  }
  
  public void list(PrintStream out)
  {
    this.p.list(out);
  }
  
  public void list(PrintWriter out)
  {
    this.p.list(out);
  }
  
  public void load(InputStream inStream)
    throws IOException
  {
    this.p.load(inStream);
  }
  
  public void loadFromXML(InputStream in)
    throws IOException, InvalidPropertiesFormatException
  {
    this.p.loadFromXML(in);
  }
  
  public Object put(Object key, Object value)
  {
    return this.p.put(key, value);
  }
  
  public void putAll(Map<? extends Object, ? extends Object> t)
  {
    this.p.putAll(t);
  }
  
  public Object remove(Object key)
  {
    return this.p.remove(key);
  }
  
  /**
   * @deprecated
   */
  public void save(OutputStream out, String comments)
  {
    this.p.save(out, comments);
  }
  
  public int size()
  {
    return this.p.size();
  }
  
  public void store(OutputStream out, String comments)
    throws IOException
  {
    this.p.store(out, comments);
  }
  
  public void storeToXML(OutputStream os, String comment, String encoding)
    throws IOException
  {
    this.p.storeToXML(os, comment, encoding);
  }
  
  public void storeToXML(OutputStream os, String comment)
    throws IOException
  {
    this.p.storeToXML(os, comment);
  }
  
  public Collection<Object> values()
  {
    return this.p.values();
  }
  
  public String toString()
  {
    return this.p.toString();
  }
  
  public static Properties restoreFromString(String str)
  {
    if (str == null) {
      return new Properties();
    }
    Properties p = new Properties();
    try
    {
      p.load(new ByteArrayInputStream(str.getBytes()));
    }
    catch (IOException e)
    {
      throw new IllegalStateException("restore properties from String occer error. str:" + str, e);
    }
    return p;
  }
  
  private static boolean isBlankString(String value)
  {
    return (value == null) || ("".equals(value.trim()));
  }
  
  private static int[] toIntArray(String[] array)
  {
    int[] result = new int[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = Integer.parseInt(array[i]);
    }
    return result;
  }
}
