// DataMatrixTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import csplugins.isb.pshannon.dataMatrix.*;
//------------------------------------------------------------------------------
public class DataMatrixTest extends TestCase {

//------------------------------------------------------------------------------
public DataMatrixTest (String name) 
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
public void testSimple4x3Matrix () throws Exception
{
  System.out.println ("testSimple4x3Matrix");

  DataMatrixReader reader = new DataMatrixFileReader ("file://", "simpleMatrix.txt");
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];

  // System.out.println ("column count: " + matrix.getColumnCount ());
  assertTrue (matrix.getColumnCount () == 3);
  assertTrue (matrix.getRowCount () == 4);

  String rowTitlesTitle = matrix.getRowTitlesTitle ();
  String [] columnTitles = matrix.getColumnTitles ();
  String [] rowTitles = matrix.getRowTitles ();

  assertTrue (columnTitles.length == 3);
  assertTrue (rowTitles.length == 4);
  assertTrue (rowTitlesTitle.equals ("gene"));
  String [] expectedRowTitles = {"a", "b", "c", "d"};
  String [] expectedColumnTitles = {"cond0", "cond1", "cond2"};

  for (int i=0; i < columnTitles.length; i++) {
    assertTrue (columnTitles [i].equals (expectedColumnTitles [i]));
    }

  for (int i=0; i < rowTitles.length; i++)
    assertTrue (rowTitles [i].equals (expectedRowTitles [i]));

  double [] row3 = matrix.get (3);
  assertTrue (row3.length == 3);
  assertTrue (row3 [0] == 999.0);
  assertTrue (row3 [1] == 99.0);
  assertTrue (row3 [2] == 9.0);

  double [] row3ByName = matrix.get ("d");
  assertTrue (row3ByName.length == 3);
  assertTrue (row3ByName [0] == 999.0);
  assertTrue (row3ByName [1] == 99.0);
  assertTrue (row3ByName [2] == 9.0);

  assertTrue (matrix.getColumnNumber ("cond0") == 0);
  assertTrue (matrix.getColumnNumber ("cond1") == 1);
  assertTrue (matrix.getColumnNumber ("cond2") == 2);

  try {
    int c = matrix.getColumnNumber ("flapadoodle!");
    assertFalse (true);  // should not execute
    }
  catch (IllegalArgumentException iae) {
    assertTrue (true);
    }

  double [] column2 = matrix.getColumn ("cond1");
  assertTrue (column2.length == 4);
  //for (int i=0; i < 4; i++)
   // System.out.println (i + ") " + column2 [i]);

  assertTrue (column2 [0] == 13.8);
  assertTrue (column2 [1] == -8.0);
  assertTrue (column2 [2] == 0.0);
  assertTrue (column2 [3] == 99.0);

}  // testSimple4x3Matrix
//-------------------------------------------------------------------------
public void testSimple4x3MatrixToString () throws Exception
{
  System.out.println ("testSimple4x3MatrixToString");

  DataMatrixReader reader = new DataMatrixFileReader ("file://", "simpleMatrix.txt");
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  String actualMatrixAsString = matrix.toString ();
  String expected = "gene	cond0	cond1	cond2\n" + 
                    "a	12.2	13.8	4.0\n" +
                    "b	-1.2	-8.0	-32.3333\n" +
                    "c	0.0	0.0	0.0\n" +
                    "d	999.0	99.0	9.0\n";
  // System.out.println ("actual\n" + actualMatrixAsString);

  assertTrue (expected.equals (actualMatrixAsString));

}  // testSimple4x3Matrix
//-------------------------------------------------------------------------
public void testCreateExplicitly () throws Exception
{
  System.out.println ("testCreateExplicitly");

  DataMatrix matrix = new DataMatrix ();
  int dataRows = 3;
  int dataColumns = 4;
  String [] columnTitles = {"one", "two", "three", "four"};
  String [] rowTitles = {"a", "b", "c"};
  matrix.setSize (3, 4);
  matrix.setRowTitlesTitle ("GENE");
  matrix.setColumnTitles (columnTitles);
  matrix.setRowTitles (rowTitles);

  for (int r=0; r < dataRows; r ++)
    for (int c=0; c < dataColumns; c++)
      matrix.set (r, c, (r * 10.0) + c * 10.0);


  assertTrue (matrix.getColumnCount () == dataColumns);
  assertTrue (matrix.getRowCount () == dataRows);

  assertTrue (Arrays.equals (matrix.getColumnTitles (), columnTitles));
  assertTrue (Arrays.equals (matrix.getRowTitles (), rowTitles));


}  // testCreateExplicitly
//-------------------------------------------------------------------------
public void testGetNames () throws Exception
{
  System.out.println ("testGetNames");

  String filename = "/users/pshannon/cy2/csplugins/isb/pshannon/dataMatrix/unitTests/matrix.icat";
  DataMatrixReader reader = new DataMatrixFileReader ("file://", filename);
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  String fullName = matrix.getName ();
  String shortName = matrix.getShortName ();
  String expected = "file:///users/pshannon/cy2/csplugins/isb/pshannon/dataMatrix/unitTests/matrix.icat";
  assertTrue (fullName.equals (expected));
  assertTrue (shortName.equals ("matrix.icat"));

}  // testGetNames
//-------------------------------------------------------------------------
public void testSetName () throws Exception
{
  System.out.println ("testSetNames");

  String filename = "/users/pshannon/cy2/csplugins/isb/pshannon/dataMatrix/unitTests/matrix.icat";
  DataMatrixReader reader = new DataMatrixFileReader ("file://", filename);
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  String fullName = matrix.getName ();
  String shortName = matrix.getShortName ();
  String expected = "file:///users/pshannon/cy2/csplugins/isb/pshannon/dataMatrix/unitTests/matrix.icat";
  assertTrue (fullName.equals (expected));
  assertTrue (shortName.equals ("matrix.icat"));

  String newName = "abracadabra"; 
  matrix.setName (newName);
  assertTrue (matrix.getName().equals (newName));
  assertTrue (matrix.getShortName().equals (newName));

}  // testGetNames
//-------------------------------------------------------------------------
public void testAddMatrix () throws Exception
{
  System.out.println ("testAddMatrix");

}
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (DataMatrixTest.class));

}// main
//------------------------------------------------------------------------------
} // DataMatrixTest
