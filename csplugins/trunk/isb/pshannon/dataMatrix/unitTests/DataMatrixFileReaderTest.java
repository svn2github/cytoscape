// DataMatrixFileReaderTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import csplugins.isb.pshannon.dataMatrix.DataMatrix;
import csplugins.isb.pshannon.dataMatrix.DataMatrixFileReader;
//------------------------------------------------------------------------------
public class DataMatrixFileReaderTest extends TestCase {

//------------------------------------------------------------------------------
public DataMatrixFileReaderTest (String name) 
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
public void testSimple () throws Exception
{ 
  System.out.println ("testSimple");
  String protocol = "file://";
  String filename = "bogus";

  DataMatrixFileReader reader = new DataMatrixFileReader (protocol, filename);
  try {
    reader.read ();
    assertTrue (false);
    }
  catch (IllegalArgumentException iae) {;}

  filename = "simpleMatrix.txt";

 reader = new DataMatrixFileReader (protocol, filename);
  try {
    reader.read ();
    assertTrue (true);
    }
  catch (IllegalArgumentException iae) {
    assertTrue (false);
    }

  DataMatrix [] results = reader.get ();
  assertTrue (results.length == 1);
  DataMatrix matrix0 = results [0];
  assertTrue (matrix0.getRowCount () == 4);
  assertTrue (matrix0.getColumnCount () == 3);

  assertTrue (matrix0.get (0,0) == 12.2);
  assertTrue (matrix0.get (0,1) == 13.8);
  assertTrue (matrix0.get (0,2) == 4);

  assertTrue (matrix0.get (1,0) == -1.2);
  assertTrue (matrix0.get (1,1) == -8);
  assertTrue (matrix0.get (1,2) == -32.3333);

  assertTrue (matrix0.get (2,0) == 0.0);
  assertTrue (matrix0.get (2,1) == 0.0);
  assertTrue (matrix0.get (2,2) == 0);

  assertTrue (matrix0.get (3,0) == 999.0);
  assertTrue (matrix0.get (3,1) == 99.0);
  assertTrue (matrix0.get (3,2) == 9.0);

  String [] columnTitles = matrix0.getColumnTitles ();
  String [] rowTitles = matrix0.getRowTitles ();

  assertTrue (matrix0.getRowTitlesTitle().equals ("gene"));
  assertTrue (columnTitles [0].equals ("cond0"));
  assertTrue (columnTitles [1].equals ("cond1"));
  assertTrue (columnTitles [2].equals ("cond2"));;

  assertTrue (rowTitles [0].equals ("a"));
  assertTrue (rowTitles [1].equals ("b"));
  assertTrue (rowTitles [2].equals ("c"));
  assertTrue (rowTitles [3].equals ("d"));

} // testCtor
//-------------------------------------------------------------------------
public void test50lineTraditionalMatrixFile () throws Exception
{ 
  System.out.println ("test50lineTraditionalMatrixFile");
  String protocol = "file://";
  String filename = "matrix.expression";

  DataMatrixFileReader reader = new DataMatrixFileReader (protocol, filename);
  try {
    reader.read ();
    }
  catch (IllegalArgumentException iae) {
    System.out.println (iae.getMessage ());
    assertTrue (false);
    }

  DataMatrix matrix = reader.get ()[0];
  assertTrue (matrix.getRowCount () == 49);
  assertTrue (matrix.getColumnCount () == 5);

  String [] expectedColumnTitles = {"Spellman_alphaT028_vs_async",
                                    "Spellman_alphaT119_vs_async", 
                                    "Spellman_alphaT091_vs_async",
                                    "Spellman_alphaT063_vs_async",
                                    "Spellman_alphaT000_vs_async"};

  assertTrue (matrix.getRowTitlesTitle().equals ("GENE"));
  String [] actualColumnTitles = matrix.getColumnTitles ();
  for (int i=0; i < actualColumnTitles.length; i++)
    assertTrue (expectedColumnTitles [i].equals (actualColumnTitles [i]));

  String [] actualRowTitles = matrix.getRowTitles ();

    // 0th, 10th, 20th, 30th, 40th row titles:
  String [] expectedRowTitles = {"YAL014C", "YAR018C", "YBL059W",
                                 "YBL101C", "YBR058C"};
  assertTrue (expectedRowTitles [0].equals (actualRowTitles [0]));
  assertTrue (expectedRowTitles [1].equals (actualRowTitles [10]));
  assertTrue (expectedRowTitles [2].equals (actualRowTitles [20]));
  assertTrue (expectedRowTitles [3].equals (actualRowTitles [30]));
  assertTrue (expectedRowTitles [4].equals (actualRowTitles [40]));


} // test50lineTraditionalMatrixFile 
//-------------------------------------------------------------------------
public void test50lineIcatMatrixFile () throws Exception
{ 
  System.out.println ("test50lineIcatMatrixFile");
  String protocol = "file://";
  String filename = "matrix.icat";

  DataMatrixFileReader reader = new DataMatrixFileReader (protocol, filename);
  try {
    reader.read ();
    }
  catch (IllegalArgumentException iae) {
    assertTrue (false);
    }

  DataMatrix matrix = reader.get ()[0];
  assertTrue (matrix.getRowCount () == 49);
  // System.out.println ("columns: " + matrix.getColumnCount ());
  assertTrue (matrix.getColumnCount () == 5);


} // test50lineTraditionalMatrixFile 
//-------------------------------------------------------------------------
public void testHttpRead () throws Exception
{ 
  System.out.println ("testHttpRead");
  String protocol = "http://";
  String filename = "db.systemsbiology.net/cytoscape/projects/static/halo/data/uvRepair.ratio";

  DataMatrixFileReader reader = new DataMatrixFileReader (protocol, filename);
  try {
    reader.read ();
    }
  catch (IllegalArgumentException iae) {
    assertTrue (false);
    }

  DataMatrix matrix = reader.get ()[0];
  assertTrue (matrix.getColumnCount () == 5);
  assertTrue (matrix.getRowCount () == 2399);


} // testHttpRead
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (DataMatrixFileReaderTest.class));

}// main
//------------------------------------------------------------------------------
} // DataMatrixFileReaderTest
