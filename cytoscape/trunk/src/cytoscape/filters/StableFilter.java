package cytoscape.filters;

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
