package edu.upenn.cis455.crawler;

public class XPathCrawler {
	public static void main(String [] args){
		if(args.length < 3 || args.length > 4){
			System.out.println("Invalid number of arguments");
			System.exit(1);
		}
		
		/** GET COMMAND LINE ARGUMENTS **/
		String seed_url = args[0];
		String dir = args[1];
		int size, num_of_files;
		
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
		 * Start threads
		 */
		//TODO
		
	}
	
	
}
