//
// Subgraph.java
//
// class to map a graph representing a
// (sub)set of nodes back to its parent
// graph
//
// dramage : 2002.1.22
//


package cytoscape.layout;

import java.io.*;
import java.util.*;

import y.base.*;
import y.layout.*;


public class Subgraph extends CopiedLayoutGraph {
    LayoutGraph iFullGraph;

    // mapping table for iGraph Node => iFullGraph node
    HashMap iSFNodeMap;
    HashMap iFSNodeMap;


    // constructor
    //
    // copy aGraph
    public Subgraph (LayoutGraph aGraph) {
	this(aGraph, aGraph.nodes());
    }

    // constructor
    //
    // create subset of aGraph
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




    // reInsert
    //
    // map graph back to iFullGraph
    // NOTE!! only maps position
    // NO OTHER NODE ATTRIBUTES MAPPED
    public void reInsert() {
	Node[] gList = getNodeArray();
	int nC = nodeCount();
	
	for (int i = 0; i < nC; i++)
	    iFullGraph.setCenter(mapSubFullNode(gList[i]),
				 getCenter(gList[i]));
    }


    // getFullGraph
    //
    // return iFullGraph
    public LayoutGraph getFullGraph() {
	return iFullGraph;
    }



    // createNode
    //
    // create a node, linked to a node in parent
    public Node createNode(Node aParentNode) {
	Node bob = createNode();

	iSFNodeMap.put(bob, aParentNode);
	iFSNodeMap.put(aParentNode, bob);

	return bob;
    }


    // mergeNodes
    //
    // adjoin second node to first, keeping edges in tact.
    // TEMP!! NEEDS MAJOR OPTIMIZATION
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


    // mapSubFullNode
    //
    // map a node from the subgraph to node in full
    public Node mapSubFullNode(Node aNode) {
	return ((Node)iSFNodeMap.get(aNode));
    }


    // mapFullSubNode
    //
    // map a node from the full graph to a subgraph node
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

