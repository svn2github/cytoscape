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

// Subgraph.java
//
// $Revision$
// $Date$
// $Author$
//


package cytoscape.layout;

import java.io.*;
import java.util.*;

import y.base.*;
import y.layout.*;


/**
 * A special type of LayoutGraph which maps
 * its nodes to corresponding nodes in a
 * "parent" graph.
 *
 * Subgraph provides a mechanism to easily
 * create a graph based on a set of nodes in
 * another graph, manipulate their positions,
 * and re-map the new locations back to the
 * original graph.
 *
 * yFiles may provide better mechanisms to do
 * this, but i haven't come across any.
 *
 * Note that <em>no node properties (other than
 * location) are mapped</em>.
 *
 */
public class Subgraph extends CopiedLayoutGraph {
    /**
     * The parent graph on which Subgraph is based.
     */
    protected LayoutGraph iFullGraph;

    /**
     * Mapping from nodes in Subgraph to nodes in
     * <code>iFullGraph</code>.
     */
    protected HashMap iSFNodeMap;
    /**
     * Mapping from nodes in <code>iFullGraph</code>
     * to nodes in Subgraph.
     */
    protected HashMap iFSNodeMap;


    /**
     * Create a Subgraph based on aGraph.
     */
    public Subgraph (LayoutGraph aGraph) {
	this(aGraph, aGraph.nodes());
    }

    /**
     * Create a Subgraph of aGraph, containing
     * only the nodes found in aNodeSubset.
     */
    public Subgraph (LayoutGraph aGraph, YCursor aNodeSubset) {
	super (aGraph);
	iFullGraph = aGraph;

	// full node list, graph node list, respectively
	Node[] fList = iFullGraph.getNodeArray();
	Node[] gList = getNodeArray();
	int nC = nodeCount();

	iSFNodeMap = new HashMap();
	iFSNodeMap = new HashMap();
	//iSFOffsetMap = new HashMap();

	// remove nodes from graph that aren't in aNodeSubset
	for (int i = 0; i < nC; i++) {
	    boolean keep = false;

	    for (aNodeSubset.toFirst(); aNodeSubset.ok(); aNodeSubset.next()) {
		if (fList[i] == (Node)aNodeSubset.current()) {
		    keep = true;
		    break;
		}
	    }

	    // initialize node maps 
	    // and remove nodes not in subset
	    if (keep) {
		iSFNodeMap.put(gList[i], fList[i]);
		iFSNodeMap.put(fList[i], gList[i]);
		//iSFOffsetMap.put(gList[i], new Integer(i));
	    } else
		removeNode(gList[i]);
	}
    }




    /**
     * Map the nodes contained in the Subgraph back
     * to the original nodes from the full graph that
     * they represent.
     *
     * Only the node center is affected. <em>No other
     * node attributes are mapped.</em>
     */
    public void reInsert() {
	Node[] gList = getNodeArray();
	int nC = nodeCount();
	
	for (int i = 0; i < nC; i++)
	    iFullGraph.setCenter(mapSubFullNode(gList[i]),
				 getCenter(gList[i]));
    }


    /**
     * Retrieve the original graph that Subgraph was
     * created from.
     *
     * @return The original graph
     */
    public LayoutGraph getFullGraph() {
	return iFullGraph;
    }



    /**
     * Create a node in the Subgraph, and register it as
     * being associated with <code>aParentNode</code>,
     * which assumed to be an existing node in the
     * {@link #getFullGraph()} parent graph.
     *
     * @param aParentNode Node in the parent graph with
     *    which the new node will be associated.
     * @return The newly created node in the Subgraph.
     */
    public Node createNode(Node aParentNode) {
	Node bob = createNode();

	iSFNodeMap.put(bob, aParentNode);
	iFSNodeMap.put(aParentNode, bob);

	return bob;
    }



