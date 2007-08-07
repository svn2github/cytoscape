// GoDataServerPopupTable
//---------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
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

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import cytoscape.data.servers.*;
//---------------------------------------------------------------------------------------
public class GoDataServerPopupTable extends JDialog {
  GoDataServerPopupTable popupTable;
  private JTable table;
  BioDataServer bioDataServer = null;
//---------------------------------------------------------------------------------------
public GoDataServerPopupTable (Frame parentFrame, String [] geneNames, 
                               BioDataServer bioDataServer)
{
  super (parentFrame, false);
  this.bioDataServer = bioDataServer;
  popupTable = this;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());

  table = new JTable (new GenesAsRowsTableModel (geneNames));
  table.setDefaultRenderer (JButton.class,
                  new ButtonCellRenderer (table.getDefaultRenderer (JButton.class)));
  table.addMouseListener (new MyMouseListener (table));

  table.getColumnModel().getColumn(0).setPreferredWidth (6);
  table.getColumnModel().getColumn(1).setPreferredWidth (8);

  table.setPreferredScrollableViewportSize (new Dimension (800, 200));
  JScrollPane scrollPane = new JScrollPane (table);
  panel.add (scrollPane, BorderLayout.CENTER);

  JPanel buttonPanel = new JPanel ();
  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction (this));
  buttonPanel.add (dismissButton, BorderLayout.CENTER);
  panel.add (buttonPanel, BorderLayout.SOUTH);
  table.setMaximumSize (new Dimension (800, 400));

  setContentPane (panel);

} // GoDataServerPopupTable ctor
//------------------------------------------------------------------------------------
public class DismissAction extends AbstractAction {
  private JDialog dialog;

  DismissAction (JDialog popup) {super (""); this.dialog = popup;}

  public void actionPerformed (ActionEvent e) {
    dialog.dispose ();
    }

} // QuitAction
//-----------------------------------------------------------------------------------
class GenesAsRowsTableModel extends AbstractTableModel {
  String [] columnNames;
  Object [][] data;

  public GenesAsRowsTableModel (String [] geneNames) {
    columnNames = new String [5];
    columnNames [0] = "NAME";
    columnNames [1] = "ORF";
    columnNames [2] = "PROCESS";
    columnNames [3] = "FUNCTION";
    columnNames [4] = "COMPONENT";
    int geneCount = geneNames.length;
    data = new Object [geneCount][5];
    for (int i=0; i < geneNames.length; i++) {
      String [] goInfo = getConciseGOInfo (geneNames [i]);
      final String name = geneNames [i];
      // data [i][0] = geneNames [i];
      data [i][1] = goInfo [0];
      data [i][2] = goInfo [1];
      data [i][3] = goInfo [2];
      data [i][4] = goInfo [3];
      JButton button = new JButton (geneNames [i]);
      data [i][0] = button;
      ((JButton)data[i][0]).addActionListener (new ActionListener () {
        public void actionPerformed (ActionEvent e) {
        PopupTextArea text = new PopupTextArea (popupTable, name, getFullGOInfo (name));
	// text.setLocationRelativeTo (popupTable);
        }});
      } // for i
    } // ctor

  public String getColumnName (int col) { return columnNames[col];}
  public int getColumnCount () { return columnNames.length;}
  public int getRowCount () { return data.length; }
  public Object getValueAt (int row, int col) { return data[row][col];}
  public boolean isCellEditable (int row, int col) {return false;}
  public Class getColumnClass (int column) {return getValueAt (0, column).getClass();}

} // inner class GenesAsRowsTableModel
//--------------------------------------------------------------------------------------
class GenesAsColumnsTableModel extends AbstractTableModel {
  String [] columnNames;
  Object [][] data;

  public GenesAsColumnsTableModel (String [] geneNames) {
    columnNames = new String [1 + geneNames.length];
    columnNames [0] = "";
    int geneCount = geneNames.length;
    data = new String [5][geneCount+1];
    data [0][0] = "ORF";
    data [1][0] = "PROCESS";
    data [2][0] = "FUNCTION";
    data [3][0] = "COMPONENT";
    data [4][0] = "SYNONYMS";
    for (int i=0; i < geneNames.length; i++) {
      columnNames [i+1] = geneNames [i];
      String [] goInfo = getConciseGOInfo (geneNames [i]);
      data [0][i+1] = goInfo [0];
      data [1][i+1] = goInfo [1];
      data [2][i+1] = goInfo [2];
      data [3][i+1] = goInfo [3];
      data [4][i+1] = goInfo [4];
      } // for i
    } // ctor

