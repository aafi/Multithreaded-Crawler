package edu.upenn.cis455.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import edu.upenn.cis455.crawler.info.URLInfo;

/**
 * Implements the HttpClient
 *
 */
public class HttpClient {
	
	/**
	 * HTTP Client that opens a connection to the URL provided.
	 * Sends HEAD and GET request.
	 * Puts in initial header checks for correct MIME types and size.
	 * @param url
	 * @param size
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void doWork(String url, int size) throws UnknownHostException, IOException{
		//HTTPS url
		if(url.startsWith("https://")){
			
		}else{ //Plain url
			URLInfo info = new URLInfo(url);
			String host = info.getHostName();
			int port = info.getPortNo();
			
			//Open socket connection
			Socket socket = new Socket(host,port);
			OutputStream output = socket.getOutputStream();
			
			//Send Head Request
			String head_request = "HEAD "+info.getFilePath()+" HTTP/1.0 /r/n"
								+"User-Agent: cis455crawler /r/n/r/n";
			output.write(head_request.getBytes());
			
			//Parse input
			InputStream input = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String initial_line = br.readLine();
			
			//Check if response is 200 OK
			if(initial_line ==null || initial_line.length()!=3 || !initial_line.split(" ")[1].trim().equals("200")){
				return;
			}
			
			HashMap <String,String> headers = parseHeaders(br);
			
			Integer len = null ;
			//Check if content-length is greater than size requested by user
			if(headers.containsKey("content-length")){
				len = Integer.parseInt(headers.get("content-length"));
				if(len > size)
					return;
			}
			
			//Check for content-type
			String [] allowed_types = new String[]{"text/html","text/xml","application/xml"};
			if(headers.containsKey("content-type")){
				String type = headers.get("content-type");
				boolean found = false;
				for(String t:allowed_types){
					if(type.equals(t)){
						found = true;
						break;
					}
				}
				
				if(!found){
					if(type.endsWith("+xml"))
						found = true;
				}
				
				//If MIME type returned does not match
				if(!found)
					return;
			}
			
			output.close();
			input.close();
			socket.close();
			
			//HEAD Checks passed. Send GET request
			Socket socket_get = new Socket(host,port);
			OutputStream output_get = socket_get.getOutputStream();
			
			String get_request = "GET "+info.getFilePath()+" HTTP/1.0 /r/n"
					+"User-Agent: cis455crawler /r/n/r/n";
			output_get.write(get_request.getBytes());
			
			InputStream input_get = socket_get.getInputStream();
			BufferedReader br_get = new BufferedReader(new InputStreamReader(input));
			initial_line = br_get.readLine();
			
			//Check if response is 200 OK
			if(initial_line ==null || initial_line.length()!=3 || !initial_line.split(" ")[1].trim().equals("200")){
				return;
			}
			
			String line = br.readLine();
			while(line!=null && !line.equals("")){
				line = br_get.readLine();
			}
			
			//Read the file
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
			
		}
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
			}
			line = br.readLine();
		}
		return headers;
	}
}
