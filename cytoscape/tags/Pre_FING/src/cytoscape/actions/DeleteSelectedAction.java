//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.util.*;

import giny.model.GraphPerspective;
import giny.view.GraphView;

import cytoscape.CyNetwork;
import cytoscape.view.NetworkView;
import cytoscape.data.GraphObjAttributes;
//-------------------------------------------------------------------------
/**
 * Giny version of the original class. Note that the original version was
 * only available when editing mode was enabled, and caused the selected
 * nodes to be permanently removed from the graph (and, necessarily, the view).
 * This version hides the selected nodes from both the graph and the view,
 * as there are currently no methods to remove a node view from the graph view
 * in Giny. The semantics of this and related classes for modifying the
 * graph and view should be clarified.
 */
public class DeleteSelectedAction extends AbstractAction {
    NetworkView networkView;
    
    public DeleteSelectedAction(NetworkView networkView) {
        super("Delete Selected Nodes and Edges");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        String callerID = "DeleteSelectedAction.actionPerformed";
        networkView.getNetwork().beginActivity(callerID);
        
        GraphView view = networkView.getView();
        GraphPerspective perspective = view.getGraphPerspective();
        // get the Selected node and edge indices
        int[] node_indicies = view.getSelectedNodeIndices();
        int[] edge_indicies = view.getSelectedEdgeIndices();
        //and the node/edge vew objects
        List selected_nodeViews = view.getSelectedNodes();
        List selected_edgeViews = view.getSelectedEdges();

        // Hide the viewable things and the perspective refs
        view.hideGraphObjects( selected_nodeViews );
        view.hideGraphObjects( selected_edgeViews );
        perspective.hideEdges( edge_indicies );
        perspective.hideNodes( node_indicies );
        
        networkView.redrawGraph(false, false);
        networkView.getNetwork().endActivity(callerID);
    } // actionPerformed
} // inner class DeleteSelectedAction
