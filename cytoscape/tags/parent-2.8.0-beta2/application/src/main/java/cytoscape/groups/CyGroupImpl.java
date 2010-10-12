/* vim :set ts=2: */
/*
  File: CyGroupImpl.java

  Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.groups;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;

import cytoscape.data.CyAttributes;
import cytoscape.giny.CytoscapeRootGraph;

import cytoscape.groups.CyGroupViewer.ChangeType;

import giny.model.Edge;
import giny.model.GraphObject;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * The CyGroup class provides the implementation for a group model that
 * maintains the list of nodes belonging to a group, the parent of a particular
 * group, and the node that represents the group.  Group information is stored
 * in the CyGroup itself, as well as in special group attributes that are associated
 * with the network, nodes, and edges involved.  These attributes provide a natural
 * mechanism for the saving and restoration of groups.  There are also opaque flags
 */
public class CyGroupImpl implements CyGroup {
	// Instance data

	/**
	 * A map storing the list of edges for a node at the time it was
	 * added to the group
	 */
	private HashMap<CyNode,List<CyEdge>> nodeToEdgeMap;

	/**
	 * The edges in this group that involve members outside of this group
	 */
	private HashMap<CyEdge, CyEdge> outerEdgeMap;

	/**
	 * This is the network that this group is part of.  If this
	 * is null, it's a global group.
	 */
	private CyNetwork network = null;

	/**
	 * The node that represents this group
	 */
	private CyNode groupNode = null;

	/**
	 * The group name
	 */
	private String groupName = null;

	/**
	 * Group state.  This is used by the view components to set the current
	 * state of the group (collapsed, expanded, etc.)
	 */
	private int groupState = 0;

	/**
	 * viewValue is an opaque type for use by view compoenents to "remember"
	 * information about the group
	 */
	private Object viewValue = null;

	/**
	 * viewer is the viewer that this group is managed by
	 */
	private String viewer = null;

	/**
 	 * the internal graph that represents this network
 	 */
	private CyNetwork myGraph = null;

	// Public methods

	/**
	 * Empty constructor
	 */
	protected CyGroupImpl() {
		this.nodeToEdgeMap = new HashMap<CyNode, List<CyEdge>>();
		this.outerEdgeMap = new HashMap<CyEdge,CyEdge>();
		this.network = null;
	}

	/**
	 * Constructor to create an empty group.
	 *
	 * @param groupNode the CyNode to use for this group
	 * @param nodeList the initial set of nodes for this group
	 * @param internalEdges the initial set of internal edges for this group
	 * @param externalEdges the initial set of external edges for this group
	 * @param network the network this group is part of
	 *
	 */
	protected CyGroupImpl(CyNode groupNode, List<CyNode> nodeList, List<CyEdge> internalEdges,
                        List<CyEdge> externalEdges, CyNetwork network) {

		// System.out.println("Creating group "+groupNode);

		this.nodeToEdgeMap = new HashMap<CyNode, List<CyEdge>>();
		this.outerEdgeMap = new HashMap<CyEdge,CyEdge>();
		this.network = network;

		this.groupNode = groupNode;
		this.groupName = this.groupNode.getIdentifier();

		if (nodeList == null && internalEdges == null && externalEdges == null)
			return;

		// System.out.println("   Group "+groupNode+" has "+nodeList.size()+" nodes");

		CyNetwork thisNetwork = network;
		if (network == null)
			thisNetwork = Cytoscape.getCurrentNetwork();

		// If we aren't provided with any inner or outer edges, we need to get them
		if (internalEdges == null) {
			// Get our list of internal edges
			internalEdges = (List<CyEdge>) thisNetwork.getConnectingEdges(nodeList);
		}

		// At this point, we've got a list of nodes and a list of edges.  We
		// could now create a CyNetwork that contains the internal components
		// of this group.
		{
			CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();

			Node[] nodeArray = null;
			Edge[] edgeArray = null;
			if (nodeList != null && nodeList.size() > 0)
				nodeArray = nodeList.toArray(new Node[0]);
			if (internalEdges != null && internalEdges.size() > 0)
				edgeArray = internalEdges.toArray(new Edge[0]);
			myGraph = rootGraph.createNetwork(nodeArray, edgeArray);
		}

		// Create our node and edge map
		for (CyNode node: nodeList) {
			List<CyEdge> adjacentEdges = (List<CyEdge>)thisNetwork.getAdjacentEdgesList(node, true, true, true);
			nodeToEdgeMap.put(node, adjacentEdges);
			// If we don't have external edges, add them now
			if (externalEdges == null && adjacentEdges != null) {
				for (CyEdge edge: adjacentEdges) {
					if (!myGraph.containsEdge(edge))
						outerEdgeMap.put(edge, edge);
				}
			}
			node.addToGroup(this);
		}

		// If we were provided with an external edge list, create our map now.
		if (externalEdges != null) {
			for (CyEdge edge: externalEdges)
				outerEdgeMap.put(edge, edge);
		}
	}

