// ConditionsVsPathwaysTopTable.java:  
//------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.event.*;

import csplugins.jActiveModules.data.ActivePath;
import csplugins.jActiveModules.ActivePathViewer;
import csplugins.jActiveModules.Component;
//------------------------------------------------------------------------
public class ConditionsVsPathwaysTopTable extends JPanel {
  JTable table;
  JScrollPane scrollPane;
  private int columnWidth = 40;
  private int rowTitleWidth = 120;
  Component [] activePaths;
//------------------------------------------------------------------------
public ConditionsVsPathwaysTopTable (csplugins.jActiveModules.Component [] activePaths) 
{
  super ();
  this.activePaths = activePaths;
  table = new JTable (new Model ());

  int preferredWidth = 0;
  for (int i=0; i < table.getColumnCount (); i++) {
    TableColumn column = table.getColumnModel().getColumn (i);
    column.setPreferredWidth (columnWidth);
    preferredWidth += columnWidth;
    }

  table.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
  table.setRowSelectionAllowed (false);
  table.setColumnSelectionAllowed (false);


  JList rowTitles = new JList (new RowTitlesModel ());

  rowTitles.setBackground (getBackground ());
  rowTitles.setFixedCellWidth (rowTitleWidth);
  rowTitles.setFixedCellHeight (table.getRowHeight());
  rowTitles.setCellRenderer (new RowTitlesRenderer (table));

  Dimension preferredSize = new Dimension (preferredWidth, 
                                           table.getRowCount () * table.getRowHeight());

  table.setPreferredScrollableViewportSize (preferredSize);
  scrollPane = new JScrollPane (table);
  scrollPane.setRowHeaderView (rowTitles);
  add (scrollPane);

} // ctor
//------------------------------------------------------------------------
class Model extends AbstractTableModel {

  String [] columnNames; // = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
  Object [][] data;
  int numberOfColumns; // = 10; // pathCount;
  int numberOfRows = 2;   // score & size
//------------------------------------------------------------------
public Model ()
{
  numberOfColumns = activePaths.length;
  columnNames = new String [numberOfColumns];
  for (int i=0; i < numberOfColumns; i++)
    columnNames [i] = (new Integer (i+1)).toString ();

  data = new Object [numberOfRows][numberOfColumns];

  for (int col=0; col < numberOfColumns; col++) {
    Component path = activePaths [col];
    data [0][col] = new Integer (path.getNodes().size());
    data [1][col] = new Integer ((int) path.getScore ());
    } // for i

} // Model ctor
//---------------------------------------------------------------------
  public int getColumnCount () { return columnNames.length;}
  public int getRowCount () { return data.length; }
  public String getColumnName (int col) { return columnNames[col];}
  public Object getValueAt (int row, int col) { return data[row][col];}
  public boolean isCellEditable (int row, int col) {return true;}

} // inner class Model
//------------------------------------------------------------------------
class RowTitlesModel extends AbstractListModel {
  String [] names = {"Size", "Score"};
  RowTitlesModel () {}
  public int getSize () { return names.length; }
  public Object getElementAt (int index) {
    return names [index];
    }

} // inner class RowTitlesModel
//-----------------------------------------------------------------------------------------
class RowTitlesRenderer extends JLabel implements ListCellRenderer {
  
  RowTitlesRenderer (JTable table) {
    JTableHeader header = table.getTableHeader ();
    setOpaque (true);
    setBorder (UIManager.getBorder("TableHeader.cellBorder"));
    setHorizontalAlignment (CENTER);
    setForeground (header.getForeground());
    setBackground (header.getBackground());
    setFont (header.getFont());
    }
  
  public java.awt.Component getListCellRendererComponent (JList list, Object value, 
                                                 int index, boolean isSelected, 
                                                 boolean cellHasFocus) {
    setText ((value == null) ? "" : value.toString());
    return this;
  }

} // inner class RowTitlesRender
//---------------------------------------------------------------------------
} // class ConditionsVsPathwaysTopTable

