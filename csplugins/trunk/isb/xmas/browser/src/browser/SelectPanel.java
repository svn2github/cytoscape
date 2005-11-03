package browser;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.util.List;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;
import javax.swing.event.SwingPropertyChangeSupport;
 
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.data.attr.*;

import giny.model.GraphObject;

public class SelectPanel extends JPanel 
  implements PropertyChangeListener,
             ActionListener,
             FlagEventListener {
  public static int NODES = 0;
  public static int EDGES = 1;
  int graphObjectType;
  JComboBox networkBox;
  JComboBox filterBox;
  DataTableModel tableModel;
  Map titleIdMap;
  JCheckBox mirrorSelection;

  CyNetwork current_network;

  public SelectPanel ( DataTableModel tableModel, int graphObjectType ) {

    this.tableModel = tableModel;
    this.graphObjectType = graphObjectType;

    titleIdMap = new HashMap();
    networkBox = getNetworkBox();
    networkBox.setMaximumSize( new Dimension(  15, (int)networkBox.getPreferredSize().getHeight()) );
    filterBox = new JComboBox( FilterManager.defaultManager().getComboBoxModel() );
    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this );
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( this );

    mirrorSelection = new JCheckBox();
    mirrorSelection.setSelected( true );


    setBorder( new TitledBorder( "Object Selection" ) );
    add( new JLabel( "Filter: ") );
    add( filterBox );
    add( new JLabel( "Network: " ) );
    add( networkBox );
    add( new JLabel( "Mirror Network Selection") );
    add( mirrorSelection );
    
    filterBox.addActionListener( this );
    networkBox.addActionListener( this );

    current_network = Cytoscape.getCurrentNetwork();
    current_network.addFlagEventListener( this );

    
  }
  
  public void onFlagEvent ( FlagEvent event ) {
    
    if ( mirrorSelection.isSelected() ) {
      if ( graphObjectType == NODES && ( event.getTargetType() == FlagEvent.SINGLE_NODE || event.getTargetType() == FlagEvent.NODE_SET ) ) {
        // node selection
        tableModel.setTableDataObjects( new ArrayList( Cytoscape.getCurrentNetwork().getFlaggedNodes() ) );
      } else if ( graphObjectType == EDGES && ( event.getTargetType() == FlagEvent.SINGLE_EDGE || event.getTargetType() == FlagEvent.EDGE_SET ) ) {
        // edge selection
        tableModel.setTableDataObjects( new ArrayList( Cytoscape.getCurrentNetwork().getFlaggedEdges() ) );
      }
    }
  }

  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == filterBox ) {
      Filter filter = ( Filter )filterBox.getSelectedItem();
      System.out.println( "Showing all that Pass Filter: "+filter );
      List list = new ArrayList( getGraphObjectCount() );
      Iterator objs = getGraphObjectIterator();
      while ( objs.hasNext() ) {
        Object obj = objs.next();
        try {
          if ( filter.passesFilter( obj ) )
            list.add( obj );
        } catch ( Exception nulle ) {}
      }
      tableModel.setTableDataObjects( list );
    }
    
    if ( e.getSource() == networkBox ) {
      String network_id = ( String )titleIdMap.get( networkBox.getSelectedItem() );
      CyNetwork network = Cytoscape.getNetwork( network_id );
      System.out.println( "Showing all that Pass Network: "+network );
      tableModel.setTableDataObjects( getGraphObjectList(network) );
    }
  }

  private List getGraphObjectList ( CyNetwork network) {
    if ( graphObjectType == NODES ) 
      return network.nodesList();
    else 
      return network.edgesList();
  }

  private Iterator getGraphObjectIterator () {
    if ( graphObjectType == NODES ) 
      return Cytoscape.getRootGraph().nodesIterator();
    else 
      return Cytoscape.getRootGraph().edgesIterator();
  }

  private int getGraphObjectCount () {
    if ( graphObjectType == NODES ) 
      return Cytoscape.getRootGraph().getNodeCount();
    else 
      return Cytoscape.getRootGraph().getEdgeCount();
  }
    



  public void propertyChange ( PropertyChangeEvent e ) {

    
    if ( e.getPropertyName().equals( Cytoscape.NETWORK_CREATED ) ||  e.getPropertyName().equals( Cytoscape.NETWORK_DESTROYED ) ) {
      updateNetworkBox();
      
    }

    else if ( e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED 
              || e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED ) {
      if ( current_network != null ) {
        current_network.removeFlagEventListener( this );
      }
      current_network = Cytoscape.getCurrentNetwork();
      current_network.addFlagEventListener( this );


    }


  }
 protected void updateNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    while ( i.hasNext() ) {
      //System.out.println( i.next().getClass() );
      CyNetwork net = ( CyNetwork )i.next();
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    networkBox.setModel( model );
  }

   protected JComboBox getNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    while ( i.hasNext() ) {
      CyNetwork net = ( CyNetwork )i.next();
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    return new JComboBox( model );
  }


}
