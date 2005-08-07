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
  
  // Each Attribute Browser operates on one CytoscapeData object, and on either Nodes or Edges.
  CytoscapeData data;

  public static int NODES = 0;
  public static int EDGES = 1;
  public int graphObjectType;

  public DataTable ( CytoscapeData data, int graphObjectType ) {

    this.data = data;
    this.graphObjectType = graphObjectType;

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    setPreferredSize(new Dimension(400, 400));
    

    tableModel = (DataTableModel)makeModel( data );
    attributePanel =  new AttributePanel( data, 
                                          new AttributeModel( data ),
                                          new LabelModel( data ) );
    attributePanel.setTableModel( tableModel );
    add( attributePanel, BorderLayout.WEST );

    modPanel = new ModPanel(  data, tableModel, attributePanel, graphObjectType );
    add( modPanel, BorderLayout.NORTH );


   
    add(new JScrollPane(new JSortTable( tableModel ) ), BorderLayout.CENTER );


   

    JFrame frame = new JFrame("DataTable");
    frame.getContentPane().setLayout(new GridLayout());
    frame.getContentPane().add(this);
    
    frame.pack();
    frame.setSize( 900, 700 );
    frame.show();

  }

  public int getGraphObjectType () {
    return graphObjectType;
  }

  public CytoscapeData getData () {
    return data;
  }


  protected SortTableModel makeModel ( CytoscapeData data ) {

    List attributes = Arrays.asList( data.getAttributeNames() );
    List graph_objects = getFlaggedGraphObjects();

    DataTableModel model = new DataTableModel();
    model.setTableData( data,
                        graph_objects,
                        attributes);
    return model;
  }

  private List getFlaggedGraphObjects () {
    if ( graphObjectType == NODES )
      return new ArrayList( Cytoscape.getCurrentNetwork().getFlaggedNodes() );
    else 
      return new ArrayList( Cytoscape.getCurrentNetwork().getFlaggedEdges() );
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
