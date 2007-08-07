// ExpressionDataTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import cytoscape.data.mRNAMeasurement;
import cytoscape.data.ExpressionData;
//------------------------------------------------------------------------------
public class ExpressionDataTest extends TestCase {

  private static String testDataDir = "../../testData";
  private static String testDataFile = testDataDir + "/gal1.22x5.mRNA";

//------------------------------------------------------------------------------
public ExpressionDataTest (String name) 
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
  ExpressionData data = new ExpressionData (testDataFile);
  Hashtable measurements = data.getAllExpressionMeasurements ();
  assertTrue (data.getCount () == measurements.size ());
  //String gene = "YHR051W";
  String gene = data.getGeneNames () [0];
  Hashtable geneInfo = (Hashtable) measurements.get (gene);
  //String condition = "gal1RG.sig";
  String condition = data.getConditionNames () [0];
  mRNAMeasurement measurement = (mRNAMeasurement) geneInfo.get (condition);
  //System.out.println ("----------" + gene + ": " + measurement);
  assertTrue (measurement.getRatio () >= -200.0);
  assertTrue (measurement.getSignificance () >= 0.0);

} // testCtor
//-------------------------------------------------------------------------
public void testGetConditionNames () throws Exception
{
  System.out.println ("testGetConditionNames");
  ExpressionData data = new ExpressionData (testDataFile);
  String [] conditionNames = data.getConditionNames ();
  assertTrue (conditionNames.length == data.getNumberOfConditions ());

} // testGetConditionNames
//-------------------------------------------------------------------------
public void testGetGeneNames () throws Exception
{
  System.out.println ("testGetGeneNames");
  ExpressionData data = new ExpressionData (testDataFile);
  String [] geneNames = data.getGeneNames ();
  for (int i=0; i < geneNames.length; i++)
    System.out.println (geneNames [i]);
  //System.out.println ("geneNames.length: " + geneNames.length);
  //System.out.println ("numberOfGenes: " + data.getNumberOfGenes ());
  assertTrue (geneNames.length == data.getNumberOfGenes ());

} // testGetGeneNames
//-------------------------------------------------------------------------
public void testGetMeasurement () throws Exception
{
  System.out.println ("testGetMeasurement");
  ExpressionData data = new ExpressionData (testDataFile);

  String gene = data.getGeneNames () [0];
  String condition = data.getConditionNames () [0];

  mRNAMeasurement measurement = data.getMeasurement (gene, condition);
  // System.out.println ("---------- measurement: " + measurement);
  double ratio = measurement.getRatio ();
  double sig = measurement.getSignificance ();

  assertTrue (ratio > -100.0);
  assertTrue (ratio < 1000.0);

  assertTrue (sig >= 0.0);
  assertTrue (sig < 10000.0);
  
} // testGetMeasurement
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  if (args.length == 1)
    testDataFile = args [0];

  junit.textui.TestRunner.run (new TestSuite (ExpressionDataTest.class));
}
//------------------------------------------------------------------------------
} // ExpressionDataTest
