//
// NodeShownUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for node shown using UndoableGraphHider.
 */
public class NodeShownUndoItem implements UndoItem {

    Node node;
    UndoableGraphHider hider;

    NodeShownUndoItem (UndoableGraphHider hider, Node node) {
	this.node  = node;
	this.hider = hider;
    }

    /**
     * Removes the node from the graph
     */
    public boolean undo() {
	hider.undoUnhide(node);
	return true;
    }

    /**
     * Re-inserts the node into the graph
     */
    public boolean redo() {
	hider.redoUnhide(node);
	return true;
    }
}

