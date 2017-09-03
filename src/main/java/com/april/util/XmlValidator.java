package com.april.util;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class XmlValidator {

	public boolean validate(String xmlPath, String xsdPath) {
		SchemaFactory factory = SchemaFactory
				.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = null;
		try {
			File xsdFile = new File(xsdPath);
			schema = factory.newSchema(xsdFile);
		} catch (SAXException e1) {
			e1.printStackTrace();
		}
		Validator validator = schema.newValidator();
		File xmlFile = new File(xmlPath);
		Source source = new StreamSource(xmlFile);
		try {
			validator.validate(source);
			return true;
		} catch (SAXException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) {
		XmlValidator validator = new XmlValidator();
		String xml = Thread.currentThread().getContextClassLoader().getResource("april.xml").getPath();
		String xsd = Thread.currentThread().getContextClassLoader().getResource("april.xsd").getPath();
		validator.validate(xml, xsd);
	}
}
