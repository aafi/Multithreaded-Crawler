package edu.upenn.cis455.crawler;

import java.io.File;
import java.util.ArrayList;

import edu.upenn.cis455.storage.DBWrapper;

public class XPathCrawler {
	
	public static String dir;
	public static int size;
	private static final int MAX_THREADS = 10;
	private static boolean shutdown = true;
	public static int num_downloaded = 0;
	public static int num_of_files = -1;
	
	public static void main(String [] args) throws InterruptedException{
		if(args.length < 3 || args.length > 4){
			System.out.println("Invalid number of arguments");
			System.exit(1);
		}
		
		/** GET COMMAND LINE ARGUMENTS **/
		String seed_url = args[0];
		dir = args[1];
		
		//If directory does not exist, create it
		File directory = new File(dir);
		if(!directory.exists()){
			directory.mkdir();
		}
		
		try{
			size = Integer.parseInt(args[2]);
		}catch(NumberFormatException e){
			System.out.println("Invalid size of document");
			System.exit(1);
		}
		
		if(args.length == 4){
			try{
				num_of_files = Integer.parseInt(args[3]);
			}catch(NumberFormatException e){
				System.out.println("Invalid number of files");
				System.exit(1);
			}
		}
		
		/**
		 * Add seed url to queue
		 */
		UrlQueue.queue.add(seed_url);
		
		/**
		 * Setup db wrapper
		 */
		DBWrapper db = new DBWrapper(dir);
		db.setup();
		
		/**
		 * Start threads
		 */
//		CrawlerWorker worker = new CrawlerWorker(db);
//		worker.run();
		ArrayList<ThreadpoolThread> threadPool = new ArrayList<ThreadpoolThread>();
		  
		for(int i=0;i<MAX_THREADS;i++){
			 CrawlerWorker worker = new CrawlerWorker(db);
			 ThreadpoolThread thread = new ThreadpoolThread(worker);
			 threadPool.add(thread);
			  
		}
		
		while(true){
			Thread.sleep(10000);
			
			synchronized(UrlQueue.queue){
				if(!UrlQueue.queue.isEmpty()){
					shutdown = false;
				}
				
				for(ThreadpoolThread t : threadPool){
					if(!t.getWorker().isWaiting()){
						shutdown = false;
					}
				}
			}
			
			if(num_of_files!=-1){
				if(num_of_files <= getNumFilesDownloaded()){
					shutdown = true;
				}
			}
			
			if(shutdown){
				for(ThreadpoolThread t : threadPool){
					t.getWorker().setShutdown(true);
				}
				System.out.println("Shutdown");
				break;
			}
		}
		
	}
	
	public static synchronized int getNumFilesDownloaded(){
		return num_downloaded;
	}
	
	public static synchronized void incrementNumFiles(){
		num_downloaded++;
	}
}
