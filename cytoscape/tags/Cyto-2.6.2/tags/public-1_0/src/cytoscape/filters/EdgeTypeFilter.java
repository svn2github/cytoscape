package cytoscape.filters;

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/


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


