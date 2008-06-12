package org.cytoscape.network;

import java.util.List;
import org.cytoscape.attrs.CyAttributes;

public interface CyNetwork {
	public String getIdentifier();

	public CyNode addNode();
	public boolean removeNode(CyNode node);

	public CyEdge addEdge(CyNode source, CyNode target, boolean isDirected);
	public boolean removeEdge(CyEdge edge);

	public int getNodeCount();
	public int getEdgeCount();

	public List<CyNode> getNodeList();
	public List<CyEdge> getEdgeList();

	public boolean contains( CyNode node );
	public boolean contains( CyEdge edge );
	public boolean contains( CyNode from, CyNode to );

	public CyNode getNode(int index);
	public CyEdge getEdge(int index);

	public List<CyNode> getNeighborList( CyNode node, EdgeType edgeType );
	public List<CyEdge> getAdjacentEdgeList( CyNode node, EdgeType edgeType );
	public List<CyEdge> getConnectingEdgeList( CyNode source, CyNode target, EdgeType edgeType );

	public CyAttributes getAttributes();
}
