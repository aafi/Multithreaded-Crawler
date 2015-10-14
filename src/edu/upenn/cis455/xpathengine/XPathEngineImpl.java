package edu.upenn.cis455.xpathengine;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.xml.sax.helpers.DefaultHandler;

public class XPathEngineImpl implements XPathEngine {
	
	private String [] queries = null;
	private Document doc = null;
	
	public XPathEngineImpl() {
		// Do NOT add arguments to the constructor!!
	}
	
	public void setXPaths(String[] s) {
		/* TODO: Store the XPath expressions that are given to this method */
		queries = s;
		
	}

	public boolean isValid(int i) {
		return checkValidity(queries[i]);
	}
	
	public boolean[] evaluate(Document d) { 
		/* TODO: Check whether the document matches the XPath expressions */
		doc = d;
		boolean [] b = new boolean[queries.length];
		
		for(int i = 0; i<queries.length;i ++){
			if(isValid(i)){
				b[i] = true;
//				b[i] = checkMatch(queries[i]);
			}else
				b[i] = false;
		}
		
		return b;
	}
	
	private boolean checkValidity(String xpath){
		
		return CheckXPathValidity.checkValidity("xpath",xpath);
	}
	
	private boolean checkMatch(String xpath){
		
		return false;
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
