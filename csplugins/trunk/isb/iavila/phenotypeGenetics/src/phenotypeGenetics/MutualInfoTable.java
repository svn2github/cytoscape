/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
/**
 * A <code>javax.swing.JFrame</code> that displays a table with node pair 
 * names and the corresponding scores.
 *
 * @author Iliana Avila
 * @author Greg Carter (adapted from I.Avila)
 */

package phenotypeGenetics;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import cytoscape.*;
import cytoscape.data.Semantics;
import cytoscape.view.*;

public class MutualInfoTable extends JFrame {

  /**
   * The data contained in the table.
   */
  protected Object [][] data;

  protected MutualInfo[] infoSet;

  MutualInfoCalculator clientObject;
  
  JTable theTable;

  /**
   * So that the <code>AbstractAction</code> objects contained in <code>buttonActions</code>
   * can access the <code>ModuleAnnotationsTable</code> that contains the <code>JButton</code>
   * that was clicked on, the <code>JButton</code> hash code is mapped to 
   * <code>ModuleAnnotationsTable</code> objects. For an action to get a hold of the associated 
   * <code>ModuleAnnotationsTable</code> do the following:
   *
   * public void actionPerformed (ActionEvent event){
   *   Object source = event.getSource();
   *   if(source instanceof JButton){
   *      JButton button = (JButton)source;
   *      int hashCode = button.hashCode();
   *      Object value = getValue(String.valueOf(hashCode));
   *      if(value instanceof ModuleAnnotationsTable){
   *        ModuleAnnotationsTable table = (ModuleAnnotationsTable)value;
   *        // Do whatever needs to be done
   *      }
   *   }
   * }
   */
  public MutualInfoTable (MutualInfoCalculator clientObject, 
                          MutualInfo[] theInfoSet, String title){
    super(title);
    this.clientObject = clientObject;
    this.infoSet = theInfoSet;
    create(theInfoSet);
  }//MutualInfoTable

  /**
   * Returns the data contained in the table.
   *
   * @return a <code>Object[][]</code> object
   */
  public Object [][] getData (){
    return this.data;
  }//getData

