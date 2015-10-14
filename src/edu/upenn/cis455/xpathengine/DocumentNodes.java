package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;

public class DocumentNodes {
	
	private String name;
	private ArrayList <String> filter = null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getFilter() {
		return filter;
	}
	public void setFilter(ArrayList <String> filter) {
		this.filter = filter;
	}
	
	
	
}
