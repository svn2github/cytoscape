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

import cytoscape.data.*;

/**
 * Filter that flags according to relations between nodes.
 * <p>
 * Executes only one passage.
 * <p>
 * Flaged nodes are those which don't have the required 
 * number of neighbors within the given depth, 
 * after one fair passage.
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class TopologyOnePassFilter extends Filter {
    /**
     * Required minimum number of neighbors.
     */
    int nNeighbors;
    /**
     * Required maximum depth to be considered a neighbor.
     */
    int maxDepth;
    /**
     * Filter limiting the nodes to be considered neighbors to those flagged.
     */
    Filter neighborF;

    public TopologyOnePassFilter(Graph2D graph,
				 Filter flaggableF,
				 int nNeighbors, 
				 int maxDepth,
				 Filter neighborF) {
	super(graph, flaggableF);

	this.nNeighbors = nNeighbors;
	this.maxDepth = maxDepth;
	this.neighborF = neighborF;
    }

    public TopologyOnePassFilter(Graph2D graph,
				 int nNeighbors, 
				 int maxDepth,
				 Filter neighborF) {
	super(graph);

	this.nNeighbors = nNeighbors;
	this.maxDepth = maxDepth;
	this.neighborF = neighborF;
    }

    public TopologyOnePassFilter(Graph2D graph,
				 int nNeighbors, 
				 int maxDepth) {
	super(graph);

	this.nNeighbors = nNeighbors;
	this.maxDepth = maxDepth;
	this.neighborF = null;
    }


    public NodeList get(NodeList hidden) {
	NodeList flagged = new NodeList();
	NodeList neighbor = new NodeList();
	NodeList flaggable = getFlaggableF().get(hidden);

	if (neighborF != null) {
	    neighbor = neighborF.get(hidden);
	}

	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();

	    if (!hidden.contains(node)
		&& (allFlaggable() || flaggable.contains(node))) {
		int countNeighbors = 0;

		try {
		    NodeList reached = Filter.bfsDepth(node, maxDepth);
		    for (NodeCursor vc = reached.nodes(); vc.ok(); vc.next()) {
			Node v = vc.node();
			
			// Don't count the node itself as its neighbor!
			if (v != node 
			    && !hidden.contains(v) 
			    && (neighborF == null || neighbor.contains(v))) {
			    countNeighbors++;
			    if (countNeighbors >= nNeighbors) {
				// No need to go on
				break;
			    }
			}
		    }
		} catch (NullPointerException e) {
		    System.out.println("!!! ERROR !!! NullPointerException !!!");
		}

		if (countNeighbors < nNeighbors) {
		    flagged.add(node);
		}

	    }
	}

	return flagged;
    }

}


