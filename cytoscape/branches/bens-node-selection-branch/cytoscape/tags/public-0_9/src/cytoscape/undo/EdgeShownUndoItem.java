//
// EdgeShownUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for edge shown using UndoableGraphHider.
 */
public class EdgeShownUndoItem implements UndoItem {

    Edge edge;
    UndoableGraphHider hider;

    EdgeShownUndoItem (UndoableGraphHider hider, Edge edge) {
	this.edge  = edge;
	this.hider = hider;
    }

    /**
     * Removes the edge from the graph
     */
    public boolean undo() {
	hider.undoUnhide(edge);
	return true;
    }

    /**
     * Re-inserts the edge into the graph
     */
    public boolean redo() {
	hider.redoUnhide(edge);
	return true;
    }
}

