package edu.upenn.cis455.crawler;

import java.util.Date;

public class DomainInfo {
	
	private String domainName = null;
	private Date last_hit = null;
	private Integer crawl_delay = null;
	
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public Date getLast_hit() {
		return last_hit;
	}
	
	public void setLast_hit(Date last_hit) {
		this.last_hit = last_hit;
	}
	
	public Integer getCrawl_delay() {
		return crawl_delay;
	}
	
	public void setCrawl_delay(Integer crawl_delay) {
		this.crawl_delay = crawl_delay;
	}
	
	
}
