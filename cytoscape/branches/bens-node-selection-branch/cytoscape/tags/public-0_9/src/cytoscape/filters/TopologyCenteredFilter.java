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
public class TopologyCenteredFilter extends Filter {
    /**
     * Filter defining the starting nodes.
     */
    Filter startersF;
    /**
     * Required minimum number of neighbors.
     */
    int nNeighbors;    
    /**
     * Required maximum depth to be considered a neighbor.
     */
    int maxDepth;

    public TopologyCenteredFilter(Graph2D graph,
				  Filter startersF,
				  int nNeighbors,
				  int maxDepth) {
	super(graph);

	this.nNeighbors = nNeighbors;
	this.maxDepth = maxDepth;
	this.startersF = startersF;
    }


    public NodeList get(NodeList hidden) {
	NodeList flagged;
	NodeList starters = startersF.get(hidden);

	// we'll use different algorithms 
	// depending on the number of starter nodes
	if (nNeighbors == 1 && starters.size() < 5) {
	    NodeList unflagged = new NodeList();
	    for (NodeCursor nc = starters.nodes(); nc.ok(); nc.next()) {
		Node node = nc.node();
		
		if (!hidden.contains(node)) {
		    try {
			NodeList reached = Filter.bfsDepth(node, maxDepth);
			for (NodeCursor vc = reached.nodes(); vc.ok(); vc.next()) {
			    Node v = vc.node();
			    
			    // Don't count the node itself as its neighbor!
			    if (!hidden.contains(v)) {
				unflagged.add(v);
			    }
			}
		    } catch (NullPointerException e) {
			System.out.println("!!! ERROR !!! NullPointerException !!!");
		    }
		}
	    }
	
	    Filter negF = new NegFilter(graph, new ListFilter(graph, unflagged));
	    flagged = negF.get(hidden);
	} else {
	    Filter memoStartersF = new ListFilter(graph,
						 starters);

	    Filter memoNotStartersF = new NegFilter(graph,
						    memoStartersF);

	    Filter topoF = new TopologyOnePassFilter(graph,
						     memoNotStartersF,
						     nNeighbors, maxDepth,
						     memoStartersF);
	    flagged = topoF.get(hidden);
	}
	return flagged;
    }

}
