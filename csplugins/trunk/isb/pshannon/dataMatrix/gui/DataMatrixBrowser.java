//DataMatrixBrowser
//-----------------------------------------------------------------------------------
//$Revision$
//$Date$
//$Author$
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui;
//-----------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.jnlp.*;
import java.util.*;
import java.io.*;
import java.awt.datatransfer.*;


import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.Semantics;

import csplugins.isb.pshannon.dataMatrix.DataMatrix;
import csplugins.isb.pshannon.dataMatrix.DataMatrixLens;
import csplugins.isb.pshannon.dataMatrix.gui.actions.*;
//-----------------------------------------------------------------------------------
public class DataMatrixBrowser extends JPanel implements ClipboardOwner {

protected int preferredTableWidth = 600;
protected int preferredTableHeight = 100;
protected JTabbedPane tabbedPane;
protected File currentDirectory;

ArrayList matrixList;
ArrayList lensList;
ArrayList nodeAttributeNameTextFieldList;
ArrayList selectionCountTextFieldList;
int matrixCount = 0;

protected ArrayList tableList = new ArrayList ();
// protected int currentTabAndTableIndex = 0;
protected boolean [][] columnState;   // tracks which columns in each table are enabled
protected int [][] columnOrder;       // tracks the (user-adjustable) order of columns

protected ArrayList columnCheckBoxHeaderMaps = new ArrayList ();
protected JPanel buttonPanel;  

