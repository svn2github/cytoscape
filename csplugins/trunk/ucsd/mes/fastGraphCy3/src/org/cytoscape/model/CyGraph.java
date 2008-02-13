package org.cytoscape.model;

import java.util.List;

public interface CyGraph<E extends CyBaseEdge> {
	public String getIdentifier();

	public CyNode addNode();
	public boolean removeNode(CyNode node);

	public boolean removeEdge(E edge);

	public int getNodeCount();
	public int getEdgeCount();

	public List<CyNode> getNodeList();
	public List<E> getEdgeList();

	public boolean contains( CyNode node );
	public boolean contains( E edge );
	public boolean contains( CyNode from, CyNode to );

	public CyNode getNode(int index);
	public E getEdge(int index);

	public List<CyNode> getNeighborList( CyNode node, EdgeType edgeType );
	public List<E> getAdjacentEdgeList( CyNode node, EdgeType edgeType );
	public List<E> getConnectingEdgeList( CyNode source, CyNode target, EdgeType edgeType );
}
