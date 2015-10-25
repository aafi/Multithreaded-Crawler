package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis455.servlet.Utilities;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.DomainEntity;
import edu.upenn.cis455.storage.LoginInfo;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

public class StorageTest extends TestCase {
	
	DBWrapper db = null;
	@Before
	public void setUp(){
		String test_dir = "testdb";
		File file = new File(test_dir);
		if(!file.exists())
			file.mkdir();
		
		db = new DBWrapper(test_dir);
		db.setup();
	}
	
	/**
	 * Tests whether the url information is getting stored properly
	 */
	@Test
	public void testDomainStore() {
		DomainEntity dom = new DomainEntity();
		dom.setUrl("testurl");
		dom.setRaw_content("testcontent");
		db.putDomainInfo(dom);
		assertEquals("testcontent",db.getDomainInfo("testurl").getRaw_content());
		db.shutdown();
	}
	
	/**
	 * Tests whether the user information gets stored
	 */
	@Test
	public void testUserStore() {
		LoginInfo log = new LoginInfo();
		log.setUsername("testname");
		log.setPassword("testpassword");
		db.putLoginInfo(log);
		assertNotNull(db.getLoginInfo("testname"));
		db.shutdown();
	}

}
