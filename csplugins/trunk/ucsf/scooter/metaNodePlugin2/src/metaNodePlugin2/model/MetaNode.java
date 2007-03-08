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
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Dimension;

// giny imports
import giny.view.NodeView;
import ding.view.*;

// Cytoscape imports
import cytoscape.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.util.CytoscapeAction;
import cytoscape.visual.VisualMappingManager;

// our imports
import metaNodePlugin2.MetaNodePlugin2;

/**
 * The MetaNode class provides a wrapper for a CyGroup that
 * maintains the additional state information we want to keep
 * to allow the MetaNode abstraction (expand/contract) to work
 * more efficiently
 */
public class MetaNode {
	private static HashMap<CyNode,MetaNode> metaMap = new HashMap();
	private CyGroup metaGroup = null;
	private CyNode groupNode = null;
	protected HashMap<CyEdge,List<CyEdge>> newEdgeMap = null;
	private	HashMap<CyNode,CyEdge> metaEdgeMap = null;
	private boolean multipleEdges = false;
	private boolean recursive = false;

	protected boolean isCollapsed = false;

	private CyAttributes edgeAttributes = null;
	private CyAttributes nodeAttributes = null;
	private CyNetworkView networkView = null;
	private CyNetwork network = null;

	/*****************************************************************
	 *                    Static methods                             *
	 ****************************************************************/

	/**
	 * Return the MetaNode associated with this group
	 *
	 * @param metaGroup the CyGroup to use to search for the 
	 * associated MetaNode.
	 * @return the associated MetaNode or null of there is none
	 */
	static public MetaNode getMetaNode(CyGroup metaGroup) {
		CyNode groupNode = metaGroup.getGroupNode();
		if (metaMap.containsKey(groupNode))
			return (MetaNode)metaMap.get(groupNode);
		return null;
	}

	/**
	 * Return the MetaNode associated with this group node
	 *
	 * @param groupNode the CyNode to use to search for the 
	 * associated MetaNode.
	 * @return the associated MetaNode or null of there is none
	 */
	static public MetaNode getMetaNode(CyNode groupNode) {
		if (metaMap.containsKey(groupNode))
			return (MetaNode)metaMap.get(groupNode);
		return null;
	}


	/*****************************************************************
	 *                    Public methods                             *
	 ****************************************************************/

	/**
	 * Main constructor
	 *
	 * @param group the group to wrap the MetaNode around
	 */
	public MetaNode(CyGroup group) {
		metaGroup = group;
		groupNode = group.getGroupNode();
		metaMap.put(groupNode, this);
		update();
		// See if we need to "fix up" the CyGroup.  We might need to
		// add external edges to the CyGroup if we have nodes that used
		// be the connected to nodes which are now part of a collapsed
		// group.  If this is the case, some of *our* external edges
		// will be meta-edges
		List<CyEdge>externalEdges = group.getOuterEdges();
		Iterator<CyEdge>iter = externalEdges.iterator();
		while (iter.hasNext()) {
			CyEdge edge = iter.next();
			if (!isMetaEdge(edge))
				continue;
			// OK, so we have a meta-edge in our list.  That means
			// that the other side of the edge points to a group, and each
			// meta-edge that points to us may represent multiple edges.  We
			// will need to add those edges to our outer edge list.
			List<CyEdge> edges = getPartnerEdgeList(edge);
			for (int i = 0; i < edges.size(); i++) {
				group.addOuterEdge(edges.get(i));
			}
		}
	}

	/**
	 * Return the CyGroup this MetaNode represents
	 */
	public CyGroup getCyGroup() {
		return metaGroup;
	}

	/**
	 * Update our "environment" variables.  These are values that might
	 * change during our execution due to actions of other plugins or
	 * the user.
	 */
	public void update() {
		// Initialize
		edgeAttributes = Cytoscape.getEdgeAttributes();
		nodeAttributes = Cytoscape.getNodeAttributes();
		networkView = Cytoscape.getCurrentNetworkView();
		network = Cytoscape.getCurrentNetwork();
	}

