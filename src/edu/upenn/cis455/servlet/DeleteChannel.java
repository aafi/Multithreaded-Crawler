package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.ChannelInfo;
import edu.upenn.cis455.storage.DBWrapper;

/**
 * Servlet to delete a channel
 *
 */
@SuppressWarnings("serial")
public class DeleteChannel extends HttpServlet {
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String dir = getServletContext().getInitParameter("BDBstore");
		DBWrapper db = new DBWrapper(dir);
		db.setup();
	
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
			page.append("Oops! You do not have permission to delete that channel!<br>");
		}else{
			db.deleteChannel(channel_name);
			page.append("Channel deleted");
		}
		String contents = Utilities.createHTML("ChannelDisplay", page.toString());
		response.getWriter().write(contents);
		
		db.shutdown();
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
		page.append("Please enter name of channel you wish to delete: <br><br>");
		page.append("<form action=\"/servlet/delete\" method = \"post\">");
		page.append("<input type=\"text\" name=\"channel_name\" <br> <br>");
		page.append("<input type=\"submit\" value=\"Delete\">");
		page.append("</form>"
				  +"<br><br>-----------------------------------------------------------------<br><br>"
				  +"<form action=\"/servlet/session\" method = \"get\">"
				  +"<input type=\"submit\" value=\"Back to home\">");
		
		String contents = Utilities.createHTML("MyChannelList", page.toString());
		response.getWriter().write(contents);
	}
	
}
