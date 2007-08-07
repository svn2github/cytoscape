// NodeAttributesPopupTable
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

import cytoscape.data.*;
import cytoscape.data.servers.*;
import cytoscape.GraphObjAttributes;
//---------------------------------------------------------------------------------------
public class NodeAttributesPopupTable extends JDialog {
  protected Frame mainFrame;
  protected NodeAttributesPopupTable popupTable;
  protected JTable table;
  protected AllAttributesTableModel model;
  protected BioDataServer bioDataServer = null;
  protected final ExpressionData geneExpressionInfo;
  protected final int defaultColumnWidth = 100;
  protected int preferredTableWidth = defaultColumnWidth; // incremented below
//---------------------------------------------------------------------------------------
public NodeAttributesPopupTable (Frame parentFrame, String [] geneNames, 
                                BioDataServer bioDataServer,
                                String conditionName, 
                                ExpressionData geneExpressionInfo,
                                GraphObjAttributes geneAttributes)


{
  super (parentFrame, "Node Attributes", false);
  mainFrame = parentFrame;
  this.geneExpressionInfo = geneExpressionInfo;
  this.bioDataServer = bioDataServer;
  popupTable = this;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());

  model = new AllAttributesTableModel (geneNames, bioDataServer,
                                                   geneExpressionInfo, conditionName,
                                                   geneAttributes);
  table = new JTable (model);
  table.setPreferredScrollableViewportSize (new Dimension (preferredTableWidth, 100));
  table.setDefaultRenderer (JButton.class,
                  new ButtonCellRenderer (table.getDefaultRenderer (JButton.class)));
  table.addMouseListener (new MyMouseListener (table));

  setPreferredColumnWidths ();

  JScrollPane scrollPane = new JScrollPane (table);
  panel.add (scrollPane, BorderLayout.CENTER);

  JPanel buttonPanel = new JPanel ();
  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction (this));
  buttonPanel.add (dismissButton, BorderLayout.CENTER);
  panel.add (buttonPanel, BorderLayout.SOUTH);

  setContentPane (panel);

} // NodeAttributesPopupTable ctor
//------------------------------------------------------------------------------------
class AllAttributesTableModel extends AbstractTableModel {

  String [] columnNames;
  Object [][] data;
  int [] columnWidths = {40};  // missing values, which are possible only at
                               // the end of array, mean that default column widths
                               // will be used

