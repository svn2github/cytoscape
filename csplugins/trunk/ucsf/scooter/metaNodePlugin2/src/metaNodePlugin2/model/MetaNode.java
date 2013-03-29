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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
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
import metaNodePlugin2.data.AttributeManager;
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
	public static final String ISMETA_EDGE_ATTR = "__isMetaEdge";
	public static final String SETTINGS_ATTR = "__metanodeSettings";

	private CyLogger logger = null;
	private CyGroup metaGroup = null;

	private AttributeManager attributeManager = null;
	private boolean hideMetanode = true;
	private boolean createMembershipEdges = true;
	private boolean dontExpandEmpty = true;
	private double metanodeOpacity = 0.;
	private boolean useNestedNetworks = false;
	private String nodeChartAttribute = null;
	private String chartType = null;
	private String chartColorType = "contrasting";

	private Map<CyEdge,CyEdge> metaEdges = new HashMap<CyEdge,CyEdge>();
	private Map<CyNode,CyEdge> membershipEdges = null;
	private List<CyNode> hiddenNodes = null;

	private boolean collapsed = false;

	/**
	 * Main constructor -- should this extend CyGroup????
	 *
	 * @param group the group to wrap the MetaNode around
	 * @param ignoreMetaEdges if true, be careful with marked metaEdges
	 */
	protected MetaNode(CyGroup group, boolean ignoreMetaEdges) {
		metaGroup = group;
		logger = CyLogger.getLogger(MetaNode.class);

		logger.debug("Creating new metanode: "+group.getGroupNode()+", ignoreMetaEdges = "+ignoreMetaEdges);

		// This method does most of the work.
		updateMetaEdges(ignoreMetaEdges);

		// Load any parameters we might have set
		loadSettings();

	}

	protected boolean hasSavedSettings() {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		return nodeAttributes.hasAttribute(getGroupNode().getIdentifier(), SETTINGS_ATTR);
	}

	public CyGroup getCyGroup() { return metaGroup; }

	public CyNode getGroupNode() { return metaGroup.getGroupNode(); }

	/**
	 * Add a node to this metaNode.  It will already have been added to the 
	 * group, but we need to update our internal data structures and (possibly)
	 * hide/restore some nodes and/or edges.
	 *
	 * @param node the CyNode that was added
	 */
	public void nodeAdded(CyNode node) {
		logger.debug("node "+node+" added to "+metaGroup);
		// Recreate our meta-edges.  There might be more efficient ways of doing this
		// than recreating everything, but the performance of updateMetaEdges isn't too
		// bad, and the complexity of managing the state necessary to update meta-edges
		// on a more granular basis isn't warranted.
		updateMetaEdges(false);

		// Now, if this node is a child of a different metaNode, we need to create
		// metaEdges for that metaNode and this node.
		updateMembershipEdges(node);

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
		updateMetaEdges(false);
		// Now, remove our member edge (if there is one)
		if (membershipEdges != null && membershipEdges.containsKey(node))
			membershipEdges.remove(node);
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
		else
			collapsed = true;

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
		logger.debug("collapse "+metaGroup+": isCollapsed = "+isCollapsed()+" isHidden = "+isHidden()+" state = "+metaGroup.getState());
		if (isCollapsed())
			return;

		// Get our node attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		hiddenNodes = new ArrayList<CyNode>();

		// Hide all of our member nodes.
		Dimension position = ViewUtils.hideNodes(metaGroup, view, nodeAttributes, hiddenNodes);

		// Restore the meta-node.
		ViewUtils.restoreMetaNode(metaGroup, view, position, useNestedNetworks, metanodeOpacity);

		// Restore our meta-edges
		ViewUtils.restoreEdges(metaGroup, metaEdges.values(), view);

		// Update our attributes (if we're aggregating)
		if (attributeManager != null)
			attributeManager.updateAttributes(this);

		collapsed = true;
	}

	/**
	 * Expand this MetaNode.
	 *
	 * @param view the CyNetworkView
	 * @param update update the display?
	 */
	@SuppressWarnings("deprecation")
	public void expand(CyNetworkView view) {
		logger.debug("expand "+metaGroup+": isCollapsed = "+isCollapsed()+" isHidden = "+isHidden()+" state = "+metaGroup.getState());
		if (!isCollapsed())
			return;

		if (dontExpandEmpty && metaGroup.getNodes().size() == 0)
			return;

		// Handle the case where we're hidden by a collapsed parent
		if (isHidden()) {
			expandParent(view);
		}

		// Get our list of edges in case anyone has addded a new edge to us
		CyNode groupNode = metaGroup.getGroupNode();
		List edgeList = view.getNetwork().getAdjacentEdgesList(metaGroup.getGroupNode(), true, true, true);
		if (edgeList != null) {
			for (Object e: edgeList) {
				CyEdge edge = (CyEdge)e;
				// Add any new edges.
				if (!metaEdges.containsKey(edge)) {
					// logger.debug("  found new edge: "+edge.getIdentifier());
					metaEdges.put(edge, edge);
					metaGroup.addOuterEdge(edge);
				}
			}
		}

		// Hide our metaNode
		Dimension position = ViewUtils.hideMetaNode(metaGroup, view, hideMetanode);

		// Restore our nodes
		ViewUtils.restoreNodes(metaGroup, metaGroup.getNetwork(), view, position, Cytoscape.getNodeAttributes(),
                           hiddenNodes);

		// Restore our edges
		ViewUtils.restoreEdges(metaGroup, metaGroup.getInnerEdges(), view);
		ViewUtils.restoreEdges(metaGroup, metaGroup.getOuterEdges(), view);
		ViewUtils.restoreEdges(metaGroup, metaEdges.values(), view);

		// If we're not hiding the metanode, we need to hide the meta-edges, and show our "membership" edges
		if (!hideMetanode) {
			if (membershipEdges == null) {
				createMembershipEdges();
			}
			// ViewUtils.hideEdges(metaGroup, metaEdges.values(), view);
			ViewUtils.restoreEdges(metaGroup, membershipEdges.values(), view);
		}

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
	 * @param edgeName the name of the edge to use as a start
	 * @param source the source node (pretty much always our group node)
	 * @param target the target node
	 * @return the created metaEdge
	 */
	public CyEdge createMetaEdge(String edgeName, CyNode source, CyNode target) {
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		// logger.debug("Creating metaedge: meta-"+edgeName+" between "+source+" and "+target);
		CyEdge newEdge = createEdge("meta-", edgeName, source, target);
		edgeAttributes.setAttribute(newEdge.getIdentifier(), ISMETA_EDGE_ATTR, Boolean.TRUE);
		edgeAttributes.setUserVisible(ISMETA_EDGE_ATTR, false);
		metaEdges.put(newEdge,newEdge);
		return newEdge;
	}

	public boolean haveMetaEdge(String edgeName, CyNode source, CyNode target) {
		CyEdge newEdge = createEdge("meta-", edgeName, source, target);
		return metaEdges.containsKey(newEdge);
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
	public Collection<CyEdge> getMetaEdges() {
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
		updateSettings();
	}

	/**
	 * gets whether or not we create a nested network when we
	 * collapse the metanode
	 *
	 * @return nestedNetwork if 'true' we will use nested networks
	 */
	public boolean getUseNestedNetworks() {
		return this.useNestedNetworks;
	}

	/**
	 * Sets whether or not we should expand empty metanodes.
	 *
	 * @param dontExpandEmpty if 'true' we will use nested networks
	 */
	public void setDontExpandEmpty(boolean dontExpandEmpty) {
		this.dontExpandEmpty = dontExpandEmpty;
		updateSettings();
	}

	/**
	 * Gets whether or not we should expand empty metanodes.
	 *
	 * @return dontExpandEmpty if 'true' we will use nested networks
	 */
	public boolean getDontExpandEmpty() {
		return this.dontExpandEmpty;
	}

	/**
	 * Sets the metanode opacity
	 *
	 * @param opacity the metanode opacity
	 */
	public void setMetaNodeOpacity(double opacity) {
		this.metanodeOpacity = opacity;
		updateSettings();
	}

	/**
	 * Gets the metanode opacity
	 *
	 * @return opacity the metanode opacity
	 */
	public double getMetaNodeOpacity() {
		return this.metanodeOpacity;
	}

	/**
	 * Sets the attribute to use node charting
	 *
	 * @param attribute the attribute to use for node charting
	 */
	public void setNodeChartAttribute(String nodeChartAttribute) {
		this.nodeChartAttribute = nodeChartAttribute;
		updateSettings();
	}

	/**
	 * Gets the attribute to use node charting
	 *
	 * @return the attribute to use for node charting
	 */
	public String getNodeChartAttribute() {
		return this.nodeChartAttribute;
	}

	/**
	 * Sets the node chart type
	 *
	 * @param chartType the chart type
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
		updateSettings();
	}

	/**
	 * Get the chart type we're using
	 *
	 * @return chart type
	 */
	public String getChartType() {
		return this.chartType;
	}

	/**
	 * Sets the node chart color type
	 *
	 * @param chartColorType the chart color type
	 */
	public void setChartColorType(String chartColorType) {
		this.chartColorType = chartColorType;
		updateSettings();
	}

	/**
	 * Get the chart color type we're using
	 *
	 * @return chart color type
	 */
	public String getChartColorType() {
		return this.chartColorType;
	}

	/**
	 * Sets whether or not we hide the metanode when we expand the
	 * network.
	 *
	 * @param hide if 'true' we hide the metanode upon expansion
	 */
	public void setHideMetaNode(boolean hide) {
		this.hideMetanode = hide;
		updateSettings();
	}

	/**
	 * Returns 'true' if we hide the metanode when we expand the
	 * network.
	 *
	 * @return 'true' if we hide the metanode upon expansion
	 */
	public boolean getHideMetaNode() {
		return this.hideMetanode;
	}

	/**
	 * Sets whether or not we create membership edges if we're
	 * not hiding the metanode.
	 *
	 * @param create if 'true' we create the edges
	 */
	public void setCreateMembershipEdges(boolean create) {
		this.createMembershipEdges = create;
		if (!create) {
			membershipEdges = new HashMap<CyNode, CyEdge>();
		}
		updateSettings();
	}

	/**
	 * Returns 'true' if we create the membership edges
	 *
	 * @return 'true' if we create membership edges
	 */
	public boolean getCreateMembershipEdges() {
		return createMembershipEdges;
	}

	/**
	 * Set the attribute manager (for attribute aggregation) that
	 * we're supposed to use.
	 *
	 * @param attributeManager the attribute manager to use
	 */
	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}

	/**
	 * Get the attribute manager (for attribute aggregation) that
	 * we're using.
	 *
	 * @return the attribute manager we're using
	 */
	public AttributeManager getAttributeManager() {
		return attributeManager;
	}

	/**
	 * Add edges to our outer edge map and recurse (if needed).
	 *
	 * @param partnerGroup the group that's calling us
	 * @param partnerEdges the list of outer edges our partner has
	 */
	protected void addPartnerOuterEdges(CyGroup partnerGroup, List<CyEdge> partnerEdges) {
		MetaNode partnerMeta = MetaNodeManager.getMetaNode(partnerGroup);
		CyNode partnerNode = partnerGroup.getGroupNode();
		CyNode myNode = metaGroup.getGroupNode();

		// logger.debug(myNode.toString()+".addPartnerOuterEdges("+partnerNode+")");

		for (CyEdge pEdge: partnerEdges) {
			// logger.debug("    edge: "+pEdge.getIdentifier());
			// Start by adding the relevant edges to our outer edge map
			CyNode pNode = getLocalNode(pEdge);
			if (pNode == null) 
				continue;

			CyEdge metaEdge = createEdge("meta-", pEdge.getIdentifier(), pNode, partnerNode);
			metaGroup.addOuterEdge(metaEdge);
		}

		/* DEBUG
		logger.debug("addPrtnerOuterEdges: outer edges for:"+metaGroup);
		for (CyEdge edge: metaGroup.getOuterEdges()) {
			logger.debug("  "+edge.getIdentifier());
		}
		*/

		// OK, check all of our nodes and if any of them are metanodes, recurse down
		for (CyNode child: metaGroup.getNodes()) {
			MetaNode childMeta = MetaNodeManager.getMetaNode(child);
			if (childMeta == null)
				continue;

			childMeta.addPartnerOuterEdges(partnerGroup, partnerEdges);
		}
	}

	/**
 	 * This method is the central method for the creation and maintenance of a
 	 * meta-node.  Essentially, it is responsible for creating all of the meta-edges
 	 * that connect this meta-node to external nodes.
 	 *
 	 * Basic approach:
 	 *	for each external edge:
 	 *		add a meta-edge to the parter
	 *		if the partner is a group and the group is in our network:
	 *			add ourselves to the group's outer edges list (recursively)
	 *			add ourselves to the partner's meta edge list
	 *		if the partner is in a group:
	 *			add ourselves to the group's meta edge list
 	 */
	private void updateMetaEdges(boolean ignoreMetaEdges) {
		// Initialize our meta-edge map
		metaEdges = new HashMap<CyEdge, CyEdge>();
		Set<MetaNode> partnersSeen = new HashSet<MetaNode>();

		// We need to use a list iterator, because we might need to add new edges to our outer
		// edge list and we want to add them to the iterator to re-examine them
		ListIterator<CyEdge> iterator = metaGroup.getOuterEdges().listIterator();
		while(iterator.hasNext()) {
			CyEdge edge = iterator.next();
			CyNode node = getPartner(edge);
			// logger.debug("Outer edge = "+edge.getIdentifier());

			if (ignoreMetaEdges && isMeta(edge)) {
				// logger.debug("...ignoring");
				addMetaEdge(edge);
				continue;
			}

			CyNode groupNode = metaGroup.getGroupNode();

			// If the edge is already on our group node, don't create a metaedge for it
			if (edge.getSource() == groupNode || edge.getTarget() == groupNode)
				continue;

			// Create the meta-edge to the external node, but maintain the directionality of the
			// original edge
			CyEdge metaEdge = null;
			if (isIncoming(edge))
				metaEdge = createMetaEdge(edge.getIdentifier(), node, groupNode);
			else
				metaEdge = createMetaEdge(edge.getIdentifier(), groupNode, node);

			MetaNode metaPartner = MetaNodeManager.getMetaNode(node);
			if (metaPartner != null && metaPartner.metaGroup.getNetwork().equals(metaGroup.getNetwork())
			    && !partnersSeen.contains(metaPartner)) { 
				// Recursively add links to the appropriate children
				addPartnerEdges(metaPartner, partnersSeen);
				metaPartner.addMetaEdge(metaEdge);
				partnersSeen.add(metaPartner);
			}

			// Now, handle the case where the partner is a member of one or more groups
			if (node.getGroups() != null && node.getGroups().size() > 0) {
				// Add ourselves to the outer edges list of the partner
				addPartnerMetaEdges(edge, node, metaEdge);
			}
		}

		/* DEBUGGING
		logger.debug("Outer edges for:"+metaGroup);
		for (CyEdge edge: metaGroup.getOuterEdges()) {
			logger.debug("  "+edge.getIdentifier());
		}
		*/
	}

	// Find the edge in our partner that links to us
	protected void addPartnerEdges(MetaNode metaPartner, Set<MetaNode> partnersSeen) {
		List<CyEdge> partnerEdges = metaPartner.getCyGroup().getOuterEdges();
		List<CyEdge> newEdges = new ArrayList<CyEdge>();
		// logger.debug("Metanode "+getGroupNode()+" adding partner edges for "+metaPartner.getGroupNode());
		for (CyEdge edge: partnerEdges) {
			// logger.debug("Looking at partner edge: "+edge.getIdentifier());
			CyNode source = (CyNode)edge.getSource();
			CyNode target = (CyNode)edge.getTarget();
			if (metaGroup.getNodes().contains(target) || metaGroup.getNodes().contains(source)) {
				CyNode partner;
				if (metaGroup.getNodes().contains(source)) {
					// We're the source
					source = metaGroup.getGroupNode();
					partner = target;
				} else {
					// We're the target
					target = metaGroup.getGroupNode();
					partner = source;
				}

				if (source == target) continue;

				// Create a new edge
				CyEdge newEdge = createEdge("meta-", edge.getIdentifier(), source, target);
				newEdges.add(newEdge);

				// logger.debug("   ... it points us -- created new edge: "+newEdge.getIdentifier());
				
				metaGroup.addOuterEdge(edge);

				MetaNode partnerMeta = MetaNodeManager.getMetaNode(partner);
				if (partnerMeta != null && !partnersSeen.contains(partnerMeta)) {
					// logger.debug("Adding partner edges for "+partnerMeta.getGroupNode());
					partnersSeen.add(partnerMeta);
					addPartnerEdges(partnerMeta, partnersSeen);
					// logger.debug("Done adding partner edges for "+partnerMeta.getGroupNode());
				}

				createMetaEdge(edge.getIdentifier(), source, target);
			}
		}
		for (CyEdge edge: newEdges) { metaPartner.getCyGroup().addOuterEdge(edge); }
		// logger.debug("Metanode "+getGroupNode()+" done adding partner edges for "+metaPartner.getGroupNode());
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
		// logger.debug("addPartnerMetaEdges to "+partnerNode);
		for (CyGroup partnerGroup: partnerNode.getGroups()) {
			if (partnerGroup.getNetwork() == metaGroup.getNetwork()) {
			  MetaNode partner = MetaNodeManager.getMetaNode(partnerGroup.getGroupNode());
				if (partner != null) {
					// Create a meta-meta edge
					CyEdge metaMetaEdge = null;
					if (isIncoming(connectingEdge))
						metaMetaEdge = createMetaEdge(connectingEdge.getIdentifier(), partnerGroup.getGroupNode(),
					                                metaGroup.getGroupNode());
					else
						metaMetaEdge = createMetaEdge(connectingEdge.getIdentifier(), metaGroup.getGroupNode(), 
					                                partnerGroup.getGroupNode());
					partner.addMetaEdge(metaMetaEdge);
					// Add our meta-edge to the partner group
					partner.addMetaEdge(metaEdge);

					// Now, get our partner's metaEdges and if any of them point to our children,
					// add them to our outer edges
					for (CyEdge outerEdge: partner.getMetaEdges()) {
						// logger.debug("Looking at "+partnerNode+" edge "+outerEdge.getIdentifier());
						if (isConnectingEdge(outerEdge)) {
							// logger.debug("Adding edge "+outerEdge.getIdentifier()+" to our outer edge map");
							metaGroup.addOuterEdge(outerEdge);
						}
					}
				}
			}
		}
	}

	private void updateMembershipEdges(CyNode node) {
		// for each group node is in:
		// 	if !ourGroup:
		// 		add "membership" metaNode between node and group
		List<CyGroup> groupList = node.getGroups();
		if (groupList == null || groupList.size() == 0)
			return;

		for (CyGroup group: groupList) {
			if (group.equals(metaGroup)) continue;

			MetaNode nodeParent = MetaNodeManager.getMetaNode(group);	
			if (nodeParent == null) continue;

			CyGroup parentGroup = nodeParent.getCyGroup();

			CyEdge metaMetaEdge = createMetaEdge("membership", parentGroup.getGroupNode(), node);
		}
		if (membershipEdges != null) {
			CyEdge memberEdge = createEdge("member", metaGroup.getGroupName(), metaGroup.getGroupNode(), node);
			membershipEdges.put(node, memberEdge);
		}
	}

	private void createMembershipEdges() {
		membershipEdges = new HashMap<CyNode, CyEdge>();
		if (createMembershipEdges) {
			for (CyNode node: metaGroup.getNodes()) {
				CyEdge memberEdge = createEdge("member", metaGroup.getGroupName(), metaGroup.getGroupNode(), node);
				membershipEdges.put(node, memberEdge);
			}
		}
	}

	private CyEdge createEdge(String prefix, String edgeName, CyNode source, CyNode target) {
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String identifier = prefix+edgeName;
		String interaction = edgeAttributes.getStringAttribute(edgeName, Semantics.INTERACTION);
		CyEdge newEdge = Cytoscape.getCyEdge(source.getIdentifier(),identifier,
		                                     target.getIdentifier(),prefix+interaction);
		return newEdge;
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

	/**
 	 * Test the direction of the node
 	 *
 	 * @param edge the edge to test the direction of
 	 * @return true if the edge is directed towards us
 	 */
	private boolean isIncoming(CyEdge edge) {
		CyNode source = (CyNode)edge.getSource();
		CyNode target = (CyNode)edge.getTarget();
		if (source == metaGroup.getGroupNode() || metaGroup.getNodes().contains(source))
			return false;
		return true;
	}

	/**
 	 * See if this edge is a metaEdge
 	 *
 	 * @param edge the edge in question
 	 * @return true if this is a metaedge
 	 */
	private boolean isMeta(CyEdge edge) {
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		Boolean b = edgeAttributes.getBooleanAttribute(edge.getIdentifier(), ISMETA_EDGE_ATTR);
		if (b != null && b.booleanValue())
			return true;
		return false;
	}

	/**
 	 * Update settings attributes for this metanode
 	 */
	private void updateSettings() {
		String settings = "";
		settings += "hideMetanode="+hideMetanode;
		settings += ";createMembershipEdges="+createMembershipEdges;
		settings += ";dontExpandEmpty="+dontExpandEmpty;
		settings += ";metanodeOpacity="+metanodeOpacity;
		settings += ";useNestedNetworks="+useNestedNetworks;
		if (nodeChartAttribute != null) {
			settings += ";nodeChartAttribute="+nodeChartAttribute;
			settings += ";chartType="+chartType;
			settings += ";chartColorType="+chartColorType;
		}
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		nodeAttributes.setAttribute(getGroupNode().getIdentifier(), SETTINGS_ATTR, settings);
		nodeAttributes.setUserVisible(SETTINGS_ATTR, false);
	}

	/**
 	 * Load settings attributes for this metanode
 	 */
	private void loadSettings() {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		if (!nodeAttributes.hasAttribute(getGroupNode().getIdentifier(), SETTINGS_ATTR))
			return;

		String settings = nodeAttributes.getStringAttribute(getGroupNode().getIdentifier(), SETTINGS_ATTR);
		String[] pairs = settings.split(";");
		for (String pair: pairs) {
			String[] nv = pair.split("=");
			if (nv[0].equals("hideMetanode")) {
				hideMetanode = Boolean.parseBoolean(nv[1]);
			} else if (nv[0].equals("createMembershipEdges")) {
				createMembershipEdges = Boolean.parseBoolean(nv[1]);
			} else if (nv[0].equals("dontExpandEmpty")) {
				dontExpandEmpty = Boolean.parseBoolean(nv[1]);
			} else if (nv[0].equals("metanodeOpacity")) {
				metanodeOpacity = Double.parseDouble(nv[1]);
			} else if (nv[0].equals("useNestedNetworks")) {
				useNestedNetworks = Boolean.parseBoolean(nv[1]);
			} else if (nv[0].equals("nodeChartAttribute")) {
				nodeChartAttribute = nv[1];
			} else if (nv[0].equals("chartType")) {
				chartType = nv[1];
			} else if (nv[0].equals("chartColorType")) {
				chartColorType = nv[1];
			}
		}
	}
}
