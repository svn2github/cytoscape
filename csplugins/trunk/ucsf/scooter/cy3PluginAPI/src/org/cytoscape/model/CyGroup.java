package org.cytoscape.model;

import java.util.List;

public interface CyGroup extends CyModelObject {

	/**
	 * Add a new inner edge to this group.  An inner
	 * edge is defined as an edge that only connects
	 * nodes that are members of the group.
	 *
	 * @param edge the inner edge to add.
	 */
	public void addInnerEdge(CyEdge edge);

	/**
	 * Add a new node to the group.  This will also iterate
	 * through all of the edges associated with this node
	 * and assign them as inner or outer edges.
	 *
	 * @param node the node to be added
	 */
	public void addNode(CyNode node);

	/**
	 * Add a new outer edge to this group.  An outer
	 * edge is defined as an edge where at least one
	 * nodes that it connects to is not a member of
	 * this group.
	 *
	 * @param edge the outer edge to add.
	 */
	public void addOuterEdge(CyEdge edge);

	/**
	 * Determine if a node is a member of this group.
	 *
	 * @param node the node to check for membership
	 * @return true if this node is a member of this group.
	 */
	public boolean contains(CyNode node);

	/**
	 * Get the name of the group.  This is basically the same call as
	 * getIdentifier().
	 *
	 * @return the group name
	 */
	public String getGroupName();


	/**
	 * Return the node that is used to represent this group.
	 *
	 * @return the node that represents this group
	 */
	public CyNode getGroupNode();

	/**
	 * Get the list of inner edges for this group
	 *
	 * @return the list of inner edges
	 */
	public List<CyEdge> getInnerEdges();

	/**
	 * Get the list of nodes for this group
	 *
	 * @return the list of nodes
	 */
	public List<CyNode> getNodes();

	/**
	 * Get the list of outer edges for this group
	 *
	 * @return the list of outer edges
	 */
	public List<CyEdge> getOuterEdges();

	/**
	 * Get the current state of this group.  <<Should this be an Object>>??
	 *
	 * @return the current state
	 */
	public int getState();

	/**
	 * Get the viewer responsible for viewing the group.  
	 * <<Issue: A case can be made to move this out of the CyGroup 
	 * object and have it only live in the view package.  This might 
	 * complicate serialization, but it could be cleaner in some ways.>>
	 *
	 * @return the name of the viewer
	 */
	public String getViewer();

	/**
	 * Remove a node from the group.  Note that this will also remove any edges
	 * connected to this node (if they are outer edges), or make them outer edges
	 * (if they are inner edges).
	 *
	 * @param node the node to be removed
	 */
	public void removeNode(CyNode node);

	/**
	 * Remove an inner edge from the group.
	 *
	 * @param edge the edge to be removed
	 */
	public void removeInnerEdge(CyEdge edge);

	/**
	 * Remove an outer edge from the group.
	 *
	 * @param edge the edge to be removed
	 */
	public void removeOuterEdge(CyEdge edge);

	/**
	 * Set the current state of this group.  <<Should this be an Object>>??
	 *
	 * @param state the group state to set
	 */
	public void setState(int state);
}
