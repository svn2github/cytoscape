package org.mike;

import java.util.List;
import java.util.Map;

public interface CyNetwork {

	CyNode addNode();
	boolean removeNode(CyNode node);

	CyEdge addEdge(CyNode source, CyNode target, boolean isDirected);
	boolean removeEdge(CyEdge edge);

	int getNodeCount();
	int getEdgeCount();

	List<CyNode> getNodeList();
	List<CyEdge> getEdgeList();

	boolean containsNode( CyNode node );
	boolean containsEdge( CyEdge edge );
	boolean containsEdge( CyNode from, CyNode to );

	CyNode getNode(int index);
	CyEdge getEdge(int index);

	List<CyNode> getNeighborList( CyNode node, EdgeType edgeType );
	List<CyEdge> getAdjacentEdgeList( CyNode node, EdgeType edgeType );
	List<CyEdge> getConnectingEdgeList( CyNode source, CyNode target, EdgeType edgeType );
}
