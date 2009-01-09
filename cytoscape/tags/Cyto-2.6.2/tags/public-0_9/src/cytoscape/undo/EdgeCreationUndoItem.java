//
// EdgeCreationUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for edge creation
 */
public class EdgeCreationUndoItem implements UndoItem {

    Graph graph;
    Edge edge;

    EdgeCreationUndoItem (Graph graph, Edge edge) {
	this.graph = graph;
	this.edge  = edge;
    }

    /**
     * Removes the edge from the graph
     */
    public boolean undo() {
	graph.removeEdge(edge);
	return true;
    }

    /**
     * Re-inserts the edge into the graph
     */
    public boolean redo() {
	graph.reInsertEdge(edge);
	return true;
    }
}

