// GoIDAttributesPopupTable
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

import java.util.HashMap;

import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import cytoscape.GraphObjAttributes;
import cytoscape.data.servers.*;
import cytoscape.data.readers.TextFileReader;
//---------------------------------------------------------------------------------------
public class GoIDAttributesPopupTable extends JDialog {
  Frame mainFrame;
  GoIDAttributesPopupTable popupTable;
  private JTable table;
  BioDataServer bioDataServer = null;
//---------------------------------------------------------------------------------------
public GoIDAttributesPopupTable (Frame parentFrame, String [] nodeNames,
                                BioDataServer bioDataServer,
                                GraphObjAttributes nodeProperties)


{
  super (parentFrame, false);
  mainFrame = parentFrame;
  this.bioDataServer = bioDataServer;
  popupTable = this;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());


  table = new JTable (new GoTermAndAttributesTableModel (nodeNames, bioDataServer,
                                                         nodeProperties));
  table.setPreferredScrollableViewportSize (new Dimension (800, 100));
  table.setDefaultRenderer (JButton.class,
                  new ButtonCellRenderer (table.getDefaultRenderer (JButton.class)));
  table.addMouseListener (new MyMouseListener (table));

  table.getColumnModel().getColumn(0).setPreferredWidth (6);

  JScrollPane scrollPane = new JScrollPane (table);
  panel.add (scrollPane, BorderLayout.CENTER);

  JPanel buttonPanel = new JPanel ();
  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction (this));
  buttonPanel.add (dismissButton, BorderLayout.CENTER);
  panel.add (buttonPanel, BorderLayout.SOUTH);

  setContentPane (panel);

} // GoIDAttributesPopupTable ctor
//------------------------------------------------------------------------------------
class GoTermAndAttributesTableModel extends AbstractTableModel {

  String [] columnNames;
  Object [][] data;
//---------------------------------------------------------------------
public GoTermAndAttributesTableModel (String [] nodeNames, 
                                BioDataServer bioDataServer,
                                GraphObjAttributes nodeProperties) 
{
  int geneCount = nodeNames.length;

  int numberOfColumns = 1;   // always at least 1 column, for the Gene name
  if (bioDataServer != null) numberOfColumns += 2;
  if (nodeProperties != null) numberOfColumns += nodeProperties.size ();

    //-----------------------------------------------------------------
    // set the column & row count, allocate data, fill the column titles
    //-----------------------------------------------------------------

  columnNames = new String [numberOfColumns];
  int nextFreeColumn = 0;
  columnNames [nextFreeColumn++] = "GO ID";

  data = new Object [geneCount][numberOfColumns];

  if (bioDataServer != null) {
    columnNames [nextFreeColumn++] = "GO Term";
    columnNames [nextFreeColumn++] = "";
    } // if bioDataServer

  if (nodeProperties != null) {
    String [] attributeNames = nodeProperties.getAttributeNames ();
    for (int i=0; i < attributeNames.length; i++) 
      columnNames [nextFreeColumn++] = attributeNames [i];
    } // if nodeProperties


    //-----------------------------------------------------------------
    // now fill the data
    //-----------------------------------------------------------------
   for (int row=0; row < geneCount; row++) 
      data [row][0] = nodeNames [row];

   nextFreeColumn = 1;

   if (bioDataServer != null) {
     for (int row=0; row < geneCount; row++) {
       final String name = nodeNames [row];
       String goInfo = getConciseGoInfo (bioDataServer, name);
       data [row][1] = goInfo;
       JButton button = new JButton ("GO...");
       button.setToolTipText ("display full GeneOntology hierarchies");
       button.setBackground (Color.white);
       data [row][2] = button;
      ((JButton)data[row][2]).addActionListener (new ActionListener () {
        public void actionPerformed (ActionEvent e) {
        PopupTextArea text = new PopupTextArea (popupTable, name, getFullGOInfo (name));
        text.setLocationRelativeTo (popupTable);
        }});

       } // for row
     } // if bioDataServer   

  if (nodeProperties != null) {
    String [] aNames = nodeProperties.getAttributeNames ();
    for (int i=0; i < aNames.length; i++) {
      int attributeColumn = 1;
      if (bioDataServer != null) 
         attributeColumn += 2;
      for (int row=0; row < geneCount; row++) {
        Double d = nodeProperties.getDoubleValue (aNames [i], nodeNames [row]);
        if (d != null) 
          data [row][attributeColumn+i] = d;
        } // for row
      } // for i:  each attribute name
    } // if nodeProperties


} // ctor
//---------------------------------------------------------------------
public String getColumnName (int col) { return columnNames[col];}
public int getColumnCount () { return columnNames.length;}
public int getRowCount () { return data.length; }
public Object getValueAt (int row, int col) { return data[row][col];}
public boolean isCellEditable (int row, int col) {return false;}

public Class getColumnClass (int column) 
// thought i do not understand the circumstances in which this method
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
//--------------------------------------------------------------------------------------
public class DismissAction extends AbstractAction {
  private JDialog dialog;

  DismissAction (JDialog popup) {super (""); this.dialog = popup;}

  public void actionPerformed (ActionEvent e) {
    dialog.dispose ();
    }

} // QuitAction
//-----------------------------------------------------------------------------------
private String getConciseGoInfo (BioDataServer bioDataServer, String nodeName)
{
  String result = "";

  try {
    int goTerm = Integer.parseInt (nodeName);
    result = bioDataServer.getGoTermName (goTerm);
    } // try
  catch (Exception e) {
    result = "error";
    }

  return result;

} // getConciseGOInfo
//--------------------------------------------------------------------------------------
private String getFullGOInfo (String nodeName)
{
  StringBuffer sb = new StringBuffer ();
  String spacer = "    ";
  try {
    sb.append (spacer);
    sb.append ("Node: ");
    sb.append (bioDataServer.getCanonicalName (nodeName));
    sb.append (spacer);
    sb.append ("\n\n");

    sb.append (spacer);
    sb.append ("GO ONTOLOGY HIERARCHIES");
    sb.append (spacer);
    sb.append ("\n");
    int goTerm = Integer.parseInt (nodeName);
    Vector allPaths = bioDataServer.getAllGoHierarchyPaths (goTerm);
      for (int v=0; v < allPaths.size (); v++) {
        Vector path = (Vector) allPaths.elementAt (v);
        for (int p=path.size()-3; p >= 0; p--) {
          Integer ID = (Integer) path.elementAt (p);
          sb.append (spacer);
          sb.append (spacer);
          sb.append (ID);
          sb.append (": ");
          String name = bioDataServer.getGoTermName (ID.intValue ());
          if (p == 0)
            sb.append (name.toUpperCase ());
          else
            sb.append (name);
          sb.append (spacer);
          sb.append ("\n");
          } // for p
        sb.append (spacer);
        sb.append ("\n");
        } // for v
    sb.append (spacer);
    sb.append ("\n");

    } // try
  catch (Exception e) {
    sb.append (spacer);
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
} // class GoIDAttributesPopupTable