  public int getColumnCount () { return columnNames.length;}
  public int getRowCount () { return data.length; }
  public String getColumnName (int col) { return columnNames[col];}
  public Object getValueAt (int row, int col) { return data[row][col];}
  public boolean isCellEditable (int row, int col) {return false;}

} // inner class GenesAsColumnsTableModel
//--------------------------------------------------------------------------------------
private String [] getConciseGOInfo (String geneName)
{
  String [] result = new String [5];
  for (int i=0; i < result.length; i++) result [i] = "";

  try {
    result [0] = bioDataServer.getCanonicalName (geneName);

    int [] bioProcessIDs = bioDataServer.getBioProcessIDs (geneName);
    if (bioProcessIDs.length > 0) 
      result [1] = bioDataServer.getGoTermName (bioProcessIDs [0]);

    int [] molFuncIDs = bioDataServer.getMolecularFunctionIDs (geneName);
    if (molFuncIDs.length > 0) 
      result [2] = bioDataServer.getGoTermName (molFuncIDs [0]);

    int [] componentIDs = bioDataServer.getCellularComponentIDs (geneName);
    if (componentIDs.length > 0) 
      result [3] = bioDataServer.getGoTermName (componentIDs [0]);
    } // try
  catch (Exception e) {
    for (int i=0; i < result.length; i++) result [i] = "rmi error";
    }

  return result;

} // getConciseGOInfo
//--------------------------------------------------------------------------------------
private String getFullGOInfo (String geneName)
{
  StringBuffer sb = new StringBuffer ();
  String leadingSpace = "    ";
  try {
    sb.append (leadingSpace);
    sb.append ("ORF: ");
    sb.append (bioDataServer.getCanonicalName (geneName));
    sb.append ("\n\n");

    sb.append (leadingSpace);
    sb.append ("SYNONYMS");
    sb.append ("\n");
    String [] synonyms = bioDataServer.getSynonyms (geneName);
    for (int i=0; i < synonyms.length; i++) {
      sb.append (leadingSpace);
      sb.append (leadingSpace);
      sb.append (synonyms [i]);
      sb.append ("\n");
      }
    sb.append ("\n\n");

    sb.append (leadingSpace);
    sb.append ("BIOLOGICAL PROCESSES\n");
    int [] bioProcessIDs = bioDataServer.getBioProcessIDs (geneName);
    for (int i=0; i < bioProcessIDs.length; i++) {
      Vector allPaths = bioDataServer.getAllBioProcessPaths (bioProcessIDs [i]);
      for (int v=0; v < allPaths.size (); v++) {
        Vector path = (Vector) allPaths.elementAt (v);
        for (int p=path.size()-3; p >= 0; p--) {
          Integer ID = (Integer) path.elementAt (p);
          sb.append (leadingSpace);
          sb.append (leadingSpace);
          sb.append (ID);
          sb.append (": ");
          String name = bioDataServer.getGoTermName (ID.intValue ());
          if (p == 0)
            sb.append (name.toUpperCase ());
          else
            sb.append (name);
          sb.append ("\n");
          } // for p
        sb.append ("\n");
        } // for v
      } // for i
    sb.append ("\n");

    sb.append (leadingSpace);
    sb.append ("MOLECULAR FUNCTIONS\n");
    int [] molfuncs = bioDataServer.getMolecularFunctionIDs (geneName);
    for (int i=0; i < molfuncs.length; i++) {
      Vector allPaths = bioDataServer.getAllMolecularFunctionPaths (molfuncs [i]);
      for (int v=0; v < allPaths.size (); v++) {
        Vector path = (Vector) allPaths.elementAt (v);
        for (int p=path.size()-3; p >= 0; p--) {
          Integer ID = (Integer) path.elementAt (p);
          sb.append (leadingSpace);
          sb.append (leadingSpace);
          sb.append (ID);
          sb.append (": ");
          String name = bioDataServer.getGoTermName (ID.intValue ());
          if (p == 0)
            sb.append (name.toUpperCase ());
          else
            sb.append (name);
          sb.append ("\n");
          } // for p
        sb.append ("\n");
        } // for v
      } // for i
    sb.append ("\n");

    sb.append (leadingSpace);
    sb.append ("CELLULAR COMPONENTS\n");
    int [] components = bioDataServer.getCellularComponentIDs (geneName);
    for (int i=0; i < components.length; i++) {
      Vector allPaths = bioDataServer.getAllCellularComponentPaths (components [i]);
      for (int v=0; v < allPaths.size (); v++) {
        Vector path = (Vector) allPaths.elementAt (v);
        for (int p=path.size()-3; p >= 0; p--) {
          Integer ID = (Integer) path.elementAt (p);
          sb.append (leadingSpace);
          sb.append (leadingSpace);
          sb.append (ID);
          sb.append (": ");
          String name = bioDataServer.getGoTermName (ID.intValue ());
          if (p == 0)
            sb.append (name.toUpperCase ());
          else
            sb.append (name);
          sb.append ("\n");
          } // for p
        sb.append ("\n");
        } // for v
      } // for i
    sb.append ("\n");
    } // try
  catch (Exception e) {
    sb.append (leadingSpace);
    sb.append ("RMI error: " + e.getMessage ());
    }

  return sb.toString ();

} // getFullGOInfo
//--------------------------------------------------------------------------------------
class ButtonCellRenderer implements TableCellRenderer {

  private TableCellRenderer defaultRenderer;

  public ButtonCellRenderer (TableCellRenderer renderer) {
   defaultRenderer = renderer;
   }

  public Component getTableCellRendererComponent (JTable table, Object value,
                                                  boolean isSelected,
                                                  boolean hasFocus,
                                                  int row, int column)
  {
    if (value instanceof Component)
      return (Component) value;
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
} // class GoDataServerPopupTable
