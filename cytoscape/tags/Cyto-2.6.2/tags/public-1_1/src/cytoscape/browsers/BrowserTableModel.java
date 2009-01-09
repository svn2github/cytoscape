// BrowserTableMode

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

//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.browsers;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.table.*;
import java.awt.BorderLayout;


import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.lang.reflect.Array;

import y.base.*;
import cytoscape.GraphObjAttributes;
//---------------------------------------------------------------------------------------
public class BrowserTableModel extends AbstractTableModel {

  String [] columnNames;
  Object [][] data;
  int [] columnWidths = {40};  // missing values, which are possible only at
                               // the end of array, mean that default column widths
                               // will be used

  protected final int defaultColumnWidth = 100;
  protected int preferredTableWidth = defaultColumnWidth; // incremented below

//---------------------------------------------------------------------------------------
public BrowserTableModel (Object [] graphObjects,
                          GraphObjAttributes objAttributes,
                          String [] attributeNames) 
{
  int graphObjectCount = graphObjects.length;
  columnNames = new String [attributeNames.length + 1];
  columnNames [0] = "Name";
  for (int i=0; i < attributeNames.length; i++)
    columnNames [i+1] = attributeNames [i];

  int numberOfColumns = columnNames.length;
  int numberOfRows = calculateMaxRowsNeeded (graphObjects, objAttributes, attributeNames);
  data = new Object [numberOfRows][numberOfColumns];

  if (objAttributes != null) {
    for (int i=0; i < columnNames.length; i++) {
      preferredTableWidth += defaultColumnWidth;
      } // for i
    } // if objAttributes


    //-----------------------------------------------------------------
    // attributes are retrived by canonicalName; collect those first
    //-----------------------------------------------------------------
  String [] canonicalNames = new String [graphObjectCount];
  for (int i=0; i < graphObjectCount; i++) {
    canonicalNames [i] = objAttributes.getCanonicalName (graphObjects [i]);
    }

    //-----------------------------------------------------------------
    // now fill the data
    // todo (pshannon, 25 oct 2002): nasty special case below for when the
    // todo: 'attribute' canonicalName is requested.  this is not reliably
    // todo: an attribute (though perhaps we should institute that policy).
    // todo: so we look for that 'attribute' and assign the table cell outside
    // todo: of the normal table flow
    //-----------------------------------------------------------------
  if (objAttributes != null) {
    int currentRowBase = 0;
    for (int graphObject=0; graphObject < graphObjects.length; graphObject++) {
      int maxRowsUsedThisObject = 1;
      String canonicalName = objAttributes.getCanonicalName (graphObjects [graphObject]);
      String commonName = objAttributes.getStringValue ("commonName", canonicalName);
      if (commonName == null || commonName.length () == 0)
        commonName = canonicalName;
      data [currentRowBase][0] = commonName;
      for (int i=1; i < columnNames.length; i++) {
        if (columnNames [i].equals ("canonicalName"))
          data [currentRowBase][i] = canonicalName;
        else {
          Object [] attributeValuesThisObject = objAttributes.getArrayValues (columnNames [i], canonicalName);
          int attributeCount = attributeValuesThisObject.length;
          for (int a=0; a < attributeCount; a++)
            if (attributeValuesThisObject [a] != null) {
              data [currentRowBase + a][i] = attributeValuesThisObject [a];
              }
          if (attributeCount > maxRowsUsedThisObject)
            maxRowsUsedThisObject = attributeCount;
          } // else: not the special case of 'canonicalName'
        } // for i
      currentRowBase += maxRowsUsedThisObject;
      } // for object:  each attribute name
    } // if objAttributes


} // ObjectBrowserTableModel
//---------------------------------------------------------------------
protected int calculateMaxRowsNeeded (Object [] graphObjects,
                                      GraphObjAttributes objAttributes,
                                      String [] attributeNames) 
{
  int max = 0;
  for (int graphObject=0; graphObject < graphObjects.length; graphObject++) {
    int maxRowsUsedThisObject = 1;
    String canonicalName = objAttributes.getCanonicalName (graphObjects [graphObject]);
    for (int i=0; i < attributeNames.length; i++) {
      String attributeName = attributeNames [i];
      int attributeCount = objAttributes.getArrayValues (attributeName, canonicalName).length;
      if (attributeCount > maxRowsUsedThisObject)
         maxRowsUsedThisObject = attributeCount;
      } // for i
    max += maxRowsUsedThisObject;
    } // for object:  each attribute name

  return max;

} // calculateMaxRowsNeeded
//---------------------------------------------------------------------
public String getColumnName (int col) { return columnNames[col];}
public int getColumnCount () { return columnNames.length;}
public int getRowCount () { return data.length; }
public boolean isCellEditable (int row, int col) {return false;}
public Object getValueAt (int row, int col) {
  Object cellData = data [row][col];
  if (cellData != null && cellData.getClass().isArray () && Array.getLength (cellData) > 0) {
    StringBuffer sb = new StringBuffer ();
    Object element0 = Array.get (cellData, 0);
    int max = Array.getLength (cellData);
    for (int i=0; i < max; i++) {
      sb.append ((Array.get (cellData, i)).toString ());
      if (i < max - 1) sb.append (" | ");
      } // for i
    return sb.toString ();
    }
  return data [row][col];
}
//---------------------------------------------------------------------
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
//--------------------------------------------------------------------------------------
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
//--------------------------------------------------------------------------------------
} // class browserTableModel


