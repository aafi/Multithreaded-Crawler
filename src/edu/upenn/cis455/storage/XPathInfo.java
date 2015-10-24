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
	String channel;
	
	ArrayList<String> matched_urls = new ArrayList<String>();
	
//	boolean isValid;
//
//	public boolean isValid() {
//		return isValid;
//	}
//
//	public void setValid(boolean isValid) {
//		this.isValid = isValid;
//	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public ArrayList<String> getMatched_urls() {
		return matched_urls;
	}

	public void setMatched_urls(ArrayList<String> matched_urls) {
		this.matched_urls = matched_urls;
	}
}
