package edu.upenn.cis455.crawler;

import java.util.HashMap;

public class RobotTxtMapping {
	
	private static HashMap <String,DomainInfo> mappings = new HashMap <String,DomainInfo>();
	
	public static void add(String url, DomainInfo info){
		if(!contains(url)){
			info.setAgentMatch("cis455crawler");
			synchronized(mappings){
				mappings.put(url, info);
			}
		}
	}
	
	public static boolean contains(String domain){
		boolean found = false;
		synchronized(mappings){
			if(mappings.containsKey(domain))
				found = true;
		}
		return found;
	}
	
	public static DomainInfo get(String domain){
		DomainInfo dom;
		synchronized(mappings){
			dom = mappings.get(domain);
		}
		
		return dom;
	}
	
	public static String getAgentMatch(String domain){
			return get(domain).getAgentMatch();
	}
	
		
//	private static ArrayList<DomainInfo> domain_list = new ArrayList<DomainInfo>();
//	
//	public static void add(DomainInfo domain){
//		domain_list.add(domain);
//	}
//	
//	public static boolean contains(String name){
//		for(DomainInfo info : domain_list){
//			if(info.getDomainName().equals(name)){
//				return true;
//			}
//		}
//		
//		return false;
//	}
//	
//	public static boolean isPastDelay(String name){
//		for(DomainInfo info : domain_list){
//			if(info.getDomainName().equals(name)){
//				//Check crawl delay
//			}
//		}
//		
//		return false;
//	}
}
