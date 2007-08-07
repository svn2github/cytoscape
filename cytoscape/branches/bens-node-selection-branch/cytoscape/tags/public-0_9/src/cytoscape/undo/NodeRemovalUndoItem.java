//
// NodeRemovalUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for node removal.
 */
public class NodeRemovalUndoItem implements UndoItem {

    Graph graph;
    Node node;

    NodeRemovalUndoItem (Graph graph, Node node) {
	this.graph = graph;
	this.node  = node;
    }

    /**
     * Removes the node from the graph
     */
    public boolean undo() {
	graph.reInsertNode(node);
	return true;
    }

    /**
     * Re-inserts the node into the graph
     */
    public boolean redo() {
	graph.removeNode(node);
	return true;
    }
}

