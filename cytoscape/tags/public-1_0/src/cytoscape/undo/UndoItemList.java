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


