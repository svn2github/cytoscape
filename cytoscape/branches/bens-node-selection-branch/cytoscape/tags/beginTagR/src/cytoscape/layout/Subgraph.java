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
    // HashMap iSFOffsetMap;



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


    // mapSubFullNode
    //
    // map a node from the subgraph to node in full
    public Node mapSubFullNode(Node aNode) {
	return ((Node)iSFNodeMap.get(aNode));
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

