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

import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class AlignHorizontalAction extends AbstractAction {
    NetworkView networkView;
    
    public AlignHorizontalAction(NetworkView networkView) {
        super("Horizontal");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        // remember state for undo - dramage 2002-08-22
        //networkView.getUndoManager().saveRealizerState();
        //networkView.getUndoManager().pause();
        //now we just notify the network
        String callerID = "AlignHorizontalAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);

        Graph2D graph = networkView.getNetwork().getGraph();
        // compute average Y coordinate
        double avgYcoord=0;
        int numSelected=0;
        for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
            Node n = nc.node();
            if (graph.isSelected(n)) {
                avgYcoord += graph.getY(n);
                numSelected++;
            }
        }
        avgYcoord /= numSelected;
        
        // move all nodes to average Y coord
        for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
            Node n = nc.node();
            if (graph.isSelected(n))
                graph.setLocation(n, graph.getX(n), avgYcoord);
        }

        // resume undo manager's listener - dramage
        //networkView.getUndoManager().resume();

        networkView.redrawGraph(false, false);
        networkView.getNetwork().endActivity(callerID);
    }
}

