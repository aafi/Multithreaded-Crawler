package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

public class XPathEngineTest {
	
	XPathEngineImpl xpath;
	
	@Before
	public void setUp(){
		xpath = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();
	}
	
	@Test
	public void testSetXPath() {
		String[] xpaths = new String []{"query1","query2"};
		xpath.setXPaths(xpaths);
		assertEquals(xpaths.length,xpath.getQueries().length);
		assertEquals(xpaths[0],xpath.getQueries()[0]);
	}

}
