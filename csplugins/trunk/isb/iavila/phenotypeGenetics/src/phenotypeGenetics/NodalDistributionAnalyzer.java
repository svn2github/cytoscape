/**  Copyright (c) 2005 Institute for Systems Biology
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
// NodalDistrbutionAnalyzer.java
//------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------
package phenotypeGenetics;
//------------------------------------------------------------------------
import java.util.*;
import java.io.*;
import cytoscape.*;
import cytoscape.data.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
//------------------------------------------------------------------------
/**
 * For each node, compute the distribution of genetic interaction types.
 */
public class NodalDistributionAnalyzer{

  /**
   * Constructs a new NodalDistributionAnalyzer
   */
  public NodalDistributionAnalyzer () {}

  //---------------------------------------------------------------------
  // Data calculation and output in a table dialog

  public void attributeDistribution (CyNetwork cy_network) {

    HashMap nodeDistribution = getNodeDistribution( cy_network, true );

    System.out.println("Starting Table Dialog for Allele Interaction Distributions");
    TableDialog d = new TableDialog(nodeDistribution);
    d.pack();
    d.setVisible(true);
  }

  //---------------------------------------------------------------------
  /**
   * For each allele, analyze the distribution of interaction classes and return
   * a HashMap to be put in a table.
   */
  public HashMap getNodeDistribution ( CyNetwork cy_network, boolean commonForm ) {
   
    //  The edges
    Iterator edgeIt = cy_network.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(edgeIt.hasNext()){
      edgeList.add(edgeIt.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    //  The nodes
    Iterator nodeIt = cy_network.nodesIterator();
    ArrayList nodeList = new ArrayList();
    while(nodeIt.hasNext()){
      nodeList.add(nodeIt.next());
    }
    CyNode [] nodes = (CyNode[])nodeList.toArray(new CyNode[nodeList.size()]);
    
    // A HashMap of interaction types (nodeDistribution) keyed on the nodes:
    HashMap nodeDistribution = new HashMap();
    
    // Cycle through the nodes
    for (int j = 0; j < nodes.length; j++) {

      //  Identify the node at hand
      CyNode node = nodes[j];
      String nodeName = 
        (String)Cytoscape.getNodeAttributeValue(node, Semantics.COMMON_NAME);
      String canonicalName = 
        (String)Cytoscape.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);

      //  Get the alleleForms for this node with a method
      String[] alleleForms = getAlleleForms(canonicalName, edges);
    
      //  Cycle through the alleleForms
      for (int af = 0; af < alleleForms.length; af++) {
        String alleleForm = alleleForms[af];

        //  Add the alleleForm to the nodeName, to make it an AlleleName
        String alleleName = new String();
        if (commonForm) {
          alleleName = nodeName + "(" + alleleForm + ")";
        } else {
          alleleName = alleleForm + MutualInfoCalculator.divider + canonicalName;
        }

        //  Cycle through all edges to see if they belong to node
        for (int i = 0; i < edges.length; i++) {

          //  Identify the edge and get it's class, edgeType
          CyEdge edge = edges[i];
          String edgeType = 
            (String)Cytoscape.getEdgeAttributeValue(edge,GeneticInteraction.ATTRIBUTE_GENETIC_CLASS);
          String edgeA =
            (String)Cytoscape.getEdgeAttributeValue(edge,GeneticInteraction.ATTRIBUTE_MUTANT_A);
          String edgeB = (String)Cytoscape.getEdgeAttributeValue(edge,GeneticInteraction.ATTRIBUTE_MUTANT_B);
          String edgeAlleleA = (String)Cytoscape.getEdgeAttributeValue(edge,GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A);
          String edgeAlleleB = (String)Cytoscape.getEdgeAttributeValue(edge,GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B);
          
          if (((canonicalName.compareTo(edgeA)==0)&&(alleleForm.compareTo(edgeAlleleA)==0))
              |
              ((canonicalName.compareTo(edgeB)==0)&&(alleleForm.compareTo(edgeAlleleB)==0))){ 
            //  Check to see if it is a source or target edge and increment
            //  the appropriate counter in updateNodeDist.
            //  Implementation of the intIndex is clumsy, but the final
            //  categories do not exactly match the edgeTypes
            if ( node == (CyNode)edge.getSourceNode() ) {
              int intIndex = 0;
              if (edgeType=="non-interacting") { intIndex = 0; };
              if (edgeType=="synthetic") { intIndex = 1; };
              if (edgeType=="asynthetic") { intIndex = 2; };
              if (edgeType=="suppression") { intIndex = 3; };
              if (edgeType=="conditional") { intIndex = 5; };
              if (edgeType=="epistatic") { intIndex = 7; };
              if (edgeType=="additive") { intIndex = 9; };
              if (edgeType=="single-nonmonotonic") { intIndex = 10; };
              if (edgeType=="double-nonmonotonic") { intIndex = 12; };
              this.updateNodeDist( intIndex, alleleName, nodeDistribution );
            } else if ( node == (CyNode)edge.getTargetNode() ) {
              int intIndex = 0;
              if (edgeType=="non-interacting") { intIndex = 0; };
              if (edgeType=="synthetic") { intIndex = 1; };
              if (edgeType=="asynthetic") { intIndex = 2; };
              if (edgeType=="suppression") { intIndex = 4; };
              if (edgeType=="conditional") { intIndex = 6; };
              if (edgeType=="epistatic") { intIndex = 8; };
              if (edgeType=="additive") { intIndex = 9; };
              if (edgeType=="single-nonmonotonic") { intIndex = 11; };
              if (edgeType=="double-nonmonotonic") { intIndex = 12; };
              this.updateNodeDist( intIndex, alleleName, nodeDistribution );
            }// if node is source or target
          }// if allele is A or B
        }// over all edges
      }// over all alleleForms for the node
      System.out.print(":");
    }// over all nodes
    
    return nodeDistribution; 
  }
  
  //---------------------------------------------------------------------
  /**
   * For each node, analyze the distribution of interaction classes and return
   * a HashMap to be put in a table.
   */
  public HashMap getNodeDistributionNoAlleles (CyNetwork cy_network) {
    
    //  The edges
    Iterator edgeIt = cy_network.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(edgeIt.hasNext()){
      edgeList.add(edgeIt.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    //  The nodes
    Iterator nodeIt = cy_network.nodesIterator();
    ArrayList nodeList = new ArrayList();
    while(nodeIt.hasNext()){
      nodeList.add(nodeIt.next());
    }
    CyNode [] nodes = (CyNode[])nodeList.toArray(new CyNode[nodeList.size()]);
        
    // A HashMap of interaction types (nodeDistribution) keyed on the nodes:
    HashMap nodeDistribution = new HashMap();
    
    // Cycle through the nodes
    for (int j = 0; j < nodes.length; j++) {

      //  Identify the node at hand
      CyNode node = nodes[j];
      String nodeName = (String)Cytoscape.getNodeAttributeValue(node, Semantics.COMMON_NAME);
      String canonicalName = 
        (String)Cytoscape.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);
      
      //  Cycle through all edges to see if they belong to node
      for (int i = 0; i < edges.length; i++) {
        
        //  Identify the edge and get it's class, edgeType
        CyEdge edge = edges[i];
        String edgeType = 
          (String)Cytoscape.getEdgeAttributeValue(edge,GeneticInteraction.ATTRIBUTE_GENETIC_CLASS);
        String edgeA = (String)Cytoscape.getEdgeAttributeValue(edge,GeneticInteraction.ATTRIBUTE_MUTANT_A);
        String edgeB = (String)Cytoscape.getEdgeAttributeValue(edge,GeneticInteraction.ATTRIBUTE_MUTANT_B);
        
        if (((canonicalName.compareTo(edgeA)==0)  ) |
            ((canonicalName.compareTo(edgeB)==0)  ) ) {
          
          //  Check to see if it is a source or target edge and increment
          //  the appropriate counter in updateNodeDist.
          //  Implementation of the intIndex is clumsy, but the final
          //  categories do not exactly match the edgeTypes
          if ( node == edge.getSourceNode() ) {
            int intIndex = 0;
            if (edgeType=="non-interacting") { intIndex = 0; };
            if (edgeType=="synthetic") { intIndex = 1; };
            if (edgeType=="asynthetic") { intIndex = 2; };
            if (edgeType=="suppression") { intIndex = 3; };
            if (edgeType=="conditional") { intIndex = 5; };
            if (edgeType=="epistatic") { intIndex = 7; };
            if (edgeType=="additive") { intIndex = 9; };
            if (edgeType=="single-nonmonotonic") { intIndex = 10; };
            if (edgeType=="double-nonmonotonic") { intIndex = 12; };
            this.updateNodeDist( intIndex, nodeName, nodeDistribution );
          } else if ( node == edge.getTargetNode() ) {
            int intIndex = 0;
            if (edgeType=="non-interacting") { intIndex = 0; };
            if (edgeType=="synthetic") { intIndex = 1; };
            if (edgeType=="asynthetic") { intIndex = 2; };
            if (edgeType=="suppression") { intIndex = 4; };
            if (edgeType=="conditional") { intIndex = 6; };
            if (edgeType=="epistatic") { intIndex = 8; };
            if (edgeType=="additive") { intIndex = 9; };
            if (edgeType=="single-nonmonotonic") { intIndex = 11; };
            if (edgeType=="double-nonmonotonic") { intIndex = 12; };
            this.updateNodeDist( intIndex, nodeName, nodeDistribution );
          }// if node is source or target
        }// if allele is A or B
      }// over all edges
    }// over all nodes

    return nodeDistribution; 
  }

  //---------------------------------------------------------------------------
  /**
   * Fills in the Nodal distribution table.
   * If there does not exist a distribution array for the node create one
   * and increment the value at index, else increment the value at index for
   * the given node.
   */
  private void updateNodeDist (int index, String nodeName, HashMap dist) {
    //  The counter array, elements are the interaction types
    int[] distArray;

    //  If the node is already in the distribution, increment the interaction
    //  type.  Otherwise, initialize a new key and array of types.
    if (dist.containsKey(nodeName)) {
      distArray = (int[])dist.get(nodeName);
      distArray[index]++;
      dist.put(nodeName, distArray);
    } else {
      distArray = new int[13];
      distArray[index]++;
      dist.put(nodeName, distArray);
    }
  }


  //---------------------------------------------------------------------------
  /**
   *  Finds a set of alleleForms for a given node -- usually one of them but maybe not
   */
  public static String[] getAlleleForms( String node, CyEdge[] edges) {
    ArrayList theAlleleForms = new ArrayList();
    for (int i = 0; i < edges.length; i++){
      String edgeName = 
        (String)Cytoscape.getEdgeAttributeValue(edges[i], Semantics.CANONICAL_NAME);
      //HashMap edgeHash = edgeAtts.getAttributes( edgeName );
      String nodeA = 
        (String)Cytoscape.getEdgeAttributeValue(edges[i],
                                                GeneticInteraction.ATTRIBUTE_MUTANT_A);
      String nodeB = 
        (String)Cytoscape.getEdgeAttributeValue(edges[i],
                                                GeneticInteraction.ATTRIBUTE_MUTANT_B);
      if ( node.compareTo(nodeA) == 0 ) {
        String alleleForm = 
          (String)Cytoscape.getEdgeAttributeValue(edges[i],
                                                  GeneticInteraction.ATTRIBUTE_ALLELE_FORM_A);
        if ( !theAlleleForms.contains( alleleForm ) ) { theAlleleForms.add( alleleForm ); }
      } else if ( node.compareTo(nodeB) == 0 ) {
        String alleleForm = 
          (String)Cytoscape.getEdgeAttributeValue(edges[i],
                                                  GeneticInteraction.ATTRIBUTE_ALLELE_FORM_B);
        if ( !theAlleleForms.contains( alleleForm ) ) { theAlleleForms.add( alleleForm ); }
      }
    }
                                                                                                          
    return (String[])theAlleleForms.toArray( new String[0] );
  }

  /**
   * The Table Dialog box for the nodal distribution.
   * A table of nodes (rows) vs interaction types (columns).
   */
  class TableDialog extends JDialog {
    JTable table;
    /**
     * Takes distribution table and displays it.
     */
    public TableDialog (HashMap dist) {
      super();

      //  Create that table!
      TableDialogModel tm = new TableDialogModel(dist);
      //this.table = new JTable(tm);
      TableSorter sorter = new TableSorter( tm, 1 );
      this.table = new JTable( sorter );
      sorter.addMouseListenerToHeaderInTable(this.table);
      table.setPreferredScrollableViewportSize(new Dimension(1040, 400));
      this.setTitle( "Interaction Classes by Allele" );

      //  Now add a scroll pane!
      JScrollPane scrollPane = new JScrollPane(table);
      JPanel p = new JPanel(new BorderLayout());
      p.add(scrollPane, BorderLayout.CENTER);

      //  Add a dismissal buttion:
      JPanel dPanel = new JPanel();
      JButton b = new JButton("Dismiss");
      b.addActionListener (new DismissAction ());
      dPanel.add( b );
      p.add(dPanel, BorderLayout.SOUTH);

      //  Add the scroll pane to the new window.
      setContentPane(p);

      //  Sort rows by total interactions (#14), in descending order (false)
      sorter.sortByColumn( 14, false );
    }
    class DismissAction extends AbstractAction {
      DismissAction () {super ();}
      public void actionPerformed (ActionEvent e) {
        TableDialog.this.dispose();
      }

    }


    /**
     * The Table Dialog Model for the nodal table.
     */
    class TableDialogModel extends AbstractTableModel {
      HashMap dist;
      Object[][] data;
      //  The column names are like the interaction classes, but directional
      //  classes have been split.
      //String[] columnNames = {"Node","Non-Interacting","Synthetic",
      //			"Asynthetic","Suppresses","Suppressed",
      //			"Conditions","Conditioned","Epistatic","Epistated",
      //			"Additive","Single-Nonmonotonic",
      //			"Double-Nonmonotonic","Total"};
      String[] columnNames = {"Allele","NonInt","Synth",
                              "Asynth","Sup+","Sup-",
                              "Cond+","Cond-","Epis+","Epis-",
                              "Additive","1x-NM+","1xNM-",
                              "2x-Nonmon","Total"};

      public TableDialogModel (HashMap dist) {
        super();
        //  The HashMap of nodes and the interaction distributions
        this.dist = dist;
        //  The array of interactions, recycled for each node
        int[] distArray;
        //  The array of interaction totals
        int[] typeTotals = new int[15];

        //  The numbers for each interaction type
        int sum;
        int j = 0;

        //  Every node has a name
        String nodeName;

        Iterator i = dist.keySet().iterator();

        //  The (#nodes)x(1 + #types + 1) array
        this.data = new Object[dist.size()][15];

        for ( int k = 0; k < 15; k++ ) {
          typeTotals[k] = 0;
        }
        //  Cycle through all the nodes and compute the array elements
        while (i.hasNext()) {
          nodeName = (String)i.next();
          distArray = (int[])dist.get(nodeName);
          sum = 0;
          //  Add them up now
          for ( int k = 0; k < 13; k++ ) {
            sum += distArray[k];
            typeTotals[k+1] += distArray[k];
          }
          typeTotals[0] += 1;
          typeTotals[14] += sum;
          //  Assign values to their proper positions in the table
          this.data[j][0] = nodeName;
          this.data[j][1] = new Integer(distArray[0]);
          this.data[j][2] = new Integer(distArray[1]);
          this.data[j][3] = new Integer(distArray[2]);
          this.data[j][4] = new Integer(distArray[3]);
          this.data[j][5] = new Integer(distArray[4]);
          this.data[j][6] = new Integer(distArray[5]);
          this.data[j][7] = new Integer(distArray[6]);
          this.data[j][8] = new Integer(distArray[7]);
          this.data[j][9] = new Integer(distArray[8]);
          this.data[j][10] = new Integer(distArray[9]);
          this.data[j][11] = new Integer(distArray[10]);
          this.data[j][12] = new Integer(distArray[11]);
          this.data[j][13] = new Integer(distArray[12]);
          this.data[j][14] = new Integer(sum);

          j++;
        }

        for ( int k = 0; k < 15; k++) {
          columnNames[k] = (String)( columnNames[k]+"("+
                                     typeTotals[k] + ")" );
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
    }
  }
}

//  The following are sorting mechanisms stolen from the online Java API
//  documentations for table classes.

/** 
 * In a chain of data manipulators some behaviour is common. TableMap
 * provides most of this behavour and can be subclassed by filters
 * that only need to override a handful of specific methods. TableMap 
 * implements TableModel by routing all requests to its model, and
 * TableModelListener by routing all events to its listeners. Inserting 
 * a TableMap which has not been subclassed into a chain of table filters 
 * should have no effect.
 *
 * @version 1.4 12/17/97
 * @author Philip Milne */

class TableMap extends AbstractTableModel 
  implements TableModelListener {
  protected TableModel model; 

  public TableModel getModel() {
    return model;
  }

  public void setModel(TableModel model) {
    this.model = model; 
    model.addTableModelListener(this); 
  }

  // By default, implement TableModel by forwarding all messages 
  // to the model. 

  public Object getValueAt(int aRow, int aColumn) {
    return model.getValueAt(aRow, aColumn); 
  }
        
  public void setValueAt(Object aValue, int aRow, int aColumn) {
    model.setValueAt(aValue, aRow, aColumn); 
  }

  public int getRowCount() {
    return (model == null) ? 0 : model.getRowCount(); 
  }

  public int getColumnCount() {
    return (model == null) ? 0 : model.getColumnCount(); 
  }
        
  public String getColumnName(int aColumn) {
    return model.getColumnName(aColumn); 
  }

  public Class getColumnClass(int aColumn) {
    return model.getColumnClass(aColumn); 
  }
        
  public boolean isCellEditable(int row, int column) { 
    return model.isCellEditable(row, column); 
  }
  //
  // Implementation of the TableModelListener interface, 
  //
  // By default forward all events to all the listeners. 
  public void tableChanged(TableModelEvent e) {
    fireTableChanged(e);
  }
}


/**
 * A sorter for TableModels. The sorter has a model (conforming to TableModel) 
 * and itself implements TableModel. TableSorter does not store or copy 
 * the data in the TableModel, instead it maintains an array of 
 * integers which it keeps the same size as the number of rows in its 
 * model. When the model changes it notifies the sorter that something 
 * has changed eg. "rowsAdded" so that its internal array of integers 
 * can be reallocated. As requests are made of the sorter (like 
 * getValueAt(row, col) it redirects them to its model via the mapping 
 * array. That way the TableSorter appears to hold another copy of the table 
 * with the rows in a different order. The sorting algorthm used is stable 
 * which means that it does not move around rows when its comparison 
 * function returns 0 to denote that they are equivalent. 
 *
 * @version 1.5 12/17/97
 * @author Philip Milne
 */

class TableSorter extends TableMap{
  int             indexes[];
  Vector          sortingColumns = new Vector();
  boolean         ascending = false;
  int compares;
  int numNameRows;

  public TableSorter() {
    indexes = new int[0]; // for consistency
  }

  public TableSorter(TableModel model, int numName) {
    setModel(model);
    setNumNameRows( numName );
  }

  public void setModel(TableModel model) {
    super.setModel(model); 
    reallocateIndexes(); 
  }

  public void setNumNameRows(int num) {
    this.numNameRows = num;
  }

  public int compareRowsByColumn(int row1, int row2, int column) {
    Class type = model.getColumnClass(column);
    TableModel data = model;

    // Check for nulls.

    Object o1 = data.getValueAt(row1, column);
    Object o2 = data.getValueAt(row2, column); 

    // If both values are null, return 0.
    if (o1 == null && o2 == null) {
      return 0; 
    } else if (o1 == null) { // Define null less than everything. 
      return -1; 
    } else if (o2 == null) { 
      return 1; 
    }

    /*
     * We copy all returned values from the getValue call in case
     * an optimised model is reusing one object to return many
     * values.  The Number subclasses in the JDK are immutable and
     * so will not be used in this way but other subclasses of
     * Number might want to do this to save space and avoid
     * unnecessary heap allocation.
     */

    if (type.getSuperclass() == java.lang.Number.class) {
      Number n1 = (Number)data.getValueAt(row1, column);
      double d1 = n1.doubleValue();
      Number n2 = (Number)data.getValueAt(row2, column);
      double d2 = n2.doubleValue();

      if (d1 < d2) {
        return -1;
      } else if (d1 > d2) {
        return 1;
      } else {
        return 0;
      }
    } else if (type == java.util.Date.class) {
      Date d1 = (Date)data.getValueAt(row1, column);
      long n1 = d1.getTime();
      Date d2 = (Date)data.getValueAt(row2, column);
      long n2 = d2.getTime();

      if (n1 < n2) {
        return -1;
      } else if (n1 > n2) {
        return 1;
      } else {
        return 0;
      }
    } else if (type == String.class) {
      String s1 = (String)data.getValueAt(row1, column);
      String s2    = (String)data.getValueAt(row2, column);
      int result = s1.compareTo(s2);

      if (result < 0) {
        return -1;
      } else if (result > 0) {
        return 1;
      } else {
        return 0;
      }
    } else if (type == Boolean.class) {
      Boolean bool1 = (Boolean)data.getValueAt(row1, column);
      boolean b1 = bool1.booleanValue();
      Boolean bool2 = (Boolean)data.getValueAt(row2, column);
      boolean b2 = bool2.booleanValue();

      if (b1 == b2) {
        return 0;
      } else if (b1) { // Define false < true
        return 1;
      } else {
        return -1;
      }
    } else {
      Object v1 = data.getValueAt(row1, column);
      String s1 = v1.toString();
      Object v2 = data.getValueAt(row2, column);
      String s2 = v2.toString();
      int result = s1.compareTo(s2);

      if (result < 0) {
        return -1;
      } else if (result > 0) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  public int compare(int row1, int row2) {
    compares++;
    for (int level = 0; level < sortingColumns.size(); level++) {
      Integer column = (Integer)sortingColumns.elementAt(level);
      int result = compareRowsByColumn(row1, row2, column.intValue());
      if (result != 0) {
        return ascending ? result : -result;
      }
    }
    return 0;
  }

  public void reallocateIndexes() {
    int rowCount = model.getRowCount();

    // Set up a new array of indexes with the right number of elements
    // for the new data model.
    indexes = new int[rowCount];

    // Initialise with the identity mapping.
    for (int row = 0; row < rowCount; row++) {
      indexes[row] = row;
    }
  }

  public void tableChanged(TableModelEvent e) {
    //System.out.println("Sorter: tableChanged"); 
    reallocateIndexes();

    super.tableChanged(e);
  }

  public void checkModel() {
    if (indexes.length != model.getRowCount()) {
      System.err.println("Sorter not informed of a change in model.");
    }
  }

  public void sort(Object sender) {
    checkModel();

    compares = 0;
    // n2sort();
    // qsort(0, indexes.length-1);
    shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);
    //System.out.println("Compares: "+compares);
  }

  public void n2sort() {
    for (int i = 0; i < getRowCount(); i++) {
      for (int j = i+1; j < getRowCount(); j++) {
        if (compare(indexes[i], indexes[j]) == -1) {
          swap(i, j);
        }
      }
    }
  }

  // This is a home-grown implementation which we have not had time
  // to research - it may perform poorly in some circumstances. It
  // requires twice the space of an in-place algorithm and makes
  // NlogN assigments shuttling the values between the two
  // arrays. The number of compares appears to vary between N-1 and
  // NlogN depending on the initial order but the main reason for
  // using it here is that, unlike qsort, it is stable.
  public void shuttlesort(int from[], int to[], int low, int high) {
    if (high - low < 2) {
      return;
    }
    int middle = (low + high)/2;
    shuttlesort(to, from, low, middle);
    shuttlesort(to, from, middle, high);

    int p = low;
    int q = middle;

    /* This is an optional short-cut; at each recursive call,
       check to see if the elements in this subset are already
       ordered.  If so, no further comparisons are needed; the
       sub-array can just be copied.  The array must be copied rather
       than assigned otherwise sister calls in the recursion might
       get out of sinc.  When the number of elements is three they
       are partitioned so that the first set, [low, mid), has one
       element and and the second, [mid, high), has two. We skip the
       optimisation when the number of elements is three or less as
       the first compare in the normal merge will produce the same
       sequence of steps. This optimisation seems to be worthwhile
       for partially ordered lists but some analysis is needed to
       find out how the performance drops to Nlog(N) as the initial
       order diminishes - it may drop very quickly.  */

    if (high - low >= 4 && compare(from[middle-1], from[middle]) <= 0) {
      for (int i = low; i < high; i++) {
        to[i] = from[i];
      }
      return;
    }

    // A normal merge. 

    for (int i = low; i < high; i++) {
      if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
        to[i] = from[p++];
      }
      else {
        to[i] = from[q++];
      }
    }
  }

  public void swap(int i, int j) {
    int tmp = indexes[i];
    indexes[i] = indexes[j];
    indexes[j] = tmp;
  }

  // The mapping only affects the contents of the data rows.
  // Pass all requests to these rows through the mapping array: "indexes".

  public Object getValueAt(int aRow, int aColumn) {
    checkModel();
    return model.getValueAt(indexes[aRow], aColumn);
  }

  public void setValueAt(Object aValue, int aRow, int aColumn) {
    checkModel();
    model.setValueAt(aValue, indexes[aRow], aColumn);
  }

  public void sortByColumn(int column) {
    sortByColumn(column, true);
  }

  public void sortByColumn(int column, boolean ascending) {
    this.ascending = ascending;
    sortingColumns.removeAllElements();
    sortingColumns.addElement(new Integer(column));
    sort(this);
    super.tableChanged(new TableModelEvent(this)); 
  }

  // There is no-where else to put this. 
  // Add a mouse listener to the Table to trigger a table sort 
  // when a column heading is clicked in the JTable. 
  public void addMouseListenerToHeaderInTable(JTable table) { 
    final TableSorter sorter = this; 
    final JTable tableView = table; 
    tableView.setColumnSelectionAllowed(false); 
    MouseAdapter listMouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          TableColumnModel columnModel = tableView.getColumnModel();
          int viewColumn = columnModel.getColumnIndexAtX(e.getX()); 
          int column = tableView.convertColumnIndexToModel(viewColumn); 
          if (e.getClickCount() == 1 && column != -1) {
            int shiftPressed = e.getModifiers()&InputEvent.SHIFT_MASK; 
            // Shifting sorts in ascending order, default is descending
            boolean ascending = !(shiftPressed == 0); 
            // unless it is names, column = 0
            if ( column < numNameRows ) { ascending = !ascending; }
            sorter.sortByColumn(column, ascending); 
          }
        }
      };
    JTableHeader th = tableView.getTableHeader(); 
    th.addMouseListener(listMouseListener); 
  }
}
