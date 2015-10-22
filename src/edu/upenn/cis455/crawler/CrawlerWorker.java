package edu.upenn.cis455.crawler;

import java.io.IOException;
import java.util.Date;

import edu.upenn.cis455.crawler.info.URLInfo;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.DomainEntity;

public class CrawlerWorker implements Runnable{
	
	private DBWrapper db;
	
	public CrawlerWorker(){
		db = new DBWrapper(XPathCrawler.dir);
	}
	
	/**
	 * Run method for threads.
	 */
	public void run(){
		
		while(true){
			String url = null;
			
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
			
			//Check if the URL was already visited
//			synchronized(UrlQueue.visited){
//				if(UrlQueue.visited.contains(url))
//					continue;
//			}
			
			
			/** Check if URL domain was hit before **/
			String domain = null;
			String protocol = null;
			
			if(url.startsWith("http://")){
				domain = new URLInfo(url).getHostName();
				protocol = "http://";
			}else if(url.startsWith("https://")){
				domain = new UrlParse(url).getHostName();
				protocol = "https://";
			}
			
			boolean isRobotTxt = false;
			if(DomainInfoList.contains(domain)){
				//TODO check for disallowed links in robots.txt
				
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
				synchronized(UrlQueue.visited){
					UrlQueue.visited.add(url);
				}
				continue;
			}
			
			if(result.equals("Error") || result.equals("301")){
				synchronized(UrlQueue.visited){
					UrlQueue.visited.add(url);
				}
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
			
			//add url to visited list
			synchronized(UrlQueue.visited){
				UrlQueue.visited.add(url);
			}
			
			//call appropriate parse function if robot.txt
			if(isRobotTxt){
				//Parse robots.txt
				RobotParser.parse(contents);
			}else{
				//Add information to database if updated
				if(updated){
					DomainEntity entity;
					if(found_in_db){
						entity = db.getDomainInfo(url);
					}else{
						entity = new DomainEntity();
						entity.setUrl(url);
						entity.setLast_checked(new Date());
					}
					
					entity.setRaw_content(contents);
					db.putDomainInfo(entity);
				}
			}
			
			if(http_client.getType().equals("text/html")){
				//TODO call html parse
			}
			
			
			
			
		} //End of while
	} //End of run
}
