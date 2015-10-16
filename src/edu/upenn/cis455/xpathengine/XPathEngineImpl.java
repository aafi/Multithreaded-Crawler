package edu.upenn.cis455.xpathengine;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

public class XPathEngineImpl implements XPathEngine {
	
	private String [] queries = null;
	private boolean [] valid;
	private boolean isHtml = false;
	
	public XPathEngineImpl() {
		// Do NOT add arguments to the constructor!!
	}
	
	public void setXPaths(String[] s) {
		/* TODO: Store the XPath expressions that are given to this method */
		queries = s;
		
	}

	public boolean isValid(int i) {
		return valid[i];
	}
	
	public boolean[] evaluate(Document d) { 
		/* TODO: Check whether the document matches the XPath expressions */
		boolean [] b = new boolean[queries.length];
		valid = new boolean[queries.length];
		
		for(int i = 0; i<queries.length;i ++){
			CheckXPathValidity checkValid = new CheckXPathValidity();
			valid[i] = checkValidity(queries[i],checkValid);
			if(valid[i]){
				System.out.println("Query is valid");
				b[i] = checkMatch(d, checkValid);
			}else
				b[i] = false;
		}
		
		return b;
	}
	
	private boolean checkValidity(String xpath, CheckXPathValidity checkValid){
		
		return checkValid.checkValidity("xpath",xpath);
	}
	
	private boolean checkMatch(Document d, CheckXPathValidity checkValid){
		
		CheckDocumentMatch docMatch = new CheckDocumentMatch(isHtml);
		
		NodeList children = d.getChildNodes();
		int number_of_children = d.getChildNodes().getLength();
		boolean return_value = false;
		
		if(number_of_children == 0){
			return false;
		}else{
			for(int i = 0; i<number_of_children;i++){
				if(docMatch.checkMatch(checkValid.node_list,0,children.item(i)))
					return_value = true;
			}
		}
		return return_value;
	}
	
	public String [] getQueries(){
		return queries;
	}
	
	public void setHtml(boolean value){
		isHtml = value;
	}
	
	public boolean getHtml(){
		return isHtml;
	}

	@Override
	public boolean isSAX() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean[] evaluateSAX(InputStream document, DefaultHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}
        
}
