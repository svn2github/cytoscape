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
import cytoscape.*;
/**
 * Filter that flags some nodes 
 * based on topology relative to nodes that meet expression requirements.
 * <p>
 * Flaged nodes are those which don't have the given number of 
 * expression conditions meeting the given cutoff expression significance value
 * and don't have the required number of neighbors that do.
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class ExpressionFilter extends Filter {
    double cutoff;
    boolean cutoffSmallerThan;
    int nConds;
    ExpressionData expressionData;
    GraphObjAttributes nodeAttributes;
    boolean hideSingletons;
    int nNeighbors;
    int maxDepth;
    Filter neighborF;
    boolean useRatio;

    public ExpressionFilter(Graph2D graph,
			    ExpressionData expressionData,
			    GraphObjAttributes nodeAttributes,
			    double cutoff, boolean cutoffSmallerThan, int nConds,
			    boolean useRatio,
			    int nNeighbors, int maxDepth,
			    boolean hideSingletons) {
	super(graph);

	this.cutoff = cutoff;
	this.cutoffSmallerThan = cutoffSmallerThan;
	this.nConds = nConds;
	this.useRatio = useRatio;
	this.expressionData = expressionData;
	this.nodeAttributes = nodeAttributes;
	this.hideSingletons = hideSingletons;
	this.nNeighbors = nNeighbors;
	this.maxDepth = maxDepth;
	this.neighborF = neighborF;
    }

    public NodeList get(NodeList hidden) {
	Filter unaffF = new ExpressionOnlyFilter(graph,
						 expressionData,
						 nodeAttributes,
						 cutoff, cutoffSmallerThan, 
						 nConds,
						 useRatio);
	
	Filter memoUnaffF = new ListFilter(graph,
					   unaffF.get(hidden));

	Filter memoAffF = new NegFilter(graph,
					memoUnaffF);


	Filter unaffTopoF = new TopologyOnePassFilter(graph,
						      memoUnaffF,
						      nNeighbors, maxDepth,
						      memoAffF);

	NodeList flagged = unaffTopoF.get(hidden);

	if (hideSingletons) {
	    Filter singletonF = new TopologyOnePassFilter(graph, 1, 1);
	    flagged.splice(singletonF.get(flagged));
	}

	return flagged;
    }
}


