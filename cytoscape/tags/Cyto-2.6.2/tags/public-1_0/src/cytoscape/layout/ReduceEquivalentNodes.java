package cytoscape.layout;

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
import y.util.*;
import y.algo.*; // for GraphHider in yFiles 1.4
import cytoscape.*;
import cytoscape.data.*;

import java.util.Hashtable;
import java.util.Iterator;

public class ReduceEquivalentNodes {
    GraphObjAttributes nodeAttributes;
    GraphObjAttributes edgeAttributes;
    Graph2D graph;
    GraphHider graphHider;
    NodeMap node2key;
    Hashtable key2nodes;

    public ReduceEquivalentNodes(GraphObjAttributes nodeAttributes,
				 GraphObjAttributes edgeAttributes,
				 Graph2D graph) {
	System.out.println("Reducing network");
	this.nodeAttributes = nodeAttributes;
	this.edgeAttributes = edgeAttributes;
	this.graph = graph;
	this.graphHider = new GraphHider(graph);
	this.node2key  = graph.createNodeMap();
	this.key2nodes = new Hashtable();

	computeReduction();  // builds node2key and key2nodes
	performReduction();  // alters the graph based on this info
	
	System.out.println("Reduction Finished");
    }

    /************************************************************/
    // main method for this class
    void computeReduction() {

	int i = 0;
	graphHider.unhideAll();
	YList listOfNodes;
	Node n;

	// construct key for each node in graph
	for (NodeCursor nc = graph.nodes(); nc.ok(); nc.next()) {   
	    n = nc.node();	
	    String name = nodeAttributes.getCanonicalName(n);
	    YList nameTypeList = new YList();
	    //	    System.out.println("Examining Node # " + i++ + " " + name);
	    
	    // examine all of this nodes neighbors
	    for (EdgeCursor ec = n.edges(); ec.ok(); ec.next()) {
		Edge e = ec.edge();
		Node n2 = e.opposite(n);
		String eStr = edgeAttributes.getCanonicalName(e);
		String type = (String) edgeAttributes.getValue("interaction", eStr);
		if (type.equals("pd") && (e.target() == n) ) 
		    type = "dp"; // handle directionality for pd edges
		String nameType = nodeAttributes.getCanonicalName(n2) + type;
		nameTypeList.add(nameType);
	    }
	    
	    // convert local topology (nameType string) to key
	    String key = collapseToString(nameTypeList);
	    //	    System.out.println("  " + key);
	    node2key.set(n, key);  // record in node map

	    // add this node to hashtable entry for this key
	    listOfNodes = (YList) key2nodes.get(key);
	    if (listOfNodes == null) listOfNodes = new YList();
	    listOfNodes.add(n);
	    key2nodes.put(key, listOfNodes);
	}
    }
    /************************************************************/
    // Collapses graph based on key2nodes hash
    void performReduction() {
	
	YList listOfNodes;
	Node n;
	int i = 0;
  
  graph.firePreEvent();

	// get equivalent nodes from each hash table entry
	for (Iterator it = key2nodes.values().iterator(); it.hasNext(); ) {
	    listOfNodes = (YList) it.next();
	    if (listOfNodes.size() >= 2) {  // only for clustered nodes
		String groupString = "Group " + i++;
		System.out.print(groupString + ": ");
		// rename first node in list to represent this group
		YCursor yc = listOfNodes.cursor();
		n = (Node) yc.current();
		System.out.print(" " + graph.getLabelText(n));

		//props.setName(n, groupString);
		graph.setLabelText(n, groupString);
		nodeAttributes.addNameMapping( groupString, n);

		// remove other nodes
		for (yc.next(); yc.ok(); yc.next()) {
		    n = (Node) yc.current();
		    System.out.print(" " + graph.getLabelText(n));
		    graph.removeNode(n);
		}   
		System.out.println();
	    }
	}

  graph.firePostEvent();
    }

    /************************************************************/
    // Collapses a list of strings into a single concatenated string
    String collapseToString(YList list) {

	String collapsed = new String();

	list.sort();  // sort list in alphabetical order
	for (YCursor yc = list.cursor(); yc.ok(); yc.next()) {
	    collapsed += yc.current();
	}
	return collapsed;
    }
}



