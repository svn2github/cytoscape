package cytoscape.filters;

import y.base.*;
import y.view.*;

import y.algo.GraphHider;

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

    public InteractionFilter(Graph2D graph,
			     GraphObjAttributes edgeAttributes,
			     Filter flaggableF,
			     String[] accTypes) {

	super(graph, flaggableF);
	this.edgeAttributes = edgeAttributes;
	this.accTypes = accTypes;
    }

    public InteractionFilter(Graph2D graph,
			     GraphObjAttributes edgeAttributes,
			     String[] accTypes) {

	super(graph);
	this.edgeAttributes = edgeAttributes;
	this.accTypes = accTypes;
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
		for (EdgeCursor ec = node.edges(); ec.ok(); ec.next()) {
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

}
