package cytoscape.filters;

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
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/


import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import java.util.Hashtable;

import cytoscape.data.*;

/**
 * Class, which applies a same filter again and again 
 * until no nodes are getting filtered.
 * <p>
 * Used  in {@link TopologyFilter}.
 * 
 * @author namin@mit.edu
 * @version 2002-02-02
 */
public class StableFilter extends Filter {
    /**
     * Filter to apply again and again.
     */
    private Filter f;

    public StableFilter(Graph2D graph, Filter f) {
	super(graph);
	this.f = f;
    }

    public NodeList get(NodeList hidden) {
	NodeList flagged = new NodeList();
	NodeList newlyFlagged;

	// "hidden" needs to be locally modified,
	// thus create a copy.
	NodeList hiddenTmp = new NodeList(hidden.nodes());

	boolean noneFlagged = false;
	while (!noneFlagged) {
	    noneFlagged = true;
	    newlyFlagged = f.get(hiddenTmp);
	    if (newlyFlagged.size() > 0) {
		// "splice" _transfers_ the content of one list to another;
		// copy the content.
		hiddenTmp.splice(new NodeList(newlyFlagged.nodes()));
		flagged.splice(new NodeList(newlyFlagged.nodes()));
		noneFlagged = false;		
	    } 
	}
	return flagged;
    }
}


