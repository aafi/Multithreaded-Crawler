package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import edu.upenn.cis455.servlet.Utilities;
import edu.upenn.cis455.servlet.XPathServlet;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

public class XPathEngineTest extends TestCase{
	
	XPathEngineImpl xpath;
	Document doc;
	
	@Before
	public void setUp(){
		xpath = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();
		String xml_file = "<node1><node2 param1=\"value1\"><node3>sometext3</node3><node4 param4=\"value4\"></node4>"+
						"</node2></node1>";
		doc = Utilities.buildXmlDom(xml_file);
	}
	
	/**
	 * Tests if the setXPath method takes in and sets the passed XPath queries
	 */
	
	@Test
	public void testSetXPath() {
		String[] xpaths = new String []{"query1","query2"};
		xpath.setXPaths(xpaths);
		assertEquals(xpaths.length,xpath.getQueries().length);
		assertEquals(xpaths[0],xpath.getQueries()[0]);
	}
	
	/**
	 * Tests whether the isValid method returns the correct result for syntactically correct and incorrect XPath queries.
	 * XPath queries not conforming to our grammar should return false.
	 */
	@Test
	public void testValidXPath() {
		String[] xpaths = new String []{"/ node1/ node2/ node3[ contains(text(  ),\"text3\")]","/node1//node4[@param4=\"value4\"]"};
		xpath.setXPaths(xpaths);
		xpath.evaluate(doc);
		assertEquals(true,xpath.isValid(0));
		assertEquals(false,xpath.isValid(1));
	}
	
	/**
	 * Tests whether syntactically correct and valid XPaths matches the given document.
	 * If a query does not match, it should be marked as false.
	 */
	@Test
	public void testMatchingXPath() {
		String[] xpaths = new String []{"/ node1/ node2[ node3[ contains(text(  ),\"text3\")]]/node4",
				"/node1/node2/ node3/node4[@param3=\"value4\"]"};
		xpath.setXPaths(xpaths);
		
		boolean[] result = xpath.evaluate(doc);
		assertEquals(true,result[0]);
		assertEquals(false,result[1]);
	}
	
	/**
	 * Tests whether a query is invalidated given "invalid" node names - for e.g., those that start with invalid characters like '.' etc,
	 * or those that contain an axis.
	 */
	@Test
	public void testInvalidNodename(){
		String[] xpaths = new String []{"/ node1/ node2::node3[ contains(text(  ),\"text3\")]]/node4",
		"/node1/-node2/. node3/node4[@param3=\"value4\"]"};
		xpath.setXPaths(xpaths);
		xpath.evaluate(doc);
		assertEquals(false,xpath.isValid(0));
		assertEquals(false,xpath.isValid(1));
		
	}
	
	/**
	 * Tests whether the query escapes quotations inside a string and matches the query correctly
	 */
	@Test
	public void testEscapedQuotes(){
		String xml_file = "<node1><node2 param1=\"value1\">some\"text\"<node3></node3><node4 param4=\"value4\"></node4>"+
				"</node2></node1>";
		doc = Utilities.buildXmlDom(xml_file);
		String[] xpaths = new String []{"/ node1/ node2[@param1=\"value1\"][text()=\"some\\\"text\\\"\"]/node3[ contains(text(  ),\"text3\")]"};
		xpath.setXPaths(xpaths);
		boolean [] result = xpath.evaluate(doc);
		assertTrue(result[0]);
	}
	
//	/**
//	 * Tests the Servlet
//	 * @throws IOException 
//	 */
//	public void testServlet() throws IOException{
//		HttpServletRequest request = mock(HttpServletRequest.class);       
//        HttpServletResponse response = mock(HttpServletResponse.class);  
//        when(request.getParameter("xpath_query")).thenReturn("/random");
//        when(request.getParameter("document")).thenReturn("http://www.thornsoft.com/pad/clipmate/pad_file.xml");
//        XPathServlet servlet = new XPathServlet();
//        
//		servlet.doPost(request, response);
//        String body = "<html><title> User Page </title><body>FAILED!! <br><br> No matches found! <br></body></html>";
//	}
}
