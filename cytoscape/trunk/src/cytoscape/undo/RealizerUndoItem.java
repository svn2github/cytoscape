//

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
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// RealizerUndoItem.java
//
// $Revision$
// $Date$
// $Author$
//

package cytoscape.undo;

import y.base.*;
import y.view.*;
import java.util.*;

/**
 * Backs up and saves Node/Edge realizers
 */
public class RealizerUndoItem implements UndoItem {

    /**
     * The graph whose realizer state will be saved.
     */
    Graph2D graph;

    /**
     * Hash of Node->NodeRealizer for layout backup.
     */
    HashMap nodeRealizers;

    /**
     * Hash of Edge->EdgeRealizer for layout backup.
     */
    HashMap edgeRealizers;

    /**
     * The default node realizer
     */
    NodeRealizer defaultNodeRealizer;

    /**
     * The default edge realizer
     */
    EdgeRealizer defaultEdgeRealizer;


    /**
     * Another RealizerUndoItem to undo the undo (i.e. redo)
     */
    RealizerUndoItem redo;

    /**
     * This constructor creates a copy of all the node and edge
     * realizers in the graph, and saves them in the appropriate
     * hashmaps.
     */
    public RealizerUndoItem (Graph2D graph) {
	this.graph = graph;

	defaultNodeRealizer = graph.getDefaultNodeRealizer().createCopy();
	defaultEdgeRealizer = graph.getDefaultEdgeRealizer().createCopy();
	
	nodeRealizers = new HashMap();
	edgeRealizers = new HashMap();
	
	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {
	    NodeRealizer nr = graph.getRealizer(nc.node());
	    nodeRealizers.put(nc.node(), nr.createCopy());
	}

	for (EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
	    EdgeRealizer er = graph.getRealizer(ec.edge());
	    edgeRealizers.put(ec.edge(), er.createCopy());
	}
    }


    /**
     * Apply the state contained within this UndoItem.  This is
     * implemented by restoring all of the node and edge realizers to
     * their state upon creation.
     */
    public boolean undo() {
	// save redo state
	redo = new RealizerUndoItem(graph);

	graph.setDefaultNodeRealizer(defaultNodeRealizer);
	graph.setDefaultEdgeRealizer(defaultEdgeRealizer);
	
	Node[] nodes = (Node[]) nodeRealizers.keySet().toArray(new Node[0]);
	Edge[] edges = (Edge[]) edgeRealizers.keySet().toArray(new Edge[0]);

	for (int i = 0; i < nodes.length; i++) {
	    graph.setRealizer(nodes[i],
			      (NodeRealizer)nodeRealizers.get(nodes[i]));
	}

	for (int i = 0; i < edges.length; i++) {
	    graph.setRealizer(edges[i],
			      (EdgeRealizer)edgeRealizers.get(edges[i]));
	}
	
	return true;
    }


    /**
     * Returns the graph to the state pre-undo().
     */
    public boolean redo() {
	return redo.undo();
    }
}


