//
// EdgeRemovalUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for edge removal.
 */
public class EdgeRemovalUndoItem implements UndoItem {

    Graph graph;
    Edge edge;

    EdgeRemovalUndoItem (Graph graph, Edge edge) {
	this.graph = graph;
	this.edge  = edge;
    }

    /**
     * Removes the edge from the graph
     */
    public boolean undo() {
	graph.reInsertEdge(edge);
	return true;
    }

    /**
     * Re-inserts the edge into the graph
     */
    public boolean redo() {
	graph.removeEdge(edge);
	return true;
    }
}

