package org.cytoscape.model;

import java.util.List;

/**
 * A CyNetwork is a collection of nodes and the edges that connect
 * them.  CyNetworks also maintains information about the selection
 * state of nodes and edges.
 */
public interface CyNetwork extends CyModelObject {

	/**
	 * Return the title of this network
	 *
	 * @return network title
	 */
	public String getTitle();

	/**
	 * Set the title for this network
	 *
	 * @param title the title for the network
	 */
	public void setTitle(String title);

	/**
	 * Add a node to this network
	 *
	 * @param node the node to be added
	 */
	public void addNode(CyNode node);

	/**
	 * Add a list of nodes to this network
	 *
	 * @param nodeList the list of nodes to be added
	 */
	public void addNodeList(List<CyNode> nodeList);

	/**
	 * Remove this node from this network
	 *
	 * @param node the node to be added
	 * @return the removed node
	 */
	public CyNode removeNode(CyNode node);

	/**
	 * Remove a list of nodes from this network
	 *
	 * @param nodeList the list of nodes to be removed
	 */
	public void removeNodeList(List<CyNode> nodeList);

	/**
	 * Return true if the given CyNode is in this CyNetwork
	 *
	 * @param node the node to check
	 */
	public boolean containsNode(CyNode node);

	/**
	 * Add an edge to this network
	 *
	 * @param edge the edge to be added
	 */
	public void addEdge(CyEdge edge);

	/**
	 * Add a list of edges to this network
	 *
	 * @param edgeList the list of edges to be added
	 */
	public void addEdgeList(List<CyEdge> edgeList);

	/**
	 * Remove an edge from this network
	 *
	 * @param edge the edge to be added
	 * @return the removed edge
	 */
	public CyEdge removeEdge(CyEdge edge);

	/**
	 * Remove a list of edges from this network
	 *
	 * @param edgeList the list of edges to be removed
	 */
	public void removeEdgeList(List<CyEdge> edgeList);

	/**
	 * Return true if the given CyEdge is in this CyNetwork
	 *
	 * @param edge the edge to check
	 */
	public boolean containsEdge(CyEdge edge);

	/**
	 * Return the list of nodes that are part of this network
	 *
	 * @return list of nodes
	 */
	public List<CyNode>getNodeList();

	/**
	 * Return the list of edges that are part of this network
	 *
	 * @return list of edges
	 */
	public List<CyEdge>getEdgeList();

	/**
	 * Return the number of nodes that are part of this network
	 *
	 * @return number of nodes in this network
	 */
	public int getNodeCount();

	/**
	 * Return the number of edges that are part of this network
	 *
	 * @return number of edges in this network
	 */
	public int getEdgeCount();

	/**
	 * Return true if the two nodes are neighbors in this network.
	 *
	 * @param node1 the first node to chack
	 * @param node2 the second node to check
	 * @return true if the two nodes share an edge
	 */
	public boolean isNeighbor(CyNode node1, CyNode node2);

	/**
	 * Add a group to this network
	 *
	 * @param group group to be added
	 */
	public void addGroup(CyGroup group);

	/**
	 * Remove a group from this network
	 *
	 * @param group group to be removed
	 */
	public void removeGroup(CyGroup group);

	/**
	 * Return the list of groups currently part of this network
	 *
	 * @return list of groups that are part of this network
	 */
	public List<CyGroup>getGroupList();

	/**
	 * Return the CyNode for a given identifier
	 *
	 * @param nodeID the node identifier
	 * @return the CyNode, if that identifier exists in this network
	 */
	public CyNode getCyNode(int nodeID);

	/**
	 * Return the CyEdge for a given identifier
	 *
	 * @param edgeID the edge identifier
	 * @return the CyEdge, if that identifier exists in this network
	 */
	public CyEdge getCyEdge(int edgeID);

}