	/**
	 * Collapse this MetaNode
	 *
	 * @param recursive if 'true', this operation is recursive
	 * @param multipleEdges if 'true', use multiple edges to represent the meta-edges
	 * @param update if 'true', actually update the network
	 */
	public void collapse(boolean recursive, boolean multipleEdges, boolean update) {
		if (isCollapsed) 
			return;

		// Carefull -- if the user has changed what (s)he wants for multipleEdges
		// we need to reset
		if (this.multipleEdges != multipleEdges) {
			newEdgeMap = null;
		}

		this.multipleEdges = multipleEdges;
		this.recursive = recursive;

		// Initialize
		update();

		Dimension center = hideNodes(recursive, multipleEdges);
		// Add the group node in the center of where the member nodes were
		network.restoreNode(groupNode);
		// Get the nodeView
		NodeView nv = (NodeView)networkView.getNodeView(groupNode);

		nv.setXPosition(center.getWidth());
		nv.setYPosition(center.getHeight());
		// Do we already have a list of edges
		if (newEdgeMap == null) {
			// No, create them
			createMetaEdges(multipleEdges);
		} else {
			// Yes, show them
			Iterator <CyEdge>edgeIter = newEdgeMap.keySet().iterator();
			while (edgeIter.hasNext()) { 
				network.addEdge(edgeIter.next()); 
			}
		}

		// Set our state
		metaGroup.setState(MetaNodePlugin2.COLLAPSED);
		isCollapsed = true;

		// If we're supposed to, update the display
		if (update) {
			VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
			vizmapper.applyAppearances();
			networkView.updateView();
		}
	}

	/**
	 * Expend this MetaNode.
	 *
	 * @param recursive if 'true', this operation is recursive
	 */
	public void expand(boolean recursive) {
		if (!isCollapsed) 
			return;

		// Initialize
		update();

		// Hide the extra edges we created
		if (newEdgeMap != null) {
			Iterator <CyEdge>edgeIter = newEdgeMap.keySet().iterator();
			while (edgeIter.hasNext()) { 
				CyEdge edge = edgeIter.next();
				network.hideEdge(edge); 
			}
		}
		// Add the nodes back in
		restoreNodes();
		// Add the edges back in
		restoreEdges();
		// Remove the metaNode
		network.hideNode(groupNode);
		// update
		VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		vizmapper.applyAppearances();

		networkView.updateView();
		metaGroup.setState(MetaNodePlugin2.EXPANDED);
		isCollapsed = false;
	}

	/**
	 * Create a meta edge 
	 *
	 * @param edge the CyEdge to create a metaEdge to replace
	 * @param ignoreCollapsed if 'true' create the metaEdge whether the
	 * partner node is collapsed or not.  This allows other MetaNodes to
	 * have us create missing metaEdges to them during expansion
	 * @return the created metaEdge
	 */
	public CyEdge createMetaEdge(CyEdge edge, boolean ignoreCollapsed) {
		CyNode source = (CyNode)edge.getSource();
		CyNode target = (CyNode)edge.getTarget();
		CyNode partner = getPartner(edge);
		if (!multipleEdges && metaEdgeMap.containsKey(partner)) {
			// We've already seen this partner and we only want
			// one edge per partner
			CyEdge metaEdge = metaEdgeMap.get(partner);
			newEdgeMap.get(metaEdge).add(edge);	// Add our edge to the list of edges represented by this meta-edge
			return metaEdge;
		} else if (!ignoreCollapsed && nodeIsCollapsed(partner)) {
			// Skip this edges since it points to a node that is already hidden
			return edge;
		}
		// Create the edge
		String identifier = "MetaEdge-"+edge.getIdentifier();
		String interaction = edgeAttributes.getStringAttribute(edge.getIdentifier(), Semantics.INTERACTION);
		if (source == partner)
			target = groupNode;
		else
			source = groupNode;
		CyEdge newEdge = Cytoscape.getCyEdge(source.getIdentifier(),identifier,
		                                     target.getIdentifier(),"meta-"+interaction);
		List<CyEdge>eL = new ArrayList();
		eL.add(edge);
		newEdgeMap.put(newEdge,eL);
		metaEdgeMap.put(source,newEdge);
		network.addEdge(newEdge);
		return newEdge;
	}

