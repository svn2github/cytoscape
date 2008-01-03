package org.cytoscape.model;

import java.util.List;
import java.util.Set;


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
	 * @return list of nodes
	 */
	public List<CyNode> getNeighborList();

	/**
	 * Get the list of edges that connect this node to another node
	 *
	 * @param neighborNode the neighbor node to look at
	 * @param set the set of edges to return
	 * @return the list of connecting edges
	 */
	public List<CyEdge> getConnectingEdgeList(CyNode neighborNode, EdgeSet set);

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
	 * Add this node to a group
	 *
	 * @param group the group to add this to
	 */
	public void addToGroup(CyGroup group);

	/**
	 * Get the groups this node is a member of
	 *
	 * @return list of groups
	 */
	public List<CyGroup> getGroupList();

	/**
	 * Return true if the node is in the group
	 *
	 * @param group the group to check
	 * @return true if node is in group
	 */
	public boolean inGroup(CyGroup group);

	/**
	 * Is this node actually a group node?
	 *
	 * @return true if this node is group node
	 */
	public boolean isaGroup();

	/**
	 * Remove this node from a group
	 *
	 * @param group the group to this node from
	 */
	public void removeFromGroup(CyGroup group);
}
