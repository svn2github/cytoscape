//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import y.base.*;
import y.view.Graph2D;

import giny.view.*;
import java.util.*;

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class AlignVerticalAction extends AbstractAction {
    NetworkView networkView;
    
    public AlignVerticalAction(NetworkView networkView) {
        super("Vertical");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {

      // Y-Files check
      if ( networkView.getCytoscapeObj().getConfiguration().isYFiles() ) {
        
        // remember state for undo - dramage 2002-08-22
        //networkView.getUndoManager().saveRealizerState();
        //networkView.getUndoManager().pause();
        String callerID = "AlignVerticalAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);

        Graph2D graph = networkView.getNetwork().getGraph();
        // compute average X coordinate
        double avgXcoord=0;
        int numSelected=0;
        for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
            Node n = nc.node();
            if (graph.isSelected(n)) {
                avgXcoord += graph.getX(n);
                numSelected++;
            }
        }
        avgXcoord /= numSelected;
        
        // move all nodes to average X coord
        for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
            Node n = nc.node();
            if (graph.isSelected(n))
                graph.setLocation(n, avgXcoord, graph.getY(n));
        }

        // resume undo manager's listener - dramage
        //networkView.getUndoManager().resume();

        networkView.redrawGraph(false, false);
        networkView.getNetwork().endActivity(callerID);
      } else {
        // GINY
        // start activity
             
        GraphView view = networkView.getView();
        double avgXcoord=0;
        
        List selected_nodes = view.getSelectedNodes();
        Iterator node_iterator;
        
        node_iterator = selected_nodes.iterator();
        while( node_iterator.hasNext() ) {
          avgXcoord += ( ( NodeView )node_iterator.next() ).getXPosition();
        }

        node_iterator = selected_nodes.iterator();
        while( node_iterator.hasNext() ) {
          NodeView nv = ( NodeView )node_iterator.next();
          nv.setXPosition( avgXcoord ); 
        }

      }
    }
}

