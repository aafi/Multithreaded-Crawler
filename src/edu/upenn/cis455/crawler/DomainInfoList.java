package edu.upenn.cis455.crawler;

import java.util.ArrayList;

public class DomainInfoList {
		
	private static ArrayList<DomainInfo> domain_list = new ArrayList<DomainInfo>();
	
	public static void add(DomainInfo domain){
		domain_list.add(domain);
	}
	
	public static boolean contains(String name){
		for(DomainInfo info : domain_list){
			if(info.getDomainName().equals(name)){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isPastDelay(String name){
		for(DomainInfo info : domain_list){
			if(info.getDomainName().equals(name)){
				//Check crawl delay
			}
		}
		
		return false;
	}
}
