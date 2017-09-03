package com.april.lifecycle;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.april.util.DomUtil;

public abstract class AbstractHook implements Hook {

	private String xmlPath;

	protected AbstractHook(String xmlClassPath) {
		this.xmlPath = Thread.currentThread().getContextClassLoader().getResource(xmlClassPath).getPath();
	}

	public void init() {
    
     
     this.doInit();
	}
	
	private void config(){
		 DomUtil du = new DomUtil();
	     try {
	    	 Document doc = du.getDocument(xmlPath);
	    	 
	    	 
	    	 
	    	 
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void exit() {
		
		this.doExit();
	}
	
	abstract void doInit();
	
	abstract void doExit();

}
