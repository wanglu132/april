package com.april.util;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomUtil {
	
	public DomUtil(){
		
	}

	public Document getDocument(String xmlPath) throws SAXException, IOException, ParserConfigurationException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc=builder.parse(xmlPath);
		doc.normalize();
		return doc;
	}
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		String path = Thread.currentThread().getContextClassLoader().getResource("april.xml").getPath();
		DomUtil du = new DomUtil();
		Document doc = du.getDocument(path);
		NodeList nl = doc.getElementsByTagName("nserver");
		for(int i = 0; i < nl.getLength(); i++){
			Element el = (Element)nl.item(i);
			NodeList nl2 = el.getElementsByTagName("server");
			for(int j = 0; j < nl2.getLength(); j++){
				Element el2 = (Element)nl2.item(j);
				System.out.println(el2.getAttribute("name"));
			}
		}
		
	}
}
