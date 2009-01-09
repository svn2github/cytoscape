// MinMaxTest.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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
import cytoscape.unitTests.AllTests;
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
  AllTests.standardOut ("testMinMaxDoubleVector");
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
  AllTests.standardOut ("testMinMaxDoubleMatrix");
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
  AllTests.standardOut ("testMinMaxIntVector");
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
  AllTests.standardOut ("testMinMaxIntMatrix");
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