  /**
   * Creates the table.
   *
   * @param theInfos the <code>MutualInfo[]</code> array of pairs.
   *
   */
  protected void create (MutualInfo[] theInfos){
    
    // Put the MutualInfo information into String data[][]

    //this.data = new Object[ theInfos.length ][ 7 ];
    this.data = new Object[ theInfos.length ][ 5 ];
    for (int i = 0; i < theInfos.length; i++) {
      Integer numCommon = new Integer( theInfos[i].getSize() );
      Double score = new Double( theInfos[i].getScore() );
      Double pval = new Double( theInfos[i].getPValue() );
      //Double scoreRS = new Double( theInfos[i].getScoreRS() );
      //Double sdRS = new Double( theInfos[i].getSdRS() );
      this.data[i][0] = makeTableLabel( theInfos[i], 1 );
      this.data[i][1] = makeTableLabel( theInfos[i], 2 );
      this.data[i][2] = numCommon;
      this.data[i][3] = score;
      this.data[i][4] = pval;
      //this.data[i][5] = scoreRS;
      //this.data[i][6] = sdRS;
    }


    //String [] columnNames = {"ALLELE 1", "ALLELE 2","COMMON ALLELES","SCORE","P-VALUE","MEAN RS","SD RS"};
    String [] columnNames = {"ALLELE 1", "ALLELE 2","COMMON ALLELES","SCORE","-Log(P)"};

    MITableDialogModel tm = new MITableDialogModel( this.data, columnNames );
    TableSorter sorter = new TableSorter( tm, 2 );
    JTable table = new JTable( sorter );

    sorter.addMouseListenerToHeaderInTable( table );
    sorter.sortByColumn( columnNames.length-1, false );
    table.setPreferredScrollableViewportSize(new Dimension(550, 250));
    this.addMouseListenerToRows( this, table );
    table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    table.setColumnSelectionAllowed( false );   
 
    //Create the scroll pane and add the table to it. 
    JScrollPane scrollPane = new JScrollPane(table);
    
    JPanel buttonPanel = new JPanel();
    JButton saveToFile = new JButton("Save To File...");
    saveToFile.addActionListener(new SaveToFileAction());
    buttonPanel.add(saveToFile);

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
   *  Makes a label for a <code>MutualInfo</code>, for the node index (= 1 or 2).
   */
  public String makeTableLabel(MutualInfo theInfo, int index) {

    String canonicalName = theInfo.getNodeName( index );
    CyNode node = Cytoscape.getCyNode(canonicalName,false);
    if(node == null){
      throw new IllegalStateException("Node with name "+ canonicalName 
                                      +" does not exist in RootGraph");
    }
    
    //clientObject.cytoscapeWindow.getNode( canonicalName );
    String nodeName =
      (String)Cytoscape.getNodeAttributeValue(node,Semantics.COMMON_NAME);
    String alleleForm = theInfo.getAlleleForm( index );

    String label = new String();
    if ( alleleForm.compareTo("") == 0 ) {
      label = nodeName;
    } else {
      label = nodeName + "(" + alleleForm + ")";
    }
    return label;
  }

  /**
   *  Add a mouse listener to the Table to trigger a row selection and
   *  then select the associated nodes and edges.
   */
  public void addMouseListenerToRows(MutualInfoTable miTable, JTable table) {
    final JTable tableView = table;
    final MutualInfoTable miTableView = miTable;
    tableView.setColumnSelectionAllowed(false);
    MouseAdapter listMouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          int selectedRow = tableView.getSelectedRow();
          // find which MutualInfo has been selected
          String chosen1 = (String)tableView.getValueAt( selectedRow, 0 );
          String chosen2 = (String)tableView.getValueAt( selectedRow, 1 );
          int row = selectedRow;
          for (int i=0; i<miTableView.infoSet.length; i++) {
            String string1 = miTableView.makeTableLabel( miTableView.infoSet[i], 1 );
            String string2 = miTableView.makeTableLabel( miTableView.infoSet[i], 2 );
            if ( ( chosen1.compareTo( string1 ) == 0 ) &&
                 ( chosen2.compareTo( string2 ) == 0 ) ) {
              row = i;
              continue;
            }
          }
          MutualInfo chosenInfo = miTableView.infoSet[row];
          CyNode [] nodesToSelect = new CyNode[chosenInfo.getSize() + 2];
          // get the 1st node
          String nodeName1 = chosenInfo.getNodeName1();
          CyNode node1 = Cytoscape.getCyNode(nodeName1, false);
          if(node1 == null){
            throw new IllegalStateException("Node with name "+nodeName1+
                                            " does not exist in RootGraph");
          }
          if(!clientObject.getCyNetwork().containsNode(node1)){
            throw new IllegalStateException("Node with name "+nodeName1+
                                            " does not exist in CyNetwork");
          }
          // get the 2nd node
          String nodeName2 = chosenInfo.getNodeName2();
          CyNode node2 = Cytoscape.getCyNode(nodeName2, false);
          if(node2 == null){
            throw new IllegalStateException("Node with name "+nodeName2+
                                            " does not exist in RootGraph");
          }
          if(!clientObject.getCyNetwork().containsNode(node2)){
            throw new IllegalStateException("Node with name "+nodeName2+
                                            " does not exist in CyNetwork");
          }
          
          nodesToSelect[0] = node1;
          nodesToSelect[1] = node2;
          for (int i = 0; i<chosenInfo.getSize(); i++) {
            String nodeName = (String)(chosenInfo.getCommonNeighbors().get(i));
            CyNode node = Cytoscape.getCyNode(nodeName, false);
            if(node == null){
              throw new IllegalStateException("Node with name "+nodeName+
                                              " does not exist in RootGraph");
            }
            if(!clientObject.getCyNetwork().containsNode(node)){
              throw new IllegalStateException("Node with name "+nodeName+
                                              " does not exist in CyNetwork");
            }
            nodesToSelect[i+2] = node;
          }        
          
          CyNetworkView netView = 
            Cytoscape.getNetworkView(clientObject.getCyNetwork().getIdentifier());
          netView.setSelected(nodesToSelect);
          
          
          ArrayList theEdges = chosenInfo.getEdges();
          CyEdge[] edges = (CyEdge[])theEdges.toArray( new CyEdge[theEdges.size()] );
          netView.setSelected(edges);
        }// mouseClicked
        
      };//  MouserAdapter
    table.addMouseListener(listMouseListener);
  }//addMouseListenerToRows


  // ----------- Internal classes --------------//
  protected class SaveToFileAction extends AbstractAction {

    SaveToFileAction (){
      super("Save To File...");
    }//SaveToFileAction

    public void actionPerformed (ActionEvent event){
      Object [][] unsortedData = MutualInfoTable.this.data;
      Object [][] data = new Object[unsortedData.length][unsortedData[0].length];
      for (int i=0; i<unsortedData.length; i++) {
        for (int j=0; j<unsortedData[i].length; j++) {
          data[i][j] = MutualInfoTable.this.theTable.getValueAt( i, j );
        }
      }

      JFileChooser chooser = new JFileChooser();
      String filePath = null;
      if(chooser.showSaveDialog(MutualInfoTable.this) == chooser.APPROVE_OPTION){
        filePath = chooser.getSelectedFile().toString();
      }//if choseer
      if(filePath == null){return;}
      try{
        File file = new File(filePath);
        if(file.exists()){
          // Ask the user if she wants to overwrite the file
          int n = JOptionPane.showConfirmDialog(MutualInfoTable.this,
                                                "The file " + 
                                                filePath + " already exists. Overwrite?",
                                                "File Exists",
                                                JOptionPane.YES_NO_OPTION);
          if(n == JOptionPane.NO_OPTION){
            return;
          }
        }// if file exists
        FileWriter fileWriter = new FileWriter(file);
        StringBuffer strBuffer = new StringBuffer();
        String nl = System.getProperty("line.separator");
        
        strBuffer.append("Allele1\tAllele2\t#common\tScore\tp-Value");
        for(int i = 0; i < data.length; i++){
          strBuffer.append(nl);
          strBuffer.append(data[i][0]);
          strBuffer.append("\t");
          strBuffer.append(data[i][1]);
          strBuffer.append("\t");
          strBuffer.append(data[i][2]);
          strBuffer.append("\t");
          strBuffer.append(data[i][3]);
          strBuffer.append("\t");
          strBuffer.append(data[i][4]);
          //strBuffer.append("\t");
          //strBuffer.append(data[i][5]);
          //strBuffer.append("\t");
          //strBuffer.append(data[i][6]);
        }// for i
        strBuffer.append(nl);
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
      MutualInfoTable.this.dispose();
    }//actionPerformed
  }//CloseWindowAction

  /**
   * The Table Dialog Model for the mutual info table.
   */
  class MITableDialogModel extends AbstractTableModel {
    HashMap dist;
    String[] columnNames;
    Object[][] data;
                                                                                                     
    public MITableDialogModel (Object[][] theData, String[] theColumnNames) {
      super();
                                                                                                     
      this.columnNames = theColumnNames;
      this.data = new Object[theData.length][theData[0].length];

      for( int j=0; j<theData.length; j++ ) {
        //  Assign values to their proper positions in the table
        this.data[j][0] = (String)theData[j][0];
        this.data[j][1] = (String)theData[j][1];
        this.data[j][2] = (Integer)theData[j][2];
        this.data[j][3] = (Double)theData[j][3];
        this.data[j][4] = (Double)theData[j][4];
        //this.data[j][5] = (Double)theData[j][5];
        //this.data[j][6] = (Double)theData[j][6];
      }
    }
    public int getColumnCount() {
      return columnNames.length;
    }
                                                                                                     
    public int getRowCount() {
      return data.length;
    }
                                                                                                     
    public String getColumnName(int col) {
      return columnNames[col];
    }
                                                                                                     
    public Object getValueAt(int row, int col) {
      return data[row][col];
    }
                                                                                                     
    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }
  }//class MITableDialogModel
  
}//class MutualInfoTable