	/**
	 * Constructor to create an empty group
	 *
	 * @param groupName the identifier to use for this group -- should be unique!
	 */
	protected CyGroupImpl(String groupName) {
		this(Cytoscape.getCyNode(groupName, true), null, null, null, null);
	}

	/**
	 * Constructor to create an empty group when the group node is specified.
	 *
	 * @param groupNode the CyNode to use for this group
	 */
	protected CyGroupImpl(CyNode groupNode) {
		this(groupNode, null, null, null, null);
	}

	/**
	 * Constructor to create a group with the listed nodes as initial members, and a predetermined
	 * CyNode to act as the group Node.
	 *
	 * @param groupNode the group node to use for this group
	 * @param nodeList the initial set of nodes for this group
	 */
	protected CyGroupImpl(CyNode groupNode, List<CyNode> nodeList) {
		this(groupNode, nodeList, null, null, null);
	}

	/**
	 * Constructor to create a group with the listed nodes as initial members.
	 *
	 * @param groupName the identifier to use for this group -- should be unique!
	 * @param nodeList the initial set of nodes for this group
	 */
	protected CyGroupImpl(String groupName, List<CyNode> nodeList) {
		this(Cytoscape.getCyNode(groupName, true), nodeList, null, null, null);
	}

	/**
	 * Return the name of this group
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Set the name of this group
	 */
	public void setGroupName(String name) {
		this.groupName = name;
	}

	/**
	 * Get all of the nodes in this group
	 *
	 * @return list of nodes in the group
	 */
	public List<CyNode> getNodes() {
		return (List<CyNode>)myGraph.nodesList();
	}

	/**
	 * Get the CyNode that represents this group
	 *
	 * @return CyNode representing the group
	 */
	public CyNode getGroupNode() {
		return this.groupNode;
	}

	/**
	 * Get an iterator over all of the nodes in this group
	 *
	 * @return node iterator
	 */
	public Iterator<CyNode> getNodeIterator() {
		return (Iterator<CyNode>)myGraph.nodesIterator();
	}

	/**
	 * Get all of the edges completely contained within this group
	 *
	 * @return list of edges in the group
	 */
	public List<CyEdge> getInnerEdges() {
		return (List<CyEdge>)myGraph.edgesList();
	}

	/**
	 * Get all of the edges partially contained within this group
	 *
	 * @return list of edges in the group
	 */
	public List<CyEdge> getOuterEdges() {
		Collection<CyEdge> v = outerEdgeMap.values();

		return new ArrayList<CyEdge>(v);
	}

	/**
	 * Add an outer edge to the map.  Some viewers may need to do this
	 * if they add and remove edges, for example.
	 *
	 * @param edge the CyEdge to add to the outer edge map
	 */
	public void addOuterEdge(CyEdge edge) {
		if (edge == null) return;
		outerEdgeMap.put(edge, edge);

		notifyViewer(edge, ChangeType.OUTER_EDGE_ADDED);
	}

	/**
	 * Remove an outer edge from the map.  Some viewers may need to do this
	 * if they add and remove edges, for example.
	 *
	 * @param edge the CyEdge to add to the outer edge map
	 */
	public void removeOuterEdge(CyEdge edge) {
		if (edge == null) return;
		outerEdgeMap.remove(edge);

		notifyViewer(edge, ChangeType.OUTER_EDGE_REMOVED);
	}

	/**
	 * Add an inner edge to the map.  Some viewers may need to do this
	 * if they add and remove edges, for example.
	 *
	 * @param edge the CyEdge to add to the innter edge map
	 */
	public void addInnerEdge(CyEdge edge) {
		if (edge == null) return;
		myGraph.addEdge(edge);

		notifyViewer(edge, ChangeType.INNER_EDGE_ADDED);

	}

