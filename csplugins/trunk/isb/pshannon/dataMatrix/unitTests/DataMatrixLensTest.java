// DataMatrixLensTest.java
//------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.unitTests;
//------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import csplugins.isb.pshannon.dataMatrix.*;
//------------------------------------------------------------------------------
public class DataMatrixLensTest extends TestCase {

//------------------------------------------------------------------------------
public DataMatrixLensTest (String name) 
{
  super (name);
}

// TODO - use the setUp and tearDown methods. Most of the test methods have
// identical setup and teardown, and this would get rid of redundancy and 
// maybe catch some problems.

//------------------------------------------------------------------------------
public void setUp () throws Exception
{
  }
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
public void testColumnEnabling () throws Exception
{
  System.out.println ("testColumnEnabling");

  DataMatrixReader reader = new DataMatrixFileReader ("file://", "simpleMatrix.txt");
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  assertTrue (matrix.getColumnCount () == 3);

  DataMatrixLens lens = new DataMatrixLens (matrix);

  boolean [] columnState;

  columnState = lens.getColumnState ();
  assertTrue (columnState.length == 3);
  assertTrue (columnState [0] == true);
  assertTrue (columnState [1] == true);
  assertTrue (columnState [2] == true);
  assertTrue (lens.getColumnState (0) == true);
  assertTrue (lens.getColumnState (1) == true);
  assertTrue (lens.getColumnState (2) == true);

  lens.setColumnState (1, false);
  assertTrue (lens.getColumnState (1) == false);

  columnState = lens.getColumnState ();
  assertTrue (columnState [0] == true);
  assertTrue (columnState [1] == false);
  assertTrue (columnState [2] == true);

  lens.setColumnState (0, false);
  lens.setColumnState (2, false);
  assertTrue (columnState.length == 3);
  assertTrue (lens.getColumnState (0) == false);
  assertTrue (lens.getColumnState (1) == false);
  assertTrue (lens.getColumnState (2) == false);

  lens.clear ();
  
  assertTrue (columnState.length == 3);
  assertTrue (lens.getColumnState (0) == true);
  assertTrue (lens.getColumnState (1) == true);
  assertTrue (lens.getColumnState (2) == true);

}  // testColumnEnabling
//-------------------------------------------------------------------------
public void testColumnOrdering () throws Exception
{
  System.out.println ("testColumnOrdering");

  DataMatrixReader reader = new DataMatrixFileReader ("file://", "simpleMatrix.txt");
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  DataMatrixLens lens = new DataMatrixLens (matrix);

  assertTrue (lens.getEnabledColumnCount () == 3);

  for (int c=0; c < lens.getEnabledColumnCount (); c++)
    assertTrue (lens.getColumnOrder (c) == c);

    // go from   0,1,2   to  2,0,1
  lens.swapColumnOrder (0, 2);   // 2,1,0
  lens.swapColumnOrder (2, 1);   // 2,0,1

  assertTrue (lens.getColumnOrder (0) == 2);
  assertTrue (lens.getColumnOrder (1) == 0);
  assertTrue (lens.getColumnOrder (2) == 1);

}  // testColumnOrdering
//-------------------------------------------------------------------------
public void testGetDataWithOrderingAndEnabling_1 () throws Exception
{
  System.out.println ("testGetDataWithOrderingAndEnabling_1");

  DataMatrixReader reader = new DataMatrixFileReader ("file://", "simpleMatrix.txt");
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  DataMatrixLens lens = new DataMatrixLens (matrix);

  String actual = matrix.toString ();
  String expected = "gene	cond0	cond1	cond2\n" + 
                    "a	12.2	13.8	4.0\n" +
                    "b	-1.2	-8.0	-32.3333\n" +
                    "c	0.0	0.0	0.0\n" +
                    "d	999.0	99.0	9.0\n";
  assertTrue (expected.equals (actual));

    // go from   0,1,2   to  2,0,1
  lens.swapColumnOrder (0, 2); 

  assertTrue (lens.getColumnOrder (0) == 2);
  assertTrue (lens.getColumnOrder (1) == 1);
  assertTrue (lens.getColumnOrder (2) == 0);

  double [] result = lens.getFromAll (0);
  assertTrue (result.length == 3);
  assertTrue (result [0] == 4.0);
  assertTrue (result [1] == 13.8);
  assertTrue (result [2] == 12.2);

  lens.setColumnState (0, false); // this is the current column 0, with value 4.0
  result = lens.getFromAll (0);          // ought to be initial 1, 0
  assertTrue (result.length == 2);
  //System.out.println ("result [0]: " +  result [0]);
  //System.out.println ("result [1]: " +  result [1]);

  assertTrue (result [0] == 13.8);
  assertTrue (result [1] == 12.2);

  lens.setColumnState (0, true);
  lens.setColumnState (1, true);
  lens.setColumnState (2, true);

  // System.out.println ("-------- to string\n" + matrix.toString ());

}  // testGetDataWithOrderingAndEnabling_1
//-------------------------------------------------------------------------
/**
 *  swap once, disable two columns
 */
public void testGetDataWithOrderingAndEnabling_2 () throws Exception
{
  System.out.println ("testGetDataWithOrderingAndEnabling_2");

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

    // go from   0,1,2   to  2,0,1
  DataMatrixLens lens = new DataMatrixLens (matrix);
  lens.swapColumnOrder (0, 2); 

  assertTrue (lens.getColumnOrder (0) == 2);
  assertTrue (lens.getColumnOrder (1) == 1);
  assertTrue (lens.getColumnOrder (2) == 0);

  double [] result = lens.getFromAll (0);

  assertTrue (result [0] == 4.0);
  assertTrue (result [1] == 13.8);
  assertTrue (result [2] == 12.2);

  lens.setColumnState (0, true);  // the current column 0, original column 2: 13.8
  lens.setColumnState (1, false); // the current column 1, original column 0: 4.0
  result = lens.getFromAll (0);
  assertTrue (result [0] == 4.0);
  assertTrue (result [1] == 12.2);

}  // testGetDataWithOrderingAndEnabling_2
//------------------------------------------------------------------------------
/**
 *  swap twice, equivalent to moving the right-most column two places to the left
 */
public void testGetDataWithOrderingAndEnabling_3 () throws Exception
{
  System.out.println ("testGetDataWithOrderingAndEnabling_3");

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

    // go from   0,1,2   to  2,0,1
  DataMatrixLens lens = new DataMatrixLens (matrix);

  double [] row = lens.getFromAll (0);
  assertTrue (row [0] == 12.2);
  assertTrue (row [1] == 13.8);
  assertTrue (row [2] == 4.0);

  lens.swapColumnOrder (1,2); 
  assertTrue (lens.getColumnOrder (0) == 0);
  assertTrue (lens.getColumnOrder (1) == 2);
  assertTrue (lens.getColumnOrder (2) == 1);

  row = lens.getFromAll (0);
  assertTrue (row [0] == 12.2);
  assertTrue (row [1] == 4.0);
  assertTrue (row [2] == 13.8);

  lens.swapColumnOrder (0,1);
  assertTrue (lens.getColumnOrder (0) == 2);
  assertTrue (lens.getColumnOrder (1) == 0);
  assertTrue (lens.getColumnOrder (2) == 1);

  row = lens.getFromAll (0);
  assertTrue (row [0] == 4.0);
  assertTrue (row [1] == 12.2);
  assertTrue (row [2] == 13.8);


}  // testGetDataWithOrderingAndEnabling_3
//------------------------------------------------------------------------------
/**
 *  swap twice, disable one column
 */
public void testColumnTitleLensing () throws Exception
{
  System.out.println ("testColumnTitleLensing");

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

  DataMatrixLens lens = new DataMatrixLens (matrix);
  String [] titlesRaw = lens.getAllColumnTitles ();
  //System.out.println ("\n\n\n");
  //System.out.println ("----- titles raw");
  //for (int i=0; i < titlesRaw.length; i++)
   // System.out.println (titlesRaw [i]);

  String [] titles0 = lens.getFilteredColumnTitles ();
  // System.out.println ("\n----- titles 0");
  //for (int i=0; i < titles0.length; i++)
  //  System.out.println (titles0 [i]);

  //System.out.println ("titles 0 length: " + titles0.length);
  //for (int i=0; i < titles0.length; i++)
  //  System.out.println ("   titles0 [" + i + "]: " + titles0 [i]);

  // System.out.println ("\n\n\n");

  assertTrue (titles0.length == 3);
  assertTrue (titles0 [0].equals ("cond0"));
  assertTrue (titles0 [1].equals ("cond1"));
  assertTrue (titles0 [2].equals ("cond2"));


  lens.setColumnState (1, false);
  String [] titles1 = lens.getFilteredColumnTitles ();
  //System.out.println ("\n----- titles 1");
  //for (int i=0; i < titles1.length; i++)
  //  System.out.println (titles1 [i]);

  assertTrue (titles1.length == 2);
  assertTrue (titles1 [0].equals ("cond0"));
  assertTrue (titles1 [1].equals ("cond2"));

  lens.setColumnState (1, true);
  lens.swapColumnOrder (0,1);

  String [] titles2 = lens.getFilteredColumnTitles ();
  //System.out.println ("\n----- titles 2");
  //for (int i=0; i < titles2.length; i++)
  //  System.out.println (titles2 [i]);

  assertTrue (titles2.length == 3);
  assertTrue (titles2 [0].equals ("cond1"));
  assertTrue (titles2 [1].equals ("cond0"));
  assertTrue (titles2 [2].equals ("cond2"));


  lens.swapColumnOrder (1,2);

  String [] titles3 = lens.getFilteredColumnTitles ();
  //System.out.println ("\n----- titles 3");
  //for (int i=0; i < titles3.length; i++)
  //  System.out.println (titles3 [i]);

  assertTrue (titles3.length == 3);
  assertTrue (titles3 [0].equals ("cond1"));
  assertTrue (titles3 [1].equals ("cond2"));
  assertTrue (titles3 [2].equals ("cond0"));

  lens.setColumnState (1, false);  // should disable titles3 [2] "cond2"

  String [] titles4 = lens.getFilteredColumnTitles ();
  //System.out.println ("\n----- titles 4");
  //for (int i=0; i < titles4.length; i++)
  //  System.out.println (titles4 [i]);

  assertTrue (titles4.length == 2);
  assertTrue (titles4 [0].equals ("cond1"));
  assertTrue (titles4 [1].equals ("cond0"));

  String lensedMatrixAsString = lens.toString ();
  //System.out.println ("\n\n\n--------- yo!\n" + lensedMatrixAsString);
  String expectedM = "gene	cond1	cond0\n" +
                    "a	13.8	12.2\n" +
                    "b	-8.0	-1.2\n" +
                    "c	0.0	0.0\n" +
                    "d	99.0	999.0\n";
  assertTrue (expectedM.equals (lensedMatrixAsString));


}  // testGetDataWithOrderingAndEnabling_3
//------------------------------------------------------------------------------
//			NOW FOR SOME TESTS WITH USER SELECTION
public void testRowSelection_1 () throws Exception 
{
  System.out.println ("testRowSelection_1");

  DataMatrixReader reader = new DataMatrixFileReader ("file://", "simpleMatrix.txt");
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix matrix = matrices [0];
  DataMatrixLens lens = new DataMatrixLens(matrix);
  lens.setSelectedRows (new int [0]);
  assertTrue(lens.getSelectedRowCount() == 0);
  
  lens.setSelectedRows(new int[]{3,1});
  assertTrue(lens.getSelectedRowCount() == 2);
  assertFalse(lens.getSelectedRowCount() == lens.getRawRowCount());
  
  assertTrue(lens.getFromSelected(0,2) == 9.0);
  
  assertTrue(lens.getEnabledColumnCount() == 3);
  
  assertTrue(lens.getEnabledColumnCount() == 
    lens.getFilteredColumnTitles().length);
  
  assertTrue(lens.getFilteredColumnTitles().length == 3);
  
  lens.setColumnState(1, false);

  assertTrue(lens.getEnabledColumnCount() == 2);

  assertTrue(lens.getFilteredColumnTitles().length == 2);
  
  
  assertTrue(lens.getFromSelected(0,1) == 9.0);
  
  String[] selectedRowTitles = lens.getSelectedRowTitles();
  assertTrue(selectedRowTitles.length == lens.getSelectedRowCount());
  
  assertTrue(selectedRowTitles[1].equals("b"));
  assertTrue(selectedRowTitles[0].equals("d"));
  

  //System.out.println("expected results:");

  String expectedM = "gene	cond0	cond2\n" +
					"d	999.0	9.0\n"+
					"b	-1.2	-32.3333\n";
  //System.out.println(expectedM);


  //System.out.println("toString(false) results:");
  //System.out.println(lens.toString(false));
  
  assertTrue(lens.toString(false).equals(expectedM));
  
  
} // testRowSelection_1

//------------------------------------------------------------------------------
/**
 *  test the matrices returned at each of these 3 steps
 *
 *    - get an unmodified view
 *    - select just rows 1 and 2 (implicitly obscuring rows 0 and 3)
 *    - disable first data column
 */
public void testGetSelectedSubMatrix () throws Exception
{
  System.out.println ("testGetSelectedSubMatrix");

  DataMatrixReader reader = new DataMatrixFileReader ("file://", "simpleMatrix.txt");
  reader.read ();
  DataMatrix [] matrices = reader.get ();
  assertTrue (matrices.length == 1);
  DataMatrix original = matrices [0];
  String actualMatrixAsString = original.toString ();
  String expected = "gene	cond0	cond1	cond2\n" + 
                    "a	12.2	13.8	4.0\n" +
                    "b	-1.2	-8.0	-32.3333\n" +
                    "c	0.0	0.0	0.0\n" +
                    "d	999.0	99.0	9.0\n";

  DataMatrixLens lens = new DataMatrixLens (original);
  lens.selectAllRows ();
  DataMatrix unchanged = lens.getSelectedSubMatrix ();
  //System.out.println ("--- unchanged\n" + unchanged.toString ());
  assertTrue (original.equals (unchanged));

  lens.setSelectedRows (new int [] {1,2});
  DataMatrix m2 = lens.getSelectedSubMatrix ();
  assertTrue (Arrays.equals (m2.getRowTitles (), new String [] {"b", "c"}));
  assertTrue (Arrays.equals (m2.getColumnTitles (), 
                             new String [] {"cond0", "cond1", "cond2"}));
  assertTrue (Arrays.equals (m2.get (0), new double [] {-1.2, -8.0, -32.3333}));
  assertTrue (Arrays.equals (m2.get (1), new double [] {0.0, 0.0, 0.0}));

  lens.setColumnState (0, false);
  DataMatrix m3 = lens.getSelectedSubMatrix ();
  assertTrue (Arrays.equals (m3.getColumnTitles (), 
                             new String [] {"cond1", "cond2"}));
  assertTrue (Arrays.equals (m3.get (0), new double [] {-8.0, -32.3333}));
  assertTrue (Arrays.equals (m3.get (1), new double [] {0.0, 0.0}));




}  // testGetSelectedSubMatrix
//------------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (DataMatrixLensTest.class));

}// main
//------------------------------------------------------------------------------
} // DataMatrixLensTest
