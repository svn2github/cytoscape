//

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// UndoManager.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import java.util.*;


/**
 * This class provides the facility to keep an undo history of any
 * given size, and to undo and redo registered changes.
 *
 * To do this, the class keeps a list of UndoItem's.  Each one is
 * responsible for returning the graph to a given state given the
 * preceding / following events.
 */
public class UndoManager {
    /**
     * A vector of UndoItem's.
     */
    Vector history;

    /**
     * Current position in the history list.
     */
    int historyPosition;

    /**
     * The maximum size of the undo history stack.
     */
    static final int MAX_HISTORY_SIZE = 20;

    /**
     * Indicates whether or not the UndoManager will accept
     * saveState() requests.
     */
    int pauseDepth;

    /**
     * Indicates whether or not the UndoManager will concatenate
     * incoming states onto the same single UndoItemList.
     */
    int squishDepth;


    /**
     * List of undo items sandwhich into a single element
     */
    UndoItemList itemList;

    
    /**
     * Default constructor.
     */
    public UndoManager () {
	history = new Vector();
	historyPosition = 0;
	pauseDepth = 0;
	itemList = null;
    }

    
    /**
     * Apply's the state from the history list before the current
     * state
     */
    public boolean undo () {
	if (historyPosition == 0)
	    return false;

	pause();
	UndoItem ui = (UndoItem) history.get(--historyPosition);
	boolean result = ui.undo();
	resume();
	
	return result;
    }

    /**
     * Re-apply's the following state in the history list.
     */
    public boolean redo () {
	if (historyPosition == history.size())
	    return false;

	pause();
	UndoItem ui = (UndoItem) history.get(historyPosition++);
	boolean result = ui.redo();
	resume();

	return result;
    }

    public int undoLength() {
	return historyPosition;
    }

    public int redoLength() {
	return (history.size() - historyPosition);
    }

    
    /**
     * Saves the given UndoItem into the history list.
     */
    public void saveState(UndoItem item) {
	if (pauseDepth>0)
	    return;

	if (itemList != null) {
	    itemList.add(item);
	} else {

	    // if we are midway through the history list, discard
	    // following history items
	    while (historyPosition < history.size()) {
		history.remove(historyPosition);
	    }

	    history.add(item);

	    // if we are already at the maximum allowed size, remove first
	    // node.  otherwise, increment historyPosition counter
	    if (historyPosition == MAX_HISTORY_SIZE)
		history.remove(0);
	    else
		historyPosition++;
	}
    }

    /**
     * Clears the undo history and reinitializes corresponding
     * structures.
     */
    public void clearHistory() {
	historyPosition = 0;
	history.removeAllElements();
    }

    /**
     * Informs the undo manager that it should not accept more state
     * save requests until a corresponding resume() has been called
     */
    public void pause () {
	pauseDepth++;
    }

    /**
     * Informs the undo manager to start accepting state save requests
     * again.
     */
    public void resume () {
	pauseDepth--;
    }

    /**
     * Informs the undo manager to squish the following saveState()
     * calls into a single undo item, until a corresponding un-squish
     * is called.
     */
    public void beginUndoItemList() {
	squishDepth++;

	if (itemList == null)
	    itemList = new UndoItemList();
    }

    /**
     * Adds the UndoItemList as a saved state and continues saving
     * state onto the stack as normal.
     */
    public void endUndoItemList() {
	squishDepth--;

	if (squishDepth == 0) {
	    UndoItemList list = itemList;
	    itemList = null;

	    saveState(list);
	}
    }
}


