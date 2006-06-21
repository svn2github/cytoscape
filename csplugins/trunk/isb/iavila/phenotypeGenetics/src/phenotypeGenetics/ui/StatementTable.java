/**
 * A <code>javax.swing.JFrame</code> that displays a table with module names, their
 * annotations, and the corresponding p-values.
 *
 * @author Greg Carter
 */

package phenotypeGenetics.ui;

import phenotypeGenetics.*;
import annotations.*;
import cytoscape.*;
import cytoscape.view.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class StatementTable extends JFrame {

  /**
   * Whether the table should contain only the most 
   * specific <code>ModuleAnnotation</code> objects
   * for the modules.
   */
  protected boolean mostSpecific;

  /**
   * The set of <code>Statement</code> objects displayed
   */
  protected Statement[] statementSet;

  /**
   * The data contained in the table.
   */
  protected String [][] data;

  /**
   * The title of this window
   */
  protected String title;
  protected JTable theTable;
  protected CyNetwork cyNetwork;


  /**
   * Constructor.&nbsp; Creates a table that displays the annotations
   * in <code>annotations_map</code>.&nbsp;The column headers of the 
   * table are MODULE NAME, CLASSIFICATION, P-VALUE.&nbsp;The module
   * names are obtained from the given <code>Map</code> of module ids
   * (<code>Integer</code> keys) to <code>String</code> objects.
   *
   * @param annotations_map the <code>ModuleAnnotationMap</code> that 
   *                        contains the annotations to be displayed
   * @param make_specific if true, only the most specific 
   *                      <code>ModuleAnnotation</code>s are represented 
   *                      in the table
   * @param title the title for the table
   * @param buttonActions an array of actions each of which will be added to
   *                      a <code>JButton</code> that will be at the bottom of the table
   *                      by default, only a "Save To file..." button is added
   * <p>
   * So that the <code>AbstractAction</code> objects contained in <code>buttonActions</code>
   * can access the <code>StatementTable</code> that contains the <code>JButton</code>
   * that was clicked on, the <code>JButton</code> hash code is mapped to 
   * <code>StatementTable</code> objects. For an action to get a hold of the associated 
   * <code>StatementTable</code> do the following:
   *
   * public void actionPerformed (ActionEvent event){
   *   Object source = event.getSource();
   *   if(source instanceof JButton){
   *      JButton button = (JButton)source;
   *      int hashCode = button.hashCode();
   *      Object value = getValue(String.valueOf(hashCode));
   *      if(value instanceof StatementTable){
   *        StatementTable table = (StatementTable)value;
   *        // Do whatever needs to be done
   *      }
   *   }
   * }
   */
  public StatementTable (CyNetwork cy_net,
                         Statement[] statements,
                         boolean make_specific,
                         String title,
                         AbstractAction [] buttonActions){
    super(title);
    this.cyNetwork = cy_net;
    this.mostSpecific = make_specific;
    this.title = title;
    this.statementSet = statements;
    create(buttonActions);
  }//StatementTable

  /**
   * Returns <code>this.mostSpecific</code>
   */
  public boolean getMostSpecific (){
    return this.mostSpecific;
  }//getMostSpecific

  /**
   * Returns the data contained in the table.
   *
   * @return a <code>String[][]</code> object
   */
  public String [][] getData (){
    return this.data;
  }//getData

  /**
   * Creates the table.
   *
   * @param annotations_map the <code>ModuleAnnotationMap</code> that 
   *                        contains the annotations to be displayed
   * @param button_actions an array of <code>AbstractAction</code> objects to add
   *                      to <code>JButton</code>s that will be added to the bottom
   *                      of the table
   */
  protected void create (AbstractAction [] button_actions){

    ArrayList dataArrayList = new ArrayList();  
    for (int i = 0; i < this.statementSet.length; i++) {
      String [] tableRow = new String[5];
      tableRow[0] = this.statementSet[i].getAlleleAction();
      tableRow[1] = this.statementSet[i].getCommonName();
      tableRow[2] = this.statementSet[i].getVerb();
      tableRow[3] = this.statementSet[i].getOntologyTerm().getName();
      tableRow[4] = String.valueOf( this.statementSet[i].getNegLogP() ).substring(0,4);
      dataArrayList.add(tableRow);
    }

    this.data = (String [][])dataArrayList.toArray( new String[dataArrayList.size()][5]);
    String [] columnNames = {"MUTATION","GENE","ACTION","CLASSIFICATION","-LOG(P)"};

    final JTable table = new JTable(this.data, columnNames);
    TableSorter sorter = new TableSorter( table.getModel(), 4 );
    table.setModel( sorter );
    sorter.addMouseListenerToHeaderInTable( table );
    sorter.sortByColumn( columnNames.length-1, false );
    table.setPreferredScrollableViewportSize(new Dimension(800, 250));
    this.addMouseListenerToRows( this, table );
    table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    table.setColumnSelectionAllowed( false );

    //Create the scroll pane and add the table to it. 
    JScrollPane scrollPane = new JScrollPane(table);
    
    JPanel buttonPanel = new JPanel();
    JButton saveToFile = new JButton("Save To File...");
    saveToFile.addActionListener(new SaveToFileAction());
    buttonPanel.add(saveToFile);
    if(button_actions != null){
      for(int i = 0; i < button_actions.length; i++){
        JButton newButton = new JButton(button_actions[i]);
        button_actions[i].putValue(String.valueOf(newButton.hashCode()) , this);
        buttonPanel.add(newButton);
      }
    }
    JButton okButton = new JButton("Dismiss");
    okButton.addActionListener(new CloseWindowAction());
    buttonPanel.add(okButton);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.add(scrollPane);
    mainPanel.add(buttonPanel);

    getContentPane().add(mainPanel, BorderLayout.CENTER);
    this.theTable = table;
  }//create


  /**
   *  Add a mouse listener to the Table to trigger a row selection and
   *  then select the associated nodes.
   */
  public void addMouseListenerToRows(StatementTable sTable, JTable table) {
    final JTable tableView = table;
    final StatementTable sTableView = sTable;
    tableView.setColumnSelectionAllowed(false);
    MouseAdapter listMouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          int selectedRow = tableView.getSelectedRow();
          // find the chosen statement from the list (table data may have been sorted)
          String chosenName = (String)tableView.getValueAt( selectedRow, 1);
          String chosenAction = (String)tableView.getValueAt( selectedRow, 2);
          String chosenOntTerm = (String)tableView.getValueAt( selectedRow, 3);
          int row = selectedRow;
          for (int i=0; i<sTableView.statementSet.length; i++){
            String name = sTableView.statementSet[i].getCommonName();
            if ( name.compareTo(chosenName)==0 ) {
              String action = sTableView.statementSet[i].getVerb();
              if ( action.compareTo(chosenAction)==0 ) {
                String ontTerm = sTableView.statementSet[i].getOntologyTerm().getName();
                if ( ontTerm.compareTo(chosenOntTerm)==0 ) {
                  row = i;
                  continue;
                }
              }
            }
          }

          Statement chosenStatement = sTableView.statementSet[row];
          CyNode[] nodesToSelect = new CyNode [ chosenStatement.getSize() + 1 ];
          ArrayList nodeArray = new ArrayList();
          String canonicalName = chosenStatement.getCanonicalName();
          CyNode n = Cytoscape.getCyNode(canonicalName, false);
          //TODO: Error checking
          //nodeNames[0] = chosenStatement.getCanonicalName();
          nodesToSelect[0] = n;
          nodeArray.add(n);
          for (int i = 0; i< (chosenStatement.getNeighbors()).length; i++) {
            canonicalName = (String)(chosenStatement.getNeighbors()[i]);
            n = Cytoscape.getCyNode(canonicalName,false);
            //TODO: Error checking
            nodesToSelect[i+1] = n;
            nodeArray.add(n);
          }
          //cyNetwork.setFlaggedNodes(nodeArray,true);
          cyNetwork.setSelectedNodeState(nodeArray,true);
          //CyNetworkView netView = 
          //Cytoscape.getNetworkView(cyNetwork.getIdentifier());
          //if(netView != null)
          //netView.setSelected(nodesToSelect);
        }
      };
    table.addMouseListener(listMouseListener);
  }//addMouseListenerToRows


  // ----------- Internal classes --------------//
  protected class SaveToFileAction extends AbstractAction {

    SaveToFileAction (){
      super("Save To File...");
    }//SaveToFileAction

    public void actionPerformed (ActionEvent event){
      String [][] unsortedData = StatementTable.this.data;
      String [][] data = new String[unsortedData.length][unsortedData[0].length];
      for (int i=0; i<unsortedData.length; i++) {
        for (int j=0; j<unsortedData[i].length; j++) {
          data[i][j] = (String)StatementTable.this.theTable.getValueAt( i, j );
        }
      }

      JFileChooser chooser = new JFileChooser();
      String filePath = null;
      if(chooser.showSaveDialog(StatementTable.this) == chooser.APPROVE_OPTION){
        filePath = chooser.getSelectedFile().toString();
      }//if choseer
      if(filePath == null){return;}
      try{
        File file = new File(filePath);
        if(file.exists()){
          // Ask the user if she wants to overwrite the file
          int n = JOptionPane.showConfirmDialog(StatementTable.this,
                                                "The file " + filePath + 
                                                " already exists. Overwrite?",
                                                "File Exists",
                                                JOptionPane.YES_NO_OPTION);
          if(n == JOptionPane.NO_OPTION){
            return;
          }
        }// if file exists
        FileWriter fileWriter = new FileWriter(file);
        StringBuffer strBuffer = new StringBuffer();
        String nl = System.getProperty("line.separator");
        
        //strBuffer.append("NODE\tACTION\tCLASSIFICATION\t-LOG(P)");
        strBuffer.append(StatementTable.this.title);
        strBuffer.append(nl);
        for(int i = 0; i < data.length; i++){
          strBuffer.append(nl);
          strBuffer.append(data[i][0]);
          //strBuffer.append("\t");
          strBuffer.append(" ");
          strBuffer.append(data[i][1]);
          strBuffer.append(" ");
          strBuffer.append(data[i][2]);
          strBuffer.append(" mutations of ");
          strBuffer.append(data[i][3]);
          strBuffer.append(" genes (");
          strBuffer.append(data[i][4]);
          strBuffer.append(").");
        }// for i
        fileWriter.write(strBuffer.toString(), 0, strBuffer.length());
        fileWriter.flush();
      }catch(IOException ioe){
        System.out.println(ioe);
        ioe.printStackTrace();
      }
    }//actionPerformed
  }//SaveToFileAction

  protected class CloseWindowAction extends AbstractAction{

    CloseWindowAction(){
      super("OK");
    }//CloseWindowAction

    public void actionPerformed (ActionEvent event){
      StatementTable.this.dispose();
    }//actionPerformed
  }//CloseWindowAction
  
}//class StatementTable
