//
// NodeHiddenUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;

/**
 * Supports undo for node hiding using UndoableGraphHider.
 */
public class NodeHiddenUndoItem implements UndoItem {

    Node node;
    UndoableGraphHider hider;

    NodeHiddenUndoItem ( UndoableGraphHider hider, Node node) {
	this.node  = node;
	this.hider = hider;
    }

    /**
     * Removes the node from the graph
     */
    public boolean undo() {
	hider.undoHide(node);
	return true;
    }

    /**
     * Re-inserts the node into the graph
     */
    public boolean redo() {
	hider.redoHide(node);
	return true;
    }
}

