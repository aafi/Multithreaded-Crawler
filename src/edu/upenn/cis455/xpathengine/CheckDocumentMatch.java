package edu.upenn.cis455.xpathengine;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.upenn.cis455.servlet.Utilities;

public class CheckDocumentMatch {
	
	public static boolean checkMatch(LinkedList <DocumentNodes> node_list, int idx, Node node){
		if(node == null || idx >= node_list.size())
			return false;
		
		DocumentNodes entry = node_list.get(idx);
		
		if(!entry.getName().equals(node.getNodeName())){
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
			for(String filter : entry.getFilter()){
				if(checkFilter(filter,node)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean checkFilter(String filter,Node node){
		//Add atomic tests
		
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
	
	public static void main(String[] args){
		String filename = "<web-app><context-param><param-name><something><else></else></something></param-name></context-param></web-app>";
		Document d = Utilities.buildXmlDom(filename);
		CheckXPathValidity checkValid = new CheckXPathValidity();
		if(checkValid.checkValidity("xpath","/web-app/context-param/param-name [something/else]")){
			checkMatch(checkValid.node_list,0,d.getFirstChild());
			System.out.print(checkMatch(checkValid.node_list,0,d.getFirstChild()));
		}
	}

}
