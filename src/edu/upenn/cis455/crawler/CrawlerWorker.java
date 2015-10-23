package edu.upenn.cis455.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import edu.upenn.cis455.crawler.info.URLInfo;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.DomainEntity;

public class CrawlerWorker implements Runnable{
	
	private DBWrapper db;
	
	public CrawlerWorker(DBWrapper db){
		this.db = db;
	}
	
	/**
	 * Run method for threads.
	 */
	public void run(){
		
		while(true){
			String url = null;
//			System.out.println(UrlQueue.queue.size());
			synchronized(UrlQueue.queue){
				if(!UrlQueue.queue.isEmpty()){
					//Retrieve URL from the queue
					url = UrlQueue.queue.remove();
				}else if(UrlQueue.queue.isEmpty()){
					//Wait for queue to be not empty
					try {
						UrlQueue.queue.wait();
					} catch (InterruptedException e) {
						//TODO handle interrupt ?
						break;
					}
				}
			} //End of synchronized block
			
			
			/** Check if URL domain was hit before **/
			String domain = null;
			String protocol = null;
			
			String path = null;
			if(url.startsWith("http://")){
				domain = new URLInfo(url).getHostName();
				path = new URLInfo(url).getFilePath();
				protocol = "http://";
			}else if(url.startsWith("https://")){
				domain = new UrlParse(url).getHostName();
				path = new UrlParse(url).getFilePath();
				protocol = "https://";
			}
//			System.out.println("Current url: "+url);
			
			boolean isRobotTxt = false;
			if(RobotTxtMapping.contains(domain)){
				String agent_match = RobotTxtMapping.getAgentMatch(domain);
//				RobotTxtMapping.get(domain).info.print();
				if(agent_match.equals("No agent found")){
					//No matching user agent was found
					continue;
				}
				DomainInfo dom = RobotTxtMapping.get(domain);
				
				int delay = dom.info.getCrawlDelay(agent_match);
				Date last_hit = dom.getLastHit();
				Date current_date = new Date();
				
				if((current_date.getTime() - last_hit.getTime()) < delay*1000){
					synchronized(UrlQueue.queue){
						UrlQueue.queue.add(url);
					}
					continue;
				}
				System.out.println("Current url: "+url);
//				System.out.println(new Date().toString());
				
				boolean allowed = false;
				if(dom.info.getAllowedLinks(agent_match)!=null){
					if(dom.info.getAllowedLinks(agent_match).contains(path)){
						allowed = true;
					}
				}
				System.out.println("path: "+path);
				boolean disallowed = false;
				if(dom.info.getDisallowedLinks(agent_match)!=null){
					for(String link : dom.info.getDisallowedLinks(agent_match)){
						System.out.println("disallowed: "+link);
						if(path.startsWith(link)){
							System.out.println("disallow match");
							disallowed = true;
							break;
						}
					}
				}
				System.out.println();
				if(disallowed && !allowed){
					System.out.println(url+" disallowed");
					continue;
				}
				//Check for crawl delay
				
			}else{ //This domain was not hit before
				
				//Put URL back in the queue as we need to get robots.txt
				synchronized(UrlQueue.queue){
					UrlQueue.queue.add(url);
				}
				url = protocol+domain+"/robots.txt";
				isRobotTxt = true;
			}
			
			/**
			 * If we have reached this point, then we have something to query for 
			 * through the HTTP Client or the database.
			 * We will query the database first and see if the document we are looking for
			 * already exists in the database.
			 * If it does, we will query through the HTTP client to see if the file has been modified
			 * since we stored it (by sending the if-modified-since header).
			 * If it has, we will fetch and parse the new document. If not we parse the same document.
			 * If the document doesn't exist in the database, we fetch and parse the document.
			 */
			
			boolean found_in_db = false;
			HttpClient http_client;
			if(db.containsDomain(url)){
				found_in_db = true;
				Date last_hit = db.getDomainInfo(url).getLast_checked();
				http_client = new HttpClient(found_in_db,last_hit);
			}else{
				http_client = new HttpClient();
			}
			
			String result;
			String contents = null;
			try {
				result = http_client.doWork(url, XPathCrawler.size);
			} catch (IOException e) {
				System.out.println(e);
//				synchronized(UrlQueue.visited){
//					UrlQueue.visited.add(url);
//				}
				continue;
			}
			
			if(result.equals("Error") || result.equals("301")){
//				synchronized(UrlQueue.visited){
//					UrlQueue.visited.add(url);
//				}
				continue;
			}
			
			boolean updated = true;
			//Get contents requested
			if(result.equals("304")){
				updated = false;
				contents = db.getDomainInfo(url).getRaw_content();
				System.out.println(url+": Not Modified");
			}else if(result.equals("Success")){
				contents = http_client.getDocument();
			}
			
//			//add url to visited list
//			synchronized(UrlQueue.visited){
//				UrlQueue.visited.add(url);
//			}
			
			//call appropriate parse function if robot.txt
			if(isRobotTxt){
				//Parse robots.txt
				RobotParser.parse(contents,domain);
			}else{
				//Add information to database if updated
				if(updated){
					DomainEntity entity;
					if(found_in_db){
						entity = db.getDomainInfo(url);
					}else{
						entity = new DomainEntity();
						entity.setUrl(url);
					}
					Date date = new Date();
					entity.setLast_checked(date);
					entity.setRaw_content(contents);
					db.putDomainInfo(entity);
					//update last hit date in domain mapping
					RobotTxtMapping.get(domain).setLastHit(date);
				}
			}
			
//			System.out.println(db.containsDomain(url));
			
			if(http_client.getType().equals("text/html")){
				HtmlParser.parse(contents,url);
			}
			
			
			
		} //End of while
	} //End of run
}
