package org.cytoscape.model;

import java.util.List;

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

	public List<CyNode> getNeighborList( CyNode node, byte edgeType );
	public List<CyEdge> getAdjacentEdgeList( CyNode node, byte edgeType );
	public List<CyEdge> getConnectingEdgeList( CyNode source, CyNode target, byte edgeType );

    public static final byte NO_EDGE        = 0;
	public static final byte UNDIRECTED_EDGE    = (1 << 0);
	public static final byte INCOMING_EDGE      = (1 << 1);
	public static final byte OUTGOING_EDGE      = (1 << 2);
	public static final byte DIRECTED_EDGE      = INCOMING_EDGE | OUTGOING_EDGE;
	public static final byte ANY_EDGE       = UNDIRECTED_EDGE | DIRECTED_EDGE;
}
