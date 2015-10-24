package edu.upenn.cis455.crawler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import edu.upenn.cis455.crawler.info.URLInfo;
import edu.upenn.cis455.servlet.Utilities;
import edu.upenn.cis455.storage.ChannelInfo;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.DomainEntity;
import edu.upenn.cis455.storage.XPathInfo;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

public class CrawlerWorker implements Runnable{
	
	private DBWrapper db;
	private static boolean shutdown = false;
	private boolean waiting = false;
	
	public CrawlerWorker(DBWrapper db){
		this.db = db;
	}
	
	/**
	 * Run method for threads.
	 */
	public void run(){
		
		while(!shutdown){
			String url = null;
//			System.out.println(UrlQueue.queue.size());
			synchronized(UrlQueue.queue){
				if(!UrlQueue.queue.isEmpty()){
					//Retrieve URL from the queue
					url = UrlQueue.queue.remove();
				}else if(UrlQueue.queue.isEmpty()){
					//Wait for queue to be not empty
					try {
						waiting = true;
						UrlQueue.queue.wait();
					} catch (InterruptedException e) {
						//TODO handle interrupt ?
						break;
					}
				}
			} //End of synchronized block
			
			String original_url = null;
			waiting = false;
			if(url!=null){
				/** Check if URL domain was hit before **/
				String domain = null;
				String protocol = null;
				
				String path = null;
	//			System.out.println("Current url: "+url);
				if(url.startsWith("http://")){
					domain = new URLInfo(url).getHostName();
					path = new URLInfo(url).getFilePath();
					protocol = "http://";
				}else if(url.startsWith("https://")){
					domain = new UrlParse(url).getHostName();
					path = new UrlParse(url).getFilePath();
					protocol = "https://";
				}
//				System.out.println("Current url in "+Thread.currentThread().getName()+" : " +url);
				
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
					Date last_hit;
					
					synchronized(dom){
						last_hit = dom.getLastHit();
					}
					
					Date current_date = new Date();
					if((current_date.getTime() - last_hit.getTime()) < delay*1000){
						synchronized(UrlQueue.queue){
//							System.out.println("Putting back "+url+" from thread "+Thread.currentThread().getName());
							UrlQueue.queue.add(url);
							UrlQueue.queue.notifyAll();
						}
						continue;
					}
					
	//				System.out.println(new Date().toString());
					
					boolean allowed = false;
					if(dom.info.getAllowedLinks(agent_match)!=null){
						if(dom.info.getAllowedLinks(agent_match).contains(path)){
							allowed = true;
						}
					}
//					System.out.println("path: "+path);
					boolean disallowed = false;
					if(dom.info.getDisallowedLinks(agent_match)!=null){
						for(String link : dom.info.getDisallowedLinks(agent_match)){
	//						System.out.println("disallowed: "+link);
							if(path.startsWith(link)){
								disallowed = true;
								break;
							}
						}
					}
//					System.out.println();
					if(disallowed && !allowed){
//						System.out.println(url+" disallowed");
						continue;
					}
					//Check for crawl delay
					
				}else{ //This domain was not hit before
					
					//Put URL back in the queue as we need to get robots.txt
					original_url = url;
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
				if(db.getDomainInfo(url)!=null){
//					System.out.println("cached in db");
					found_in_db = true;
					Date last_hit = db.getDomainInfo(url).getLast_checked();
					http_client = new HttpClient(found_in_db,last_hit);
				}else{
					http_client = new HttpClient();
				}
				
				String result;
				String contents = null;
				
				//start
				if(isRobotTxt){
					try {
						result = http_client.doWork(url, XPathCrawler.size);
					} catch (IOException e) {
						System.out.println(e);
						continue;
					}
				}else{
					DomainInfo dom = RobotTxtMapping.get(domain);
					String agent_match = RobotTxtMapping.getAgentMatch(domain);
					if(agent_match.equals("No agent found")){
						//No matching user agent was found
						continue;
					}
					
					int delay = dom.info.getCrawlDelay(agent_match);
					Date last_hit;
					
					synchronized(dom){
						last_hit = dom.getLastHit();
						Date current_date = new Date();
						if((current_date.getTime() - last_hit.getTime()) < delay*1000){
							synchronized(UrlQueue.queue){
								UrlQueue.queue.add(url);
								UrlQueue.queue.notifyAll();
							}
							continue;
						}
						
						try {
							result = http_client.doWork(url, XPathCrawler.size);
						} catch (IOException e) {
							System.out.println(e);
							continue;
						}
						
						dom.setLastHit(new Date());
					}
				}
				
				if(result.equals("Error") || result.equals("301")){
					continue;
				}
				
				boolean updated = true;
				String type = null;
				//Get contents requested
				if(result.equals("304")){
					updated = false;
					contents = db.getDomainInfo(url).getRaw_content();
					System.out.println(url+": Not Modified");
				}else if(result.equals("Success")){
					contents = http_client.getDocument();
				}
				
				//call appropriate parse function if robot.txt
				if(isRobotTxt){
					//Parse robots.txt
					RobotParser.parse(contents,domain);
					type = http_client.getType();
					synchronized(UrlQueue.queue){
						UrlQueue.queue.add(original_url);
						UrlQueue.queue.notifyAll();
					}
				}else{
					//Add information to database if updated
					Date date = null;
					if(updated){
						DomainEntity entity;
						if(found_in_db){
							entity = db.getDomainInfo(url);
						}else{
							entity = new DomainEntity();
							entity.setUrl(url);
							entity.setType(http_client.getType());
						}
						
						date = new Date();
						entity.setLast_checked(date);
						
//						DomainInfo d = RobotTxtMapping.get(domain);
//						synchronized(d){
//							d.setLastHit(date);
//						}
						entity.setRaw_content(contents);
						
						int given_files;
						if(XPathCrawler.num_of_files == -1){
							given_files = Integer.MAX_VALUE;
						}else{
							given_files = XPathCrawler.num_of_files;
						}
						
						if(given_files > XPathCrawler.getNumFilesDownloaded()){
							db.putDomainInfo(entity);
							XPathCrawler.incrementNumFiles();
						}
						type = entity.getType();
					}else{
						if(found_in_db){
							if(date == null){
								date = new Date();
							}
//							DomainInfo d = RobotTxtMapping.get(domain);
//							synchronized(d){
//								d.setLastHit(date);
//							}
							type = db.getDomainInfo(url).getType();
						}
					}
				}
				
	//			System.out.println(db.containsDomain(url));
				if(type.equals("text/html")){
					HtmlParser.parse(contents,url);
				}
				
				if(type.endsWith("xml")){
					try {
						matchDocument(contents,url);
					} catch (UnsupportedEncodingException e) {
						System.out.println("Exception thrown");
						e.printStackTrace();
					}
				}
			
			
			}
		} //End of while
//		System.out.println("While exited for thread "+Thread.currentThread().getName());
	} //End of run
	
	
	public void matchDocument(String contents,String url) throws UnsupportedEncodingException{
		Tidy tidy = new Tidy();
		tidy.setInputEncoding("UTF-8");
	    tidy.setOutputEncoding("UTF-8");
	    tidy.setXmlTags(true);
	    tidy.setXmlOut(true);
	    tidy.setSmartIndent(true);
	    tidy.setShowErrors(0);
	    tidy.setShowWarnings(false);
	    tidy.setQuiet(true);
	    ByteArrayInputStream inputStream = new ByteArrayInputStream(contents.getBytes("UTF-8"));
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    tidy.parseDOM(inputStream, outputStream);
	    contents = outputStream.toString("UTF-8");
		Document d = Utilities.buildXmlDom(contents);
		
		Set <String> xpath_set = new HashSet<String>();
		ArrayList<ChannelInfo> channels = db.getAllChannels();
		
		for(ChannelInfo info:channels){
			for(String path : info.getXpaths()){
				xpath_set.add(path);
			}
		}
		
		String [] xpath_list = xpath_set.toArray(new String[xpath_set.size()]);
		//Get an object of XPathEngineImpl
		XPathEngineImpl xpath = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();
		
		//Set the queries to be evaluated
		xpath.setXPaths(xpath_list);
		xpath.setHtml(false);
	
		//Evaluate the document for matching queries
		boolean [] match = xpath.evaluate(d);
		for(int i=0;i<match.length;i++){
			XPathInfo xi = db.getXpathInfo(xpath_list[i]);
			
			if(xi == null)
				xi = new XPathInfo();
			
			ArrayList<String> url_match = xi.getMatched_urls();
			if(match[i] == true){
				url_match.add(url);
				xi.setMatched_urls(url_match);
				db.putXPathInfo(xi);
			}
		}
	
	}
	
	public boolean isWaiting() {
		return waiting;
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}
}
