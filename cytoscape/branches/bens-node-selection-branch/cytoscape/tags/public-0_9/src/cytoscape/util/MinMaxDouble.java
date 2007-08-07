// MinMaxDouble
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.util;
//--------------------------------------------------------------------------------------
/**
 *  find the min and max of a 1- or 2-d aarray of doubles.
 */
public class MinMaxDouble {
  double min = new Double (Double.MAX_VALUE).doubleValue ();
  double max = new Double (Double.MIN_VALUE).doubleValue ();

//--------------------------------------------------------------------------------------
public MinMaxDouble (double [] array) 
{
  for (int i=0; i < array.length; i++) {
    double val = array [i];
    if (val > max) max = val;
    if (val < min) min = val;
    } // for i

} // ctor
//--------------------------------------------------------------------------------------
public MinMaxDouble (double [][] array) 
{
  for (int i=0; i < array.length; i++)
    for (int j=0; j < array [0].length; j++) {
      double val = array [i][j];
      if (val > max) max = val;
      if (val < min) min = val;
      } // for j

} // ctor
//--------------------------------------------------------------------------------------
public double getMin () {return min;}
public double getMax () {return max;}
public String toString () {return min + " -> " + max;}
//--------------------------------------------------------------------------------------
} // class MinMaxDouble
