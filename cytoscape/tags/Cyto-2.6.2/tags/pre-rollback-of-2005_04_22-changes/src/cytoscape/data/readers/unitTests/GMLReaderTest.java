package cytoscape.data.readers.unitTests;


import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import cytoscape.data.readers.GMLReader2;
import cytoscape.unitTests.AllTests;


public class GMLReaderTest extends TestCase {
  private static String testDataDir;



public GMLReaderTest (String name) 
{
  super (name);
  if(AllTests.runAllTests()) {
      testDataDir = "testData";
  }
}


public void setUp () throws Exception
{
  }


public void tearDown () throws Exception
{
}


public void testSmallGraphRead () throws Exception
{ 
  AllTests.standardOut("testSmallGraphRead");
  //GMLReader reader = new GMLReader (testDataDir + "/gal.gml");
  //reader.read ();
  //RootGraph graph = reader.getRootGraph ();
  

  //CyNetwork network = Cytoscape.createNetwork( testDataDir + "/gal.gml" );

  //assertTrue ("node count", network.getNodeCount () == 11);
  //assertTrue ("edge count", network.getEdgeCount () == 10);

} // testSmallGraphRead

public void testMediumGraphRead () throws Exception
{ 
  AllTests.standardOut ("testMediumGraphRead");
  // GMLReader reader = new GMLReader (testDataDir + "/noLabels.gml");
  //reader.read ();
  //RootGraph graph = reader.getRootGraph ();
  

  //CyNetwork network = Cytoscape.createNetwork( testDataDir + "/noLabels.gml" );

  //assertTrue ("node count", network.getNodeCount () == 332);
  // changed to 361, since there must be a dupe...
  //assertTrue ("edge count", network.getEdgeCount () == 361);

} // testMediumGraphRead

public static void main (String[] args) 
{
  if (args.length != 1) {
    System.out.println ("Error!  must supply path to test data directory on command line");
    System.exit (0);
    }

  testDataDir = args [0];

  junit.textui.TestRunner.run (new TestSuite (GMLReaderTest.class));
}


} // GMLReaderTest
