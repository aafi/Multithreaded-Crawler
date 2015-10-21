package edu.upenn.cis455.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import edu.upenn.cis455.crawler.info.URLInfo;

/**
 * Implements the HttpClient
 *
 */
public class HttpClient {
	
	private boolean found_in_db = false;
	private Date last_hit = null;
	private String document;
	private String mime_type;
	
	public HttpClient(){
		
	}
	
	public HttpClient(boolean found_in_db, Date last_hit){
		found_in_db = found_in_db;
		last_hit = last_hit;
	}
	
	/**
	 * HTTP Client that opens a connection to the URL provided.
	 * Sends HEAD and GET request.
	 * Puts in initial header checks for correct MIME types and size.
	 * @param url
	 * @param size
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public String doWork(String url, int size) throws UnknownHostException, IOException{
		//HTTPS url
		if(url.startsWith("https://")){
			
			return "";
		}else{ //Plain url
			URLInfo info = new URLInfo(url);
			String host = info.getHostName();
			int port = info.getPortNo();
			
			//Open socket connection
			Socket socket = new Socket(host,port);
			OutputStream output = socket.getOutputStream();
			
			//Send Head Request
			String head_request;
			if(!found_in_db){
				head_request = "HEAD "+info.getFilePath()+" HTTP/1.0 /r/n"
								+"User-Agent: cis455crawler /r/n/r/n";
			}else{
				
				SimpleDateFormat dateFormatGMT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
				dateFormatGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
				
				String date = dateFormatGMT.format(last_hit);
				head_request = "HEAD "+info.getFilePath()+" HTTP/1.0 /r/n"
						+"User-Agent: cis455crawler /r/n"
						+"If-Modified-Since: "+date+"/r/n/r/n";
			}
			output.write(head_request.getBytes());
			
			//Parse input
			InputStream input = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String initial_line = br.readLine();
			
			//Check if response is well formed
			if(initial_line ==null || initial_line.length()!=3){
				socket.close();
				return "Error";
			}
			
			HashMap <String,String> headers = parseHeaders(br);
			
			//Check for return status
			String status_num = initial_line.split(" ")[1].trim();
			if(status_num.equals("304")){
				//File has not been modified since last hit
				socket.close();
				return "304";
			}else if(status_num.equals("301")){
				String location = headers.get("location");
				synchronized(UrlQueue.queue){
					UrlQueue.queue.add(location);
				}
				socket.close();
				return "301";
			}else if(!status_num.equals("200")){
				//Do not allow any other status codes
				socket.close();
				return "Error";
			}
				
			
			Integer len = null ;
			//Check if content-length is greater than size requested by user
			if(headers.containsKey("content-length")){
				len = Integer.parseInt(headers.get("content-length"));
				if(len > size){
					socket.close();
					return "Error";
				}
			}
			
			//Check for content-type
			String [] allowed_types = new String[]{"text/html","text/xml","application/xml"};
			if(headers.containsKey("content-type")){
				String type = headers.get("content-type");
				boolean found = false;
				for(String t:allowed_types){
					if(type.equals(t)){
						mime_type = type;
						found = true;
						break;
					}
				}
				
				if(!found){
					if(type.endsWith("+xml"))
						mime_type = type;
						found = true;
				}
				
				//If MIME type returned does not match
				if(!found){
					socket.close();
					return "Error";
				}
			}
			
			output.close();
			input.close();
			socket.close();
			
			//HEAD Checks passed. Send GET request
			boolean success = sendGet(host,port,info.getFilePath(),len);
			if(success)
				return "Success";
			else
				return "Error";
		}	
	}
	
	private boolean sendGet(String host, int port, String filepath, Integer len) throws IOException{
		Socket socket_get = new Socket(host,port);
		OutputStream output_get = socket_get.getOutputStream();
		
		String get_request = "GET "+filepath+" HTTP/1.0 /r/n"
				+"User-Agent: cis455crawler /r/n/r/n";
		output_get.write(get_request.getBytes());
		
		InputStream input_get = socket_get.getInputStream();
		BufferedReader br_get = new BufferedReader(new InputStreamReader(input_get));
		String initial_line = br_get.readLine();
		
		String line = br_get.readLine();
		while(line!=null && !line.equals("")){
			line = br_get.readLine();
		}
		
		//Read the file
		String doc = null;
		if(len!=null){
			int total_read = 0;
			int b;
			StringBuilder s = new StringBuilder();
			while(total_read<len && ((b = br_get.read())!=-1)){
				s.append((char)b);
				total_read++;
			}
			doc = s.toString();
		}
		
		socket_get.close();
		if(doc == null)
			return false;
		else
			return true;
		
	}
	
	/** 
	 * Parse the response to get headers
	 * @param BufferedReader having input stream
	 * @return hash map of headers
	 * @throws IOException 
	 */
	private HashMap<String, String> parseHeaders(BufferedReader br) throws IOException {
		HashMap<String,String> headers = new HashMap<String,String>();
		String line = br.readLine();
		
		while(line!=null && !line.equals("")){
			if(line.contains(":")){
				String header  = line.split(":")[0].trim().toLowerCase();
				if(header.contains("content-length")){
					String len = line.split(":")[1].trim();
					headers.put("content-length", len);
				}
				if(header.contains("content-type")){
					String type = line.split(":")[1].trim();
					headers.put("content-type", type);
				}
				if(header.contains("location")){
					String location = line.split(":")[1].trim();
					headers.put("location", location);
				}
			}
			line = br.readLine();
		}
		return headers;
	}
	
	/**
	 * Getter for fetched document body
	 * @return
	 */
	public String getDocument() {
		return document;
	}
	
	/**
	 * Getter for mime type of document
	 * @return
	 */
	public String getType(){
		return mime_type;
	}
}
