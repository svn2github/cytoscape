// NodeBrowserTableMode
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
public class NodeBrowserTableModel extends AbstractTableModel {

  String [] columnNames;
  Object [][] data;
  int [] columnWidths = {40};  // missing values, which are possible only at
                               // the end of array, mean that default column widths
                               // will be used

  protected final int defaultColumnWidth = 100;
  protected int preferredTableWidth = defaultColumnWidth; // incremented below

//---------------------------------------------------------------------------------------
public NodeBrowserTableModel (Node [] nodes,
                              GraphObjAttributes nodeAttributes,
                              String [] attributeNames) 
{
  int nodeCount = nodes.length;
  columnNames = new String [attributeNames.length + 1];
  columnNames [0] = "Name";
  for (int i=0; i < attributeNames.length; i++)
    columnNames [i+1] = attributeNames [i];

  int numberOfColumns = columnNames.length;
  int numberOfRows = calculateMaxRowsNeeded (nodes, nodeAttributes, attributeNames);
  data = new Object [numberOfRows][numberOfColumns];

  if (nodeAttributes != null) {
    for (int i=0; i < columnNames.length; i++) {
      preferredTableWidth += defaultColumnWidth;
      } // for i
    } // if nodeAttributes


    //-----------------------------------------------------------------
    // attributes are retrived by canonicalName; collect those first
    //-----------------------------------------------------------------
  String [] canonicalNames = new String [nodeCount];
  for (int i=0; i < nodeCount; i++) {
    canonicalNames [i] = nodeAttributes.getCanonicalName (nodes [i]);
    }

    //-----------------------------------------------------------------
    // now fill the data
    // todo (pshannon, 25 oct 2002): nasty special case below for when the
    // todo: 'attribute' canonicalName is requested.  this is not reliably
    // todo: an attribute (though perhaps we should institute that policy).
    // todo: so we look for that 'attribute' and assign the table cell outside
    // todo: of the normal table flow
    //-----------------------------------------------------------------
  if (nodeAttributes != null) {
    int currentRowBase = 0;
    for (int node=0; node < nodes.length; node++) {
      int maxRowsUsedThisNode = 1;
      String canonicalName = nodeAttributes.getCanonicalName (nodes [node]);
      String commonName = nodeAttributes.getStringValue ("commonName", canonicalName);
      if (commonName == null || commonName.length () == 0)
        commonName = canonicalName;
      data [currentRowBase][0] = commonName;
      for (int i=1; i < columnNames.length; i++) {
        if (columnNames [i].equals ("canonicalName"))
          data [currentRowBase][i] = canonicalName;
        else {
          Object [] attributeValuesThisNode = nodeAttributes.getArrayValues (columnNames [i], canonicalName);
          int attributeCount = attributeValuesThisNode.length;
          for (int a=0; a < attributeCount; a++)
            if (attributeValuesThisNode [a] != null) {
              data [currentRowBase + a][i] = attributeValuesThisNode [a];
              }
          if (attributeCount > maxRowsUsedThisNode)
            maxRowsUsedThisNode = attributeCount;
          } // else: not the special case of 'canonicalName'
        } // for i
      currentRowBase += maxRowsUsedThisNode;
      } // for node:  each attribute name
    } // if nodeAttributes


} // NodeBrowserTableModel
//---------------------------------------------------------------------
public NodeBrowserTableModel (Object [] graphObjs,
                              GraphObjAttributes attributes,
                              String [] attributeNames) 
{
  int objCount = graphObjs.length;
  columnNames = new String [attributeNames.length + 1];
  columnNames [0] = "canonical";
  for (int i=0; i < attributeNames.length; i++)
    columnNames [i+1] = attributeNames [i];

  int numberOfColumns = columnNames.length;
  int numberOfRows = calculateMaxRowsNeeded (graphObjs, attributes, attributeNames);
  data = new Object [numberOfRows][numberOfColumns];

  if (attributes != null) {
    for (int i=0; i < columnNames.length; i++) {
      preferredTableWidth += defaultColumnWidth;
      } // for i
    } // if attributes


    //-----------------------------------------------------------------
    // now fill the data
    //-----------------------------------------------------------------
  String [] canonicalNames = new String [objCount];
  for (int i=0; i < objCount; i++) {
    canonicalNames [i] = attributes.getCanonicalName (graphObjs [i]);
    data [i][0] = canonicalNames [i];
    }

  if (attributes != null) {
    int currentRowBase = 0;
    for (int obj=0; obj < graphObjs.length; obj++) {
      int maxRowsUsedThisObj = 1;
      String canonicalName = attributes.getCanonicalName (graphObjs [obj]);
      //System.out.println ("about to assign canonicalName " + canonicalName + " to row: " +
      //                    currentRowBase);
      data [currentRowBase][0] = canonicalName;
      for (int i=1; i < columnNames.length; i++) {
        Object [] attributeValuesThisObj = attributes.getArrayValues (columnNames [i], canonicalName);
        int attributeCount = attributeValuesThisObj.length;
        //System.out.println ("--- " + columnNames [i] + ", value count: " + attributeCount);
        for (int a=0; a < attributeCount; a++)
          if (attributeValuesThisObj [a] != null) {
            //System.out.println ("-- adding to data [" + (currentRowBase+a) + "][" + i + "]");
            data [currentRowBase + a][i] = attributeValuesThisObj [a];
            }
        if (attributeCount > maxRowsUsedThisObj)
          maxRowsUsedThisObj = attributeCount;
        } // for i
      currentRowBase += maxRowsUsedThisObj;
      } // for obj:  each attribute name
    } // if objAttributes


} // ctor
//---------------------------------------------------------------------
protected int calculateMaxRowsNeeded (Node [] nodes,
                                      GraphObjAttributes nodeAttributes,
                                      String [] attributeNames) 
{
  int max = 0;
  for (int node=0; node < nodes.length; node++) {
    int maxRowsUsedThisNode = 1;
    String canonicalName = nodeAttributes.getCanonicalName (nodes [node]);
    for (int i=0; i < attributeNames.length; i++) {
      String attributeName = attributeNames [i];
      int attributeCount = nodeAttributes.getArrayValues (attributeName, canonicalName).length;
      if (attributeCount > maxRowsUsedThisNode)
         maxRowsUsedThisNode = attributeCount;
      } // for i
    max += maxRowsUsedThisNode;
    } // for node:  each attribute name

  return max;

} // calculateMaxRowsNeeded
//---------------------------------------------------------------------
protected int calculateMaxRowsNeeded (Object [] graphObjs,
                                      GraphObjAttributes attributes,
                                      String [] attributeNames) 
{
  int max = 0;
  for (int obj=0; obj < graphObjs.length; obj++) {
    int maxRowsUsedThisObj = 1;
    String canonicalName = attributes.getCanonicalName (graphObjs [obj]);
    for (int i=0; i < attributeNames.length; i++) {
      String attributeName = attributeNames [i];
      int attributeCount = attributes.getArrayValues (attributeName, canonicalName).length;
      if (attributeCount > maxRowsUsedThisObj)
         maxRowsUsedThisObj = attributeCount;
      } // for i
    max += maxRowsUsedThisObj;
    } // for obj:  each attribute name

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
} // class NodebrowserTableModel
