package csplugins.edit;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cern.colt.list.*;
import cern.colt.map.*;
 
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;

public class Cut extends CytoscapeAction {

  public Cut ( ImageIcon icon ) {
    super( "Cut Nodes and Connecting Edges", icon );
    setPreferredMenu( "Edit" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_X,  ActionEvent.CTRL_MASK );
  }


  public void actionPerformed ( ActionEvent ev ) {

    final CyNetwork network = Cytoscape.getCurrentNetwork();
    final int[] nodes = network.getFlaggedNodeIndicesArray();

    OpenIntIntHashMap emap = new OpenIntIntHashMap(nodes.length*2);
    for ( int i = 0; i < nodes.length; ++i ) {
      int[] e = network.getAdjacentEdgeIndicesArray(nodes[i], true, true, true );
      for ( int j = 0; j < e.length; ++j ) {
        emap.put( network.getRootGraphEdgeIndex(e[j]), 1 );
      }
    }
    final int[] edges = emap.keys().elements();
    // System.out.println( "Edges: "+edges.length );
//     for ( int i = 0; i < edges.length; ++i ) {
//       System.out.print( " "+edges[i] );
//     }
//     System.out.println( "" );
//     System.out.println( "Nodes: "+nodes.length );
//     for ( int i = 0; i < nodes.length; ++i ) {
//       System.out.print( " "+nodes[i] );
//     }

    //final int[] e = network.getConnectingEdgeIndicesArray( n );
   
    //network.hideEdges( e );
    network.hideNodes( nodes );
   
    EditPlugin.getNodeClipBoard().elements( nodes );
    EditPlugin.getEdgeClipBoard().elements( edges );
    EditPlugin.setNetworkClipBoard( network.getIdentifier() );
    

    Cytoscape.getDesktop().addEdit(new AbstractUndoableEdit () {
         
        
        final String network_id = network.getIdentifier();

        public String	getPresentationName () {
          return "Cut";
        }
          
        public String getRedoPresentationName () {
          if ( edges.length == 0 )
            return "Redo: Cut Nodes";
          else 
            return "Redo: Cut Nodes and Edges";
        }
        
        public String getUndoPresentationName () {
         
          if ( edges.length == 0 )
            return "Undo: Cut Nodes";
          else 
            return "Undo: Cut Nodes and Edges";
            
        }
        
        public void	redo () {
          // removes the removed nodes and edges to the network
          CyNetwork network = Cytoscape.getNetwork( network_id );
          if ( network != null ) {
            network.hideEdges( edges );
            network.hideNodes( nodes );
            EditPlugin.getNodeClipBoard().elements( nodes );
            EditPlugin.getEdgeClipBoard().elements( edges );
          }
            
        }
        
        public void undo () {
          CyNetwork network = Cytoscape.getNetwork( network_id );
          if ( network != null ) {
            network.restoreNodes( nodes );
            network.restoreEdges( edges );
          }
        }

         
      } ); 

  }

                                         



}
