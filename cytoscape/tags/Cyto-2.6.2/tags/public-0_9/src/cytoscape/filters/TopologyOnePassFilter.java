package cytoscape.filters;

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
