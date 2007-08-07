//
// RealizerUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;
import y.view.*;
import java.util.*;

/**
 * Backs up and saves Node/Edge realizers
 */
public class RealizerUndoItem implements UndoItem {

    /**
     * The graph whose realizer state will be saved.
     */
    Graph2D graph;

    /**
     * Hash of Node->NodeRealizer for layout backup.
     */
    HashMap nodeRealizers;

    /**
     * Hash of Edge->EdgeRealizer for layout backup.
     */
    HashMap edgeRealizers;

    /**
     * The default node realizer
     */
    NodeRealizer defaultNodeRealizer;

    /**
     * The default edge realizer
     */
    EdgeRealizer defaultEdgeRealizer;


    /**
     * Another RealizerUndoItem to undo the undo (i.e. redo)
     */
    RealizerUndoItem redo;

    /**
     * This constructor creates a copy of all the node and edge
     * realizers in the graph, and saves them in the appropriate
     * hashmaps.
     */
    public RealizerUndoItem (Graph2D graph) {
	this.graph = graph;

	defaultNodeRealizer = graph.getDefaultNodeRealizer().createCopy();
	defaultEdgeRealizer = graph.getDefaultEdgeRealizer().createCopy();
	
	nodeRealizers = new HashMap();
	edgeRealizers = new HashMap();
	
	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    NodeRealizer nr = graph.getRealizer(nc.node());
	    nodeRealizers.put(nc.node(), nr.createCopy());
	}

	for (EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    EdgeRealizer er = graph.getRealizer(ec.edge());
	    edgeRealizers.put(ec.edge(), er.createCopy());
	}
    }


    /**
     * Apply the state contained within this UndoItem.  This is
     * implemented by restoring all of the node and edge realizers to
     * their state upon creation.
     */
    public boolean undo() {
	// save redo state
	redo = new RealizerUndoItem(graph);

	graph.setDefaultNodeRealizer(defaultNodeRealizer);
	graph.setDefaultEdgeRealizer(defaultEdgeRealizer);
	
	Node[] nodes = (Node[]) nodeRealizers.keySet().toArray(new Node[0]);
	Edge[] edges = (Edge[]) edgeRealizers.keySet().toArray(new Edge[0]);

	for (int i = 0; i < nodes.length; i++) {
	    graph.setRealizer(nodes[i],
			      (NodeRealizer)nodeRealizers.get(nodes[i]));
	}

	for (int i = 0; i < edges.length; i++) {
	    graph.setRealizer(edges[i],
			      (EdgeRealizer)edgeRealizers.get(edges[i]));
	}
	
	return true;
    }


    /**
     * Returns the graph to the state pre-undo().
     */
    public boolean redo() {
	return redo.undo();
    }
}
