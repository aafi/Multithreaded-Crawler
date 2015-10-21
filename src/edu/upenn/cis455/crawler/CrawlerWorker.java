package edu.upenn.cis455.crawler;

import edu.upenn.cis455.crawler.info.URLInfo;

public class CrawlerWorker implements Runnable{
	
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
			
			if(DomainInfoList.contains(domain)){
				if(!DomainInfoList.isPastDelay(domain)){
					//Put URL back in the queue if crawl delay is still active
					synchronized(UrlQueue.queue){
						UrlQueue.queue.add(url);
					}
					continue;
				}else{ //TODO
					//Check robots.txt if the requested URL is disallowed
				}
			}else{ //This domain was not hit before
				
				//Put URL back in the queue as we need to get robots.txt
				synchronized(UrlQueue.queue){
					UrlQueue.queue.add(url);
				}
				url = protocol+domain+"/robots.txt";
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
			
			
			
		} //End of while
	} //End of run
}