  final ExpressionData geneExpression;
//---------------------------------------------------------------------
public AllAttributesTableModel (String [] geneNames, 
                                BioDataServer bioDataServer,
                                ExpressionData geneExpressionInfo,
                                String conditionName,
                                GraphObjAttributes geneAttributes) 
{
  this.geneExpression = geneExpressionInfo;
  int geneCount = geneNames.length;
  int columnsDevotedToGoData = 5;

  int numberOfColumns = 1;   // always at least 1 column, for the Gene name
  if (bioDataServer != null) numberOfColumns += columnsDevotedToGoData;
  boolean displayingGeneExpressionInfo = geneExpression != null && 
                                         conditionName != null;
  if (displayingGeneExpressionInfo) numberOfColumns += 3;
  if (geneAttributes != null) numberOfColumns += geneAttributes.size ();

    //-----------------------------------------------------------------
    // set the column & row count, allocate data, fill the column titles
    //-----------------------------------------------------------------

  columnNames = new String [numberOfColumns];
  int nextFreeColumn = 0;
  columnNames [nextFreeColumn++] = "NAME";

  data = new Object [geneCount][numberOfColumns];

  if (bioDataServer != null) {
    columnNames [nextFreeColumn++] = "SYNONYMS";
    columnNames [nextFreeColumn++] = "PROCESS";
    columnNames [nextFreeColumn++] = "FUNCTION";
    columnNames [nextFreeColumn++] = "COMPONENT";
    columnNames [nextFreeColumn++] = "";
    preferredTableWidth += (5 * defaultColumnWidth);
    } // if bioDataServer

  if (displayingGeneExpressionInfo) {
    columnNames [nextFreeColumn++] = "EXPRESSION RATIO";
    columnNames [nextFreeColumn++] = "LAMBDA";
    columnNames [nextFreeColumn++] = "";
    preferredTableWidth += (3 * defaultColumnWidth);
    } // if geneExpressionInfo

  if (geneAttributes != null) {
    String [] attributeNames = geneAttributes.getAttributeNames ();
    for (int i=0; i < attributeNames.length; i++) {
      columnNames [nextFreeColumn++] = attributeNames [i];
      preferredTableWidth += defaultColumnWidth;
      } // for i
    } // if geneAttributes


    //-----------------------------------------------------------------
    // now fill the data
    //-----------------------------------------------------------------
   for (int row=0; row < geneCount; row++) 
      data [row][0] = geneNames [row];

   nextFreeColumn = 1;

   if (bioDataServer != null) {
     for (int row=0; row < geneCount; row++) {
       final String name = geneNames [row];
       String [] goInfo = getConciseGOInfo (bioDataServer, name);
       data [row][1] = goInfo [0];
       data [row][2] = goInfo [1];
       data [row][3] = goInfo [2];
       data [row][4] = goInfo [3];
       JButton button = new JButton ("GO...");
       button.setToolTipText ("display full GeneOntology hierarchies");
       button.setBackground (Color.white);
       data [row][5] = button;
      ((JButton)data[row][5]).addActionListener (new ActionListener () {
        public void actionPerformed (ActionEvent e) {
        PopupTextArea text = new PopupTextArea (popupTable, name, getFullGOInfo (name));
	// text.setLocationRelativeTo(popupTable);
        }});

       } // for row
     } // if bioDataServer   

  if (displayingGeneExpressionInfo) {
    int ratioColumn = 1;
    int significanceColumn = 2;
    int buttonColumn = 3;
    if (bioDataServer != null) {
      ratioColumn += columnsDevotedToGoData;
      significanceColumn += columnsDevotedToGoData;
      buttonColumn += columnsDevotedToGoData;
      }
    for (int row=0; row < geneCount; row++) {
      String gene = geneNames [row];
      mRNAMeasurement measurement = 
         geneExpression.getMeasurement (geneNames [row], conditionName);
      final String condition = conditionName;
      final String name = geneNames [row];
      if (measurement != null) {
        data [row][ratioColumn] = new Double (measurement.getRatio ());
        data [row][significanceColumn] = new Double (measurement.getSignificance ()); 
        } // if measurement
      JButton button = new JButton ("mRNA...");
      button.setBackground (Color.white);
      button.setToolTipText ("display expression values for all conditions");
      data [row][buttonColumn] = button;
      ((JButton)data[row][buttonColumn]).addActionListener (
          new ActionListener () {
        public void actionPerformed (ActionEvent e) {
         String [] conditions = geneExpression.getConditionNames ();
         ExpressionDataPopupTable crossConditionsTable = 
           new ExpressionDataPopupTable (mainFrame, name, conditions, geneExpression);
         crossConditionsTable.pack ();
         crossConditionsTable.setLocationRelativeTo (mainFrame);
         crossConditionsTable.setVisible (true);
        }});
      } // for row
    } // if displayingGenexpressionInfo

  if (geneAttributes != null) {
    String [] aNames = geneAttributes.getAttributeNames ();
    for (int i=0; i < aNames.length; i++) {
      int attributeColumn = 1;
      if (bioDataServer != null) 
         attributeColumn += columnsDevotedToGoData;
      if (displayingGeneExpressionInfo)
         attributeColumn += 3; 
      for (int row=0; row < geneCount; row++) {
        Double d = geneAttributes.getDoubleValue (aNames [i], geneNames [row]);
        if (d != null) 
          data [row][attributeColumn+i] = d;
        } // for row
      } // for i:  each attribute name
    } // if geneAttributes


} // ctor
//---------------------------------------------------------------------
public String getColumnName (int col) { return columnNames[col];}
public int getColumnCount () { return columnNames.length;}
public int getRowCount () { return data.length; }
public Object getValueAt (int row, int col) { return data[row][col];}
public boolean isCellEditable (int row, int col) {return false;}

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
//--------------------------------------------------------------------------------------
private void setPreferredColumnWidths ()
{
  TableColumnModel columnModel = table.getColumnModel ();
  int numberOfColumns = table.getModel().getColumnCount ();

  for (int i=0; i < numberOfColumns ; i++) {
    int preferredWidth = model.getPreferredColumnWidth (i);
    if (preferredWidth > 0)
      columnModel.getColumn (i).setPreferredWidth (preferredWidth);
    } // for i

} // setPreferredColumnWidths
//------------------------------------------------------------------------
public class DismissAction extends AbstractAction {
  private JDialog dialog;

