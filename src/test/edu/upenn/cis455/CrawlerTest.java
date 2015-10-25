package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

import edu.upenn.cis455.crawler.DomainInfo;
import edu.upenn.cis455.crawler.RobotParser;
import edu.upenn.cis455.crawler.RobotTxtMapping;
import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.crawler.XPathCrawlerFactory;

public class CrawlerTest extends TestCase{
	
	/**
	 * Tests whether the number of files downloaded are less than or equal to 
	 * the number of files specified
	 * @throws InterruptedException
	 */
	@Test
	public void testNumFilesDownloaded() throws InterruptedException {
		XPathCrawlerFactory factory = new XPathCrawlerFactory();
		XPathCrawler crawler = factory.getCrawler();
		
		String dir = "testdb";
		String path = "https://dbappserv.cis.upenn.edu/crawltest/nytimes/";
		String [] args = new String[]{path,dir,"100","10"};
		crawler.main(args);
		
		assertTrue(10 >= crawler.getNumFilesDownloaded());
	}
	
	/**
	 * Checks if the fetched robots txt file gets parsed correctly
	 */
	@Test
	public void testRobotsParsing(){
		String sample_robot = "User-agent: *"
				+"Disallow: /crawltest/marie/"
				+"Crawl-delay: 10";
		
		String test_domain = "www.test.com";
		RobotParser.parse(sample_robot, test_domain);
		DomainInfo info = RobotTxtMapping.get(test_domain);
		info.info.addCrawlDelay("*", 10);
		assertEquals(10,info.info.getCrawlDelay("*"));
		
	}

}
