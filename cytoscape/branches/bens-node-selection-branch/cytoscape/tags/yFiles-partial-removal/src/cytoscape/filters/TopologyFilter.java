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
 * Filter that flags according to relation between nodes.
 * Uses {@link StableFilter} to apply a topology filter
 * until no nodes are flagged.
 * <p>
 * Flagged nodes are those which don't have the required
 * number of neighbors within the given depth.
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class TopologyFilter extends Filter {
    Filter topoOnePassF;

    public TopologyFilter(Graph2D graph,
			  Filter flaggableF,
			  int nNeighbors, 
			  int maxDepth,
			  Filter neighborF) {
	super(graph);

	topoOnePassF = new TopologyOnePassFilter(graph,
						 flaggableF,
						 nNeighbors, maxDepth,
						 neighborF);
    }

    public TopologyFilter(Graph2D graph,
				 int nNeighbors, 
				 int maxDepth,
				 Filter neighborF) {
	super(graph);


	topoOnePassF = new TopologyOnePassFilter(graph,
						 nNeighbors, maxDepth,
						 neighborF);
    }

    public TopologyFilter(Graph2D graph,
				 int nNeighbors, 
				 int maxDepth) {
	super(graph);

	topoOnePassF = new TopologyOnePassFilter(graph,
						 nNeighbors, maxDepth);
    }


    public NodeList get(NodeList hidden) {
	Filter topoF = new StableFilter(graph, topoOnePassF);

	return topoF.get(hidden);
    }

}