    /**
     * Merge the two given nodes into a single node, by
     * removing removing <code>aChild</code> from the
     * Subgraph (and the assiciated node in the parent
     * graph).  All vertices connected to <code>aChild</code>
     * are then connected to <code>aParent</code>.
     *
     * The parent graph is left unalterred.
     *
     * <em>Note:</em> Needs MAJOR optimization.  Currently,
     * this function generates a full connectedness matrix
     * (GroupingAlgorithm.gaConnected(this)) each time it is
     * called.
     *
     * Also, all attributes of edges coming in to
     * <code>aChild</code> are lost.
     *
     * @param aParent The Node in the Subgraph onto which
     *    new edges will be added.
     * @param aChild The Node in the Subgraph which will
     *    be deleted.  Its edges will be reconstructed
     *    using <code>aParent</code>.
     */
    public void mergeNodes(Node aParent, Node aChild) {
	// get connectedness list
	int[][] connected = GroupingAlgorithm.gaConnected(this);

	// find child index
	Node[] nodeList = getNodeArray();
	int c = 0, p = 0;
	for (int i = 0; i < nodeCount(); i++) {
	    if (nodeList[i] == aChild)
		c = i;
	    if (nodeList[i] == aParent)
		p = i;
	}

	// update edges
	for (int i = 0; i < nodeCount(); i++) {
	    switch (connected[c][i]) {
	    case GroupingAlgorithm.CONNECTED_BI:
		switch (connected[p][i]) {
		case GroupingAlgorithm.CONNECTED_TO:
		    createEdge(nodeList[i], nodeList[p]);
		    break;
		case GroupingAlgorithm.CONNECTED_FROM:
		    createEdge(nodeList[p], nodeList[i]);
		    break;
		case GroupingAlgorithm.NOT_CONNECTED:
		    createEdge(nodeList[i], nodeList[p]);
		    createEdge(nodeList[p], nodeList[i]);
		    break;
		} connected[p][i] = connected[i][p]
		      = GroupingAlgorithm.CONNECTED_BI;
		break;

	    case GroupingAlgorithm.CONNECTED_TO:
		switch (connected[p][i]) {
		case GroupingAlgorithm.CONNECTED_FROM:
		    createEdge(nodeList[p], nodeList[i]);
		    connected[p][i] = connected[i][p]
			= GroupingAlgorithm.CONNECTED_BI;
		    break;
		case GroupingAlgorithm.NOT_CONNECTED:
		    createEdge(nodeList[p], nodeList[i]);
		    connected[p][i] = GroupingAlgorithm.CONNECTED_TO;
		    connected[i][p] = GroupingAlgorithm.CONNECTED_FROM;
		    break;
		} break;

	    case GroupingAlgorithm.CONNECTED_FROM:
		switch (connected[p][i]) {
		case GroupingAlgorithm.CONNECTED_TO:
		    createEdge(nodeList[i], nodeList[p]);
		    connected[p][i] = connected[i][p]
			= GroupingAlgorithm.CONNECTED_BI;
		    break;
		case GroupingAlgorithm.NOT_CONNECTED:
		    createEdge(nodeList[i], nodeList[p]);
		    connected[p][i] = GroupingAlgorithm.CONNECTED_FROM;
		    connected[i][p] = GroupingAlgorithm.CONNECTED_TO;
		    break;
		} break;
	    }
	}
		

	removeNode(aChild);
    }



    /**
     * Map a node in Subgraph to the corresponding node
     * in the full (parent) graph.
     *
     * @param aNode The Node in Subgraph.
     * @return The corresponding Node in the full graph.
     */
    public Node mapSubFullNode(Node aNode) {
	return ((Node)iSFNodeMap.get(aNode));
    }


    /**
     * Map a node in the full (parent) graph to the
     * corresponding node kept in this SubGraph.
     *
     * @param aNode The node in the parent graph.
     * @return The corresponding node in this Subgraph.
     */
    public Node mapFullSubNode(Node aNode) {
	return ((Node)iFSNodeMap.get(aNode));
    }


    /*
    // mapSubFullOffsetFast
    //
    // map a node from the subgraph to offset in full
    // NOTE!! this is FAST but NOT SAFE!
    // if nodes have been deleted from iFullGraph
    // the returned offset will be WRONG
    public int mapSubFullOffsetFast(Node aNode) {
	return ((Integer)iSFOffsetMap.get(aNode)).intValue();
    }

    // generateOffsetMap
    //
    // remake the offset map used by mapSubFullOffset
    // UNTESTED!!
    public void generateOffsetMap() {
	if (iSFOffsetMap != null)
	    iSFOffsetMap = new HashMap();

	Node[] fList = iFullGraph.getNodeArray();
	Node[] gList = getNodeArray();
	int nC = nodeCount();

	for (int i = 0; i < nC; i++) {
	    for (int j = 0; j < nC; j++) {
		if (gList[i] == fList[j]) {
		    iSFOffsetMap.put(gList[i], new Integer(j));
		    break;
		}
	    }
	}
    }
    */
}