	/**
	 * Remove an inner edge from the map.  Some viewers may need to do this
	 * if they add and remove edges, for example.
	 *
	 * @param edge the CyEdge to add to the innter edge map
	 */
	public void removeInnerEdge(CyEdge edge) {
		if (edge == null) return;
		myGraph.removeEdge(edge.getRootGraphIndex(), false);

		notifyViewer(edge, ChangeType.INNER_EDGE_REMOVED);
	}

	/**
	 * Get the network this group is a member of
	 *
	 * @return the network, or null if this is a global group
	 */
	public CyNetwork getNetwork() {
		return network;
	}

	/**
	 * Set (or change) the network fro this group
	 *
	 * @param network the network to change this group to
	 * @param notify whether to notify any viewers
	 */
	public void setNetwork(CyNetwork network, boolean notify) {

		// Change our attribute
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		if (network != null)
			nodeAttributes.setAttribute(groupNode.getIdentifier(), CyGroup.GROUP_LOCAL_ATTR, Boolean.TRUE);
		else
			nodeAttributes.setAttribute(groupNode.getIdentifier(), CyGroup.GROUP_LOCAL_ATTR, Boolean.FALSE);
			
		if (notify && this.network != network) {
			notifyViewer(null, ChangeType.NETWORK_CHANGED);
		}
		this.network = network;
	}

	/**
	 * Determine if a node is a member of this group
	 *
	 * @param node the CyNode to test
	 * @return true if node is a member of the group
	 */
	public boolean contains(CyNode node) {
		if (myGraph.containsNode(node))
			return true;

		return false;
	}

	/**
 	 * Return a CyNetwork that represents
 	 * the internal components of this group
 	 *
 	 * @return the CyNetwork 
 	 */
	public CyNetwork getGraphPerspective() {
		return myGraph;
	}

	/**
	 * Set the state of the group.  Setting the state of a group has
	 * two byproducts.  First, the attribute "__groupState" is set to
	 * the state value to allow persistance across saves and restores.
	 * Second, if there is a viewer for this group, it is informed that
	 * the state has changed.  This is used by the metaNode viewer, for
	 * example to provide programmatic control of whether the group is
	 * expanded or collapsed.
	 *
	 * @param state the state to set
	 */
	public void setState(int state) {
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		this.groupState = state;
		attributes.setUserVisible(GROUP_STATE_ATTR, false);
		attributes.setAttribute(this.groupName, GROUP_STATE_ATTR, this.groupState);

		notifyViewer(null, ChangeType.STATE_CHANGED);
	}

	/**
	 * Get the state of the group
	 *
	 * @return group state
	 */
	public int getState() {
		return this.groupState;
	}

	/**
	 * Set the viewValue for the group
	 *
	 * @param viewValue the view value to set
	 */
	public void setViewValue(Object viewValue) {
		this.viewValue = viewValue;
	}

	/**
	 * Get the viewValue for the group
	 *
	 * @return the view value
	 */
	public Object getViewValue() {
		return this.viewValue;
	}

	/**
	 * Provide the default toString method
	 *
	 * @return group name
	 */
	public String toString() {
		return this.groupName;
	}

	/**
	 * Set the viewer for this group.  In order to maintain the
	 * static tables correctly, this method is protected and
	 * CyGroup.setGroupViewer(group, viewer, notify) should be used
	 * instead.
	 *
	 * @param viewerName name of the viewer for the group
	 */
	protected void setViewer(String viewerName) {
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		this.viewer = viewerName;

		if (this.viewer != null) {
			attributes.setUserVisible(GROUP_VIEWER_ATTR, false);
			attributes.setAttribute(this.groupName, GROUP_VIEWER_ATTR, this.viewer);
		}
	}

	/**
	 * Get the name of the viewer for this group
	 *
	 * @return viewer for this group
	 */
	public String getViewer() {
		return this.viewer;
	}

	/**
	 * Add a new node to this group
	 *
	 * @param node the node to add
	 */
	public void addNode ( CyNode node ) {
		addNode(node, true);
	}

