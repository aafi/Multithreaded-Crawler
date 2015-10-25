package edu.upenn.cis455.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.LoginInfo;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet{
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String dir = getServletContext().getInitParameter("BDBstore");
		File file = new File(dir);
		if(!file.exists())
			file.mkdir();
		DBWrapper db = new DBWrapper(dir);
		db.setup();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		LoginInfo log;
		if((log = db.getLoginInfo(username))!=null){
			if(log.getPassword().equals(password)){
				db.shutdown();
				//Route to login page
				HttpSession session = request.getSession(true);
				session.setAttribute("username", username);
				new SessionServlet().doGet(request, response);
			}else{
				db.shutdown();
				invalid(request,response);
			}
		}else{
			db.shutdown();
			invalid(request,response);
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		if(request.getSession(false)!=null){
			request.getSession().invalidate();
		}

		String header = "Welcome!<br>Please login<br><br>";
		
		String body = header
				  +"<form action=\"/servlet/login\" method = \"post\">"
				  +"Username: <br>"
				  +"<input type=\"text\" name=\"username\" <br> <br>"
				  +"Password: <br>"
				  +"<input type=\"password\" name=\"password\" <br> <br>"
				  +"<input type=\"submit\" value=\"Submit\">"
				  +"</form>"
				  +"<br><br>-----------------------------------------------------------------<br><br>"
				  +"<br><br>Do not have an account? Register!<br><br>"
				  +"<form action=\"/servlet/register\" method = \"get\">"
				  +"<input type=\"submit\" value=\"Register\">"
				  +"</form>"
				  +"<br><br>-----------------------------------------------------------------<br><br>"
				  +"Show me all the channels available!<br>"
				  +"<form action=\"/servlet/channels\" method = \"get\">"
				  +"<input type=\"submit\" value=\"Show!\">";
		
		String page = Utilities.createHTML("Login", body);
		response.setContentLength(page.length());
		out.write(page);
	}
	
	private void invalid(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		String header="Username/Password Invalid <br>Try again!<br>";
		
		String body = header
				  +"<form action=\"/servlet/login\" method = \"post\">"
				  +"Username: <br>"
				  +"<input type=\"text\" name=\"username\" <br> <br>"
				  +"Password: <br>"
				  +"<input type=\"password\" name=\"password\" <br> <br>"
				  +"<input type=\"submit\" value=\"Submit\">"
				  +"</form>"
				  +"<br><br>-----------------------------------------------------------------<br><br>"
				  +"<br><br>Do not have an account? Register!<br><br>"
				  +"<form action=\"/servlet/register\" method = \"get\">"
				  +"<input type=\"submit\" value=\"Register\">";
		
		String page = Utilities.createHTML("Login", body);
		response.setContentLength(page.length());
		out.write(page);
	}
}
