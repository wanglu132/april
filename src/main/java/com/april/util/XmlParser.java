package com.april.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser {

	public Map<String, Object> onlySon2map(String xml) {
		Map<String, Object> map = new HashMap<String, Object>();
		String key = null, value = null;
		Pattern patt1 = Pattern.compile("<([^<></]+)>[^<></]+</\\1>");
		Pattern patt2 = Pattern.compile("<[^<></]+");
		Pattern patt3 = Pattern.compile(">[^<]+");
		Matcher mat1 = patt1.matcher(xml);
		while (mat1.find()) {
			String dan = mat1.group();
			Matcher mat2 = patt2.matcher(dan);
			if (mat2.find()) {
				key = mat2.group().substring(1);
			}
			Matcher mat3 = patt3.matcher(dan);
			if (mat3.find()) {
				value = mat3.group().substring(1);
			}
			map.put(key, value);
		}
		return map;
	}

	public boolean contains(String xml, String eleName) {
//		String f = "\\s?\n?";
		String f = "";
		String regex = "<" + eleName + ">" + f + "(<([^<></]+)>[^<></]+</\\2>"
				+ f + ")+</" + eleName + ">";
		return Pattern.matches(regex, xml);
	}

	public Map<String, List> extract(String xml, String eleName,
			String... eleNames) {
		String f = "\\s?\n?";
		String regex = null;

		String[] eNames = new String[eleNames.length + 1];
		eNames[0] = eleName;
		System.arraycopy(eleNames, 0, eNames, 1, eleNames.length);

		Map<String, List> map = new HashMap<String, List>(eNames.length);

		for (String eName : eNames) {
			regex = "<" + eName + ">" + f + "(<([^<></]+)>[^<></]+</\\2>" + f
					+ ")+</" + eName + ">";
			List<Map> list = new ArrayList<Map>();
			Pattern patt = Pattern.compile(regex);
			Matcher mat = patt.matcher(xml);
			while (mat.find()) {
				String dan = mat.group();
				list.add(onlySon2map(dan));
			}
			if (list.size() > 0) {
				map.put(eName, list);
			}
		}

		return map;
	}

	public String delete(String xml, String eleName, String... eleNames) {
		String f = "\\s?\n?";
		String regex = null;

		String[] eNames = new String[eleNames.length + 1];
		eNames[0] = eleName;
		System.arraycopy(eleNames, 0, eNames, 1, eleNames.length);

		for (String eName : eNames) {
			regex = "<" + eName + ">" + f + "(<([^<></]+)>[^<></]+</\\2>" + f
					+ ")+</" + eName + ">";
			xml = xml.replaceAll(regex, "");
		}
		return xml;
	}
	
	public String fetchAttribute(String xml, String eleName, String attrName){
		String regex1 = "<" + eleName + "[^<]+";
		String regex2 = attrName + "\\s?=\\s?\"[^\"]+";
		String regex3 = "\"[^\"]+";
		Matcher mat1 = Pattern.compile(regex1).matcher(xml);
		if(mat1.find()){
			String grop1 = mat1.group();
			Matcher mat2 = Pattern.compile(regex2).matcher(grop1);
			if(mat2.find()){
				String grop2 = mat2.group();
				Matcher mat3 = Pattern.compile(regex3).matcher(grop2);
				if(mat3.find()){
					return mat3.group().substring(1);
				}
			}
		}
		return null;
	}
	
	public void getElementsByTagName(String xml, String eleName){
		String regex1 = "<" + eleName + ".*" + "/" + eleName;
		Matcher mat1 = Pattern.compile(regex1).matcher(xml);
		if(mat1.find()){
			String grop1 = mat1.group();
			System.out.println(grop1);
		}
	}
	
	public static void main(String[] args) {
		XmlParser parser = new XmlParser();
		
//		String xml = "<PACKAGE><FUNCODE>1006</FUNCODE><TRANSAMT>00000100</TRANSAMT><REQDATE>20080506</REQDATE><MOBILENO>15910671642</MOBILENO><ACCESSTYPE>01</ACCESSTYPE><BANKID>73100000</BANKID><ORDERDETAIL><GOODSDESC>Q��</GOODSDESC><GOODSPRICE>12.5</GOODSPRICE><GOODSID>0001</GOODSID><GOODSNUM>2</GOODSNUM></ORDERDETAIL><ORDERDETAIL><GOODSDESC>��Ʊ</GOODSDESC><GOODSPRICE>22.5</GOODSPRICE><GOODSID>0002</GOODSID><GOODSNUM>3</GOODSNUM></ORDERDETAIL><ORDERTIME>151703</ORDERTIME><DESC>�ֻ�Ǯ���¶���</DESC><TRANSID>000000000104</TRANSID><ORDERDATE>20080506</ORDERDATE><ORDERID>000000010000000100000001</ORDERID><SERVICETYPE>00000001</SERVICETYPE><REQTIME>151703</REQTIME><MERID>10731</MERID></PACKAGE>";
//		String xml2 = "<PACKAGE><TRANSID>1234</TRANSID><FUNCODE>1005</FUNCODE><ORDERDETAIL><ORDERID>1000</ORDERID><ORDERNUM>56</ORDERNUM></ORDERDETAIL><DESC>O'Reilly</DESC><ORDERDETAIL><ORDERID>2000</ORDERID><ORDERNUM>78</ORDERNUM></ORDERDETAIL><GOODSDETAIL><GOODID>2000</GOODID><GOODNUM>78</GOODNUM></GOODSDETAIL></PACKAGE>";
//		if(parser.contains(xml2, "ORDERDETAIL")){
//			System.out.println("la");
//		}
	    String xml3 = "<april xmlns=\"http://www.example.org/schema1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.example.org/schema1 april.xsd\"><db><ds><driver value=\"com.mysql.jdbc.Driver\" use=\"wanglu\" /><Url value=\"jdbc:mysql://localhost:3306/test\" /><Username value=\"root\" /><Password value=\"1\" /></ds></db></april>";
	    String xm14 = "<server name=\"req\" port=\"8803\" readBufferSize=\"600\"><handler class=\"\"/><spliter class=\"\"/></server><server name=\"res\" port=\"8805\" readBufferSize=\"600\"><handler class=\"\"/><spliter class=\"\"/></server>";
	    parser.getElementsByTagName(xm14, "server");
	
	}

}
