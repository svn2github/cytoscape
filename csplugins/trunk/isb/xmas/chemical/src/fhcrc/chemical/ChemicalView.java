package fhcrc.chemical;

import cytoscape.*;
import cytoscape.data.*;
import filter.view.*;
import filter.model.*;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;

public class ChemicalView 
  extends 
    JFrame 
  implements 
    ActionListener {

  FilterListPanel filterListPanel;
  JButton run;

  public ChemicalView () {
    super( "Chemical Genetics" );
    initialize();
  }

  protected void initialize () {

    
    filterListPanel = new FilterListPanel();
    run = new JButton( "run" );
    run.addActionListener( this );

    JPanel panel = new JPanel();
    panel.add( filterListPanel );
    panel.add( run );

    getContentPane().add( panel );
    pack();
    
  }

  public void actionPerformed ( ActionEvent e ) {

    if ( e.getSource() == run ) {
      runAlgo();
    }
  }
  
  protected void runAlgo () {

    System.out.println( "Running Algo" );

    CyNetwork network = Cytoscape.getCurrentNetwork();
    Filter filter = filterListPanel.getSelectedFilter();
    Set passes = new HashSet();
    List nodes_l = network.nodesList();
    Iterator nodes_i = nodes_l.iterator();

    while ( nodes_i.hasNext() ) {
      CyNode node = ( CyNode )nodes_i.next();
      try {
        if ( filter.passesFilter( node ) ) {
          passes.add( node );
          System.out.println( node.getIdentifier()+" passed filter: "+filter );
        }
      } catch(StackOverflowError soe){
        return ;
      }
    }

    Map score = new HashMap();
    nodes_i = nodes_l.iterator();
    while ( nodes_i.hasNext() ) {
      CyNode node = ( CyNode )nodes_i.next();
      List neighbors = network.neighborsList( node );
      for ( Iterator i = neighbors.iterator(); i.hasNext(); ) {
        CyNode neighbor = ( CyNode )i.next();
        if ( passes.contains( neighbor ) ) {
          // node is a neighbor of a passed node
          if ( score.get( node ) == null ) {
            List list = new ArrayList();
            list.add( neighbor );
            System.out.println( node.getIdentifier()+" neighbor of :"+neighbor );
            score.put( node, list );
          } else {
            ( ( List )score.get( node ) ).add( neighbor );
            System.out.println( node.getIdentifier()+" neighbor of :"+neighbor );
          }
        }
      }
    }
    

    // now put the results into attributes
    Iterator keys = score.keySet().iterator();
    while ( keys.hasNext() ) {
      CyNode node = ( CyNode )keys.next();
      System.out.println( node.getIdentifier()+" having attributes set" );
      if ( score.get( node ) == null ) {
        Cytoscape.setNodeAttributeValue( node, "chemical_score", new Integer(0) );
      } else {
        List list = ( List )score.get( node );
        Cytoscape.setNodeAttributeValue( node, "chemical_score", new Integer(list.size()) );
        Cytoscape.setNodeAttributeValue( node, "chemical_list", list );
        StringBuffer b = new StringBuffer();
        for ( Iterator i = list.iterator(); i.hasNext(); ) {
          b.append( Cytoscape.getNodeAttributeValue( ( CyNode )i.next(),
                                                     Semantics.COMMON_NAME ) +" ");
        }
        Cytoscape.setNodeAttributeValue( node, "chemical_names", b.toString() );
      }
    }
    Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED, null, null );

  }


  private List neighborsViaEdgeFilter ( CyNetwork network, CyNode node, Filter edge_filter, Filter node_filter ) {

    Set passed_neighbors = new TreeSet();
    List adj_edges = network.getAdjacentEdgesList( node, true, true, true );
    for ( Iterator i = adj_edges.iterator(); i.hasNext(); ) {

      CyEdge edge = ( CyEdge )i.next();
    }
    return adj_edges;
  }


}
