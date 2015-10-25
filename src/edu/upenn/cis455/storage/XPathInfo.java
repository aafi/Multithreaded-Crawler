package edu.upenn.cis455.storage;


import java.util.ArrayList;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * Stores all the documents matched with a XPath
 * @author cis455
 *
 */
@Entity
public class XPathInfo {
	
	@PrimaryKey
	String xpath;
	
	ArrayList<String> matched_urls = new ArrayList<String>();
	

	public String getXPath() {
		return xpath;
	}

	public void setXPath(String XPath) {
		this.xpath = XPath;
	}

	public ArrayList<String> getMatched_urls() {
		return matched_urls;
	}

	public void setMatched_urls(ArrayList<String> matched_urls) {
		this.matched_urls = matched_urls;
	}
}
