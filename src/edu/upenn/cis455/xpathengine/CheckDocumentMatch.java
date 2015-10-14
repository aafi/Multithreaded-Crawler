package edu.upenn.cis455.xpathengine;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

public class CheckDocumentMatch {
	
	public boolean checkMatch(LinkedList <DocumentNodes> node_list, int idx, Node node){
		if(node == null || idx >= node_list.size())
			return false;
		
		DocumentNodes entry = node_list.get(idx);
		
		if(!entry.getName().equals(node.getNodeName())){
			return false;
		}
		
		//No attribute filters
		if(entry.getFilter().size() == 0){
			
		}
		
		
		return false;
	}

}
