package edu.upenn.cis455.crawler;

/**
 * The Class ThreadpoolThread.
 */
public class ThreadpoolThread {
	
	/** The thread */
	private Thread thread;
	
	private CrawlerWorker worker;
	
	/**
	 * Instantiates a new threadpool thread.
	 *
	 * @param worker the worker object
	 */
	public ThreadpoolThread(CrawlerWorker worker){
		this.thread = new Thread(worker);
		this.worker = worker;
		this.thread.start();
	}

	/**
	 * Gets the thread.
	 *
	 * @return the thread
	 */
	public Thread getThread() {
		return thread;
	}

	/**
	 * Gets the worker.
	 *
	 * @return the worker
	 */
	public CrawlerWorker getWorker() {
		return worker;
	}
	
	
}