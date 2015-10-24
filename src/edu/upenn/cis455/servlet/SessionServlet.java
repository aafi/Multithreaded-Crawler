package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.*;

import edu.upenn.cis455.storage.ChannelInfo;
import edu.upenn.cis455.storage.DBWrapper;

/**
 * Servlet showing the logged in page for a user
 * @author cis455
 *
 */

@SuppressWarnings("serial")
public class SessionServlet extends HttpServlet{
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		
		String dir = getServletContext().getInitParameter("BDBstore");
		DBWrapper db = new DBWrapper(dir);
		db.setup();
		
		String header;
		
		if(db.getChannelInfo(request.getParameter("channel")) == null){
			ChannelInfo info = new ChannelInfo();
		
			info.setChannel(request.getParameter("channel"));
			info.setUsername((String)session.getAttribute("username"));
			String[] paths = request.getParameter("xpath").split(";");
		
			ArrayList<String> xpath_list = new ArrayList<String>();
			for(String p :paths){
				xpath_list.add(p);
			}
		
			info.setXpaths(xpath_list);
		
			db.putChannelInfo(info);
			header = "Successfully added new channel "+request.getParameter("channel")+"<br><br>";
		}else{
			header = "Channel with name "+request.getParameter("channel")+" already exists. <br><br>"
					+"Please choose another one. <br><br>";
		}
		
		String body = header
				  +"<form action=\"/servlet/session\" method = \"get\">"
				  +"<input type=\"submit\" value=\"Back to Home\">";
	
		String page = Utilities.createHTML("Channel", body);
		response.setContentType("text/html");
		response.setContentLength(page.length());
		response.getWriter().write(page);
		
		db.shutdown();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter pw = response.getWriter();
		response.setContentType("text/html");
		String username;
		
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
		
		username = (String) request.getSession(false).getAttribute("username");
		
		String header = "Welcome "+username+"<br><br><br><br>";
		String body = header
				  +"Display all my channels!"
				  +"<form action=\"/servlet/display\" method = \"get\">"
				  +"<input type=\"submit\" value=\"Display Channels\">"
				  +"</form>"
				  +"<br><br>-----------------------------------------------------------------<br><br>"
				  +"Delete a channel<br><br>"
				  +"<form action=\"/servlet/delete\" method = \"get\">"
				  +"<input type=\"submit\" value=\"Delete Channel\">"
				  +"</form>"
				  +"<br><br>-----------------------------------------------------------------<br><br>"
				  +"Add a new channel<br><br>"
				  +"<form action=\"/servlet/session\" method = \"post\">"
				  +"Channel Name: <br>"
				  +"<input type=\"text\" name=\"channel\" <br> <br>"
				  +"XPaths: <br>"
				  +"Please enter multiple xpaths separated by a semi-colon or ; <br>"
				  +"<input type=\"text\" name=\"xpath\" <br> <br>"
				  +"<input type=\"submit\" value=\"Create\">"
				  +"</form>"
				  +"<br><br>-----------------------------------------------------------------<br><br>"
				  +"<form action=\"/servlet/login\" method = \"get\">"
				  +"<input type=\"submit\" name=\"logout\" value=\"Logout\">";
	
		String page = Utilities.createHTML("Channel", body);
		response.setContentLength(page.length());
		pw.write(page);
		
	}
}
