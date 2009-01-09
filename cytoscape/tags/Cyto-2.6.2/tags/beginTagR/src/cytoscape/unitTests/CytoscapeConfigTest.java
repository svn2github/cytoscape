// CytoscapeConfigTest.java:  a junit test for the class which sets run-time configuration,
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

import cytoscape.CytoscapeConfig;
//import cytoscape.plugins.activePaths.data.ActivePathFinderParameters;
//------------------------------------------------------------------------------
public class CytoscapeConfigTest extends TestCase {


//------------------------------------------------------------------------------
public CytoscapeConfigTest (String name) 
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
public void testAllArgs () throws Exception
{ 
  System.out.println ("testAllArgs");

  String bioDataDirectory = "../data/GO";
  String geometryFilename = "../data/galFiltered.gml";
  String interactionsFilename = "../data/tideker0/yeastSmall.intr";
  String expressionFilename   = "../data/tideker0/gal1-20.mrna";
  String nodeAttributeFile1 = "xxx.fooB";
  String nodeAttributeFile2 = "xxx.barA";
  String nodeAttributeFile3 = "xxx.zooC";

  String edgeAttributeFile1 = "xxx.edgeA";
  String edgeAttributeFile2 = "xxx.edgeB";

  double activePathSignificanceThreshold = 32.0;

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

  CytoscapeConfig config = new CytoscapeConfig (args);

  assertTrue (config.getBioDataDirectory().equals (bioDataDirectory));
  assertTrue (config.getGeometryFilename().equals (geometryFilename));
  assertTrue (config.getInteractionsFilename().equals (interactionsFilename));
  assertTrue (config.getExpressionFilename().equals (expressionFilename));

  assertTrue (config.getNumberOfNodeAttributeFiles() == 3);
  String [] nafs = config.getNodeAttributeFilenames ();
  assertTrue (nafs.length == 3);

  for (int i=0; i < nafs.length; i++) {
    String af = nafs [i];
    assertTrue (af.equals (nodeAttributeFile1) ||
                af.equals (nodeAttributeFile2) ||
                af.equals (nodeAttributeFile3));
    } // for i

  assertTrue (config.getNumberOfEdgeAttributeFiles() == 2);
  String [] eafs = config.getEdgeAttributeFilenames ();
  assertTrue (eafs.length == 2);

  for (int i=0; i < eafs.length; i++) {
    String af = eafs [i];
    assertTrue (af.equals (edgeAttributeFile1) ||
                af.equals (edgeAttributeFile2));
    } // for i

  System.out.println ("--------------------- config: \n" + config.toString ());
  assertTrue (config.helpRequested ());
  assertTrue (config.displayVersion ());

  String [] extensions = config.getAllDataFileExtensions ();
  assertTrue (config.getAllDataFileNames().length == 8);

  assertTrue (config.getAllDataFileExtensions().length == 8);

    // choose two file extensions to look for
  boolean foundFooB = false;
  boolean foundIntr = false;

  for (int i=0; i < extensions.length; i++) {
    if (extensions [i].equals ("fooB")) foundFooB = true;
    if (extensions [i].equals ("intr")) foundIntr = true;
    }

  assertTrue (foundFooB);
  assertTrue (foundIntr);

} // testAllArgs
//-------------------------------------------------------------------------
public void testForExpectedNullValues () throws Exception
{ 
  System.out.println ("testForExpectedNullValues");

  String [] args = {"-h"};

  CytoscapeConfig config = new CytoscapeConfig (args);
  assertTrue (config.getBioDataDirectory() == null);
  assertTrue (config.getGeometryFilename() == null);
  assertTrue (config.getInteractionsFilename() == null);
  assertTrue (config.getExpressionFilename() == null);
  assertTrue (config.getAllDataFileNames().length == 0);
  assertTrue (config.helpRequested());
  assertTrue (!config.inputsError ());


} // testForExpectedNullValues
//-------------------------------------------------------------------------
/*******************************************************
public void testNoActivePathParameters () throws Exception
{ 
  System.out.println ("testNoActivePathParameters");
  String [] args = new String [0];
  CytoscapeConfig config = new CytoscapeConfig (args);
  assertTrue (config.activePathParametersPresent () == false);

}
//-------------------------------------------------------------------------
public void testAllActivePathParameters () throws Exception
{ 
  System.out.println ("testNoActivePathParameters");

  double significanceThreshold = 18.45;
  double initialTemperature = 10.8; 
  double finalTemperature = 0.2;
  int totalIterations = 499;
  int numberOfPaths = 43;
  int displayInterval = 503;
  int randomSeed = 123;

  String [] args =  
   {"--APsig", (new Double (significanceThreshold)).toString (),
    "--APt0",  (new Double (initialTemperature)).toString (),
    "--APtf",  (new Double (finalTemperature)).toString (),
    "--APni",  (new Integer (totalIterations)).toString (),
    "--APnp",  (new Integer (numberOfPaths)).toString (),
    "--APdi",  (new Integer (displayInterval)).toString (),
    "--APrs",  (new Integer (randomSeed)).toString ()};

  CytoscapeConfig config = new CytoscapeConfig (args);
  assertTrue (config.activePathParametersPresent () == true);
  ActivePathFinderParameters apfp = config.getActivePathParameters();
  // System.out.println (apfp.toString());
  assertTrue (config.getActivePathParameters().getTotalIterations() == totalIterations);
  assertTrue (config.getActivePathParameters().getSignificanceThreshold() == 
                                                                 significanceThreshold);
  assertTrue (config.getActivePathParameters().getInitialTemperature() == initialTemperature);
  assertTrue (config.getActivePathParameters().getFinalTemperature() == finalTemperature);
  assertTrue (config.getActivePathParameters().getNumberOfPaths () == numberOfPaths);
  assertTrue (config.getActivePathParameters().getDisplayInterval() == displayInterval);
  assertTrue (config.getActivePathParameters().getRandomSeed() == randomSeed);


} // testAllActivePathParameters
//-------------------------------------------------------------------------
public void testAllActivePathAndRegularParameters () throws Exception
{ 
  System.out.println ("testAllActivePathAndRegularParameters");

  String bioDataDirectory = "../data/GO";
  String geometryFilename = "../data/galFiltered.gml";
  String interactionsFilename = "../data/tideker0/yeastSmall.intr";
  String expressionFilename   = "../data/tideker0/gal1-20.mrna";
  String nodeAttributeFile1 = "fooB";
  String nodeAttributeFile2 = "barA";
  String nodeAttributeFile3 = "zooC";

  String edgeAttributeFile1 = "edgeA";
  String edgeAttributeFile2 = "edgeB";



  double significanceThreshold = 18.45;
  double initialTemperature = 10.8; 
  double finalTemperature = 0.2;
  int totalIterations = 499;
  int numberOfPaths = 43;
  int displayInterval = 503;
  int randomSeed = 123;


  String [] args =  
    {"-b", bioDataDirectory, 
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
    "--APsig", (new Double (significanceThreshold)).toString (),
    "--APt0",  (new Double (initialTemperature)).toString (),
    "--APtf",  (new Double (finalTemperature)).toString (),
    "--APni",  (new Integer (totalIterations)).toString (),
    "--APnp",  (new Integer (numberOfPaths)).toString (),
    "--APdi",  (new Integer (displayInterval)).toString (),
    "--APrs",  (new Integer (randomSeed)).toString ()};

  CytoscapeConfig config = new CytoscapeConfig (args);
  assertTrue (config.activePathParametersPresent () == true);
  assertTrue (config.getActivePathParameters().getTotalIterations() == totalIterations);
  assertTrue (config.getActivePathParameters().getSignificanceThreshold() == 
                                                                 significanceThreshold);
  assertTrue (config.getActivePathParameters().getInitialTemperature() == initialTemperature);
  assertTrue (config.getActivePathParameters().getFinalTemperature() == finalTemperature);
  assertTrue (config.getActivePathParameters().getNumberOfPaths () == numberOfPaths);
  assertTrue (config.getActivePathParameters().getDisplayInterval() == displayInterval);
  assertTrue (config.getActivePathParameters().getRandomSeed() == randomSeed);

  assertTrue (config.getBioDataDirectory().equals (bioDataDirectory));
  assertTrue (config.getGeometryFilename().equals (geometryFilename));
  assertTrue (config.getInteractionsFilename().equals (interactionsFilename));
  assertTrue (config.getExpressionFilename().equals (expressionFilename));

  assertTrue (config.getNumberOfNodeAttributeFiles() == 3);
  String [] nafs = config.getNodeAttributeFilenames ();
  assertTrue (nafs.length == 3);

  for (int i=0; i < nafs.length; i++) {
    String af = nafs [i];
    assertTrue (af.equals (nodeAttributeFile1) ||
                af.equals (nodeAttributeFile2) ||
                af.equals (nodeAttributeFile3));
    } // for i

  assertTrue (config.getNumberOfEdgeAttributeFiles() == 2);
  String [] eafs = config.getEdgeAttributeFilenames ();
  assertTrue (eafs.length == 2);

  for (int i=0; i < eafs.length; i++) {
    String af = eafs [i];
    assertTrue (af.equals (edgeAttributeFile1) ||
                af.equals (edgeAttributeFile2));
    } // for i

  assertTrue (config.helpRequested ());
  assertTrue (config.displayVersion ());
  // assertTrue (!config.inputsError ());

} // testAllActivePathAndRegularParameters
*******************************************************/
//-------------------------------------------------------------------------
/**
 *  ensure that multiple sources of the input graph (e.g., a gml file, and
 *  an interactions file) are detected and reported as an error
 */
public void testLegalArgs0 () throws Exception
{ 
  System.out.println ("testLegalArgs0");

  String geometryFilename = "../data/galFiltered.gml";
  String interactionsFilename = "../data/tideker0/yeastSmall.intr";

  String [] args = {"-g", geometryFilename, 
                    "-i", interactionsFilename};

  CytoscapeConfig config = new CytoscapeConfig (args);

  assertTrue (config.inputsError ());

} // testLegalArgs0
//-------------------------------------------------------------------------
/**
 * ensure that if active path calculation is requested -- by the presence
 * of at least one active path parameter on the command line -- that the
 * other prerequisites of that calcuation are present:  a graph, and
 * expression data
 */
/***************************
public void testLegalArgs1 () throws Exception
{ 
  System.out.println ("testLegalArgs1");

  String [] args0 = {"-g",    "xxx.gml",
                    "-e",     "expessionData",
                    "--APni", "5000"};

  CytoscapeConfig config = new CytoscapeConfig (args0);
  assertTrue (!config.inputsError ());

  String [] args1 = {"-g",     "xxx.gml",
                     "--APni", "5000"};

  config = new CytoscapeConfig (args1);
  assertTrue (config.inputsError ());

  String [] args2 = {"-e",     "expessionData",
                     "--APni", "5000"};

  config = new CytoscapeConfig (args2);
  assertTrue (config.inputsError ());
  

} // testLegalArgs1
***********************/
//-------------------------------------------------------------------------
/**
 * make sure that system properties are read, and that user props can
 * override and extend them
 */
public void testReadProperties () throws Exception
{ 
  System.out.println ("testReadProperties");

  String [] args = new String [0];

  CytoscapeConfig config = new CytoscapeConfig (args);
  Properties props = config.getProperties ();
  assertTrue (props.size () > 10);
  System.out.println (props.getProperty ("edge.color.controller"));
  assertTrue (props.getProperty ("edge.color.controller").equals ("interaction"));
  assertTrue (props.getProperty ("edge.color.map.interaction.pd").equals ("255,0,0"));

} // testReadProperties
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (CytoscapeConfigTest.class));
}
//------------------------------------------------------------------------------
} // CytoscapeConfigTest
