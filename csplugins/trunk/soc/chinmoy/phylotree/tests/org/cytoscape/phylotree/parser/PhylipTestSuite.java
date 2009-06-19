package org.cytoscape.phylotree.parser;
import junit.framework.Test;
import junit.framework.TestSuite;


public class PhylipTestSuite {
 
  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    suite.addTestSuite(PhylipTreeImplTest.class);
    return suite;
    
    
  }
  
  public static void main (String [] args){
    junit.textui.TestRunner.run(suite());
    
  }
  
}