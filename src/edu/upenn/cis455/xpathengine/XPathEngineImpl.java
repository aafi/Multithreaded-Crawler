package edu.upenn.cis455.xpathengine;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.xml.sax.helpers.DefaultHandler;

public class XPathEngineImpl implements XPathEngine {
	
	private String [] queries = null;
	private boolean [] valid;
	
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
				System.out.print("Query is valid");
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
		
		CheckDocumentMatch docMatch = new CheckDocumentMatch();
		return docMatch.checkMatch(checkValid.node_list,0,d.getFirstChild());
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
