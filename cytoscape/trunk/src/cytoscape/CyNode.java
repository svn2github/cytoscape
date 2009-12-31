/*
  File: CyNode.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape;

import giny.model.GraphPerspective;
import giny.model.RootGraph;

import java.util.ArrayList;
import java.util.List;

import cytoscape.giny.CytoscapeFingRootGraph;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;


/**
 *
 */
public class CyNode implements giny.model.Node {
	public static final String NESTED_NETWORK_ID_ATTR = "nested_network_id";
	public static final String NESTED_NETWORK_IS_VISIBLE_ATTR = "nested_network_is_visible";
	public static final String HAS_NESTED_NETWORK_ATTR = "has_nested_network";
	public static final String PARENT_NODES_ATTR = "parent_nodes";

	// Variables specific to public get/set methods.
	CytoscapeFingRootGraph m_rootGraph = null;
	int m_rootGraphIndex = 0;
	String m_identifier = null;
	ArrayList<CyGroup> groupList = null;

	private GraphPerspective nestedNetwork;

	/**
	 * Creates a new CyNode object.
	 *
	 * @param root  DOCUMENT ME!
	 * @param rootGraphIndex  DOCUMENT ME!
	 */
	public CyNode(final RootGraph root, final int rootGraphIndex) {
		this.m_rootGraph = (CytoscapeFingRootGraph) root;
		this.m_rootGraphIndex = rootGraphIndex;
		this.m_identifier = new Integer(m_rootGraphIndex).toString();
		this.nestedNetwork = null;
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public GraphPerspective getGraphPerspective() {
		return m_rootGraph.createGraphPerspective(m_rootGraph.getNodeMetaChildIndicesArray(m_rootGraphIndex),
		                                          m_rootGraph.getEdgeMetaChildIndicesArray(m_rootGraphIndex));
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @param gp DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setGraphPerspective(GraphPerspective gp) {
		if (gp.getRootGraph() != m_rootGraph)
			return false;

		final int[] nodeInx = gp.getNodeIndicesArray();
		final int[] edgeInx = gp.getEdgeIndicesArray();

		for (int i = 0; i < nodeInx.length; i++)
			m_rootGraph.addNodeMetaChild(m_rootGraphIndex, nodeInx[i]);

		for (int i = 0; i < edgeInx.length; i++)
			m_rootGraph.addEdgeMetaChild(m_rootGraphIndex, edgeInx[i]);

		return true;
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public RootGraph getRootGraph() {
		return m_rootGraph;
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getRootGraphIndex() {
		return m_rootGraphIndex;
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getIdentifier() {
		return m_identifier;
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @param new_id DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean setIdentifier(final String new_id) {
		if (new_id == null) {
			m_rootGraph.setNodeIdentifier(m_identifier, 0);
		} else {
			m_rootGraph.setNodeIdentifier(new_id, m_rootGraphIndex);
		}

		m_identifier = new_id;

		return true;
	}


	/**
	 * Add this node to the specified group.
	 *
	 * @param group CyGroup to add this group to
	 */
	public void addToGroup(final CyGroup group) {
		// We want to create this lazily to avoid any unnecessary performance/memory
		// hits on CyNodes!
		if (groupList == null)
			groupList = new ArrayList<CyGroup>();

		groupList.add(group);

		if (!group.contains(this))
			group.addNode(this);
	}


	/**
	 * Remove this node from the specified group.
	 *
	 * @param group CyGroup to remove this group from
	 */
	public void removeFromGroup(final CyGroup group) {
		groupList.remove(group);
		groupList.trimToSize();

		if (group.contains(this))
			group.removeNode(this);
	}


	/**
	 * Return the list of groups this node is a member of
	 *
	 * @return list of CyGroups this group is a member of
	 */
	public List<CyGroup> getGroups() {
		return groupList;
	}


	/**
	 * Check to see if this node is a member of the requested group
	 *
	 * @param group the group to check
	 * @return 'true' if this node is in group
	 */
	public boolean inGroup(CyGroup group) {
		if (groupList == null)
			return false;

		return groupList.contains(group);
	}


	/**
	 * Check to see if this node is a group
	 *
	 * @return 'true' if this node is a group
	 */
	public boolean isaGroup() {
		return CyGroupManager.isaGroup(this);
	}


	/**
	 * Return the "name" of a node
	 *
	 * @return string representation of the node
	 */
	public String toString() {
		return getIdentifier();
	}


	/**
	 * Assign a graph perspective reference to this node.
	 */
	public void setNestedNetwork(final GraphPerspective graphPerspective) {
		// Sanity check.
		if (graphPerspective == this.nestedNetwork)
			return;

		final GraphPerspective oldNestedNetwork = this.nestedNetwork;
		final String nodeID = this.getIdentifier();

		// create a Node Attribute "nested.network.id" for this Node
		final String networkID = ((CyNetwork)(graphPerspective == null ? this.nestedNetwork : graphPerspective)).getIdentifier();
		this.nestedNetwork = graphPerspective;

		// create or update Network Attribute "parent.node.name.list" for the Network
		final String[] attributeNames = Cytoscape.getNetworkAttributes().getAttributeNames();
		boolean attrFound = false;
		for (final String name : attributeNames) {
			if (name.equals(PARENT_NODES_ATTR)) {
				attrFound = true;
				break;
			}
		}
		List<String> parentNodeList;
		if (!attrFound) {
			parentNodeList = new ArrayList<String>();
			parentNodeList.add(nodeID);
		} else {
			parentNodeList = (List<String>) Cytoscape.getNetworkAttributes().getListAttribute(networkID, PARENT_NODES_ATTR);
			if (this.nestedNetwork != null) {
				parentNodeList.add(nodeID);
			} else {
				parentNodeList.remove(nodeID);
			}
		}
		Cytoscape.getNetworkAttributes().setListAttribute(networkID, PARENT_NODES_ATTR, parentNodeList);

		// tag or untag the node as having a nested network
		if (graphPerspective != null) {
			Cytoscape.getNodeAttributes().setAttribute(nodeID, HAS_NESTED_NETWORK_ATTR, "yes");
			Cytoscape.getNodeAttributes().setAttribute(nodeID, NESTED_NETWORK_IS_VISIBLE_ATTR, new Boolean(true));
			Cytoscape.getNodeAttributes().setAttribute(nodeID, NESTED_NETWORK_ID_ATTR, networkID);
		} else {
			Cytoscape.getNodeAttributes().deleteAttribute(nodeID, HAS_NESTED_NETWORK_ATTR);
			Cytoscape.getNodeAttributes().deleteAttribute(nodeID, NESTED_NETWORK_IS_VISIBLE_ATTR);
			Cytoscape.getNodeAttributes().deleteAttribute(nodeID, NESTED_NETWORK_ID_ATTR);
		}

		// Let listeners know that the previous nested network was removed
		if (oldNestedNetwork != null)
			Cytoscape.getPropertyChangeSupport().firePropertyChange(Cytoscape.NESTED_NETWORK_DESTROYED, this, oldNestedNetwork);

		// Let listeners know nested network was assigned to this node.
		if (this.nestedNetwork != null) {
			Cytoscape.getPropertyChangeSupport().firePropertyChange(Cytoscape.NESTED_NETWORK_CREATED, this, graphPerspective);
		}
	}


	/**
	 * Return the currently set graph perspective (may be null) associated with this node.
	 *
	 *  @return a network reference or null.
	 */
	public GraphPerspective getNestedNetwork() {
		return nestedNetwork;
	}

	/** Determines whether a nested network should be rendered as part of a node's view or not.
	 * @return true if the node has a nested network and we want it rendered, else false.
	 */
	public boolean nestedNetworkIsVisible() {
		final Boolean nestedNetworkIsVisibleAttr = Cytoscape.getNodeAttributes().getBooleanAttribute(this.getIdentifier(), NESTED_NETWORK_IS_VISIBLE_ATTR);
		return nestedNetworkIsVisibleAttr != null && nestedNetworkIsVisibleAttr;
	}

	/** Set the visibility of a node's nested network when rendered.
	 * @param makeVisible forces the visibility of a nested network.
	 * Please note that this call has no effect if a node has no associated nested network!
	 */
	public void showNestedNetwork(final boolean makeVisible) {
		if (getNestedNetwork() == null || nestedNetworkIsVisible() == makeVisible)
			return;

		Cytoscape.getNodeAttributes().setAttribute(this.getIdentifier(), NESTED_NETWORK_IS_VISIBLE_ATTR, new Boolean(makeVisible));
	}
}
