package edu.upenn.cis455.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.upenn.cis455.storage.ChannelInfo;
import edu.upenn.cis455.storage.DBWrapper;

@SuppressWarnings("serial")
public class ChannelServlet extends HttpServlet {
	
		@Override
		public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		}
		
		@Override
		public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
			String dir = getServletContext().getInitParameter("BDBstore");
			File file = new File(dir);
			if(!file.exists())
				file.mkdir();
			DBWrapper db = new DBWrapper(dir);
			
			db.setup();
			
			StringBuilder page = new StringBuilder();
			page.append("All the channels available are: <br><br>");
			ArrayList<ChannelInfo> channel_list = db.getAllChannels();
			db.shutdown();
			
			for(ChannelInfo name : channel_list){
				page.append(name.getChannel()+"<br>");
			}
			
			page.append("<br><br>");
			page.append("<form action=\"/servlet/login\" method = \"get\">");
			page.append("<input type=\"submit\" value=\"Back to Login\">");
			
			String contents = Utilities.createHTML("Channel List", page.toString());
			response.getWriter().write(contents);
			
			
		}
}
