package cytoscape.filters;

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
