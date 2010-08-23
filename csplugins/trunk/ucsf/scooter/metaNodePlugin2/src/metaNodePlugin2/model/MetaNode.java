/* vim: set ts=2: */
/**
 * Copyright (c) 2007 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package metaNodePlugin2.model;

// System imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.awt.Dimension;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.logger.CyLogger;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

// our imports
import metaNodePlugin2.MetaNodePlugin2;
import metaNodePlugin2.view.ViewUtils;

// import csplugins.layout.Profile;

/**
 * The MetaNode class provides a wrapper for a CyGroup that
 * maintains the additional state information we want to keep
 * to allow the MetaNode abstraction (expand/contract) to work
 * more efficiently
 */
public class MetaNode {
	public static final String X_HINT_ATTR = "__metanodeHintX";
	public static final String Y_HINT_ATTR = "__metanodeHintY";
	public static final String CHILDREN_ATTR = "NumChildren";
	public static final String DESCENDENTS_ATTR = "NumDescendents";

	private CyLogger logger = null;
	private CyGroup metaGroup = null;

	private boolean aggregateAttributes = false;
	private boolean hideMetanode = true;
	private double metanodeOpacity = 0.;
	private boolean useNestedNetworks = false;

	private Map<CyEdge,CyEdge> metaEdges = new HashMap<CyEdge,CyEdge>();

	private boolean collapsed = false;

	/**
	 * Main constructor -- should this extend CyGroup????
	 *
	 * @param group the group to wrap the MetaNode around
	 */
	protected MetaNode(CyGroup group) {
		metaGroup = group;
		logger = CyLogger.getLogger(MetaNode.class);

		// This method does most of the work.
		updateMetaEdges();

	}

	public CyGroup getCyGroup() { return metaGroup; }

	/**
	 * Add a node to this metaNode.  It will already have been added to the 
	 * group, but we need to update our internal data structures and (possibly)
	 * hide/restore some nodes and/or edges.
	 *
	 * @param node the CyNode that was added
	 */
	public void nodeAdded(CyNode node) {
		logger.debug("node added "+metaGroup);
		// Recreate our meta-edges.  There might be more efficient ways of doing this
		// than recreating everything, but the performance of updateMetaEdges isn't too
		// bad, and the complexity of managing the state necessary to update meta-edges
		// on a more granular basis isn't warranted.
		updateMetaEdges();

		// If we're collapsed, we need to recollapse to update
		// our attributes
		if (isCollapsed())
			recollapse(null);
	}

	/**
	 * Remove a node from this metaNode.  It will already have been removed from the 
	 * group, but we need to update our internal data structures and (possibly)
	 * hide/restore some nodes and/or edges.
	 *
	 * @param node the CyNode that was removed
	 */
	public void nodeRemoved(CyNode node) {
		logger.debug("node removed "+metaGroup);
		// First step, we need to remove any new meta-edges
		updateMetaEdges();
	}

	/**
	 * Recollapse a MetaNode.  This is only used when we're restoring a metaNode
	 * from XGMML.  The problem is that we need to remember the hints of the
	 * nodes before we collapse, then update those hints so that we expand
	 * properly.
	 *
	 * @param view the view to use
	 */
	public void recollapse(CyNetworkView view) {
		logger.debug("recollapse "+metaGroup);
		if (view == null)
			view = Cytoscape.getNetworkView(metaGroup.getNetwork().getIdentifier());
		expand(view);
		collapse(view);
	}
	

	/**
	 * Collapse this MetaNode
	 *
	 * @param view the CyNetworkView
	 * @param updateNetwork if 'true', actually update the network
	 */
	public void collapse(CyNetworkView view) {
		logger.debug("collapse "+metaGroup);
		if (isCollapsed())
			return;

		// Get our node attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// Hide all of our member nodes.
		Dimension position = ViewUtils.hideNodes(metaGroup, view, nodeAttributes);

		// Restore the meta-node.
		ViewUtils.restoreMetaNode(metaGroup, view, position, useNestedNetworks, metanodeOpacity);

		// Restore our meta-edges
		ViewUtils.restoreEdges(metaGroup, metaEdges.values(), view);

		collapsed = true;
	}

	/**
	 * Expand this MetaNode.
	 *
	 * @param view the CyNetworkView
	 * @param update update the display?
	 */
	public void expand(CyNetworkView view) {
		logger.debug("expand");
		if (!isCollapsed())
			return;

		// Handle the case where we're hidden by a collapsed parent
		if (isHidden()) {
			expandParent(view);
		}

		// Hide our metaNode
		Dimension position = ViewUtils.hideMetaNode(metaGroup, view, hideMetanode);

		// Restore our nodes
		ViewUtils.restoreNodes(metaGroup, metaGroup.getNetwork(), view, position, Cytoscape.getNodeAttributes());

		// Restore our edges
		ViewUtils.restoreEdges(metaGroup, metaGroup.getInnerEdges(), view);
		ViewUtils.restoreEdges(metaGroup, metaGroup.getOuterEdges(), view);
		ViewUtils.restoreEdges(metaGroup, metaEdges.values(), view);

		collapsed = false;
	}

