// DataMatrix.java 
//--------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix;
//--------------------------------------------------------------------
import java.util.*;
import java.io.*;
//--------------------------------------------------------------------
public class DataMatrix {

  String rowTitlesTitle;
  String [] columnTitles = new String [0];
  String [] rowTitles = new String [0];
  double [][] data = null;
  String uri = "";
  String name = "";

//--------------------------------------------------------------------
public DataMatrix () throws Exception
{
  this.uri = "";
  name = uri;
}
//--------------------------------------------------------------------
public DataMatrix (String uri) throws Exception
{
  this.uri = uri.trim ();
  name = uri;
}
//--------------------------------------------------------------------
public String getShortName ()
{
  String [] tokens = name.split ("/");
  int lastToken = tokens.length - 1;
  if (lastToken < 0)
    return name;
  else
    return tokens [lastToken];
}
//--------------------------------------------------------------------
public void setName (String newValue)
{
   name = newValue;
}
//--------------------------------------------------------------------
public String getName ()
{
   return name;
}
//--------------------------------------------------------------------
public void setSize (int rows, int columns)
{
  data = new double [rows][columns];

} // setSize
//--------------------------------------------------------------------
public void assign (double value)
{
  if (data == null)
    return;

  for (int r=0; r < data.length; r++)
    for (int c=0; c < data [0].length; c++)
      data [r][c] = value;
}
//--------------------------------------------------------------------
public void assign (int row, int column, double value)
{
  data [row][column] = value;  
}
//--------------------------------------------------------------------
public void set (int row, int column, double value)
{
   assign (row, column, value);
}
//--------------------------------------------------------------------
public void setColumnTitles (String [] newValues)
{
  columnTitles = newValues;
}
//--------------------------------------------------------------------
public void setRowTitles (String [] newValues)
{
   rowTitles = newValues;
}
//--------------------------------------------------------------------
public void setRowTitlesTitle (String newValue)
{
   rowTitlesTitle = newValue;
}
//--------------------------------------------------------------------
public int getRowCount ()
{
  return data.length;
}
//--------------------------------------------------------------------
/**
 * returns a count of all enabled columns
 */
public int getColumnCount ()
{
  // return data[0].length;
  return columnTitles.length;

}
//--------------------------------------------------------------------
public double get (int row, int column)
{
  return data [row][column];
}
//--------------------------------------------------------------------
/**
 * returns the original data, ignoring swapped and/or disabled columns.
 */
public double [] getUntransformed (int row)
{
  return data [row];
}
//--------------------------------------------------------------------
public double [] get (int row)
{
  return data [row];
}
//--------------------------------------------------------------------
public double [] get (String rowName)
{
  for (int i=0; i < rowTitles.length; i++) 
    if (rowTitles [i].equals (rowName))
      return data [i];

  throw new IllegalArgumentException ("no data for '" + rowName + "'");
}
//--------------------------------------------------------------------
public String [] getRowTitles ()
{
  return rowTitles;
}
//--------------------------------------------------------------------
public void setData (double[][] d) {
	data = d;
}
//--------------------------------------------------------------------
public double[][] getData() {
	return data;
}
//--------------------------------------------------------------------
public int getColumnNumber (String columnName)
{
  for (int c=0; c < getColumnCount (); c++) {
    if (columnTitles [c].equals (columnName)) 
      return c;   
    } // for
  throw new IllegalArgumentException ("no column named " + columnName);

}
//--------------------------------------------------------------------
public double [] getColumn (String columnName) 
{
  int columnNumber = getColumnNumber (columnName);
  double [] result = new double [getRowCount ()];
  for (int r=0; r < getRowCount (); r++)
    result [r] = get (r, columnNumber);

  return result;
  
}
//--------------------------------------------------------------------
public String [] getUnmaskedColumnTitles ()
{
  return columnTitles;
}
//--------------------------------------------------------------------
public String getRowTitlesTitle ()
{
  return rowTitlesTitle;
}
//--------------------------------------------------------------------
public String [] getColumnTitles ()
{
  return columnTitles;
}
//--------------------------------------------------------------------
public void add (DataMatrix newMatrix)
{
  System.out.println ("DataMatrix.add (DataMatrix)");
  double [][] originalData;  
  
}
//--------------------------------------------------------------------
public String toString ()
{
  StringBuffer sb = new StringBuffer ();
  int colMax = columnTitles.length;
  String [] columnTitles = getColumnTitles ();

  sb.append (rowTitlesTitle);
  for (int c=0; c < colMax; c++) {
    sb.append ("\t");
    sb.append (columnTitles [c]);
    }
  sb.append ("\n");

  int rowMax = rowTitles.length;
  for (int r=0; r < rowMax; r++) {
    double [] adjustedRow = get (r);
    sb.append (rowTitles [r]);
    for (int c=0; c < colMax; c++) {
      sb.append ("\t");
      sb.append (adjustedRow [c]);
      } // for c
    sb.append ("\n");
    } // for r

  return sb.toString ();

} // toString
//--------------------------------------------------------------------
public boolean equals (DataMatrix other)
{
  return (toString ().equals (other.toString ()));
}
//--------------------------------------------------------------------
} // class DataMatrix
