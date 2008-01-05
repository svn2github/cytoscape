package org.cytoscape.model;

import java.util.List;

/**
 * A CyNode is the primary data object used in Cytoscape.
 */
public interface CyNode extends CyModelObject {
	public static enum EdgeSet {EDGESET_ANY, EDGESET_INCOMING, EDGESET_OUTGOING};

	/**
	 * Return the list of edges connected to this node
	 *
	 * @param set the set of edges we're interested in
	 * @return list of edges
	 */
	public List<CyEdge> getEdgeList(EdgeSet set);

	/**
	 * Add an edge to this node.  If neither source nor target
	 * are this node, this call silently fails.
	 *
	 * @param edge the edge to add
	 */
	public void addEdge(CyEdge edge);

	/**
	 * Remove an edge from this node
	 *
	 * @param edge the edge to remove
	 */
	public CyEdge removeEdge(CyEdge edge);

	/**
	 * Return the list of nodes that are neighbor to this node
	 *
	 * @param set the set of edges to consider
	 * @param edgeType a (possibly null) edge type to restrict the list to
	 * @return list of nodes
	 */
	public List<CyNode> getNeighborList(EdgeSet set, String edgeType);

	/**
	 * Get the list of edges that connect this node to another node
	 *
	 * @param neighborNode the neighbor node to look at
	 * @param edgeType a (possibly null) edge type to restrict the list to
	 * @param set the set of edges to return
	 * @return the list of connecting edges
	 */
	public List<CyEdge> getConnectingEdgeList(CyNode neighborNode, String edgeType, EdgeSet set);

	/**
	 * Get the degree of this node
	 *
	 * @param set the set of edges to examine
	 * @return the degree
	 */
	public int getDegree(EdgeSet set);

	/**
	 * Return true if the neighborNode is a neighbor of this node
	 *
	 * @param neighborNode the node to test against
	 * @return true if this node is a neighbor of neighborNode 
	 */
	public boolean isNeighbor(CyNode neighborNode);

	/**
	 * Get the groups this node is a member of
	 *
	 * @return list of groups
	 */
	public List<CyGroup> getGroupList();

	/**
	 * Is this node actually a group node?
	 *
	 * @return true if this node is group node
	 */
	public boolean isaGroup();
}
