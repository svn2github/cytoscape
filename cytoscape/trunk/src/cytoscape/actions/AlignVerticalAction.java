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

import cytoscape.CytoscapeWindow;
//-------------------------------------------------------------------------
public class AlignVerticalAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public AlignVerticalAction(CytoscapeWindow cytoscapeWindow) {
        super("Vertical");
        this.cytoscapeWindow = cytoscapeWindow;
    }

    public void actionPerformed (ActionEvent e) {
        // remember state for undo - dramage 2002-08-22
        cytoscapeWindow.getUndoManager().saveRealizerState();
        cytoscapeWindow.getUndoManager().pause();

        Graph2D graph = cytoscapeWindow.getGraph();
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
        cytoscapeWindow.getUndoManager().resume();

        cytoscapeWindow.redrawGraph(false, false);
    }
}