  // objects needed to construct filtered matrices
private Vector selectedRows = new Vector();
private Vector colOrder = new Vector();
private Vector enabled = new Vector();
private Vector colMap = new Vector();

private ArrayList listeners = new ArrayList();

protected boolean updateSelectionsToCytoscapeWindow = true;
protected JToolBar toolbar;
protected SelectionPlotter plotter = null;


//-----------------------------------------------------------------------------------
public DataMatrixBrowser (DataMatrix [] matrices) 
  throws Exception
{
super ();
currentDirectory = new File (System.getProperty ("user.dir"));

this.matrixList = new ArrayList ();
this.lensList = new ArrayList ();

setLayout (new BorderLayout ());
toolbar = new JToolBar ();
add (toolbar, BorderLayout.NORTH);

add (createGui (matrices), BorderLayout.CENTER);


addActions ();

setVisible (true);

} // ctor

//-----------------------------------------------------------------------------------

public synchronized void addBrowserListener(DataMatrixBrowserListener l) {
	listeners.add(l);
}

public synchronized void removeBrowserListener(DataMatrixBrowserListener l) {
	listeners.remove(l);
}



//-----------------------------------------------------------------------------------

protected void addActions ()
{
plotter = new SelectionPlotter (this);
CreateNewMatrixFromSelection creator = new CreateNewMatrixFromSelection (this);
RunMovie movieController = new RunMovie (this);
ExportMatrix exporter = new ExportMatrix (this);
FindCorrelations correlator = new FindCorrelations (this);
GetSelectionsFromNetwork getter = new GetSelectionsFromNetwork (this);
PyConsoleLauncher pyConsole = new PyConsoleLauncher (this);

}
//-----------------------------------------------------------------------------------
public void addActionToToolbar (AbstractAction action)
{
toolbar.add (action);
}
//-----------------------------------------------------------------------------------
public boolean hasSelectedRows ()
{
ListSelectionModel lsm = getCurrentTable().getSelectionModel ();
return (!lsm.isSelectionEmpty ());
}
//-----------------------------------------------------------------------------------
public File getCurrentDirectory ()
{
return currentDirectory;
}
//-----------------------------------------------------------------------------------
public void setCurrentDirectory (File newValue)
{
currentDirectory = newValue;
}
//-----------------------------------------------------------------------------------
public DataMatrixLens [] getAllLenses ()
{
return (DataMatrixLens []) lensList.toArray (new DataMatrixLens [0]);
}
//-----------------------------------------------------------------------------------
public DataMatrixLens getCurrentLens ()
{
return getLens (getCurrentTabAndTableIndex ());
}
//-----------------------------------------------------------------------------------
public JTable getCurrentTable ()
{
return getTable (getCurrentTabAndTableIndex ());
}
//-----------------------------------------------------------------------------------
public DataMatrixLens getLens (int index)
{
return (DataMatrixLens) lensList.get (index);
}
//-----------------------------------------------------------------------------------
public JTable getTable (int index)
{
return (JTable) tableList.get (index);
}
//-----------------------------------------------------------------------------------
public JTable [] getAllTables ()
{
return (JTable []) tableList.toArray (new JTable [0]);
}
//-----------------------------------------------------------------------------------
public String [] getMatrixAliases ()
{
int max = matrixList.size ();
String [] result = new String [max];
for (int i=0; i < max; i++)
  result [i] = ((JTextField) nodeAttributeNameTextFieldList.get (i)).getText ();

return result;
}
//-----------------------------------------------------------------------------------
private int [] calculateColumnWidths (JTable table, DataMatrix matrix)
//column titles can be significantly longer than the column contents; this method
//calculates a width based upon the title string's length, inflates it a bit
//and then (if the value is larger than the default) returns the calculated value.
//
//this method leaves a lot to be desired.  the flaws include:  (pshannon, 2004/01/22)
//  1) i don't understand why the FontMetrics.stringWidth (s) result needs inflation
//  2) the font on the table should be a run-time user option, and these column
//     widths will need to be recalculated
{
String [] columnTitles = matrix.getColumnTitles ();
int columnCount = columnTitles.length;
int defaultWidth = 100;
int [] result = new int [columnCount];

FontMetrics fontMetrics = table.getFontMetrics (table.getFont ());

for (int i=0; i < columnCount; i++) {
  String s = columnTitles [i];
  int calculatedWidth = fontMetrics.stringWidth (s);
  int repairedWidth = (int) (calculatedWidth * 1.4);
  result [i] = (repairedWidth > defaultWidth) ? repairedWidth : defaultWidth;
  } // for i

return result;

} // calculateColumnWidth
//-----------------------------------------------------------------------------------
JTabbedPane createGui (DataMatrix [] matrices)
{
tabbedPane = new JTabbedPane ();

class PaneListener implements ChangeListener {
  public void stateChanged (ChangeEvent e) {
    if (tableList == null) return;
    int currentIndex = getCurrentTabAndTableIndex ();
    if (currentIndex < 0) return;
      JTable table = (JTable) tableList.get (currentIndex);
      if (null != table.getSelectedRows() && (!(table.getSelectedRows().length < 1)))
        resetPlotter();                 
     } // stateChaged
  } // inner clsas PaneListener

tabbedPane.addChangeListener (new PaneListener());
nodeAttributeNameTextFieldList = new ArrayList ();
selectionCountTextFieldList = new ArrayList ();

for (int i=0; i < matrices.length; i++) {
  addMatrixToGui (matrices [i]);
 } // for i

return tabbedPane;

} // createGui
//--------------------------------------------------------------------------
public void addMatrixToGui (DataMatrix matrix)
{
matrixList.add (matrix);
try {
  lensList.add (new DataMatrixLens (matrix));
  }
catch (Exception e0) {
  e0.printStackTrace ();
  }
DataMatrixTableModel model = new DataMatrixTableModel (matrix);
HashMap columnCheckBoxHeaderMap = new HashMap ();
columnCheckBoxHeaderMaps.add (columnCheckBoxHeaderMap);
JTable table = new JTable (model);
table.setShowGrid (true);
int [] suggestedColumnWidths = calculateColumnWidths (table, matrix);
JList rowHeader = new JList (matrix.getRowTitles ());
rowHeader.setFixedCellWidth (100);
rowHeader.setFixedCellHeight(table.getRowHeight());
rowHeader.setCellRenderer(new RowHeaderRenderer(table));

tableList.add (table);
table.getColumnModel().addColumnModelListener (new TableColumnMovedListener ());

Enumeration enumeration = table.getColumnModel().getColumns();
int columnCount = 0;

while (enumeration.hasMoreElements ()) {
  TableColumn aColumn = (TableColumn) enumeration.nextElement ();
  aColumn.setPreferredWidth (suggestedColumnWidths [columnCount]);
  CheckBoxHeader checkBoxHeader = new CheckBoxHeader (new TableHeaderCheckboxListener ());
  String columnTitle = matrix.getColumnTitles()[columnCount];
  columnCheckBoxHeaderMap.put (columnTitle, checkBoxHeader);
  aColumn.setHeaderRenderer (checkBoxHeader);
  columnCount++;
  }

ListSelectionModel selectionModel = table.getSelectionModel ();
selectionModel.addListSelectionListener (new TableSelectionListener (table, matrix));
table.setSelectionModel (selectionModel);

table.setPreferredScrollableViewportSize (new Dimension (preferredTableWidth, preferredTableHeight));
JScrollPane scrollPane = new JScrollPane (table);
scrollPane.setRowHeaderView (rowHeader);
scrollPane.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); 
table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 

JPanel tabPanel = new JPanel ();
tabPanel.setLayout (new BorderLayout ());

String matrixName = matrix.getShortName ();
String correspondingNodeAttributeName = "";
int pos = matrixName.lastIndexOf (".");
if (pos > 0 && (pos < matrixName.length () - 1))
  correspondingNodeAttributeName = matrixName.substring (pos+1);
else
  correspondingNodeAttributeName = matrixName;

if (matrixName.length () == 0)
  matrixName = (new Integer (matrixList.size ())).toString ();
JPanel topPanel = new JPanel ();
topPanel.setBorder (BorderFactory.createEmptyBorder (5, 5, 5, 5)); 

JPanel nodeAttributeNamePanel = new JPanel ();
nodeAttributeNamePanel.add (new JLabel ("Node attribute name: "));
JTextField nodeAttributeNameTextField = new JTextField (correspondingNodeAttributeName, 10);
nodeAttributeNameTextFieldList.add (nodeAttributeNameTextField);
nodeAttributeNamePanel.add (nodeAttributeNameTextField);

JTextField selectionCountTextField = new JTextField ("0", 5);
selectionCountTextFieldList.add (selectionCountTextField);
nodeAttributeNamePanel.add (new JLabel ("Selected Rows: "));
nodeAttributeNamePanel.add (selectionCountTextField);
String buttonLabel = new String ("<html><font=+2><b>X</b></font></html>");
//JButton closeButton = new JButton ("X");
JButton closeButton = new JButton (buttonLabel);
closeButton.addActionListener (new CloseTabAction (tabPanel));
nodeAttributeNamePanel.add (closeButton);

topPanel.add (nodeAttributeNamePanel);

tabPanel.add (topPanel, BorderLayout.NORTH);
tabPanel.add (scrollPane,  BorderLayout.CENTER);
tabbedPane.add (matrixName, tabPanel);

} // addMatrixToGui
//---------------------------------------------------------------------------------------------------
public class CloseTabAction extends AbstractAction 
{
JPanel tab;

CloseTabAction (JPanel tab) {
  super (""); this.tab = tab;
  }
public void actionPerformed (ActionEvent e) {
    // assume that delete has been called on the topmost -- the visible --
    // tab.  this will always be true, since this action method is called
    // when the user presses the delete button on the visible tab.
  int currentIndex = getCurrentTabAndTableIndex ();
  matrixList.remove (currentIndex);
  lensList.remove (currentIndex);
  nodeAttributeNameTextFieldList.remove (currentIndex);
  selectionCountTextFieldList.remove (currentIndex);
  tableList.remove (currentIndex);
  tabbedPane.remove (tab);    
  }

} // CloseTabAction
//---------------------------------------------------------------------------------------------------
class TableHeaderCheckboxListener implements ItemListener {

public void itemStateChanged (ItemEvent e) {
  int currentTabAndTableIndex = getCurrentTabAndTableIndex ();
  if (currentTabAndTableIndex < 0)
    return;
  Object source = e.getSource ();
  String text;
  if (source instanceof AbstractButton)
    text = ((AbstractButton) source).getText();
  else
    return;

  boolean state = e.getStateChange() == ItemEvent.SELECTED;
  int column = ((CheckBoxHeader) (e.getItem())).getColumn();
  ((DataMatrixLens) lensList.get (currentTabAndTableIndex)).setColumnState (column, state);
  //System.out.println ("-- just selected column " + column);
  //System.out.println (((DataMatrixLens) lensList.get (currentTabAndTableIndex)).toString ());
   resetPlotter ();
  
  } // itemStateChanged

} // inner class TableHeaderCheckboxListener
//--------------------------------------------------------------------------
class TableColumnMovedListener implements TableColumnModelListener {

public void columnMoved (TableColumnModelEvent e) {
  int currentTabAndTableIndex = getCurrentTabAndTableIndex ();
  int from = e.getFromIndex ();
  int to = e.getToIndex ();
  if (from == to) return;
  ((DataMatrixLens) lensList.get (currentTabAndTableIndex)).swapColumnOrder (from, to);
  resetPlotter();     
  } // columnMoved

public void columnAdded (TableColumnModelEvent e) {;}
public void columnRemoved (TableColumnModelEvent e) {;}
public void columnMarginChanged (ChangeEvent e) {;}
public void columnSelectionChanged (ListSelectionEvent e) {;}

}  // inner class TableColumnMovedListener
//--------------------------------------------------------------------------
public class SaveTableAction extends AbstractAction {

private JFrame frame;

SaveTableAction (JFrame frame) {super (""); this.frame = frame;}

public void actionPerformed (ActionEvent e) {
   int currentTabAndTableIndex = getCurrentTabAndTableIndex ();
        // if the customizer tab is selected, do nothing
        if (currentTabAndTableIndex == 0) return;

        // get currently selected table and table size
        JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        JTable table = (JTable) scrollPane.getViewport().getView();
        int rowCount = table.getRowCount();
        int columnCount = table.getColumnCount();

        // query for filename
        JFileChooser chooser = new JFileChooser (currentDirectory);
        if (chooser.showSaveDialog (DataMatrixBrowser.this) == chooser.APPROVE_OPTION) {
        String name = chooser.getSelectedFile ().toString ();
        currentDirectory = chooser.getCurrentDirectory();
        File file = new File(name);
        try {
                FileWriter fout = new FileWriter(file);
                for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                        Class classType = table.getColumnClass(col);
                        Object data = table.getValueAt(row,col);
                        fout.write(data + "\t");
                        //System.out.print(data + "\t");
                }
                fout.write("\n");
                //System.out.println();
                }
                fout.close();
        } catch (IOException exc) {
                JOptionPane.showMessageDialog(null, exc.toString(),
                                              "Error Writing to \"" + file.getName()+"\"",
                                              JOptionPane.ERROR_MESSAGE);
        }
        }
} // actionPerformed
} // SaveTableAction
//---------------------------------------------------------------------------------
public class DismissAction extends AbstractAction {

private JDialog dialog;

DismissAction (JDialog dialog) {super (""); this.dialog = dialog;}

public void actionPerformed (ActionEvent e) {
      dialog.dispose ();
      }

} // DismissAction
//---------------------------------------------------------------------------------
public boolean hasCytoscapeParent ()
{
return Cytoscape.getNetworkSet().size () > 0;
}
//----------------------------------------------------------------------------
private String extendMatrixWithNodeAttributes (String matrixAsString)
{
if (hasCytoscapeParent ())
  return matrixAsString;

// GraphObjAttributes nodeAttributes = cytoscapeWindow.getNodeAttributes ();
String [] lines = matrixAsString.split ("\n");
// String [] attributeNames = nodeAttributes.getAttributeNames ();
String [] attributeNames = Cytoscape.getCurrentNetwork().getNodeAttributesList();
StringBuffer sb = new StringBuffer ();
sb.append (lines [0]);
for (int i=0; i < attributeNames.length; i++) {
  sb.append ("\t");
  sb.append (attributeNames [i]);
  }

StringBuffer result = new StringBuffer ();
result.append (sb.toString ());
result.append ("\n");

for (int i=1; i < lines.length; i++) {
  String [] tokens = lines [i].split ("\t");
  String nodeName = tokens [0];
  StringBuffer lineSB = new StringBuffer ();
  lineSB.append (lines [i]);
  lineSB.append ("\t");
  for (int a=0; a < attributeNames.length; a++) {
    String attribute = attributeNames [a];
    // Object obj = nodeAttributes.getValue (attributeNames [a], nodeName);
    CyNode node = Cytoscape.getCyNode (nodeName, false);
    if (node == null) 
      continue;
    Object obj = Cytoscape.getCurrentNetwork().getNodeAttributeValue (node, attributeNames [a]);
    // Object obj = nodeAttributes.getValue (attributeNames [a], nodeName);
    if (obj != null) {
      String value = obj.toString ();
      lineSB.append (value);
      } // if ! null
    if (a < (attributeNames.length - 1)) 
      lineSB.append ("\t");
    } // for a
  lineSB.append ("\n");
  result.append (lineSB.toString ());
  } // for i

return result.toString ();

} // extendMatrixWithNodeAttributes
//---------------------------------------------------------------------------------
private void setPreferredColumnWidths (JTable table)
{
TableColumnModel columnModel = table.getColumnModel ();
int numberOfColumns = table.getModel().getColumnCount ();

columnModel.getColumn (0).setPreferredWidth (80);
//columnModel.getColumn (1).setPreferredWidth (450);

} // setPreferredColumnWidths
//----------------------------------------------------------------------
private void placeInCenter ()
{
GraphicsConfiguration gc = getGraphicsConfiguration ();
int screenHeight = (int) gc.getBounds().getHeight ();
int screenWidth = (int) gc.getBounds().getWidth ();
int windowWidth = getWidth ();
int windowHeight = getHeight ();
setLocation ((screenWidth-windowWidth)/2, (screenHeight-windowHeight)/2);

} // placeInCenter
//----------------------------------------------------------------------
/***********************/
private synchronized void resetPlotter() 
{
if (null != plotter) {
  JTable table = (JTable)tableList.get(getCurrentTabAndTableIndex());
  DataMatrixLens lens = (DataMatrixLens) lensList.get (getCurrentTabAndTableIndex ());
  lens.setSelectedRows (table.getSelectedRows());
  // fire event to listeners
  DataMatrixBrowserEvent evt = new DataMatrixBrowserEvent(lens);
  Iterator it = listeners.iterator();
		  while( it.hasNext() ) {
			  ( (DataMatrixBrowserListener) it.next() ).browserSelectionChanged( evt );
		  }

  
  }

} // resetPlotter
/***********************/
//----------------------------------------------------------------------------
protected void syncDataMatrixLensToBrowser () 
{
JTable table = (JTable) tableList.get (getCurrentTabAndTableIndex());
DataMatrixLens lens = (DataMatrixLens) lensList.get (getCurrentTabAndTableIndex ());
//System.out.println ("sync, selected rows in table: " + table.getSelectedRows().length);
lens.setSelectedRows (table.getSelectedRows ());
//System.out.println ("sync, selected rows in lens: " + lens.getSelectedRowCount ());

} // syncDataMatrixLensToBrowser
//----------------------------------------------------------------------------
public void setUpdateSelectionsToCytoscape (boolean newValue)
{
updateSelectionsToCytoscapeWindow = newValue;
}
//----------------------------------------------------------------------------
class TableSelectionListener implements ListSelectionListener {
JTable table;
DataMatrix matrix;
TableSelectionListener (JTable table, DataMatrix matrix) {
      this.table = table;
      this.matrix = matrix;
      }
public void valueChanged (ListSelectionEvent e) {
  int currentTabAndTableIndex = getCurrentTabAndTableIndex ();
  if (e.getValueIsAdjusting ()) return;
  // System.out.println ("table selection changed");
  //if (!updateSelectionsToCytoscapeWindow)
  //  return;
      
  ListSelectionModel lsm = (ListSelectionModel) e.getSource();
  ArrayList nodeNameList = new ArrayList ();
  int numberOfSelectedRows = 0;
  if (!lsm.isSelectionEmpty ()) {
    int minIndex = lsm.getMinSelectionIndex();
    int maxIndex = lsm.getMaxSelectionIndex();
    for (int row = minIndex; row <= maxIndex; row++) {
      if (lsm.isSelectedIndex (row)) {
        //String nodeName = ((DataMatrix) matrixList.get (currentTabAndTableIndex)).getRowTitles ()[row];
        String nodeName = matrix.getRowTitles ()[row];
        //System.out.println ("selected: " + nodeName);
        nodeNameList.add (nodeName);
        numberOfSelectedRows++;
        } // if selected
      }  // for row
    } // if selection !empty
    if (hasCytoscapeParent ())
      selectNodesByName ((String []) nodeNameList.toArray (new String [0]));
    String countString = (new Integer (numberOfSelectedRows)).toString ();
    //System.out.println ("countString: " + countString);
    ((JTextField) selectionCountTextFieldList.get (currentTabAndTableIndex)).setText (countString);
    syncDataMatrixLensToBrowser ();
    resetPlotter();
  } // valueChanged

} // TableSelectionListener
//----------------------------------------------------------------------------
public void selectNodesByName (String [] nameArray) 
{
ArrayList converted = new ArrayList (Arrays.asList (nameArray));
selectNodesByName (converted);
}
//----------------------------------------------------------------------------
public void selectNodesByName (ArrayList nameList) 
{
for (Iterator iter = nameList.iterator(); iter.hasNext(); ) {
  String name = (String)iter.next();
  //System.out.println ("selecting " + name);
  CyNode node = Cytoscape.getCyNode(name, false);
  if (node != null)
    Cytoscape.getCurrentNetwork().getFlagger().setFlagged(node, true);
  } // for iter

} // selectNodeByName
//----------------------------------------------------------------------------
public void enableColumnsByName (String [] columnNames)
{
JTable [] tables = (JTable []) tableList.toArray (new JTable [0]);
for (int tableIndex=0; tableIndex < tables.length; tableIndex++)
  enableColumnsByName (tableIndex, columnNames);
}
//----------------------------------------------------------------------------
protected void enableColumnsByName (int tableIndex, String [] enabledColumnNames)
{

HashMap columnCheckBoxHeaderMap = (HashMap) columnCheckBoxHeaderMaps.get (tableIndex);
JTable table = (JTable) tableList.get (tableIndex);
String [] allTitles = (String []) columnCheckBoxHeaderMap.keySet().toArray (new String [0]);

for (int i=0; i < allTitles.length; i++) {
  CheckBoxHeader cbh = (CheckBoxHeader) columnCheckBoxHeaderMap.get (allTitles [i]);
  cbh.setSelected (false);
  }

for (int i=0; i < enabledColumnNames.length; i++) {
  String name = enabledColumnNames [i];
  boolean recognized = columnCheckBoxHeaderMap.containsKey (name);
  if (recognized) {
    CheckBoxHeader cbh = (CheckBoxHeader) columnCheckBoxHeaderMap.get (name);
    cbh.setSelected (true);
    }
  }

table.getTableHeader().resizeAndRepaint();

} // enableColumnsByName
//----------------------------------------------------------------------------
public void disableAllColumnsInCurrentlyVisibleTable ()
{
int currentTabAndTableIndex = getCurrentTabAndTableIndex ();
HashMap columnCheckBoxHeaderMap = 
      (HashMap) columnCheckBoxHeaderMaps.get (currentTabAndTableIndex);
String [] allTitles = (String []) columnCheckBoxHeaderMap.keySet().toArray (new String [0]);

for (int i=0; i < allTitles.length; i++) {
  CheckBoxHeader cbh = (CheckBoxHeader) columnCheckBoxHeaderMap.get (allTitles [i]);
  cbh.setSelected (false);
  }

JTable table = (JTable) tableList.get (currentTabAndTableIndex);
table.getTableHeader().resizeAndRepaint();

} // disableAllColumnsInCurrentlyVisibleTable
//----------------------------------------------------------------------------
protected void enableColumnsByNameInCurrentlyVisibleTable (String [] enabledColumnNames)
{
HashMap columnCheckBoxHeaderMap = 
      (HashMap) columnCheckBoxHeaderMaps.get (getCurrentTabAndTableIndex ());

for (int i=0; i < enabledColumnNames.length; i++) {
  String name = enabledColumnNames [i];
  boolean recognized = columnCheckBoxHeaderMap.containsKey (name);
  if (recognized) {
    CheckBoxHeader cbh = (CheckBoxHeader) columnCheckBoxHeaderMap.get (name);
    cbh.setSelected (true);
    } // if recognized
  } // for i

JTable table = (JTable) tableList.get (getCurrentTabAndTableIndex ());
table.getTableHeader().resizeAndRepaint();

} // enableColumnsByNameInCurrentlyVisibleTable
//----------------------------------------------------------------------------
public void clearAllSelections ()
{
JTable [] tables = (JTable []) tableList.toArray (new JTable [0]);
for (int tableIndex=0; tableIndex < tables.length; tableIndex++)
  tables [tableIndex].clearSelection ();
syncDataMatrixLensToBrowser ();

} // clearAllSelections
//----------------------------------------------------------------------------
public void selectRowsByName (String [] nodeNames)
{
JTable [] tables = (JTable []) tableList.toArray (new JTable [0]);
for (int tableIndex=0; tableIndex < tables.length; tableIndex++) {
  selectRowsByName (tableIndex, nodeNames);
  }

} // selectRowsByName
//----------------------------------------------------------------------------
public void selectRowsByName (int tableIndex, String [] nodeNames)
{

JTable table = (JTable) tableList.get (tableIndex);
java.util.List listOfNamesToSelect = Arrays.asList (nodeNames);
String [] namesInTable = ((DataMatrix) matrixList.get (tableIndex)).getRowTitles ();
int selectionsInThisTable = 0;
for (int r=0; r < namesInTable.length; r++) {
  String tableRowName = namesInTable [r];
  boolean nameFoundBothInTableAndList = listOfNamesToSelect.contains (tableRowName);
  if (nameFoundBothInTableAndList) {
    table.getSelectionModel().addSelectionInterval (r, r);
    selectionsInThisTable++;
    } // if
  } // for r

String countString = (new Integer (selectionsInThisTable).toString ());
((JTextField) selectionCountTextFieldList.get (tableIndex)).setText (countString);

} // selectRowsByName
//----------------------------------------------------------------------------
protected boolean columnNamesIntersect (String [] candidateColumnNames)
{
HashMap columnCheckBoxHeaderMap = 
      (HashMap) columnCheckBoxHeaderMaps.get (getCurrentTabAndTableIndex ());
String [] allTitles = (String []) columnCheckBoxHeaderMap.keySet().toArray (new String [0]);

for (int i=0; i < candidateColumnNames.length; i++) {
  String name = candidateColumnNames [i];
  if (columnCheckBoxHeaderMap.containsKey (name))
    return true;
  } // for i

return false;

} // columnNamesIntersect
//----------------------------------------------------------------------------
protected boolean rowNamesIntersect (String [] candidateRowNames)
{
int tableIndex = getCurrentTabAndTableIndex ();
java.util.List listOfNamesToSelect = Arrays.asList (candidateRowNames);

String [] namesInTable = ((DataMatrix) matrixList.get (tableIndex)).getRowTitles ();
for (int i=0; i < namesInTable.length; i++)
 if (listOfNamesToSelect.contains (namesInTable [i]))
   return true;

return false;

} // rowNamesIntersect
//----------------------------------------------------------------------------
/**
* if any portion of the subtable specified by the arugments is present in
* the target table (currently on top in the browser), then clear any 
* existing selection and select the intersecting subtable.  if there
* is no intersection between the requested subtable and the current table,
* simply return.
*/
public void selectSubTableInCurrentlyVisibleTable (String [] selectedRowNames,
                                                 String [] selectedColumnNames)
{
clearAllSelections ();
disableAllColumnsInCurrentlyVisibleTable ();

if (!columnNamesIntersect (selectedColumnNames))
  return;

if (!rowNamesIntersect (selectedRowNames))
  return;

selectRowsByNameInCurrentlyVisibleTable (selectedRowNames);
disableAllColumnsInCurrentlyVisibleTable ();
enableColumnsByNameInCurrentlyVisibleTable (selectedColumnNames);

} // selectSubTableInCurrentlyVisibleTable
//----------------------------------------------------------------------------
public void selectRowsByNameInCurrentlyVisibleTable (String [] nodeNames)
{
int tableIndex = getCurrentTabAndTableIndex ();

JTable table = (JTable) tableList.get (tableIndex);
java.util.List listOfNamesToSelect = Arrays.asList (nodeNames);
String [] namesInTable = ((DataMatrix) matrixList.get (tableIndex)).getRowTitles ();
int selectionsInThisTable = 0;
for (int r=0; r < namesInTable.length; r++) {
  String tableRowName = namesInTable [r];
  boolean nameFoundBothInTableAndList = listOfNamesToSelect.contains (tableRowName);
  if (nameFoundBothInTableAndList) {
    table.getSelectionModel().addSelectionInterval (r, r);
    selectionsInThisTable++;
    } // if
  } // for r

//selectionCount [tableIndex] = selectionsInThisTable;
String countString = (new Integer (selectionsInThisTable).toString ());
((JTextField) selectionCountTextFieldList.get (tableIndex)).setText (countString);

} // selectRowsByNameInCurrentlyVisibleTable
//----------------------------------------------------------------------------
public JTable getTable ()
{
JTable currentlyVisibleTable = 
     (JTable) tableList.get (getCurrentTabAndTableIndex ());
return currentlyVisibleTable;
}
//----------------------------------------------------------------------------
	// this class is needed by interface ClipboardOwner
