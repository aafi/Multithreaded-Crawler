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
	
	/**
	 * Sets the user input xpaths
	 * @param String array containing user given xpaths
	 */
	public void setXPaths(String[] s) {
		/* TODO: Store the XPath expressions that are given to this method */
		queries = s;
		
	}
	
	/**
	 * Checks whether the i'th xpath is syntactically valid
	 * @param index i
	 * @return boolean indicating validity
	 */
	public boolean isValid(int i) {
		return valid[i];
	}
	
	/**
	 * Evaluates whether the user given xpaths match the given document
	 * @param The document to be matched to
	 * @return Boolean array indicating which xpaths matched
	 */
	public boolean[] evaluate(Document d) { 
		boolean [] b = new boolean[queries.length];
		valid = new boolean[queries.length];
		
		for(int i = 0; i<queries.length;i ++){
			CheckXPathValidity checkValid = new CheckXPathValidity();
			valid[i] = checkValidity(queries[i],checkValid);
			if(valid[i]){
				b[i] = checkMatch(d, checkValid);
			}else
				b[i] = false;
		}
		
		return b;
	}
	
	/**
	 * Calls the appropriate method to perform the syntactic validity check
	 * @param xpath to be checked
	 * @param checkValid
	 * @return boolean variable indicating validity
	 */
	private boolean checkValidity(String xpath, CheckXPathValidity checkValid){
		
		return checkValid.checkValidity("xpath",xpath);
	}
	
	/**
	 * Calls the appropriate method to check if the xpaths match with all the children nodes of 
	 * the document root
	 * @param document to be matched against
	 * @param checkValid
	 * @return boolean indicating match
	 */
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
	
	/**
	 * Getter method for queries array
	 * @return queries array
	 */
	public String [] getQueries(){
		return queries;
	}
	
	/**
	 * Sets the boolean indicating if the given document is an xpath
	 * @param value
	 */
	public void setHtml(boolean value){
		isHtml = value;
	}
	
	/**
	 * Getter method for isHtml flag
	 * @return value of isHtml flag
	 */
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
