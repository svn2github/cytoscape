/**
 * The Table Dialog box for the nodal distribution.
 * A table of nodes (rows) vs interaction types (columns).
 */

package phenotypeGenetics.ui;

import phenotypeGenetics.*;
import java.util.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class TableDialog extends JDialog {
  
  protected JTable table;
  
  /**
   * Takes distribution table and displays it.
   */
  public TableDialog (HashMap dist) {
    super();
      
    //  Create that table!
    TableDialogModel tm = new TableDialogModel(dist);
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
    b.addActionListener (new AbstractAction(){
        public void actionPerformed (ActionEvent e) {
          TableDialog.this.dispose();
        }//actionPerformed
      });
    dPanel.add( b );
    p.add(dPanel, BorderLayout.SOUTH);

    //  Add the scroll pane to the new window.
    setContentPane(p);

    //  Sort rows by name (first), in ascending order (true)
    sorter.sortByColumn( 0, true );

  }//TableDialog

   /**
    * Creates and returns a TableDialogModel
    */
  public TableDialogModel makeTableDialogModel( HashMap nodeDist ) {
    TableDialogModel m = new TableDialogModel(nodeDist);
    return m;
  }//makeTableDialogModel
  
  /**
   * The Table Dialog Model for the nodal table.
   */
  public class TableDialogModel extends AbstractTableModel {
    
    protected HashMap dist;
    protected Object[][] data;
    protected String[] columnNames;
    protected String[] columnNamesNoTotal;

    /**
     * Constructor
     */
    public TableDialogModel (HashMap dist) {
      super();
      //  The HashMap of nodes and the interaction distributions
      this.dist = dist;

      // Find the column names
      ArrayList cNames = new ArrayList();
      cNames.add( "Allele" );
      Iterator it = dist.keySet().iterator();
      while ( it.hasNext() ) {
        HashMap alleleHash = (HashMap)dist.get( (String)it.next() );
        Set nodeCols = alleleHash.keySet();
        Iterator inC = nodeCols.iterator();
        while ( inC.hasNext() ) {
          String colName = (String)inC.next();
          if ( !cNames.contains( colName ) ) {
            cNames.add( colName );
          }
        }
      }
      cNames.add( "Total" );
      columnNames = (String[])cNames.toArray( new String[0] );
      int numColumns = columnNames.length;

      //  The array of interactions, recycled for each node
      int[] distArray;
      //  The array of interaction totals
      int[] typeTotals = new int[ numColumns ];

      //  The numbers for each interaction type
      int j = 0;

      //  The (#nodes)x(1 + #intTypes + 1) array
      this.data = new Object[dist.size()][numColumns];
      //  Initialize the numerical parts of the data array
      for ( int r = 0; r < dist.size(); r++) { for ( int c = 1; c < numColumns; c++) {
          this.data[r][c] = new Integer( 0 );
        } }
      for ( int k = 0; k < numColumns; k++ ) {
        typeTotals[k] = 0;
      }
      typeTotals[0] = dist.keySet().size();
      //  Cycle through all the nodes and compute the array elements
      Iterator i = dist.keySet().iterator();
      while (i.hasNext()) {
        String nodeName = (String)i.next();
        int sum = 0;
        HashMap alleleHash = (HashMap)dist.get(nodeName);
        Iterator iaM = alleleHash.keySet().iterator();
        while (iaM.hasNext()) {
          String intType = (String)iaM.next();
          // Look only at columns which are interaction types (exclude first and last)
          for (int iC = 1; iC < columnNames.length-1; iC++ ) {
            if ( intType.compareTo( columnNames[iC] ) == 0 ) {
              Integer count = (Integer)alleleHash.get( intType );
              this.data[j][iC] = count;
              typeTotals[ iC ] += count.intValue();
              sum += count.intValue();
            }
          } 
        }
        this.data[j][0] = nodeName;
        this.data[j][numColumns-1] = new Integer(sum);
        typeTotals[numColumns-1] += sum;
        j++;
      }

      String[] cNNT = new String[ numColumns ];
      for ( int k = 0; k < numColumns; k++) {
        cNNT[k]=columnNames[k];
        columnNames[k] = (String)( columnNames[k]+"("+ typeTotals[k] + ")" );
      }
      columnNamesNoTotal = cNNT;
    }//TableDialogModel

    // Get methods //
    
    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.length;
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public String getColumnNameNoTotal(int col) {
      return columnNamesNoTotal[col];
    }

    public Object getValueAt(int row, int col) {
      return data[row][col];
    }

    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }
    
  }//TableDialogModel

}//TableDialog
