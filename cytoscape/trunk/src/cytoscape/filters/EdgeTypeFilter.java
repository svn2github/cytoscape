package cytoscape.filters;

import y.base.*;
import y.view.*;

import java.util.Hashtable;

import cytoscape.undo.UndoableGraphHider;

import cytoscape.data.*;
import cytoscape.*;
/**
 * A filter a little apart for now
 * that select edges of certain interaction types.
 *
 * @author namin@mit.edu
 * @version 2002-05-14
 */

public class EdgeTypeFilter extends Filter {
    protected Graph2D graph;
    GraphObjAttributes edgeAttributes;
    String[] accTypes;

    public EdgeTypeFilter(Graph2D graph,
		  GraphObjAttributes edgeAttributes,
		  String[] accTypes) {
	// dummy
	super(graph);
	this.graph = graph;
	this.edgeAttributes = edgeAttributes;
	this.accTypes = accTypes;
    }

    public String getType(Edge edge) {
	String type = (String) edgeAttributes.getValue("interaction", 
						       edgeAttributes.getCanonicalName(edge));
	return type;
    }

    public boolean accType(String edgeType) {
	boolean accType = false;
	for (int i = 0; i < accTypes.length; i++) {
	    accType= edgeType.equals(accTypes[i]);
	    if (accType) {
		// accepted interaction type found
		break;
	    }
	}
	return accType;
    }

    public int select() {
	int counter = 0;
	for (EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    Edge edge = ec.edge();
	    if (!graph.isSelected(edge)) {
		if (accType(getType(edge))) {
		    graph.setSelected(edge, true);
		    counter++;
		}
	    }
	}
	System.out.println(counter + " edges selected.");
	return counter;
    }

    // dummy kludgy
    public NodeList get() {
	return new NodeList();
    }

    public NodeList get(NodeList hidden) {
	return new NodeList();	
    }

    public int hide(UndoableGraphHider graphHider) {
	return 0;
    }

}
