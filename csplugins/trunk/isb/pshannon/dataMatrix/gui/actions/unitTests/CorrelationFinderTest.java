// CorrelationFinderTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui.actions.unitTests;

//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import csplugins.common.vectormath.*;
import csplugins.isb.pshannon.dataMatrix.*;
import csplugins.isb.pshannon.dataMatrix.gui.actions.*;
//------------------------------------------------------------------------------
public class CorrelationFinderTest extends TestCase {

//------------------------------------------------------------------------------
public CorrelationFinderTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void testSingleGene () throws Exception 
{
  System.out.println ("testSingleGene");
  String filename =  "sampleMatrix.txt";
  DataMatrixReader reader = new DataMatrixFileReader ("file://", filename);
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  assertTrue (matrix.getColumnCount () == 3);
  assertTrue (matrix.getRowCount () == 5);
  DataMatrixLens lens = new DataMatrixLens (matrix);
  CorrelationFinder finder = new CorrelationFinder (lens);
  finder.buildCorrelationTable ("a");

  Map correlations = finder.getCorrelationTable ();
  String [] keys = (String []) correlations.keySet().toArray (new String [0]);
  assertTrue (keys.length == 5);

  for (int i=0; i < keys.length; i++) {
    String gene = keys [i];
    double corr = ((Double) correlations.get (gene)).doubleValue ();
    //System.out.println (gene + ": " + corr);
    } // for i

  assertTrue (((Double) correlations.get ("a")).doubleValue () == 0.9999999999999999);
  assertTrue (((Double) correlations.get ("b")).doubleValue () ==  -0.40490389262229654);
  assertTrue (((Double) correlations.get ("c")).doubleValue () ==  0.0);
  assertTrue (((Double) correlations.get ("d")).doubleValue () ==  0.7181729057836225);
  assertTrue (((Double) correlations.get ("e")).doubleValue () ==  -0.9999999999999999);

} // testSingleGene
//-------------------------------------------------------------------------
public void testSingleGeneWithDisabledColumn () throws Exception 
{
  System.out.println ("testSingleGeneWithDisabledColumn");
  String filename =  "sampleMatrix.txt";
  DataMatrixReader reader = new DataMatrixFileReader ("file://", filename);
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  assertTrue (matrix.getColumnCount () == 3);
  assertTrue (matrix.getRowCount () == 5);
  DataMatrixLens lens = new DataMatrixLens (matrix);
  lens.setColumnState (1, false);
  CorrelationFinder finder = new CorrelationFinder (lens);
  finder.buildCorrelationTable ("a");

  Map correlations = finder.getCorrelationTable ();
  String [] keys = (String []) correlations.keySet().toArray (new String [0]);
  assertTrue (keys.length == 5);

  System.out.println ("---------- just back from getting correlation table");
  for (int i=0; i < keys.length; i++) {
    String gene = keys [i];
    double corr = ((Double) correlations.get (gene)).doubleValue ();
    //System.out.println (gene + ": " + corr);
    } // for i

  double [] expected = {1.0000000000000002, -0.3465783488702321, 0.0, 
                        0.9529976308307069, -1.000000000};


  assertTrue (((Double) correlations.get ("a")).doubleValue () == expected [0]);
  assertTrue (((Double) correlations.get ("b")).doubleValue () == expected [1]);
  assertTrue (((Double) correlations.get ("c")).doubleValue () == expected [2]);
  assertTrue (((Double) correlations.get ("d")).doubleValue () == expected [3]);

} // testSingleGene
//-------------------------------------------------------------------------
public void testSeveralGenes () throws Exception 
{
  System.out.println ("testSeveralGenes");
  String filename =  "sampleMatrix.txt";
  DataMatrixReader reader = new DataMatrixFileReader ("file://", filename);
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  assertTrue (matrix.getColumnCount () == 3);
  assertTrue (matrix.getRowCount () == 5);
  DataMatrixLens lens = new DataMatrixLens (matrix);
  CorrelationFinder finder = new CorrelationFinder (lens);
  String [] baseGenes = {"a", "b", "d"};
  finder.buildCorrelationTable (baseGenes);

  Map correlations = finder.getCorrelationTable ();
  String [] keys = (String []) correlations.keySet().toArray (new String [0]);
  assertTrue (keys.length == 5);

  for (int i=0; i < keys.length; i++) {
    String gene = keys [i];
    double corr = ((Double) correlations.get (gene)).doubleValue ();
    //System.out.println (gene + ": " + corr);
    } // for i

  assertTrue (((Double) correlations.get ("a")).doubleValue () == 0.7029614322271802);
  assertTrue (((Double) correlations.get ("b")).doubleValue () == 0.28203872119163176);
  assertTrue (((Double) correlations.get ("c")).doubleValue () == 0.0);
  assertTrue (((Double) correlations.get ("d")).doubleValue () == 0.8831948185308136);

} // testSeveralGenes
//-------------------------------------------------------------------------
public void testSingleGeneNegativeCorrelation () throws Exception 
{
  System.out.println ("testSingleGeneNegativeCorrelation");
  String filename =  "sampleMatrix.txt";
  DataMatrixReader reader = new DataMatrixFileReader ("file://", filename);
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  assertTrue (matrix.getColumnCount () == 3);
  assertTrue (matrix.getRowCount () == 5);
  DataMatrixLens lens = new DataMatrixLens (matrix);
  CorrelationFinder finder = new CorrelationFinder (lens);
  finder.buildCorrelationTable ("a");

  Map correlations = finder.getCorrelationTable ();
  String [] keys = (String []) correlations.keySet().toArray (new String [0]);
  assertTrue (keys.length == 5);

  for (int i=0; i < keys.length; i++) {
    String gene = keys [i];
    double corr = ((Double) correlations.get (gene)).doubleValue ();
    //System.out.println (gene + ": " + corr);
    } // for i

  assertTrue (((Double) correlations.get ("a")).doubleValue () == 0.9999999999999999);
  assertTrue (((Double) correlations.get ("b")).doubleValue () ==  -0.40490389262229654);
  assertTrue (((Double) correlations.get ("c")).doubleValue () ==  0.0);
  assertTrue (((Double) correlations.get ("d")).doubleValue () ==  0.7181729057836225);
  assertTrue (((Double) correlations.get ("e")).doubleValue () ==  -0.9999999999999999);

} // testSingleGene
//-------------------------------------------------------------------------
public static void main(String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite(CorrelationFinderTest.class));
}
//------------------------------------------------------------------------------
} // CorrelationFinderTest
