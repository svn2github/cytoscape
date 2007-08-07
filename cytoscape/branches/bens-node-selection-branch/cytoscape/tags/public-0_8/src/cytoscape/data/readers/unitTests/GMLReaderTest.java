// GMLReaderTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.readers.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;

import y.base.*;
import y.view.Graph2D;

import cytoscape.data.readers.GMLReader;
//-----------------------------------------------------------------------------------------
public class GMLReaderTest extends TestCase {

  private static String testDataDir;

//------------------------------------------------------------------------------
public GMLReaderTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
  }
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
public void testSmallGraphRead () throws Exception
{ 
  System.out.println ("testSmallGraphRead");
  GMLReader reader = new GMLReader (testDataDir + "/gal.gml");
  Graph2D graph = reader.read ();
  assertTrue ("node count", graph.nodeCount () == 11);
  assertTrue ("edge count",  graph.edgeCount () == 10);

} // testSmallGraphRead
//-------------------------------------------------------------------------
public void testMediumGraphRead () throws Exception
{ 
  System.out.println ("testMediumGraphRead");
  GMLReader reader = new GMLReader (testDataDir + "/noLabels.gml");
  Graph2D graph = reader.read ();
  assertTrue ("node count", graph.nodeCount () == 332);
  assertTrue ("edge count",  graph.edgeCount () == 362);

} // testMediumGraphRead
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  if (args.length != 1) {
    System.out.println ("Error!  must supply path to test data directory on command line");
    System.exit (0);
    }

  testDataDir = args [0];

  junit.textui.TestRunner.run (new TestSuite (GMLReaderTest.class));
}
//------------------------------------------------------------------------------
} // GMLReaderTest
