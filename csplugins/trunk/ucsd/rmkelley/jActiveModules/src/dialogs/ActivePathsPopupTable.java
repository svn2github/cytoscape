// ActivePathsPopupTable
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
//---------------------------------------------------------------------------------------
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.AbstractTableModel;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;


import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.SwingUtilities;

import java.util.HashMap;

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import cytoscape.*;
import cytoscape.data.*;
import csplugins.jActiveModules.ActivePathViewer;
import csplugins.jActiveModules.Component;
//------------------------------------------------------------------------------------
public class ActivePathsPopupTable extends JDialog {
  private Frame mainFrame;
  private ActivePathsPopupTable popupTable;
  private ActivePathViewer pathViewer;
  private JTable table;
  private csplugins.jActiveModules.Component [] activePaths;
  
//------------------------------------------------------------------------------------
public ActivePathsPopupTable (Frame parentFrame, Component [] activePaths,
                              ActivePathViewer pathViewer)

{
  super (parentFrame, false);
  mainFrame = parentFrame;
  this.pathViewer = pathViewer;
  this.activePaths = activePaths;
  popupTable = this;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());

  table = new JTable (new ActivePathsTableModel (activePaths));
  table.setPreferredScrollableViewportSize (new Dimension (400, 100));
  table.addMouseListener (new MyMouseListener (table));

  setupMouseClickDisplayOfActivePaths ();

  JScrollPane scrollPane = new JScrollPane (table);
  panel.add (scrollPane, BorderLayout.CENTER);

  JPanel buttonPanel = new JPanel ();
  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction (this));
  buttonPanel.add (dismissButton, BorderLayout.CENTER);
  panel.add (buttonPanel, BorderLayout.SOUTH);

  setContentPane (panel);

} // ActivePathsPopupTable ctor
//---------------------------------------------------------------------------------
private void  setupMouseClickDisplayOfActivePaths ()
// clicking a row in the table causes that activePath to be differentially
//  displayed in the originating LucaWindow
{
  table.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
  ListSelectionModel rowSM = table.getSelectionModel ();
  rowSM.addListSelectionListener (new ListSelectionListener () {
    public void valueChanged (ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel)e.getSource ();
      if (!lsm.isSelectionEmpty ()) {
        int selectedRow = lsm.getMinSelectionIndex ();
        pathViewer.displayPath (activePaths [selectedRow], "");
        }
      }  // valueChanged
    }); // addListSelectionListener
  
} // setupMouseClickDisplayOfActivePaths
//---------------------------------------------------------------------------------
class ActivePathsTableModel extends AbstractTableModel {

  String [] columnNames;
  Object [][] data;
//------------------------------------------------------------------
public ActivePathsTableModel (csplugins.jActiveModules.Component [] activePaths)
{
  int numberOfRows = activePaths.length;
  int numberOfColumns = 3;

    //-----------------------------------------------------------------
    // set the column & row count, allocate data, fill the column titles
    //-----------------------------------------------------------------
  columnNames = new String [numberOfColumns];
  columnNames [0] = "Score";
  columnNames [1] = "Node Count";
  columnNames [2] = "Condition";

  data = new Object [numberOfRows][numberOfColumns];

    //-----------------------------------------------------------------
    // now fill the data
    //-----------------------------------------------------------------
   for (int row=0; row < numberOfRows; row++) {
     final int pathIndex = row;
     final csplugins.jActiveModules.Component currentPath = activePaths [row];
     data [row][0] = new Double (activePaths [row].getScore ());
     data [row][1] = new Integer (activePaths [row].getNodes().size());
     String [] conditions = activePaths [row].getConditions ();
     String condition = conditions [0];
     System.out.println ("number of conditions: " + conditions.length);
     if (condition == null)
       condition = " error! ";
     data [row][2] = condition;
     } // for i

} // ctor
//---------------------------------------------------------------------
public String getColumnName (int col) { return columnNames[col];}
public int getColumnCount () { return columnNames.length;}
public int getRowCount () { return data.length; }
public Object getValueAt (int row, int col) { return data[row][col];}
public boolean isCellEditable (int row, int col) {return false;}

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

} // class AllAttributesTableModel
//-----------------------------------------------------------------------------------
public class DismissAction extends AbstractAction {
  private JDialog dialog;

  DismissAction (JDialog popup) {super (""); this.dialog = popup;}

  public void actionPerformed (ActionEvent e) {
    dialog.dispose ();
    }

} // QuitAction
//--------------------------------------------------------------------------------
class ButtonCellRenderer implements TableCellRenderer {

  private TableCellRenderer defaultRenderer;

  public ButtonCellRenderer (TableCellRenderer renderer) {
   defaultRenderer = renderer;
   }

  public java.awt.Component getTableCellRendererComponent (JTable table, Object value,
                                                  boolean isSelected,
                                                  boolean hasFocus,
                                                  int row, int column)
  {
    if (value instanceof java.awt.Component)
      return (java.awt.Component) value;
    else
      return defaultRenderer.getTableCellRendererComponent (
              table, value, isSelected, hasFocus, row, column);
  }

} // inner class ButtonCellRenderer
//-------------------------------------------------------------------------------
class MyMouseListener implements MouseListener 
{
  private JTable table;

  public MyMouseListener (JTable table) {
    this.table = table;
    }

  private void forwardEventToButton (MouseEvent e) {
    TableColumnModel columnModel = table.getColumnModel ();
    int column = columnModel.getColumnIndexAtX (e.getX ());
    int row  = e.getY() / table.getRowHeight();
    Object value;
    JButton button;
    MouseEvent buttonEvent;
    if (row >= table.getRowCount () || row < 0 || 
        column >= table.getColumnCount() || column < 0)
      return;
    value = table.getValueAt (row, column);
    boolean isButton = value instanceof JButton;
    if (!isButton) return;
    button = (JButton) value;
    buttonEvent = (MouseEvent) SwingUtilities.convertMouseEvent (table, e, button);
    // button.dispatchEvent (buttonEvent);
    button.doClick ();
    table.repaint ();
    }
   public void mouseClicked  (MouseEvent e) {forwardEventToButton(e);}
   public void mouseEntered  (MouseEvent e) {}
   public void mouseExited   (MouseEvent e) {}
   public void mousePressed  (MouseEvent e) {}
   public void mouseReleased (MouseEvent e) {}

} // inner class MyMouseListener
//-------------------------------------------------------------------------------
} // class ActivePathsPopupTable
