package fhcrc.chemical;

import cytoscape.*;
import cytoscape.data.*;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.util.List;

import filter.model.*;
import filter.view.*;
import filter.cytoscape.*;

public class DrugScore {

  public static void computeDrugScore ( String drug_name,
                                        int cutoff,
                                        List interaction_types ) {

    System.out.println( "Running Algo" );
    ChemicalPlugin.addDrug( drug_name );

    CyNetwork network = Cytoscape.getCurrentNetwork();
    Set passes = new HashSet();
    List nodes_l = network.nodesList();
    Iterator nodes_i = nodes_l.iterator();

    while ( nodes_i.hasNext() ) {
      CyNode node = ( CyNode )nodes_i.next();
      try {
        double value = ( ( Double )Cytoscape.getNodeAttributeValue( node, drug_name ) ).doubleValue();
        if ( value >= cutoff ) {
          passes.add( node );
          System.out.println( node.getIdentifier()+" passed cutoff: "+cutoff );
        }
      } catch ( Exception soe){
        
      }
    }

    Map score = new HashMap();
    nodes_i = nodes_l.iterator();
    while ( nodes_i.hasNext() ) {
      // get the node
      CyNode node = ( CyNode )nodes_i.next();
      
      // find only the nodes that are neighbors with a good edge
      List adj = network.getAdjacentEdgesList( node, true, true, true );
      Set neighbors = new HashSet();
      for ( Iterator adj_i = adj.iterator(); adj_i.hasNext(); ) {
        CyEdge edge = ( CyEdge )adj_i.next();
        String type = ( String )Cytoscape.getEdgeAttributeValue( edge, Semantics.INTERACTION );
        if ( interaction_types.contains( type ) ) {
          if ( edge.getSource() == node ) {
            neighbors.add( edge.getTarget() );
          } else {
            neighbors.add( edge.getSource() );
          }
        }
      }

      // iterate through the neighbors
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
    Iterator keys = nodes_l.iterator();//score.keySet().iterator();
    while ( keys.hasNext() ) {
      CyNode node = ( CyNode )keys.next();
      System.out.println( node.getIdentifier()+" having attributes set" );
      if ( score.get( node ) == null ) {
        System.out.println( node.getIdentifier()+ " had no score setting to zero." );
        Cytoscape.setNodeAttributeValue( node, drug_name+"_score", new Double(0) );
      } else {
        List list = ( List )score.get( node );
        System.out.println( node.getIdentifier()+ " had score setting to: "+list.size() );
        Cytoscape.setNodeAttributeValue( node, drug_name+"_score", new Double(list.size()) );
        Cytoscape.setNodeAttributeValue( node, drug_name+"_list", list );
        StringBuffer b = new StringBuffer();
        for ( Iterator i = list.iterator(); i.hasNext(); ) {
          b.append( Cytoscape.getNodeAttributeValue( ( CyNode )i.next(),
                                                     Semantics.COMMON_NAME ) +" ");
        }
        Cytoscape.setNodeAttributeValue( node, drug_name+"_names", b.toString() );
      }
    }
    Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED, null, null );



  }



}
