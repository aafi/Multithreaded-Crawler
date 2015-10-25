package edu.upenn.cis455.crawler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import edu.upenn.cis455.servlet.Utilities;

/**
 * This class parses a HTML document for links
 *
 */
public class HtmlParser {
	
	public static void parse(String content, String url){
//		Tidy tidy = new Tidy();
//		tidy.setInputEncoding("UTF-8");
//	    tidy.setOutputEncoding("UTF-8");
//	    tidy.setXmlOut(true);
//	    tidy.setShowWarnings(false);
//	    tidy.setSmartIndent(true);
//	    
//	    ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
//	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//	    tidy.parseDOM(inputStream, outputStream);
//	    content = outputStream.toString("UTF-8");
//	    
//	    Document doc = Utilities.buildXmlDom(content);
		
		Document doc = Jsoup.parse(content, url, Parser.htmlParser());
	    
	    
	    extractLinks(doc);
	}

	private static void extractLinks(Document doc) {
		Elements links = doc.select("a[href]");
		for (Element link : links) {
            String link_to_be_added = link.attr("abs:href");
            synchronized(UrlQueue.queue){
				UrlQueue.queue.add(link_to_be_added);
				UrlQueue.queue.notifyAll();
			}
        }
//		System.out.println("links size: "+links.size());
//		System.out.println("Queue size: "+UrlQueue.queue.size());

		
	}
	
}


