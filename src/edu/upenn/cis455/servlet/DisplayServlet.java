package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.ChannelInfo;
import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.XPathInfo;

@SuppressWarnings("serial")
public class DisplayServlet extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String dir = getServletContext().getInitParameter("BDBstore");
		DBWrapper db = new DBWrapper(dir);
		db.setup();
		
//		/**debug**/
//		ChannelInfo info = db.getChannelInfo(request.getParameter("channel_name"));
//		ArrayList<String> temp = info.getXpaths();
//		temp.add("somehing");
//		info.setXpaths(temp);
//		db.putChannelInfo(info);
//		
//		XPathInfo inf = new XPathInfo();
//		inf.setXPath("somehing");
//		ArrayList<String> t = new ArrayList<String>();
//		t.add("https://dbappserv.cis.upenn.edu/crawltest/nytimes/Science.xml");
//		t.add("https://dbappserv.cis.upenn.edu/crawltest/nytimes/Africa.xml");
//		inf.setMatched_urls(t);
//		db.putXPathInfo(inf);
//		
//		/**debug**/
	
		StringBuilder page = new StringBuilder();
		
		String channel_name = request.getParameter("channel_name");
		
		String username = (String) request.getSession(false).getAttribute("username");
		ArrayList <ChannelInfo> channels = db.getChannelsByUser(username);
		boolean contains = false;
		for(ChannelInfo chan : channels){
			if(chan.getChannel().equals(channel_name)){
				contains = true;
				break;
			}
		}
		
		if(!contains){
			page.append("Oops! You do not have permission to view that channel!<br>");
		}else{
		
			ArrayList<String> xpaths = db.getChannelInfo(channel_name).getXpaths();
			for(String xpath : xpaths){
				ArrayList <String> url_matches = db.getXpathInfo(xpath).getMatched_urls();
				for(String url : url_matches){
					Date date = db.getDomainInfo(url).getLast_checked();
					String contents = db.getDomainInfo(url).getRaw_content();
					String formatted_date = getFormattedDate(date);
					page.append("Crawled on: "+formatted_date+"<br>");
					page.append("Location: "+url+"<br>");
					page.append("<div style=\"color:#0000FF\">");
					page.append("<textarea style=\"border: 0\">");
					page.append(contents);
					page.append("</div>");
				}
			}
		}
		String contents = Utilities.createHTML("ChannelDisplay", page.toString());
		response.getWriter().write(contents);
		
		db.shutdown();
	}
	
	private String getFormattedDate(Date date) {
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("YYYY-MM-d");
		sb.append(dateFormat1.format(date));
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm:ss");
		sb.append("T");
		sb.append(dateFormat2.format(date));
		return sb.toString();
		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		PrintWriter pw = response.getWriter();
		response.setContentType("text/html");
		
		if(request.getSession(false)== null){
				String body = 
						  "You are not logged in!<br><br>"
						  +"<form action=\"/servlet/login\" method = \"get\">"
						  +"<input type=\"submit\" value=\"Login\">"
						  +"</form>";
			
				String page = Utilities.createHTML("Channel", body);
				response.setContentLength(page.length());
				pw.write(page);
		}
		
		String dir = getServletContext().getInitParameter("BDBstore");
		DBWrapper db = new DBWrapper(dir);
		db.setup();
		
		StringBuilder page = new StringBuilder();
		page.append("Your channel list: <br><br>");
		HttpSession session = request.getSession(false);
		ArrayList<ChannelInfo> channel_list = db.getChannelsByUser((String)session.getAttribute("username"));
		db.shutdown();
		
		for(ChannelInfo name : channel_list){
			page.append(name.getChannel()+"<br>");
		}
		
		page.append("<br><br>");
		page.append("Please enter name of channel you wish to view: <br><br>");
		page.append("<form action=\"/servlet/display\" method = \"post\">");
		page.append("<input type=\"text\" name=\"channel_name\" <br> <br>");
		page.append("<input type=\"submit\" value=\"View\">");
		page.append("</form>"
				  +"<br><br>-----------------------------------------------------------------<br><br>"
				  +"<form action=\"/servlet/session\" method = \"get\">"
				  +"<input type=\"submit\" value=\"Back to home\">");
		
		String contents = Utilities.createHTML("MyChannelList", page.toString());
		response.getWriter().write(contents);
	}
}
