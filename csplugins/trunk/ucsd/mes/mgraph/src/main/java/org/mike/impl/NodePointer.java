
package org.mike.impl; 

import org.mike.CyNode;
import org.mike.CyNetwork;

// Package visible.
class NodePointer {

	final CyNode cyNode;
	final int index;

	NodePointer nextNode;
	NodePointer prevNode;

	EdgePointer firstOutEdge;
	EdgePointer firstInEdge;

	// The number of directed edges whose source is this node.
	int outDegree;

	// The number of directed edges whose target is this node.
	int inDegree;

	// The number of undirected edges which touch this node.
	int undDegree;

	// The number of directed self-edges on this node.
	int selfEdges;

	NodePointer(final int nid,final CyNetwork n,final NodePointer next) {
		index = nid; 
		cyNode = new CyNodeImpl(n,index);
		nextNode = next;
		outDegree = 0;
		inDegree = 0;
		undDegree = 0;
		selfEdges = 0;
	}
}
