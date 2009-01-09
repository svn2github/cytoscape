package cytoscape.filters;

import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import cytoscape.data.*;

/**
 * Filter to combine two filters in and "and" or "or" fashion.
 * 
 * @author namin@mit.edu
 * @version 2002-03-02
 */
public class CombinationFilter extends Filter {
    boolean andOp;
    Filter[] filters;

    public CombinationFilter(Graph2D graph, Filter[] filters, boolean andOp) {
	super(graph);
	this.filters = filters;
	this.andOp = andOp;
    }

    public NodeList get(NodeList hidden) {
	NodeList flagged = new NodeList();

	NodeList[] lists = new NodeList[filters.length];
	for (int i = 0; i < filters.length; i++) {
	    lists[i] = filters[i].get(hidden);
	}

	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();
	    if (!hidden.contains(node)) {
		boolean met = true;
		for (int i = 0; i < lists.length; i++) {
		    met = !lists[i].contains(node);
		    if (met == !andOp) {
			// "met" is
			// true with OR
			// false with AND;
			// no need to go on.
			break;
		    }
		}
		
		if (!met) {
		    flagged.add(node);
		}
	    }
	}
	return flagged;
    }

}