	/**
	 * Create a meta edge.  A meta-edge is an edge that replaces one
	 * or more edges between two nodes with an edge between one of the
	 * nodes and a collapsed group that contains the other node.  More
	 * than one edge might be replaced if the node that is not part
	 * of our group (the partner node) has edges to more than one node
	 * that is part of our group. 
	 *
	 *     There are a couple of different cases when we go to create a
	 * meta-edge:
	 * 1) Neither node is hidden: Just create the edge.
	 * 2) Our partner node is hidden: Create a meta-edge to the parent
	 *	  of the partner and store it in both our list and our partners
	 *
	 * @param edge the edge to use as a start
	 * @param source the source node (pretty much always our group node)
	 * @param target the target node
	 * @return the created metaEdge
	 */
	public CyEdge createMetaEdge(CyEdge edge, CyNode source, CyNode target) {
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String identifier = "meta-"+edge.getIdentifier();
		String interaction = edgeAttributes.getStringAttribute(edge.getIdentifier(), Semantics.INTERACTION);
		CyEdge newEdge = Cytoscape.getCyEdge(source.getIdentifier(),identifier,
		                                     target.getIdentifier(),"meta-"+interaction);
		metaEdges.put(newEdge,newEdge);
		return newEdge;
	}

	/**
 	 * Determine if this metanode is collapsed in this view
 	 *
 	 * @param view the network view to check
 	 * @return True if it is collapsed, False otherwise
 	 */
	public boolean isCollapsed() {
		if (collapsed)
			return true;
		return false;
	}

	/**
 	 * If we are currently hidden (a child of a collapsed metanode) return
 	 * true.
 	 *
 	 * @return True if hidden, False otherwise
 	 */
	public boolean isHidden() {
		CyNode groupNode = metaGroup.getGroupNode();
		CyNetwork network = metaGroup.getNetwork();
		// Are we in any groups?
		if (groupNode.getGroups() == null || groupNode.getGroups().size() == 0)
			return false;

		// If we're collapsed, but not in the network -- we are, by definition
		// "hidden"
		if (isCollapsed() && !network.containsNode(groupNode))
			return true;

		return false;
	}

	/**
 	 * Return our list of meta-edges
 	 *
 	 * @return meta-edge list
 	 */
	public Collection getMetaEdges() {
		return metaEdges.values();
	}

	/**
 	 * Add a meta-edge to our list
 	 *
 	 * @param metaEdge the metaEdge to add
 	 */
	public void addMetaEdge(CyEdge metaEdge) {
		metaEdges.put(metaEdge, metaEdge);
	}

	/**
 	 * Test to see if this meta-node has a particular meta-edge
 	 *
 	 * @param metaEdge the meta-edge to test
 	 * @return true if this metanode has this meta-edge
 	 */
	public boolean containsMetaEdge(CyEdge metaEdge) {
		return metaEdges.containsKey(metaEdge);
	}

	/**
	 * Sets whether or not we create a nested network when we
	 * collapse the metanode
	 *
	 * @param nestedNetwork if 'true' we will use nested networks
	 */
	public void setUseNestedNetworks(boolean nestedNetwork) {
		this.useNestedNetworks = nestedNetwork;
	}

	/**
	 * Sets the metanode opacity
	 *
	 * @param opacity the metanode opacity
	 */
	public void setMetaNodeOpacity(double opacity) {
		this.metanodeOpacity = opacity;
	}

	/**
	 * Sets whether or not we hide the metnode when we expand the
	 * network.
	 *
	 * @param hide if 'true' we hide the metanode upon expansion
	 */
	public void setHideMetaNode(boolean hide) {
		this.hideMetanode = hide;
	}

	/**
	 * Controls whether this metanode is aggregating attributes
	 *
	 * @param aggregate if 'true' aggregate
	 */
	public void setAggregateAttributes(boolean aggregate) {
		if (!this.aggregateAttributes && aggregate) {
			this.aggregateAttributes = aggregate;
			// updateAttributes();
		} else {
			this.aggregateAttributes = aggregate;
		}
	}

	/**
	 * Returns 'true' if we hide the metnode when we expand the
	 * network.
	 *
	 * @return 'true' if we hide the metanode upon expansion
	 */
	public boolean getHideMetaNode() {
		return this.hideMetanode;
	}

