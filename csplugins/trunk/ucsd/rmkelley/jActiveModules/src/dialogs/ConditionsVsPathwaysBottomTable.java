// ConditionsVsPathwaysBottomTable.java:  
//---------------------------------------------------------------------------------------
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
import javax.swing.border.Border;
import java.util.HashMap;

import csplugins.jActiveModules.ActivePathViewer;
import csplugins.jActiveModules.Component;
//------------------------------------------------------------------------
public class ConditionsVsPathwaysBottomTable extends JPanel {
  JTable table;
  JScrollPane scrollPane;
  JPanel self;
  private int columnWidth = 40;
  private int rowTitleWidth = 120;
  csplugins.jActiveModules.Component [] activePaths;
  String [] conditionNames;
  private ActivePathViewer pathViewer;
//------------------------------------------------------------------------
public ConditionsVsPathwaysBottomTable (String [] conditionNames, 
                                        csplugins.jActiveModules.Component [] activePaths, 
                                        ActivePathViewer pathViewer)
{
  super ();
  self = this;
  this.activePaths = activePaths;
  this.pathViewer = pathViewer;
  this.conditionNames = conditionNames;
  table = new JTable (new Model ());

  int preferredWidth = 0;
  for (int i=0; i < table.getColumnCount (); i++) {
    TableColumn column = table.getColumnModel().getColumn (i);
    column.setPreferredWidth (columnWidth);
    preferredWidth += columnWidth;
    }

  table.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
  table.setRowSelectionAllowed (false);
  table.setColumnSelectionAllowed (true);
  setupColorRenderer ();
  setupMouseClickDisplayOfActivePaths ();

  ListModel listModel = new ConditionNamesListModel (conditionNames);
  JList rowHeader = new JList (listModel);
  rowHeader.setBackground (getBackground ());

  rowHeader.setFixedCellWidth (rowTitleWidth);
  rowHeader.setFixedCellHeight (table.getRowHeight());
  rowHeader.setCellRenderer (new RowHeaderRenderer (table));

  Dimension preferredSize = new Dimension (preferredWidth, 
                                           table.getRowCount () * table.getRowHeight());

  table.setPreferredScrollableViewportSize (preferredSize);
  JScrollPane scrollPane = new JScrollPane (table);
  scrollPane.setRowHeaderView (rowHeader);
  add (scrollPane);

} // ctor
//------------------------------------------------------------------------
class Model extends AbstractTableModel {
  int pathCount;
  String [] columnNames = new String [0];
  Object [][] data;

//-------------------------------------------------------------------
public Model ()
{
  this.pathCount = activePaths.length;
  data = new Object [conditionNames.length][pathCount];

  for (int row = 0; row < conditionNames.length; row++)
    for (int column = 0; column < pathCount; column++) {
      csplugins.jActiveModules.Component path = activePaths [column];
      //debug
      //System.out.println("Calling from model constructor");
      //debug
      String [] conditionsForThisPath = path.getConditions ();
      boolean matchedCondition = false;
      for (int cond=0; cond < conditionsForThisPath.length; cond++) {
        String condition = conditionsForThisPath [cond];
        if (conditionNames [row].equalsIgnoreCase (condition)) {
          matchedCondition = true;
          break;
          }
        }
      if (matchedCondition)
        data [row][column] = Color.red;
      else          
        data [row][column] = Color.white;
      } // for column
} // ctor

public int getColumnCount () { return pathCount;}
public int getRowCount () { return data.length; }
//public String getColumnName (int col) { return columnNames[col];}
public String getColumnName (int col) { return null; }
public Object getValueAt (int row, int col) { return data[row][col];}
public boolean isCellEditable (int row, int col) {return true;}
public Class getColumnClass (int c) {return getValueAt(0, c).getClass();}

} // inner class Model
//---------------------------------------------------------------------
class RowHeaderRenderer extends JLabel implements ListCellRenderer {
  
  RowHeaderRenderer (JTable table) {
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

} // inner class RowHeaderRender
//---------------------------------------------------------------------------
private void setupMouseClickDisplayOfActivePaths ()
// clicking a row in the table causes that activePath to be differentially
//  displayed in the originating LucaWindow
{
  table.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
  ListSelectionModel colSM = table.getColumnModel().getSelectionModel ();
  colSM.addListSelectionListener (new ListSelectionListener () {
    public void valueChanged (ListSelectionEvent e) {
      ListSelectionModel lsm = (ListSelectionModel)e.getSource ();
      if (!lsm.isSelectionEmpty () && !e.getValueIsAdjusting ()) {
        int selectedColumn = lsm.getMinSelectionIndex ();
        String title = "path #" + (selectedColumn + 1);
        pathViewer.displayPath (activePaths [selectedColumn], title);
        }
      }  // valueChanged
    }); // addListSelectionListener
  
} // setupMouseClickDisplayOfActivePaths
//------------------------------------------------------------------------------
class ColorRenderer extends JLabel implements TableCellRenderer 
{
  Border unselectedBorder = null;
  Border selectedBorder = null;
  boolean isBordered = true;

  public ColorRenderer (boolean isBordered) {
    super();
    this.isBordered = isBordered;
    setOpaque(true); //MUST do this for background to show up.
    }

  public java.awt.Component getTableCellRendererComponent (JTable table, Object color, 
                                                  boolean isSelected, boolean hasFocus,
                                                  int row, int column) {
   setBackground ((Color) color);
   if (isBordered) {
     if (isSelected) {
       if (selectedBorder == null)
         selectedBorder = 
           BorderFactory.createMatteBorder (2,5,2,5, table.getSelectionBackground());
       setBorder (selectedBorder);
       }
    else {
      if (unselectedBorder == null)
        unselectedBorder = 
          BorderFactory.createMatteBorder (2,5,2,5, table.getBackground ());
      setBorder (unselectedBorder);
      }
    } // if isBordered

  return this;
  } // getTableCellRendererComponent

} // inner class ColorRenderer
//--------------------------------------------------------------------------------
private void setupColorRenderer ()
{
 table.setDefaultRenderer (Color.class, new ColorRenderer (true));
}
//--------------------------------------------------------------------------------
class ConditionNamesListModel extends AbstractListModel {
  String [] names;
  ConditionNamesListModel (String [] conditionNames) {
    names = conditionNames;
    }
  public int getSize () { return names.length; }
  public Object getElementAt (int index) {
    return names [index];
    }

} // inner class ConditionNamesListModel
//-----------------------------------------------------------------------------------------
private String [] allConditionNames (Component [] activePaths)
{
  HashMap hash = new HashMap ();
  for (int i=0; i < activePaths.length; i++) {
    //debug
    //System.out.println("Calling from allConditionNames");
    //debug
    String [] conditions = activePaths [i].getConditions ();
    for (int c=0; c < conditions.length; c++) {
      String condition = conditions [c];
      if (!hash.containsKey (condition)) {
        hash.put (condition, new Integer (0));
        System.out.println (".... adding condition: " + condition);
        }
      } // for c
    } // for i

  return (String []) hash.keySet().toArray (new String [0]);

} // allConditionNames
//-----------------------------------------------------------------------------------------
} // class ConditionsVsPathwaysBottomTable

