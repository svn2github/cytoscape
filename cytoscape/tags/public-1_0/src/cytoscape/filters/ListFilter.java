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


import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import cytoscape.data.*;

/**
 * Filter that flags a given list of nodes.
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class ListFilter extends Filter {
    NodeList flagged;

    public ListFilter(Graph2D graph, NodeList flagged) {
	super(graph);

	initialize(flagged);
    }

    public ListFilter(Graph2D graph, 
		      Filter flaggableF,
		      NodeList flagged) {
	super(graph, flaggableF);

	initialize(flagged);
    }

    private void initialize(NodeList init) {
	// COPY list, to avoid exterior modifications
	flagged = new NodeList(init.nodes());
    }

    public NodeList get(NodeList hidden) {
	NodeList tmp = new NodeList();
	NodeList flaggable = getFlaggableF().get(hidden);

	for (NodeCursor nc = flagged.nodes(); nc.ok(); nc.next()) {	
	    Node node = nc.node();
	    if (!tmp.contains(node)
		&& !hidden.contains(node)
		&& (allFlaggable() || flaggable.contains(node))) {
		tmp.add(node);
	    }
	}

	return tmp;
    }
}


