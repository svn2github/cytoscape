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

import cytoscape.data.CyNetwork;
import cytoscape.view.NetworkView;
import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------
public class DeleteSelectedAction extends AbstractAction {
    NetworkView networkView;
    
    public DeleteSelectedAction(NetworkView networkView) {
        super("Delete Selected Nodes and Edges");
        this.networkView = networkView;
    }
    
    public void actionPerformed(ActionEvent e) {
        String callerID = "DeleteSelectedAction.actionPerformed";
        
        CyNetwork network = networkView.getNetwork();
        network.beginActivity(callerID); //also fires a graph pre-event
        Graph2D graph = network.getGraph();
        GraphObjAttributes nodeAttributes = network.getNodeAttributes();
        GraphObjAttributes edgeAttributes = network.getEdgeAttributes();
             
        NodeCursor nc = graph.selectedNodes(); 
        EdgeCursor ec = graph.selectedEdges();
        if (nc.size() == 0 && ec.size() == 0) {//nothing to do
            networkView.getNetwork().endActivity(callerID);
            return;
        }

        while (nc.ok()) {
            Node node = nc.node();
            graph.removeNode(node);
            nodeAttributes.removeObjectMapping(node);
            nc.next();
        } // while
        while (ec.ok()) {
            Edge edge = ec.edge();
            graph.removeEdge(edge);
            ec.next();
            edgeAttributes.removeObjectMapping(edge);
        }
        
        networkView.redrawGraph(false, false);
        network.endActivity(callerID);
    } // actionPerformed
} // inner class DeleteSelectedAction
