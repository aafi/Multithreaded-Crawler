package edu.upenn.cis455.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import javax.servlet.http.*;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {
	public String body;
	/**
	 * doPost method which opens a connection to the URL given by the user and fetches the document.
	 * It then calls the appropriate methods to perform checks on whether the URL matches or not.
	 * @param HttpServletRequest object
	 * @param HttpServletResponse object 
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//Get the query parameters from the post request
		String[] xpath_query = request.getParameter("xpath_query").split(";");
		String document = request.getParameter("document");
		
		/**Open Connection and Retrieve document**/
		URL url = new URL(document);
		String servername = url.getHost();
		int port = url.getPort();
		if(port==-1)
			port = 80;
		
		Socket socket = new Socket(servername,port);
		String requestLine = "GET "+url.getPath()+" HTTP/1.0 \r\n"
							//  +"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
							  +"\r\n";
		OutputStream output = socket.getOutputStream();
		output.write(requestLine.getBytes());
		
		//Get the response
		InputStream input = socket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));

		String line = br.readLine();
		boolean success = false;
		boolean error = false;
		boolean isHtml = false;
		StringBuilder sb = new StringBuilder();
		if(line == null || line.equals(""))
			error = true;
		else if(!line.split(" ")[1].equals("200")){
			error = true;
			sb.append(line.split(" ")[1]+" "+line.split(" ")[2]);
			sb.append("<br>");
		}
		else{
			line = br.readLine();
			Integer len = null;
			while(line!=null && !line.equals("")){
				if(line.contains(":")){
					String header  = line.split(":")[0].trim().toLowerCase();
					if(header.contains("content-length")){
						len = Integer.parseInt(line.split(":")[1].trim());
					}
					if(header.contains("content-type")){
						if(line.split(":")[1].trim().equals("text/html")){
							isHtml = true;
						}
					}
				}
				line = br.readLine();
			}
			
			String doc = null;
			if(len!=null){
				int total_read = 0;
				int b;
				StringBuilder s = new StringBuilder();
				while(total_read<len && ((b = br.read())!=-1)){
					s.append((char)b);
					total_read++;
				}
				doc = s.toString();
			}
			
			socket.close();
			
			if(doc == null){
				error = true;
				sb.append("No file served <br>");
			}else{
			
				if(isHtml){
					Tidy tidy = new Tidy();
					tidy.setInputEncoding("UTF-8");
				    tidy.setOutputEncoding("UTF-8");
				    tidy.setXmlOut(true);
				    tidy.setSmartIndent(true);
				    ByteArrayInputStream inputStream = new ByteArrayInputStream(doc.getBytes("UTF-8"));
				    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				    tidy.parseDOM(inputStream, outputStream);
				    doc = outputStream.toString("UTF-8");
//				    System.out.println("DATA is: \n"+doc);
				}
				
				Document d = Utilities.buildXmlDom(doc);
			
				//Get an object of XPathEngineImpl
				XPathEngineImpl xpath = (XPathEngineImpl) XPathEngineFactory.getXPathEngine();
			
				//Set the queries to be evaluated
				xpath.setXPaths(xpath_query);
				xpath.setHtml(isHtml);
			
				//Evaluate the document for matching queries
				boolean [] match = xpath.evaluate(d);
			
			
				for(int i=0;i<match.length;i++){
					if(match[i] == true){
						success = true;
						sb.append(xpath_query[i]+" ---- SUCCESS");
						sb.append("<br>");
					}else{
						sb.append(xpath_query[i]+" ---- FAILURE");
						sb.append("<br>");
					}
				}
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
			page = start+"One or more queries matched. <br><br> Matches are: <br>"+sb.toString()+end;
		}else{
			if(error)
				page = start+sb.toString()+end;
			else
				page = start+"FAILED!! <br><br> No matches found! <br>"+end;
		}
		
		
		
		PrintWriter out = response.getWriter();
		response.setStatus(200);
//		this.body = page;
//		System.out.println(body);
		out.write(page);
		out.flush();
		
	}
	
	/**
	 * doGet method generates a form for the user which takes in the xpath queries and the document url.
	 * Generates a post request with the given information.
	 * @param HttpServletRequest object
	 * @param HttpServletResponse object
	 */

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		StringBuilder sb = new StringBuilder();
		String start =
				"<html>" +
				"<title> User Page </title>" +
				"<body>";
		String body = "NAME: ANWESHA DAS <br>SEAS login: anwesha<br><br>"
					  +"<form action=\"/servlet/xpath\" method = \"post\">"
					  +"XPath Query: <br>"
					  +"Please enter multiple queries separated by a semi-colon or ; <br>"
					  +"<input type=\"text\" name=\"xpath_query\" <br> <br>"
					  +"HTML/XML Document URL: <br>"
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









