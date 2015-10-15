package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckXPathValidity {
	private static Pattern test_pattern = Pattern.compile("^.*[\\s*\\[.*\\]]*[\\s*\\/.*]?");
	public LinkedList <DocumentNodes> node_list = new LinkedList <DocumentNodes>();
	
	public boolean checkValidity(String label, String query){
		switch (label){
		case "xpath": 
			
			//Should have axis step
			boolean value = false;
			if(query.startsWith("//"))
				return false;
			
			if(query.startsWith("/")){
				value = checkValidity("axis","/") && checkValidity("step",query.substring(1).trim());
			}
			
			return value;
			
		case "axis":
			if(query.equals("/"))
				return true;
			else
				return false;
		
		case "nodename":
			//Put in checks for valid xml node names
			if(query.startsWith("/"))
				return false;
			
			return true;
			
		case "test":
			Pattern patt_text = Pattern.compile("\\s*(text)\\s*\\(\\s*\\)\\s*=\\s*\".*\"\\s*");
			Matcher text = patt_text.matcher(query);
			if(text.matches())
				return true;
			
			Pattern patt_contains = Pattern.compile("\\s*contains\\s*\\(\\s*text\\(\\s*\\),\\s*\".*\"\\)\\s*");
			Matcher contains = patt_contains.matcher(query);
			if(contains.matches())
				return true;
			
			Pattern patt_att = Pattern.compile("\\s*@[a-zA-Z0-9]*\\s*=\\s*\".*\"\\s*");
			Matcher att = patt_att.matcher(query);
			if(att.matches())
				return true;
			
			Matcher matcher_test = test_pattern.matcher(query);
			if(matcher_test.matches())
				return checkTestValidity("step",query);
			
			return false;
			
			
		case "step":
			Matcher matcher = test_pattern.matcher(query);
			if(!matcher.matches())
				return false;
			
			int i=0;
			StringBuilder nodename = new StringBuilder();
			String axis_step = null;
			ArrayList<String> test = new ArrayList<String>();
			boolean found_nodename = false;
			
			while(i<query.length()){
				while(!found_nodename && query.charAt(i)!='[' && query.charAt(i)!='/'){
					nodename.append(query.charAt(i));
					i++;
					
					if(i>=query.length())
						break;
				}
				
				//We found the node name. Do not consider any other letter
				found_nodename= true;
				
				if(i>=query.length())
					break;
				
				//Get the test part
				if(query.charAt(i) == '['){
					int open = 0;
					StringBuilder sb = new StringBuilder();
					while(true){
						if(query.charAt(i) == '['){
							if(open!=0)
								sb.append(query.charAt(i));
							open++;
							i++;
						}else if(query.charAt(i) == ']'){
							open--;
							if(open!=0)
								sb.append(query.charAt(i));
							i++;
						}else{
							sb.append(query.charAt(i));
							i++;
						}
						
						if(open == 0)
							break;
					}
					test.add(sb.toString().trim());
				}
				
				//Get the axis_step part
				else if(query.charAt(i) == '/'){
					axis_step = query.substring(i);
					//Our grammar does not accept '//'
					if(query.charAt(i+1)=='/')
						return false;
					break;
				}
				//FOR ANY OTHER CHARACTER
				else{
					return false;
				}
					
			} //End of query parsing
			
			
			//Build our linkedlist with xpath nodes
			DocumentNodes doc_node = new DocumentNodes();
			doc_node.setName(nodename.toString().trim());
			doc_node.setFilter(test);
			node_list.add(doc_node);
			
			if(test.size()!=0){
				for(String test_part : test){
					if(!checkValidity("test",test_part))
						return false;
				}
			}
			
//			System.out.println("axis step for nodename "+nodename.toString().trim()+" is "+axis_step);
			if(axis_step!=null){
				if(!checkValidity("xpath",axis_step.trim()))
					return false;
			}
			
			if(checkValidity("nodename",nodename.toString().trim())){
				return true;
			}
			
			return false;
			
		} // end of switch
			
		return false;
	}
	
	
	private boolean checkTestValidity(String label, String query){
		switch (label){
		case "xpath": 
			
			//Should have axis step
			boolean value = false;
			if(query.startsWith("//"))
				return false;
			
			if(query.startsWith("/")){
				value = checkTestValidity("axis","/") && checkTestValidity("step",query.substring(1).trim());
			}
			
			return value;
			
		case "axis":
			if(query.equals("/"))
				return true;
			else
				return false;
		
		case "nodename":
			//Put in checks for valid xml node names
			if(query.startsWith("/"))
				return false;
			
			return true;
			
		case "test":
			Pattern patt_text = Pattern.compile("\\s*(text)\\s*\\(\\s*\\)\\s*=\\s*\".*\"\\s*");
			Matcher text = patt_text.matcher(query);
			if(text.matches())
				return true;
			
			Pattern patt_contains = Pattern.compile("\\s*contains\\s*\\(\\s*text\\(\\s*\\),\\s*\".*\"\\)\\s*");
			Matcher contains = patt_contains.matcher(query);
			if(contains.matches())
				return true;
			
			Pattern patt_att = Pattern.compile("\\s*@[a-zA-Z0-9]*\\s*=\\s*\".*\"\\s*");
			Matcher att = patt_att.matcher(query);
			if(att.matches())
				return true;
			
			Matcher matcher_test = test_pattern.matcher(query);
			if(matcher_test.matches())
				return checkTestValidity("step",query);
			
			return false;
			
			
		case "step":
			Matcher matcher = test_pattern.matcher(query);
			if(!matcher.matches())
				return false;
			
			int i=0;
			StringBuilder nodename = new StringBuilder();
			String axis_step = null;
			ArrayList<String> test = new ArrayList<String>();
			
			while(i<query.length()){
				
				while(query.charAt(i)!='[' && query.charAt(i)!='/'){
					nodename.append(query.charAt(i));
					i++;
					
					if(i>=query.length())
						break;
				}
				
				if(i>=query.length())
					break;
				
				//Get the test part
				if(query.charAt(i) == '['){
					int open = 0;
					StringBuilder sb = new StringBuilder();
					while(true){
						if(query.charAt(i) == '['){
							if(open!=0)
								sb.append(query.charAt(i));
							open++;
							i++;
						}else if(query.charAt(i) == ']'){
							open--;
							if(open!=0)
								sb.append(query.charAt(i));
							i++;
						}else{
							sb.append(query.charAt(i));
							i++;
						}
						
						if(open == 0)
							break;
					}
					test.add(sb.toString().trim());
				}
				
				//Get the axis_step part
				else if(query.charAt(i) == '/'){
					axis_step = query.substring(i);
					
					//Our grammar does not accept '//'
					if(query.charAt(i+1)=='/')
						return false;
				}
				
				i++;
				
			} //End of query parsing
			
			if(test.size()!=0){
				for(String test_part : test){
					if(!checkTestValidity("test",test_part))
						return false;
				}
			}
			
			if(axis_step!=null){
				if(!checkTestValidity("xpath",axis_step.trim()))
					return false;
			}
			
			if(checkTestValidity("nodename",nodename.toString().trim())){
				return true;
			}
			
			return false;
			
		} // end of switch
			
		return false;
	}
	
//	public static void main(String [] args){
//		boolean something = checkValidity("xpath","/this/that[something/else][ok]/yes[dhrubeel]");
////		Pattern patt_text = Pattern.compile("^.*[\\s*\\[(.*\\)]]*[\\s*\\/.*]?");
////		Matcher text = patt_text.matcher("/foo/bar[@attname= something]/xyz");
////		text.matches();
////		System.out.print(text.group(1));
////		System.out.println(something);
//		
//		System.out.println(node_list.size());
//		for(int i=0;i<node_list.size();i++){
//			System.out.println(node_list.get(i).getName());
//			if(!node_list.get(i).getFilter().isEmpty()){
//				System.out.println(node_list.get(i).getFilter().size());
//				System.out.println(node_list.get(i).getFilter().get(0));
//				System.out.println(node_list.get(i).getFilter().get(1));
//			}
//			System.out.println();
//		}
//	}

}
