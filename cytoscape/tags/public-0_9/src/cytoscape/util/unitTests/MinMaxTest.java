// MinMaxTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.util.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.util.MinMaxInt;
import cytoscape.util.MinMaxDouble;
//------------------------------------------------------------------------------
public class MinMaxTest extends TestCase {


//------------------------------------------------------------------------------
public MinMaxTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void testMinMaxDoubleVector () throws Exception
{ 
  System.out.println ("testMinMaxDoubleVector");
  double min = -888.88;
  double max = 123456.789;
  double [] input = {1.0, 23.2, 18.5, -100.232, max, min};
  MinMaxDouble minMax = new MinMaxDouble (input);
  assertTrue (minMax.getMin () == min);
  assertTrue (minMax.getMax () == max);

} // testMinMaxDoubleVector
//-------------------------------------------------------------------------
public void testMinMaxDoubleMatrix () throws Exception
{ 
  System.out.println ("testMinMaxDoubleMatrix");
  double min = -888.88;
  double max = 123456.789;
  double [][] input = {{1.0, 23.2, 18.5, -100.232, max,  min},
                       {8.7,  1.2,  1.2,    1.2,   1.2, -50.3},
                       {23.4, 2.1,  2.1,    2.1,   2.2,  0.0}};
  MinMaxDouble minMax = new MinMaxDouble (input);
  assertTrue (minMax.getMin () == min);
  assertTrue (minMax.getMax () == max);

} // testMinMaxDoubleMatrix
//-------------------------------------------------------------------------
public void testMinMaxIntVector () throws Exception
{ 
  System.out.println ("testMinMaxIntVector");
  int min = -888;
  int max = 123456;
  int [] input = {1, 232, 18, -100, max, min};

  MinMaxInt minMax = new MinMaxInt (input);
  assertTrue (minMax.getMin () == min);
  assertTrue (minMax.getMax () == max);

} // testMinMaxIntVector
//-------------------------------------------------------------------------
public void testMinMaxIntMatrix () throws Exception
{ 
  System.out.println ("testMinMaxIntMatrix");
  int min = -888;
  int max = 123456;
  int [][] input = {{1, 23, 18, -100, max,  min},
                    {8,  1,  1,    1,   1, -50},
                    {23, 2,  2,    2,   2,  0}};

  MinMaxInt minMax = new MinMaxInt (input);
  assertTrue (minMax.getMin () == min);
  assertTrue (minMax.getMax () == max);

} // testMinMaxIntMatrix
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (MinMaxTest.class));
}
//------------------------------------------------------------------------------
} // MinMaxTest
