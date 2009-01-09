//
// UndoItemList.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import java.util.*;


/**
 * This class implements a collection of UndoItems, so that multiple
 * changes can be undone and redone at-a-once.
 */
public class UndoItemList extends Vector implements UndoItem {

    /**
     * Undo all items stored in the vector
     */
    public boolean undo () {
	for (int i = size()-1; i >= 0; i--) {
	    UndoItem ui = (UndoItem) elementAt(i);
	    if (ui.undo() == false)
		return false;
	}

	return true;
    }

    /**
     * Redo all items stored in the vector
     */
    public boolean redo () {
	for (int i = 0; i < size(); i++) {
	    UndoItem ui = (UndoItem) elementAt(i);
	    if (ui.redo() == false)
		return false;
	}

	return true;
    }
}
