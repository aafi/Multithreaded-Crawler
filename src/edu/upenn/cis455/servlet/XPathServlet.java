package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;

import org.w3c.dom.Document;

import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {
	
	/* TODO: Implement user interface for XPath engine here */
	
	/* You may want to override one or both of the following methods */

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//Get the query parameters from the post request
		String xpath_query = request.getParameter("xpath_query");
		String document = request.getParameter("document");
		
		//Get an object of XPathEngineImpl
		XPathEngineImpl xpath = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();
		Document d = Utilities.buildXmlDom(document);
		
		PrintWriter out = response.getWriter();
		out.write(d.getFirstChild().getNodeName());
		out.flush();
		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		StringBuilder sb = new StringBuilder();
		String start =
				"<html>" +
				"<title> User Page </title>" +
				"<body>";
		String body = "<form action=\"/servlet/xpath\" method = \"post\">"
					  +"XPath Query: <br>"
					  +"<input type=\"text\" name=\"xpath_query\" <br> <br>"
					  +"HTML/XML Document: <br>"
					  +"<input type=\"text\" name=\"document\" <br> <br>"
					  +"<input type=\"submit\" value=\"Submit\">";
		String end =
				"</body>" +
				"</html>";
		sb.append(start);
		sb.append(body);
		sb.append(end);
		
		response.setContentLength(sb.toString().length());
		out.write(sb.toString());
		response.flushBuffer();
	}

}









