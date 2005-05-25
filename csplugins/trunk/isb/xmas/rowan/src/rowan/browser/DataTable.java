package rowan.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.data.CytoscapeData;
import javax.swing.*;

public class DataTable
  extends JPanel {

  AttributePanel attributePanel;
  ModPanel modPanel;
  DataTableModel tableModel;
  

  public DataTable () {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    setPreferredSize(new Dimension(400, 400));
    

    tableModel = (DataTableModel)makeModel( Cytoscape.getNodeNetworkData() );
    attributePanel =  new AttributePanel
      ( Cytoscape.getNodeNetworkData(),
        new AttributeModel 
        ( Cytoscape.getNodeNetworkData() ),
        new LabelModel 
        ( Cytoscape.getNodeNetworkData() ) );
    attributePanel.setTableModel( tableModel );
    add( attributePanel, BorderLayout.WEST );

    modPanel = new ModPanel(  Cytoscape.getNodeNetworkData(), tableModel, attributePanel );
    add( modPanel, BorderLayout.NORTH );


   
    add(new JScrollPane(new JSortTable( tableModel ) ), BorderLayout.CENTER );


   

    JFrame frame = new JFrame("DataTable");
    frame.getContentPane().setLayout(new GridLayout());
    frame.getContentPane().add(this);
    
    frame.pack();
    frame.setSize( 900, 700 );
    frame.show();

  }

  protected SortTableModel makeModel ( CytoscapeData data ) {

    List attributes = Arrays.asList( data.getAttributeNames() );
    List graph_objects = new ArrayList( Cytoscape.getCurrentNetwork().getFlaggedNodes() );

    DataTableModel model = new DataTableModel();
    model.setTableData( data,
                        graph_objects,
                        attributes);
    return model;
  }



  public static void main(String[] args)
  {
    JFrame frame = new JFrame("JSortTable Test");
    frame.getContentPane().setLayout(new GridLayout());
    frame.getContentPane().add(new JSortTableTest());
    frame.pack();
    frame.show();
  }
}
