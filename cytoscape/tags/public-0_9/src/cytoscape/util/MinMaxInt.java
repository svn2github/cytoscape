// MinMaxInt
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.util;
//--------------------------------------------------------------------------------------
/**
 *  find the min and max of a 1- or 2-d array of ints.
 */
public class MinMaxInt {
  int min = new Integer (Integer.MAX_VALUE).intValue ();
  int max = new Integer (Integer.MIN_VALUE).intValue ();

//--------------------------------------------------------------------------------------
public MinMaxInt (int [] array) 
{
  for (int i=0; i < array.length; i++) {
    int val = array [i];
    if (val > max) max = val;
    if (val < min) min = val;
    } // for i

} // ctor
//--------------------------------------------------------------------------------------
public MinMaxInt (int [][] array) 
{
  for (int i=0; i < array.length; i++)
    for (int j=0; j < array [0].length; j++) {
      int val = array [i][j];
      if (val > max) max = val;
      if (val < min) min = val;
      } // for i

} // ctor
//--------------------------------------------------------------------------------------
public int getMin () {return min;}
public int getMax () {return max;}
public String toString () {return min + " -> " + max;}
//--------------------------------------------------------------------------------------
} // class MinMaxInt
