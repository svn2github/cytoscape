package cytoscape.filters;

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
