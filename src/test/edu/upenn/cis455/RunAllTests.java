package test.edu.upenn.cis455;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RunAllTests extends TestCase 
{
  public static Test suite() 
  {
    try {
      Class[]  testClasses = {Class.forName(XPathEngineTest.class.getName()),
    		  Class.forName(CrawlerTest.class.getName())
        /* TODO: Add the names of your unit test classes here */
        // Class.forName("your.class.name.here") 
      };   
      
      return new TestSuite(testClasses);
    } catch(Exception e){
      e.printStackTrace();
    } 
    
    return null;
  }
}
