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
import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------
public class DeleteSelectedAction extends AbstractAction {
    CytoscapeWindow cytoscapeWindow;
    
    public DeleteSelectedAction(CytoscapeWindow cytoscapeWindow) {
        super("Delete Selected Nodes and Edges");
        this.cytoscapeWindow = cytoscapeWindow;
    }
    
    public void actionPerformed(ActionEvent e) {
        Graph2D graph = cytoscapeWindow.getGraph();
        GraphObjAttributes nodeAttributes = cytoscapeWindow.getNodeAttributes();
        GraphObjAttributes edgeAttributes = cytoscapeWindow.getEdgeAttributes();
        
        // added by dramage 2002-08-23
        graph.firePreEvent();
        
        int nodeNum = 0;
        NodeCursor nc = graph.selectedNodes(); 
        Node node;
        while (nc.ok()) {
            node = nc.node();
            graph.removeNode(node);
            nodeNum++;
            nodeAttributes.removeObjectMapping(node);
            //System.out.println("Removed node " + nodeNum);
            //System.out.flush();
            nc.next();
        } // while
        EdgeCursor ec = graph.selectedEdges();
        Edge edge;
        while (ec.ok()) {
            edge = ec.edge();
            graph.removeEdge(edge);
            ec.next();
            edgeAttributes.removeObjectMapping(edge);
        }
        
        // added by dramage 2002-08-23
        graph.firePostEvent();
        
        cytoscapeWindow.redrawGraph(false, false);
    } // actionPerformed
} // inner class DeleteSelectedAction
