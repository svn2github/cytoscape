package cytoscape.filters;

import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import cytoscape.data.*;
import cytoscape.*;
/**
 * Filter that flags according to the interaction type.
 * 
 * @author namin@mit.edu
 * @version 2002-04-03
 */
public class InteractionFilter extends Filter {
    GraphObjAttributes edgeAttributes;
    String[] accTypes;
    // source, target or both
    String search;
    public InteractionFilter(Graph2D graph,
			     GraphObjAttributes edgeAttributes,
			     Filter flaggableF,
			     String[] accTypes) {

	super(graph, flaggableF);
	this.edgeAttributes = edgeAttributes;
	this.accTypes = accTypes;
	search = "both";
    }

    public InteractionFilter(Graph2D graph,
			     GraphObjAttributes edgeAttributes,
			     String[] accTypes) {

	super(graph);
	this.edgeAttributes = edgeAttributes;
	this.accTypes = accTypes;
	search = "both";
    }


    public InteractionFilter(Graph2D graph,
			     GraphObjAttributes edgeAttributes,
			     Filter flaggableF,
			     String[] accTypes,
			     String search) {

	super(graph, flaggableF);
	this.edgeAttributes = edgeAttributes;
	this.accTypes = accTypes;
	this.search = search;
    }

    public InteractionFilter(Graph2D graph,
			     GraphObjAttributes edgeAttributes,
			     String[] accTypes,
			     String search) {

	super(graph);
	this.edgeAttributes = edgeAttributes;
	this.accTypes = accTypes;
	this.search = search;
    }

    public NodeList get(NodeList hidden) {
	NodeList flagged = new NodeList();
	NodeList flaggable = getFlaggableF().get(hidden);

	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    Node node = nc.node();

	    if (!hidden.contains(node)
		&& (allFlaggable() || flaggable.contains(node))) {

		boolean flagNode = true;
	    findAccType:
		for (EdgeCursor ec = getCursor(node, search); ec.ok(); ec.next()) {
		    Edge edge = ec.edge();
		    String edgeType = getType(edge);
		    for (int i = 0; i < accTypes.length; i++) {
			flagNode= !edgeType.equals(accTypes[i]);
			if (!flagNode) {
			    // accepted interaction type found
			    break findAccType;
			}
		    }
		}
		if (flagNode) {
		    flagged.add(node);
		}
	    }   
	}
	return flagged;
    }

    public String getType(Edge edge) {
	String type = (String) edgeAttributes.getValue("interaction", 
						       edgeAttributes.getCanonicalName(edge));
	return type;
    }

    public static EdgeCursor getCursor(Node node, String search) {
	EdgeCursor ec;
	if (search.equals("source")) {
	    ec = node.outEdges();
	} else if (search.equals("target")) {
	    ec = node.inEdges();
	} else {
	    ec = node.edges();
	}
	return ec;
    }
}
