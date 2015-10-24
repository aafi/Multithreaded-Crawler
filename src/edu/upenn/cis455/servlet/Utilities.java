package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class Utilities {
	
	/**
	 * Builds a DOM object given the contents of XML/HTML file
	 * @param contents of the file
	 * @return the DOM object
	 */
	public static Document buildXmlDom(String content){
		InputSource is = new InputSource(new StringReader(content));
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;
		try {
			
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return doc;
	}
	
	/**
	 * Create HTML page.
	 *
	 * @param title the title
	 * @param body the body
	 * @return byte array of HTML page
	 */
	public static String createHTML(String title, String body){
		String start =
				"<html>" +
				"<title>"+title+"</title>" +
				"<meta charset=\"utf-8\">"+
				"<body>";

		String end =
				"</body>" +
				"</html>";
		
		StringBuilder page = new StringBuilder();
		page.append(start);
		page.append(body);
		page.append(end);
		
		return page.toString();
		
		
	}

}

