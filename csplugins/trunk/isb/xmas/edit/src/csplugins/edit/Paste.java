package csplugins.edit;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cern.colt.list.*;
import cern.colt.map.*;
 
import javax.swing.undo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;

public class Paste extends CytoscapeAction {

  public Paste ( ImageIcon icon ) {
    super( "Paste Nodes and Edges", icon );
    setPreferredMenu( "Edit" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_V,  ActionEvent.CTRL_MASK );
  }


  public void actionPerformed ( ActionEvent ev ) {

    final CyNetwork network = Cytoscape.getCurrentNetwork();
    final int[] nodes = EditPlugin.getNodeClipBoard().elements();
    
    network.restoreNodes( nodes, true );
    
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

    CyNetworkView new_view = Cytoscape.getNetworkView( network.getIdentifier() );
    if ( new_view != null ) {
      if ( EditPlugin.getNetworkClipBoard() != network.getIdentifier() ) {
        CyNetworkView old_view = Cytoscape.getNetworkView( EditPlugin.getNetworkClipBoard() );
        if ( old_view != null ) {
        //ok, we are going to attempt to get the old postions for the just added nodes
          for ( int i = 0; i < nodes.length; ++i ) {
            double x = old_view.getNodeDoubleProperty( nodes[i], CyNetworkView.NODE_X_POSITION );
            double y = old_view.getNodeDoubleProperty( nodes[i], CyNetworkView.NODE_Y_POSITION );

            new_view.setNodeDoubleProperty( nodes[i], CyNetworkView.NODE_X_POSITION, x );
            new_view.setNodeDoubleProperty( nodes[i], CyNetworkView.NODE_Y_POSITION, y );
            new_view.getNodeView( nodes[i] ).setNodePosition( false );
          }
        }
      }
    }
          


    EditPlugin.addEdit(new AbstractUndoableEdit () {
         
        
        final String network_id = network.getIdentifier();

        public String	getPresentationName () {
          return "Paste";
        }
          
        public String getRedoPresentationName () {
          if ( edges.length == 0 )
            return "Redo: Paste Nodes";
          else 
            return "Redo: Paste Nodes and Edges";
        }
        
        public String getUndoPresentationName () {
         
          if ( edges.length == 0 )
            return "Undo: Paste Nodes";
          else 
            return "Undo: Paste Nodes and Edges";
            
        }
        
        public void	redo () {
          // removes the removed nodes and edges to the network
          CyNetwork network = Cytoscape.getNetwork( network_id );
          if ( network != null ) {
           network.restoreNodes( nodes, true );
          }
            
        }
        
        public void undo () {
          CyNetwork network = Cytoscape.getNetwork( network_id );
          if ( network != null ) {
            network.hideNodes( nodes );
            EditPlugin.getNodeClipBoard().elements( nodes );
            EditPlugin.getEdgeClipBoard().elements( edges );
          }
        }

         
      } ); 

  }

                                         



}
