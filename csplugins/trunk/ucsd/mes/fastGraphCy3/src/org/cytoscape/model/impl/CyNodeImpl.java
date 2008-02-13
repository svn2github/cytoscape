package org.cytoscape.model.impl;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyBaseEdge;
import org.cytoscape.model.CyGraph;
import org.cytoscape.model.EdgeType;
import java.util.List; 

class CyNodeImpl<E extends CyBaseEdge> extends GraphObjImpl implements CyNode {

	final private int index;
	final private CyGraph<E> net;

	CyNodeImpl(CyGraph<E> n, int ind) {
		super();
		net = n; 
		index = ind;
	}

	public int getIndex() {
		return index;
	}

	public String toString() {
		return Integer.toString(index);
	}

    public List<CyNode> getNeighborList( EdgeType edgeType ) {
		return net.getNeighborList(this, edgeType);
	}

    public List<E> getAdjacentEdgeList( EdgeType edgeType ) {
		return net.getAdjacentEdgeList(this, edgeType);
	}

    public List<E> getConnectingEdgeList( CyNode target, EdgeType edgeType ) {
		return net.getConnectingEdgeList(this, target, edgeType);
	}
}
