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
		String[] xpath_query = request.getParameter("xpath_query").split(";");
		String document = request.getParameter("document");
		
		Document d = Utilities.buildXmlDom(document);
		
		//Get an object of XPathEngineImpl
		XPathEngineImpl xpath = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();
		
		//Set the queries to be evaluated
		xpath.setXPaths(xpath_query);
		
		//Evaluate the document for matching queries
		boolean [] match = xpath.evaluate(d);
		
		boolean success = false;
		StringBuilder sb = new StringBuilder();
		
		for(int i=0;i<match.length;i++){
			if(match[i] == true){
				success = true;
				sb.append(xpath_query[i]);
				sb.append("<br>");
			}
		}
		
		String start =
				"<html>" +
				"<title> User Page </title>" +
				"<body>";
		
		String end =
				"</body>" +
				"</html>";
		
		String page;
		if(success){
			page = start+"SUCCESS!! <br><br> Matches are: <br>"+sb.toString()+end;
		}else{
			page = start+"FAILED!! <br><br> No matches found! <br>"+end;
		}
		
		
		PrintWriter out = response.getWriter();
//		out.write("Number of xpath queries: "+xpath_query.length);
//		out.write(xpath_query[0]);
//		out.write(d.getFirstChild().getNodeName());
		response.setStatus(200);
		out.write(page);
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
		String body = "NAME: ANWESHA DAS <br>Pennkey: anwesha<br><br>"
					  +"<form action=\"/servlet/xpath\" method = \"post\">"
					  +"XPath Query: <br>"
					  +"Please enter multiple queries separated by a semi-colon or ; <br>"
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









