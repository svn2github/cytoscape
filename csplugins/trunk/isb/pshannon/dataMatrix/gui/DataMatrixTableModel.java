// DataMatrixTableModel
//-----------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui;
//-----------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.table.*;
import java.awt.BorderLayout;


import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.lang.reflect.Array;

import csplugins.isb.pshannon.dataMatrix.*;
//-----------------------------------------------------------------------------------
public class DataMatrixTableModel extends AbstractTableModel {

  String [] columnNames;

  Object [][] data;
  int [] columnWidths = {40};  // missing values, which are possible only at
                               // the end of array, mean that default column widths
                               // will be used

  protected final int defaultColumnWidth = 100;
  protected int preferredTableWidth = defaultColumnWidth; // incremented below

//-----------------------------------------------------------------------------------
public DataMatrixTableModel (DataMatrix matrix)
{
  int numberOfColumns = matrix.getColumnCount ();
  int numberOfRows = matrix.getRowCount ();
  columnNames = new String [numberOfColumns];

  for (int i=0; i < columnNames.length; i++)
    columnNames [i] = matrix.getColumnTitles() [i];

  data = new Object [numberOfRows][numberOfColumns];

  String [] rowNames = matrix.getRowTitles ();

  for (int r=0; r < numberOfRows; r++) {
    //data [r][0] = rowNames [r];
    for (int c=0; c < numberOfColumns; c++) {
      double d = matrix.get (r, c);
      data [r][c] = new Double (d);
      } // for c
    } // for r

} // ObjectDataMatrixTableModel
//-----------------------------------------------------------------------------------
public String getColumnName (int col) { return columnNames[col];}
public int getColumnCount () { return columnNames.length;}
public int getRowCount () { return data.length; }
public boolean isCellEditable (int row, int col) {return false;}
public Object getValueAt (int row, int col) {
  return data [row][col];
}
//-----------------------------------------------------------------------------------
public int getPreferredColumnWidth (int col) 
// '0' means: there is no preferred width, use the default
//  the columnWidths array can be incomplete. so if, for example,
//  only the first column has a specified width, then the array
//  need only contain one value.
{ 
  if (col >= columnWidths.length)
    return 0;
  else
    return columnWidths [col];
}
//-----------------------------------------------------------------------------------
public Class getColumnClass (int column) 
// though i do not understand the circumstances in which this method
// is called, trial and error has led me to see that -some- class
// must be returned, and that if the 0th row in the specified column
// is null, then returning the String Class seems to work okay.
{
   Object cellValue = getValueAt (0, column);
   if (cellValue == null) { 
     String s = new String ();
     return s.getClass ();
     }
   else
     return getValueAt (0, column).getClass();

} // getColumnClass
//-----------------------------------------------------------------------------------
} // class DataMatrixTableModel


