package edu.upenn.cis455.crawler;

import java.util.Date;

import edu.upenn.cis455.crawler.info.RobotsTxtInfo;

public class DomainInfo {
	
	public RobotsTxtInfo info = null;
	private Date last_hit = null;
	
	public void setInfo(RobotsTxtInfo info){
		this.info = info;
	}
	
	public void setLastHit(Date date){
		last_hit = date;
	}
	
	
	
//	private String domainName = null;
//	private Date last_hit = null;
//	private RobotsTxt
//	
//	public String getDomainName() {
//		return domainName;
//	}
//	
//	public void setDomainName(String domainName) {
//		this.domainName = domainName;
//	}
//	
//	public Date getLast_hit() {
//		return last_hit;
//	}
//	
//	public void setLast_hit(Date last_hit) {
//		this.last_hit = last_hit;
//	}
//	
//	public Integer getCrawl_delay() {
//		return crawl_delay;
//	}
//	
//	public void setCrawl_delay(Integer crawl_delay) {
//		this.crawl_delay = crawl_delay;
//	}
	
	
}
