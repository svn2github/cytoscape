package cytoscape.filters;

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
