// PluginLoaderTest.java:  a junit test for the class which sets run-time configuration,
// usually from command line arguments
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.rmi.*;
import java.io.*;
import java.util.*;

import cytoscape.PluginInfo;
import cytoscape.PluginLoader;
import cytoscape.CytoscapeConfig;
import cytoscape.GraphObjAttributes;
//------------------------------------------------------------------------------
public class PluginLoaderTest extends TestCase {


//------------------------------------------------------------------------------
public PluginLoaderTest (String name) 
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
public void testCtor () throws Exception
{ 
  System.out.println ("testCtor");
  String bioDataDirectory = "../data/GO";
  String geometryFilename = "../data/galFiltered.gml";
  String interactionsFilename = "../data/tideker0/yeastSmall.intr";
  String expressionFilename   = "../data/tideker0/gal1-20.mrna";
  String nodeAttributeFile1 = "xxx.foo";
  String nodeAttributeFile2 = "xxx.barA";
  String nodeAttributeFile3 = "xxx.zooC";

  String edgeAttributeFile1 = "xxx.edgeA";
  String edgeAttributeFile2 = "xxx.edgeB";

  double activePathSignificanceThreshold = 32.0;
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();


  String [] args = {"-b", bioDataDirectory, 
                    "-g", geometryFilename, 
                    "-i", interactionsFilename, 
                    "-e", expressionFilename, 
                    "-n", nodeAttributeFile1,
                    "-n", nodeAttributeFile2,
                    "-n", nodeAttributeFile3,
                    "-j", edgeAttributeFile1,
                    "-j", edgeAttributeFile2,
                    "-h",
                    "-v",
                    };

  // String [] args = {"-n", "data.foo"};

  CytoscapeConfig config = new CytoscapeConfig (args);

  PluginLoader loader = new PluginLoader (null, new CytoscapeConfig (args),
                                          nodeAttributes, edgeAttributes);

  assertTrue (loader.getClassesToLoad().length == 2);

  
} // testDefaultCtor
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (PluginLoaderTest.class));
}
//------------------------------------------------------------------------------
} // PluginLoaderTest
