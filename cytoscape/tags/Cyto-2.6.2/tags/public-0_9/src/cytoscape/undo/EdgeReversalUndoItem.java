//
// EdgeReversalUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for edge reversing.
 */
public class EdgeReversalUndoItem implements UndoItem {

    Graph graph;
    Edge edge;

    EdgeReversalUndoItem (Graph graph, Edge edge) {
	this.graph = graph;
	this.edge  = edge;
    }

    /**
     * Removes the edge from the graph
     */
    public boolean undo() {
	graph.reverseEdge(edge);
	return true;
    }

    /**
     * Re-inserts the edge into the graph
     */
    public boolean redo() {
	graph.reverseEdge(edge);
	return true;
    }
}
