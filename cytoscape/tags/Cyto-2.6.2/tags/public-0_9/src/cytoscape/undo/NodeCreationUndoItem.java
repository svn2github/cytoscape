//
// NodeCreationUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for node creation
 */
public class NodeCreationUndoItem implements UndoItem {

    Graph graph;
    Node node;

    NodeCreationUndoItem (Graph graph, Node node) {
	this.graph = graph;
	this.node  = node;
    }

    /**
     * Removes the node from the graph
     */
    public boolean undo() {
	graph.removeNode(node);
	return true;
    }

    /**
     * Re-inserts the node into the graph
     */
    public boolean redo() {
	graph.reInsertNode(node);
	return true;
    }
}

