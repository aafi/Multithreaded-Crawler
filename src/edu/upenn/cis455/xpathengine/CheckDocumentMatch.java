package edu.upenn.cis455.xpathengine;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.upenn.cis455.servlet.Utilities;


public class CheckDocumentMatch {
	private boolean isHtml = false;
	
	/**
	 * Constructor 
	 * @param value of isHtml flag
	 */
	public CheckDocumentMatch(boolean value){
		isHtml = value;
	}
	
	/**
	 * Recursively checks if the given xpath query matches the document
	 * @param node_list containing the given xpath query
	 * @param current idx
	 * @param current node to be checked against
	 * @return whether the xpath matches the document
	 */
	public boolean checkMatch(LinkedList <DocumentNodes> node_list, int idx, Node node){
		if(node == null){
			return false;
		}
		
		if(idx >= node_list.size()){
			return true;
		}
		DocumentNodes entry = node_list.get(idx);
		
		String entryName = entry.getName();
		String nodename = node.getNodeName();
		
		if(isHtml){
			entryName = entryName.toLowerCase();
			nodename = nodename.toLowerCase();
		}
		
		if(!entryName.equals(nodename)){
			return false;
		}
		
		//No attribute filters
		if(entry.getFilter().size() == 0){
			NodeList children = node.getChildNodes();
			int number_of_children = node.getChildNodes().getLength();
			if(number_of_children == 0){
				return true;
			}else{
				for(int i = 0; i<number_of_children;i++){
					if(checkMatch(node_list,idx+1,children.item(i)))
						return true;
				}
			}
		}else{
			boolean not_correct = true;
			for(String filter : entry.getFilter()){
				if(!checkFilter(filter,node)){
					not_correct = false;
				}
			}
			
			return not_correct;
		}
		
		return false;
	}
	
	/**
	 * Checks the test conditions for each node
	 * @param filter condition to be checked
	 * @param node to which the filter belongs
	 * @return whether the filter applies
	 */
	
	private boolean checkFilter(String filter,Node node){
		
		//Atomic Tests
		
		String nodeTextContent = node.getTextContent();
		if(isHtml)
			nodeTextContent = nodeTextContent.toLowerCase();
		
		Pattern patt_text = Pattern.compile("\\s*text\\s*\\(\\s*\\)\\s*=\\s*\\\"(.*)\\\"\\s*");
		Matcher text = patt_text.matcher(filter);
		if(text.matches()){
			String content = text.group(1);
			if(content.contains("\\\"")){
				content = content.replaceAll("\\\\\\\"", "\"");
			}
			
			if(isHtml)
				content = content.toLowerCase();
			if(nodeTextContent.equals(content))
				return true;
			else
				return false;
		}
			
		
		Pattern patt_contains = Pattern.compile("\\s*contains\\s*\\(\\s*text\\s*\\(\\s*\\)\\s*,\\s*\"(.*)\"\\s*\\)\\s*");
		Matcher contains = patt_contains.matcher(filter);
		if(contains.matches()){
			String content = contains.group(1);
			if(content.contains("\\\"")){
				content = content.replaceAll("\\\\\\\"", "\"");
			}
			
			if(isHtml)
				content = content.toLowerCase();
			
			if(nodeTextContent.contains(content))
				return true;
			else
				return false;
		}
			
		
		Pattern patt_att = Pattern.compile("\\s*@\\s*([a-zA-Z0-9]*)\\s*=\\s*\"(.*)\"\\s*");
		Matcher att = patt_att.matcher(filter);
		if(att.matches()){
			String content = att.group(2);
			if(content.contains("\\\"")){
				content = content.replaceAll("\\\\\\\"", "\"");
			}
			
			String attribute = att.group(1);
			if(isHtml)
				attribute = attribute.toLowerCase();
			
			NamedNodeMap attrs = node.getAttributes();
			if(attrs == null)
				return false;
			
			if(attrs.getLength() == 0)
				return false;
			
			for(int i = 0;i<attrs.getLength();i++){
				Attr attr = (Attr) attrs.item(i);
				String attrname = attr.getName();
				if(isHtml)
					attrname = attrname.toLowerCase();
				
				if(attrname.equals(attribute)){
					if(attr.getValue().equals(content)){
						return true;
					}
				}
			}
		}
		
		
		//Filter is of the syntax "step"
		CheckXPathValidity temp = new CheckXPathValidity();
		
		NodeList children = node.getChildNodes();
		int number_of_children = node.getChildNodes().getLength();
		
		//If the current node has no children, then no "step" condition will be valid
		if(number_of_children == 0)
			return false;
		
		if(temp.checkValidity("step", filter)){
			LinkedList <DocumentNodes> node_list = temp.node_list;
			for(int i = 0; i<number_of_children;i++){
				if(checkMatch(node_list,0,children.item(i)))
					return true;
			}
		}
		return false;
	}
	
}