	/*****************************************************************
	 *                   Private methods                             *
	 ****************************************************************/

	/**
	 * Create all of the necessary metaEdges
	 *
	 * @param multipleEdges if 'true' create one metaEdge for each replaced edge
	 */
	private void createMetaEdges(boolean multipleEdges) {
		newEdgeMap = new HashMap();
		metaEdgeMap = new HashMap();
		List<CyNode> nodes = metaGroup.getNodes();
		// Get the list of external edges
		List<CyEdge> edges = metaGroup.getOuterEdges();
	
		// Attach them to the group node
		Iterator<CyEdge> iter = edges.iterator();
		while (iter.hasNext()) {
			CyEdge edge = iter.next();
			CyEdge newEdge = createMetaEdge(edge, false);
		}
		return;
	}

	/**
	 * Restore the edges we hid when we collapsed.  This
	 * is called as part of the expansion process.
	 */
	private void restoreEdges() {
		List<CyEdge> edges = metaGroup.getOuterEdges();
		Iterator<CyEdge> iter = edges.iterator();
		MetaNode parent = null;
		while (iter.hasNext()) {
			// First, see if this edge is a meta-edge
			CyEdge edge = iter.next();
			String identifier = edge.getIdentifier();
			// Get the edge partner
			CyNode partner = getPartner(edge);
			if (nodeIsCollapsed(partner)) {
				// We need to add a meta edge, but they will need to
				// do it.
				parent = getParent(partner);
				if (parent != null) {
					parent.createMetaEdge(edge, true);
				}
			} else if (((parent = MetaNode.getMetaNode(partner)) != null) && !parent.isCollapsed) {
				// We point to a node whose parent is collapsed, but we need to make sure we have the
				// proper meta edge defined.
				CyEdge newEdge = parent.createMetaEdge(edge, true);
				continue;
			} else {
				network.restoreEdge(edge);
			}
		}
		edges = metaGroup.getInnerEdges();
		iter = edges.iterator();
		while (iter.hasNext()) {
			network.restoreEdge(iter.next());
		}
	}

	/**
	 * Restore the nodes we hid when we collapsed.  This
	 * is called as part of the expansion process.
	 */
	private void restoreNodes () {
		NodeView metaNodeView = (NodeView)networkView.getNodeView(groupNode);
		double xCenter = metaNodeView.getXPosition();
		double yCenter = metaNodeView.getYPosition();
		double xOffset = 0;
		double yOffset = 0;

		List<CyNode> nodes = metaGroup.getNodes();
		Iterator<CyNode> nodeIter = nodes.iterator();
		while (nodeIter.hasNext()) {
			CyNode node = nodeIter.next();
			// Is this a metaNode that has since been expanded?
			if (metaMap.containsKey(node) && !metaMap.get(node).isCollapsed) 
				continue;
			network.restoreNode(node);
			NodeView nodeView = (NodeView)networkView.getNodeView(node);
			if (nodeView != null) {
				xOffset = nodeAttributes.getDoubleAttribute(node.getIdentifier(),"__metanodeHintX");
				yOffset = nodeAttributes.getDoubleAttribute(node.getIdentifier(),"__metanodeHintY");
				nodeView.setXPosition(xCenter-xOffset);
				nodeView.setYPosition(yCenter-yOffset);
			}
		}
	}

