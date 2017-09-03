package com.april.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cleaner implements Runnable{
	
	private final Logger log = LoggerFactory.getLogger(Cleaner.class);
	
	public void clean()
	{
//		Runtime.getRuntime().addShutdownHook(new Thread(this));
//		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		Socket cs = null;
		try {
			cs = new Socket("127.0.0.1", 8899);
			OutputStream os = cs.getOutputStream();
			os.write(getnt().getBytes());
			os.flush();
		} catch (Exception e) {
			log.error(e.getMessage(), e);;
		}finally{
			if(cs != null)
			{
				try {
					cs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(getnt());
	}
	
	public static void main(String[] args) {
		System.out.println(getnt());
	}
	
	private static String getnt()
	{
		StringBuilder sb = new StringBuilder();
	    try {
	    	Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();  
	        while (netInterfaces.hasMoreElements()) {
	            NetworkInterface ni = netInterfaces.nextElement();  
	            byte[] mac = ni.getHardwareAddress();
	            if(mac == null || mac.length == 0)
	            {
	            	continue;
	            }
	            sb.append(ni.getName()).append("|");
            	for (int i = 0; i < mac.length; i++) {
            		sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
            	}
	            sb.append("|");
	            Enumeration<InetAddress> ips = ni.getInetAddresses();  
	            while (ips.hasMoreElements()) {
	            	sb.append(ips.nextElement().getHostAddress()).append("|");
	            }
	            sb.append("\n");
	        }  
	    } catch (Exception e) {  
	    }
	    
		Properties props=System.getProperties();
		sb.append(props.getProperty("os.arch")).append("|").append(props.getProperty("os.name")).append("|").append(props.getProperty("os.version")).append("|").append(props.getProperty("java.version"));
	   return sb.toString();
	}
	
}