	/**
	 * Add a new node to this group
	 *
	 * @param node the node to add
	 */
	public void addNode ( CyNode node, boolean addEdges ) {
		// First see if this node is already in this group
		if (myGraph.containsNode(node))
			return;

		myGraph.addNode(node);

		if (!addEdges)
			return;

		// We need to go throught our outerEdgeMap first to see if this
		// node has outer edges and proactively move them to inner edges.
		// this needs to be done here because some viewers might have
		// hidden edges on us, so the the call to getAdjacentEdgeIndices in
		// addNodeToGroup won't return all of the edges.
		List <CyEdge> eMove = new ArrayList<CyEdge>();
		for (CyEdge edge: outerEdgeMap.keySet()) {
			if (edge.getTarget() == node || edge.getSource() == node) {
				eMove.add(edge);
			}
		}
		for (CyEdge edge: eMove) {
			outerEdgeMap.remove(edge);
			myGraph.addEdge(edge);
		}

		// Note the cute little trick we play -- making sure these
		// are added to the edgeMap
		nodeToEdgeMap.put(node, eMove);

		addNodeToGroup(node);

		notifyViewer(node, ChangeType.NODE_ADDED);
	}


	/**
	 * Remove a node from a group
	 *
	 * @param node the node to remove
	 */
	public void removeNode ( CyNode node ) {
		removeNodeFromGroup(node);

		notifyViewer(node, ChangeType.NODE_REMOVED);

	}

	/**
	 * Add a new node to this group
	 *
	 * @param node the node to add
	 */
	private void addNodeToGroup ( CyNode node ) {
		CyNetwork groupNetwork = this.network;

		if (groupNetwork == null)
			groupNetwork = Cytoscape.getCurrentNetwork();

		List <CyEdge>edgeList = null;

		if (nodeToEdgeMap.containsKey(node)) {
			edgeList = nodeToEdgeMap.get(node);
		} else {
			edgeList = new ArrayList<CyEdge>();
		}

		// Add all of the edges
		List<CyEdge> adjacentEdges = (List<CyEdge>)groupNetwork.getAdjacentEdgesList(node, true, true, true);
		for (CyEdge edge: adjacentEdges) {
			// Not sure if this is faster or slower than going through the entire loop
			if (myGraph.containsEdge(edge))
				continue;

			edgeList.add(edge);
			CyNode target = (CyNode)edge.getTarget();
			CyNode source = (CyNode)edge.getSource();

			// Check to see if this edge is one of our own metaEdges
			if (source == groupNode || target == groupNode) {
				// It is -- skip it
				continue;
			}

			if (outerEdgeMap.containsKey(edge)) {
				outerEdgeMap.remove(edge);
				myGraph.addEdge(edge);
			} else if (myGraph.containsNode(target) && myGraph.containsNode(source)) {
				myGraph.addEdge(edge);
			} else if (myGraph.containsNode(target) || myGraph.containsNode(source)) {
				outerEdgeMap.put(edge,edge);
			}
		}
		nodeToEdgeMap.put(node, edgeList);

		// Tell the node about it (if necessary)
		if (!node.inGroup(this))
			node.addToGroup(this);
	}

	/**
	 * Remove a node from a group
	 *
	 * @param node the node to remove
	 */
	private void removeNodeFromGroup ( CyNode node ) {
		// Get the list of edges
		List <CyEdge>edgeArray = nodeToEdgeMap.get(node);
		for (CyEdge edge: edgeArray) {
			if (myGraph.containsEdge(edge)) {
				outerEdgeMap.put(edge,edge);
			} else if (outerEdgeMap.containsKey(edge)) {
				outerEdgeMap.remove(edge);
			}
		}
		nodeToEdgeMap.remove(node);

		// Remove the node from our map
		myGraph.removeNode(node.getRootGraphIndex(), false);

		// Tell the node about it (if necessary)
		if (node.inGroup(this))
			node.removeFromGroup(this);
	}

	/**
 	 * Notify our viewer that something has changed
 	 *
 	 */
	private void notifyViewer(GraphObject object, CyGroupViewer.ChangeType change) {
		// Get our viewer
		CyGroupViewer v = CyGroupManager.getGroupViewer(this.viewer);
		if (v != null) {
			// Tell the viewer that something has changed
			v.groupChanged(this, object, change);
		}
	}
}