	/**
	 * Hide the nodes during collapse.
	 *
	 * @param recursive if 'true' this is recursive
	 * @param multipleEdges if 'true' make one metaEdge for each edge
	 * @return the X,Y location of the center of the hidden nodes
	 */
	private Dimension hideNodes(boolean recursive, boolean multipleEdges) {
		List<CyNode> nodes = metaGroup.getNodes();
		// Remove each of the member nodes (but remember where they were)
		Iterator <CyNode> nodeIter = nodes.iterator();
		double xCenter = 0;
		double yCenter = 0;
		double xLocations[] = new double[nodes.size()];
		double yLocations[] = new double[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			CyNode node = nodes.get(i);
			NodeView nodeView = (NodeView)networkView.getNodeView(node);
			if (nodeView != null) {
				xLocations[i] = nodeView.getXPosition();
				yLocations[i] = nodeView.getYPosition();
				
				xCenter += xLocations[i];
				yCenter += yLocations[i];
			}
			// Check and see if this is a group
			if (metaMap.containsKey(node)) {
				// Yes, recurse down
				MetaNode child = (MetaNode)metaMap.get(node);
				child.collapse(recursive, multipleEdges, false);
			}
		}
		xCenter = xCenter / nodes.size();
		yCenter = yCenter / nodes.size();

		for (int i = 0; i < nodes.size(); i++) {
			CyNode node = nodes.get(i);
			String nodeName = node.getIdentifier();
			nodeAttributes.setAttribute(nodeName,"__metanodeHintX",xCenter-xLocations[i]);
			nodeAttributes.setAttribute(nodeName,"__metanodeHintY",yCenter-yLocations[i]);
			// Hide the attributes
			network.hideNode(node);
		}
		Dimension dim = new Dimension();
		dim.setSize(xCenter, yCenter);
		return dim;
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
		if (metaGroup.getNodes().contains(source))
			return target;
		return source;
	}

	/**
	 * If our parter is a MetaNode, get our partner's
	 * newEdge (i.e. metaEdge) list.
	 *
	 * @param metaEdge the metaEdge to use to get the partner MetaNode
	 * @return the List of metaEdges created by the partner
	 */
	private List<CyEdge> getPartnerEdgeList(CyEdge metaEdge) {
		CyNode partner = getPartner(metaEdge);
		// We have the partner node -- get the group
		if (!metaMap.containsKey(partner)) {
			// Shouldn't happen!
			return new ArrayList<CyEdge>(0);
		}
		MetaNode metaPartner = metaMap.get(partner);
		if (!metaPartner.newEdgeMap.containsKey(metaEdge)) {
			// Shouldn't happen!
			return new ArrayList<CyEdge>(0);
		}
		return metaPartner.newEdgeMap.get(metaEdge);
	}


	/**
	 * Test to see if a node has been collapsed
	 *
	 * @param node the CyNode to test
	 * @return 'true' if the node is a part of a MetaNode and has been
	 * collapsed
	 */
	private boolean nodeIsCollapsed(CyNode node) {
		// First, get the list of groups
		List<CyGroup> groupList = node.getGroups();
		if (groupList == null) return false;

		// Is this node in our group?
		if (groupList.contains(metaGroup))
			return false;

		Iterator<CyGroup>iter = groupList.iterator();
		while (iter.hasNext()) {
			CyGroup group = iter.next();
			if (metaMap.containsKey(group.getGroupNode())) {
				MetaNode meta = metaMap.get(group.getGroupNode());
				if (meta != this && meta.isCollapsed) return true;
			}
		}
		return false;
	}

	/**
	 * Return the MetaNode for a node, if this node is part
	 * of a MetaNode
	 *
	 * @param node the CyNode to find the parent for
	 * @return the MetaNode that is the parent for the node
	 */
	private MetaNode getParent(CyNode node) {
		// First, get the list of groups
		List<CyGroup> groupList = node.getGroups();
		if (groupList == null) return null;

		// Is this node in our group?
		if (groupList.contains(metaGroup))
			return null;

		Iterator<CyGroup>iter = groupList.iterator();
		while (iter.hasNext()) {
			CyGroup group = iter.next();
			if (metaMap.containsKey(group.getGroupNode())) {
				MetaNode meta = metaMap.get(group.getGroupNode());
				if (meta != this) return meta;
			}
		}
		return null;
	}

	/**
	 * Return 'true' if the edge is a meta-edge
	 *
	 * @param edge the edge to test
	 * @return true if edge is a meta-edge
	 */
	private boolean isMetaEdge(CyEdge edge) {
		String interaction = edgeAttributes.getStringAttribute(edge.getIdentifier(), Semantics.INTERACTION);
		if (interaction.startsWith("meta-"))
			return true;

		return false;
	}
}
