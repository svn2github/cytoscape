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
 * Filter which flags all nodes not flagged by a given filter.
 *
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class NegFilter extends Filter {
    Filter posF;

    public NegFilter(Graph2D graph, Filter posF) {
	super(graph);

	this.posF = posF;
    }

    public NegFilter(Graph2D graph, 
		     Filter flaggableF,
		     Filter posF) {
	super(graph, flaggableF);

	this.posF = posF;
    }

    public NodeList get(NodeList hidden) {
	NodeList flagged = new NodeList();
	NodeList pos = posF.get(hidden);
	NodeList flaggable = getFlaggableF().get(hidden);

	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    // Don't include nodes 
	    // flagged by the positive filter
	    // and the hidden list
	    if (!pos.contains(node) && !hidden.contains(node)
		&& (allFlaggable() || flaggable.contains(node))) {
		flagged.add(node);
	    }
	}
	return flagged;
    }

}


