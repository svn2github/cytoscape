package cytoscape.filters;

import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import cytoscape.data.*;
import cytoscape.*;
/**
 * Filter that flags according to the node type.
 * 
 * @author namin@mit.edu
 * @version 2002-04-03
 */
public class NodeTypeFilter extends Filter {
    GraphObjAttributes nodeAttributes;
    String[] accTypes;

    public NodeTypeFilter(Graph2D graph,
		      GraphObjAttributes nodeAttributes,
		      Filter flaggableF,
		      String[] accTypes) {

	super(graph, flaggableF);
	this.nodeAttributes = nodeAttributes;
	this.accTypes = accTypes;
    }

    public NodeTypeFilter(Graph2D graph,
			     GraphObjAttributes nodeAttributes,
			     String[] accTypes) {

	super(graph);
	this.nodeAttributes = nodeAttributes;
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
		String nodeType = getType(node);
	    findAccType:
		for (int i = 0; i < accTypes.length; i++) {
		    flagNode= !nodeType.equals(accTypes[i]);
		    if (!flagNode) {
			// accepted type found
			break findAccType;
		    }
		}
		if (flagNode) {
		    flagged.add(node);
		}
	    }   
	}
	return flagged;
    }

    public String getType(Node node) {
	// Infer the type from the node name
	String name = nodeAttributes.getCanonicalName(node);
	if (name != null) {
	    char s = name.charAt(0);
	    if (s == 'C') {
		return "compound";
	    } else if (s == 'R') {
		return "reaction";
	    }
	}
	// default
	return "gene";
    }

}
