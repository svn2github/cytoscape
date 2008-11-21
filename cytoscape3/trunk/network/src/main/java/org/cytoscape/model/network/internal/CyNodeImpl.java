package org.cytoscape.model.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.EdgeType;
import org.cytoscape.model.CyDataTable;
import java.util.List; 
import java.util.Map; 

class CyNodeImpl extends GraphObjImpl implements CyNode {

	final private int index;
	final private CyNetwork net;

	CyNodeImpl(CyNetwork n, int ind, Map<String,CyAttributesManager> attrMgr) {
		super(attrMgr);
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

    public List<CyEdge> getAdjacentEdgeList( EdgeType edgeType ) {
		return net.getAdjacentEdgeList(this, edgeType);
	}

    public List<CyEdge> getConnectingEdgeList( CyNode target, EdgeType edgeType ) {
		return net.getConnectingEdgeList(this, target, edgeType);
	}
}
