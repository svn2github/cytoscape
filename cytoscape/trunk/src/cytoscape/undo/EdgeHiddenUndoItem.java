//
// EdgeHiddenUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for edge hiding using UndoableGraphHider.
 */
public class EdgeHiddenUndoItem implements UndoItem {

    Edge edge;
    UndoableGraphHider hider;

    EdgeHiddenUndoItem (UndoableGraphHider hider, Edge edge) {
	this.edge  = edge;
	this.hider = hider;
    }

    /**
     * Removes the edge from the graph
     */
    public boolean undo() {
	hider.undoHide(edge);
	return true;
    }

    /**
     * Re-inserts the edge into the graph
     */
    public boolean redo() {
	hider.redoHide(edge);
	return true;
    }
}

