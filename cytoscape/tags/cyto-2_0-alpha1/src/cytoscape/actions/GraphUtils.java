//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import y.base.*;
import y.view.Graph2D;
import y.view.NodeRealizer;
import y.view.EdgeRealizer;

import cytoscape.undo.UndoableGraphHider;
//-------------------------------------------------------------------------
public class GraphUtils {
    
    public static void hideSelectedNodes(Graph2D graph, UndoableGraphHider hider) {
        graph.firePreEvent();
        NodeCursor nc = graph.selectedNodes(); 
        while (nc.ok()) {
            Node node = nc.node();
            hider.hide(node);
            nc.next();
        }
        graph.firePostEvent();
    }

    public static void hideSelectedEdges(Graph2D graph, UndoableGraphHider hider) {
        graph.firePreEvent();
        EdgeCursor nc = graph.selectedEdges(); 
        while (nc.ok()) {
            Edge edge = nc.edge();
            hider.hide(edge);
            nc.next();
        }
        graph.firePostEvent();
    }
    
    public static void invertSelectedNodes(Graph2D graph) {
        graph.firePreEvent();
        Node[] nodes = graph.getNodeArray();
        
        for (int i=0; i < nodes.length; i++) {
            NodeRealizer nodeRealizer = graph.getRealizer(nodes[i]);
            nodeRealizer.setSelected(!nodeRealizer.isSelected());
        }
        graph.firePostEvent();
    }
    
    public static void invertSelectedEdges(Graph2D graph) {
        graph.firePreEvent();
        Edge[] edges = graph.getEdgeArray();
        
        for (int i=0; i < edges.length; i++) {
            EdgeRealizer edgeRealizer = graph.getRealizer(edges[i]);
            edgeRealizer.setSelected(!edgeRealizer.isSelected());
        }
        graph.firePostEvent();
    }
}

