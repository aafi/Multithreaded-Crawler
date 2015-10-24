package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DBWrapper;
import edu.upenn.cis455.storage.LoginInfo;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet{
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String dir = getServletContext().getInitParameter("BDBstore");
		DBWrapper db = new DBWrapper(dir);
		db.setup();
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if(db.getLoginInfo(username)==null){
			LoginInfo info = new LoginInfo();
			info.setUsername(username);
			info.setPassword(password);
			db.putLoginInfo(info);
			db.shutdown();
			//Route to success page
			HttpSession session = request.getSession(true);
			session.setAttribute("username", username);
			new SessionServlet().doGet(request, response);
		}else{
			db.shutdown();
			invalid(request,response);
			
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		String header = "Registration<br>Please provide a user name and password<br><br>";
		
		String body = header
				  +"<form action=\"/servlet/register\" method = \"post\">"
				  +"Username: <br>"
				  +"<input type=\"text\" name=\"username\" <br> <br>"
				  +"Password: <br>"
				  +"<input type=\"password\" name=\"password\" <br> <br>"
				  +"<input type=\"submit\" value=\"Submit\">";
		
		String page = Utilities.createHTML("Register", body);
		response.setContentLength(page.length());
		out.write(page);
	}
	
	private void invalid(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		String header = "Username already exists! Please choose another one<br><br>";
		
		String body = header
				  +"<form action=\"/servlet/register\" method = \"post\">"
				  +"Username: <br>"
				  +"<input type=\"text\" name=\"username\" <br> <br>"
				  +"Password: <br>"
				  +"<input type=\"password\" name=\"password\" <br> <br>"
				  +"<input type=\"submit\" value=\"Submit\">";
		
		String page = Utilities.createHTML("Register", body);
		response.setContentLength(page.length());
		out.write(page);
	}
}