public void lostOwnership (Clipboard clipboard, Transferable contents) {}
//-----------------------------------------------------------------------------
class RowHeaderRenderer extends JLabel implements ListCellRenderer {

RowHeaderRenderer(JTable table) {
 JTableHeader header = table.getTableHeader();
 setOpaque(true);
 setBorder(UIManager.getBorder("TableHeader.cellBorder"));
 setHorizontalAlignment(CENTER);
 setForeground(header.getForeground());
 setBackground(header.getBackground());
 setFont(header.getFont());
 }

public Component getListCellRendererComponent (JList list, Object value, 
                                               int index, boolean isSelected, 
                                               boolean cellHasFocus) 
 {
   setText((value == null) ? "" : value.toString());
   return this;
 }

} // inner class RowHeaderRenderer
//-----------------------------------------------------------------------------
public int getCurrentTabAndTableIndex ()
{
if (tabbedPane == null)
  return -1;

return tabbedPane.getSelectedIndex ();

} // getCurrentTabAndTableIndex
//-----------------------------------------------------------------------------
public DataMatrix [] getMatrices ()
{
return (DataMatrix []) matrixList.toArray (new DataMatrix [0]);
}
//-----------------------------------------------------------------------------
public ArrayList getSelectedNodeNames () 
{
Set nodeSet = Cytoscape.getCurrentNetwork().getFlagger().getFlaggedNodes();
ArrayList nameList = new ArrayList();
for (Iterator iter = nodeSet.iterator(); iter.hasNext(); ) {
  CyNode node = (CyNode)iter.next();
  String name = 
    (String)Cytoscape.getCurrentNetwork().getNodeAttributeValue (
              node, Semantics.CANONICAL_NAME);
  nameList.add(name);
  } // for iter

return nameList;
}
//-----------------------------------------------------------------------------
} // class DataMatrixBrowser
