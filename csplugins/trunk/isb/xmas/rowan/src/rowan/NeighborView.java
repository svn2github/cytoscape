package rowan;


// cytoscape import
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;

// giny import
import giny.model.*;

//colt import
import cern.colt.map.*;
import cern.colt.list.*;

// plugin import
import filter.model.*;
import filter.view.*;

// java import
import java.awt.BorderLayout;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;


public class NeighborView 
  extends 
    JFrame
  implements
    ActionListener, 
    PropertyChangeListener {

  // variables

  JComboBox networkBoxFrom, networkBoxTo, neighborCountBox;
  FilterListPanel filterListPanel;
  JButton apply;
  HashMap titleIdMap;
  JRadioButton allNodes, selectedNetwork;


  public NeighborView () {
    super( "Add Neighbors" );
    initialize();
  }

  protected void initialize () {

   Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this );
   titleIdMap = new HashMap();

   JPanel main_panel = new JPanel();
   main_panel.setLayout( new BorderLayout() );
   
   JPanel top_panel = new JPanel();
   networkBoxTo = getNetworkBox();
   top_panel.add( new JLabel( "Add Nodes to: "));
   top_panel.add( networkBoxTo );

   JPanel bottom_panel = new JPanel();
   filterListPanel = new FilterListPanel(1);
   bottom_panel.add( filterListPanel );


   JPanel side_panel = new JPanel();
   side_panel.setLayout( new BorderLayout() );

   JPanel c_p = new JPanel();
   Integer[] counts = new Integer[20];
   for ( int i = 0; i < 20; ++i ) {
     counts[i] = new Integer( i + 1 );
   }
   neighborCountBox = new JComboBox(counts);
   c_p.add( new JLabel( "Number of Neighbors" ) );
   c_p.add( neighborCountBox );
   side_panel.add( c_p, BorderLayout.NORTH );

   JPanel b_p = new JPanel();
   allNodes = new JRadioButton( "All Networks" ); 
   selectedNetwork = new JRadioButton( "Selected Network" );
   ButtonGroup group = new ButtonGroup();
   group.add( allNodes );
   group.add( selectedNetwork );
   b_p.add( allNodes );
   b_p.add( selectedNetwork );
   allNodes.setSelected( true );
   allNodes.addActionListener( this );
   selectedNetwork.addActionListener( this );
   

   networkBoxFrom = getNetworkBox();
   networkBoxFrom.setEnabled( false );

   side_panel.add( b_p, BorderLayout.CENTER );
   side_panel.add( networkBoxFrom, BorderLayout.SOUTH );

   main_panel.add( top_panel, BorderLayout.NORTH );
   main_panel.add( bottom_panel, BorderLayout.WEST );
   main_panel.add( side_panel, BorderLayout.CENTER );

   apply = new JButton( "Apply" );
   apply.addActionListener( this );
   main_panel.add( apply, BorderLayout.SOUTH );

   setContentPane( main_panel );
   pack();
   setVisible( true );
   



  }

  public void actionPerformed ( ActionEvent e ) {

    if ( e.getSource() == selectedNetwork || e.getSource() == allNodes ) {
      if ( allNodes.isSelected() )
        networkBoxFrom.setEnabled( false );
      else
        networkBoxFrom.setEnabled( true );
    }

    if ( e.getSource() == apply ) {
      addNeighbors();
    }

  }

  public  void addNeighbors() {

    String network_id_to = ( String )titleIdMap.get( networkBoxTo.getSelectedItem() );
    String network_id_from = ( String )titleIdMap.get( networkBoxFrom.getSelectedItem() );

    CyNetwork to_network = Cytoscape.getNetwork( network_id_to );
    CyNetwork from_network = Cytoscape.getNetwork( network_id_from );

    int iterations = ( ( Integer )neighborCountBox.getSelectedItem() ).intValue();

    Filter filter = filterListPanel.getSelectedFilter();


    for ( int i = 0; i < iterations; ++i ) {
      if ( allNodes.isSelected() ) {
        // find all neighbors in the root graph
        RootGraph root = Cytoscape.getRootGraph();
        int[] adj_edges;
        OpenIntIntHashMap nodes_to_add = new OpenIntIntHashMap();
        Iterator nodes_i = to_network.nodesIterator();
        while ( nodes_i.hasNext() ) {
                    
          Node node = ( Node )nodes_i.next();
          adj_edges = root.getAdjacentEdgeIndicesArray( node.getRootGraphIndex(), true, true, true );
          nodes_to_add.put( node.getRootGraphIndex(), 1 );
          for ( int e = 0; e < adj_edges.length; ++e ) {
            int source = root.getEdgeSourceIndex( adj_edges[e] );
            int target = root.getEdgeTargetIndex( adj_edges[e] );
            if (  filter == null || filter.passesFilter( root.getNode( source ) ) )
              nodes_to_add.put( source, 1 );
            if (  filter == null || filter.passesFilter( root.getNode( target ) ) )
              nodes_to_add.put( target, 1 );
          }
        }
        IntArrayList node_s = nodes_to_add.keys();
        node_s.trimToSize();
        int[] nodes = node_s.elements();
        int[] edges = root.getConnectingEdgeIndicesArray( nodes );

        to_network.restoreNodes( nodes );
        to_network.restoreEdges( edges );

      } else {
        // find all neighbors in the selected Network
        int[] adj_edges;
        OpenIntIntHashMap nodes_to_add = new OpenIntIntHashMap();
        Iterator nodes_i = to_network.nodesIterator();
        while ( nodes_i.hasNext() ) {
                    
          Node node = ( Node )nodes_i.next();
          adj_edges = from_network.getAdjacentEdgeIndicesArray( node.getRootGraphIndex(), true, true, true );
          nodes_to_add.put( node.getRootGraphIndex(), 1 );
          for ( int e = 0; e < adj_edges.length; ++e ) {
            int source = from_network.getEdgeSourceIndex( adj_edges[e] );
            int target = from_network.getEdgeTargetIndex( adj_edges[e] );
            if ( filter == null || filter.passesFilter( from_network.getNode( source ) ) )
              nodes_to_add.put( from_network.getRootGraphNodeIndex( source ), 1 );
            if ( filter == null || filter.passesFilter( from_network.getNode( target ) ) )
              nodes_to_add.put(  from_network.getRootGraphNodeIndex( target ), 1 );
          }
        }
        IntArrayList node_s = nodes_to_add.keys();
        node_s.trimToSize();
        int[] nodes = node_s.elements();
        int[] edges = from_network.getConnectingEdgeIndicesArray( nodes );
        for ( int j = 0; j < edges.length; ++j ) 
          edges[j] =  from_network.getRootGraphEdgeIndex( edges[j] );

        to_network.restoreNodes( nodes );
        to_network.restoreEdges( edges );
        
      }
    }
    


  }


  public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName().equals( Cytoscape.NETWORK_CREATED ) ||  e.getPropertyName().equals( Cytoscape.NETWORK_DESTROYED ) ) {
      updateNetworkBox();
    }
  }



  protected void updateNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    vector.add( "Current Network" );
    while ( i.hasNext() ) {
      //System.out.println( i.next().getClass() );
      CyNetwork net = Cytoscape.getNetwork( ( String )i.next() );
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    networkBoxFrom.setModel( model );
    model =new DefaultComboBoxModel( vector );
    networkBoxTo.setModel( model );
  }


  protected JComboBox getNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    vector.add( "Current Network" );
    while ( i.hasNext() ) {
      CyNetwork net = Cytoscape.getNetwork( ( String )i.next() );
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    return new JComboBox( model );
  }


}