  DismissAction (JDialog popup) {super (""); this.dialog = popup;}

  public void actionPerformed (ActionEvent e) {
    dialog.dispose ();
    }

} // QuitAction
//-----------------------------------------------------------------------------------
private String [] getConciseGOInfo (BioDataServer bioDataServer, String nodeName)
{
  String [] result = new String [4];
  for (int i=0; i < result.length; i++) result [i] = "";

  try {
    String [] synonyms = bioDataServer.getSynonyms (nodeName);
    StringBuffer sb = new StringBuffer ();
    for (int i=0; i < synonyms.length; i++) {
      sb.append (synonyms [i]);
      if (i < (synonyms.length-1)) sb.append (", ");
      }
    result [0] = sb.toString ();
    int [] bioProcessIDs = bioDataServer.getBioProcessIDs (nodeName);
    if (bioProcessIDs.length > 0)
      result [1] = bioDataServer.getGoTermName (bioProcessIDs [0]);

    int [] molFuncIDs = bioDataServer.getMolecularFunctionIDs (nodeName);
    if (molFuncIDs.length > 0) 
      result [2] = bioDataServer.getGoTermName (molFuncIDs [0]);

    int [] componentIDs = bioDataServer.getCellularComponentIDs (nodeName);
    if (componentIDs.length > 0) 
      result [3] = bioDataServer.getGoTermName (componentIDs [0]);
    } // try
  catch (Exception e) {
    e.printStackTrace ();
    for (int i=0; i < result.length; i++) result [i] = "rmi error";
    }

  return result;

} // getConciseGOInfo
//--------------------------------------------------------------------------------------
private String getFullGOInfo (String geneName)
{
  StringBuffer sb = new StringBuffer ();
  String spacer = "    ";
  try {
    sb.append (spacer);
    sb.append ("GENE: ");
    sb.append (bioDataServer.getCanonicalName (geneName));
    sb.append (spacer);
    sb.append ("\n\n");

    sb.append (spacer);
    sb.append ("SYNONYMS");
    sb.append (spacer);
    sb.append ("\n");
    String [] synonyms = bioDataServer.getSynonyms (geneName);
    for (int i=0; i < synonyms.length; i++) {
      sb.append (spacer);
      sb.append (spacer);
      sb.append (synonyms [i]);
      sb.append (spacer);
      sb.append ("\n");
      }
    sb.append ("\n\n");

    sb.append (spacer);
    sb.append ("BIOLOGICAL PROCESSES");
    sb.append (spacer);
    sb.append ("\n");
    int [] bioProcessIDs = bioDataServer.getBioProcessIDs (geneName);
    for (int i=0; i < bioProcessIDs.length; i++) {
      Vector allPaths = bioDataServer.getAllBioProcessPaths (bioProcessIDs [i]);
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
      } // for i
    sb.append (spacer);
    sb.append ("\n");

    sb.append (spacer);
    sb.append ("MOLECULAR FUNCTIONS");
    sb.append (spacer);
    sb.append ("\n");
    int [] molfuncs = bioDataServer.getMolecularFunctionIDs (geneName);
    for (int i=0; i < molfuncs.length; i++) {
      Vector allPaths = bioDataServer.getAllMolecularFunctionPaths (molfuncs [i]);
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
      } // for i
    sb.append (spacer);
    sb.append ("\n");

    sb.append (spacer);
    sb.append ("CELLULAR COMPONENTS");
    sb.append (spacer);
    sb.append ("\n");
    int [] components = bioDataServer.getCellularComponentIDs (geneName);
    for (int i=0; i < components.length; i++) {
      Vector allPaths = bioDataServer.getAllCellularComponentPaths (components [i]);
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
      } // for i
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
} // class NodeAttributesPopupTable