	/**
 	 * This method is the central method for the creation and maintenance of a
 	 * meta-node.  Essentially, it is responsible for creating all of the meta-edges
 	 * that connect this meta-node to external nodes.
 	 *
 	 * Basic approach:
 	 * 	for each external edge:
 	 * 	     if edge.partner is in a group AND group is in our network:
 	 * 	         add a meta-edge to the parter group node
 	 * 	     add a meta-edge to the parter
 	 *	 
 	 * We also handle the following special cases:
 	 * 	Partner node is itself a group
 	 * 	Partner node is a member of a group
 	 */
	private void updateMetaEdges() {
		// Initialize our meta-edge map
		metaEdges = new HashMap<CyEdge, CyEdge>();

		// We need to use a list iterator, because we might need to add new edges to our outer
		// edge list and we want to add them to the iterator to re-examine them
		ListIterator<CyEdge> iterator = metaGroup.getOuterEdges().listIterator();
		while(iterator.hasNext()) {
			CyEdge edge = iterator.next();
			CyNode node = getPartner(edge);

			// Create the meta-edge to the external node
			CyEdge metaEdge = createMetaEdge(edge, metaGroup.getGroupNode(), node);

			// Special case for the situation where our partner is a group.  In this
			// case, some of our outer edges might be missing because they were hidden
			// so we need to add it to our outerEdges list and to our iterator so that
			// we'll revisit it.
			if (MetaNodeManager.getMetaNode(node) != null) { 
				addPartnerEdges(iterator, node);
			}
			// Now, handle the case where the partner is a member of one or more groups
			if (node.getGroups() != null && node.getGroups().size() > 0) {
				addPartnerMetaEdges(edge, node, metaEdge);
			}
		}
	}

	/**
 	 * Expand the our parent, if it is collapsed
 	 *
 	 * @param view the network view we're operating in
 	 */
	private void expandParent(CyNetworkView view) {
		CyNode groupNode = metaGroup.getGroupNode();
		List<CyGroup> parentList = groupNode.getGroups();
		// We need to expand any collapsed parent since we
		// don't know which (if any) we were collapsed by
		for (CyGroup group: parentList) {
			// Is it a metanode?
			MetaNode parent = MetaNodeManager.getMetaNode(group);
			if (parent == null) {
				// Nope, just continue
				continue;
			}
			if (parent.isCollapsed())
				group.setState(MetaNodePlugin2.EXPANDED);
		}
	}

	/**
	 * Test to see if a node is hidden
	 *
	 * @param node the node to check
	 * @return true if the node is hidden, false otherwise
	 */
	private boolean isNodeHidden(CyNode node) {
		return (!metaGroup.getNetwork().containsNode(node));
	}

	private void addPartnerMetaEdges(CyEdge connectingEdge, CyNode partnerNode, CyEdge metaEdge) {
		for (CyGroup partnerGroup: partnerNode.getGroups()) {
			if (partnerGroup.getNetwork() == metaGroup.getNetwork()) {
			  MetaNode partner = MetaNodeManager.getMetaNode(partnerGroup.getGroupNode());
				if (partner != null) {
					// Create a meta-meta edge
					CyEdge metaMetaEdge = createMetaEdge(connectingEdge, metaGroup.getGroupNode(), partnerGroup.getGroupNode());
					partner.addMetaEdge(metaMetaEdge);
					// Add our meta-edge to the partner group
					partner.addMetaEdge(metaEdge);
				}
			}
		}
	}

	private void addPartnerEdges(ListIterator<CyEdge> iterator, CyNode partnerNode) {
		CyGroup partnerGroup = CyGroupManager.getCyGroup(partnerNode);
		MetaNode partnerMeta = MetaNodeManager.getMetaNode(partnerGroup);
		if (partnerMeta.isCollapsed()) {
			// Get the outer edges of the partner node, and if they point to us,
			// add the edge to our outer edge list
			CyNetwork myGraph = metaGroup.getGraphPerspective();
			List<CyEdge> partnerEdges = partnerGroup.getOuterEdges();
			for (CyEdge partnerEdge: partnerEdges) {
				// We are only interested in edges that connect to one of our nodes
				if (isConnectingEdge(partnerEdge)) {
					// Actually add it to the list
					metaGroup.addOuterEdge(partnerEdge);
					// Now, add it to our iterator so that we can re-examine it
					iterator.add(partnerEdge);
					// We actually want to examine this edge again, so we need to back up
					iterator.previous();
				}
			}
		}
	}

	private boolean isConnectingEdge(CyEdge edge) {
		CyNetwork myGraph = metaGroup.getGraphPerspective();
		CyNode source = (CyNode)edge.getSource();
		CyNode target = (CyNode)edge.getTarget();
		if (myGraph.containsNode(source) || myGraph.containsNode(target))
			return true;
		return false;
	}

	/**
	 * Get the partner (i.e. the 'other' node) for an edge
	 *
	 * @param edge the edge to get the partner for
	 * @return the partner CyNode
	 */
	private CyNode getPartner(CyEdge edge) {
		CyNode source = (CyNode)edge.getSource();
		CyNode target = (CyNode)edge.getTarget();
		if (source == metaGroup.getGroupNode() || metaGroup.getGraphPerspective().containsNode(source))
			return target;
		return source;
	}

	/**
	 * Get the localNode for an edge
	 *
	 * @param edge the edge to get the localnode for
	 * @return the local CyNode
	 */
	private CyNode getLocalNode(CyEdge edge) {
		CyNode source = (CyNode)edge.getSource();
		CyNode target = (CyNode)edge.getTarget();
		if (source == metaGroup.getGroupNode() || metaGroup.getNodes().contains(source))
			return source;
		return target;
	}

}
