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
package metaNodePlugin2.view;

// System imports
import java.util.Collection;
import java.util.List;
import java.awt.Color;
import java.awt.Dimension;

// giny imports
import giny.view.EdgeView;
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

// Metanode imports
import metaNodePlugin2.MetaNodePlugin2;
import metaNodePlugin2.model.MetaNode;
import metaNodePlugin2.model.MetaNodeManager;


/**
 * The ViewUtils class provides several static methods that
 * manage the network view
 */
public class ViewUtils {
	/**
 	 * Hide a meta-node
 	 *
 	 * @param group the group to hide
 	 * @param view the view to use to get the location information
 	 * @param hideMetaNode if false, we don't actually want to hide ourselves.
 	 * @return the location of the meta-node when it was hidden
 	 */
	public static Dimension hideMetaNode(CyGroup group, CyNetworkView view, boolean hideMetaNode) {
		CyNode groupNode = group.getGroupNode();
		Dimension loc = getPosition(groupNode, view);

		if (hideMetaNode) {
			// Hide it
			group.getNetwork().hideNode(groupNode);
		} else {
		}

		// If we used a nested network, remove it
		NestedNetworkView.destroy(group, view);

		return loc;
	}

	/**
 	 * Restore a meta-node
 	 *
 	 * @param group the group to hide
 	 * @param view the view to use to get the location information
 	 * @param location the location of the metanode
 	 * @param nestedNetworks if true, use nested networks to visualize the metanode
 	 * @param opacity the opacity of the metanode
 	 */
	public static void restoreMetaNode(CyGroup group, CyNetworkView view, Dimension location, 
	                                   boolean nestedNetworks, double opacity) {
		CyNode groupNode = group.getGroupNode();

		// Restore the node
		group.getNetwork().restoreNode(groupNode);
		
		// Update the position
		setPosition(groupNode, view, location);

		// Create the nested network view, if desired
		if (nestedNetworks) {
			// System.out.println("Creating nested network");
			NestedNetworkView.create(group, view, opacity);
		} else {
			// Set the opacity, if desired
			if (opacity != 1.0)
				setOpacity(groupNode, view, opacity);
		}
	}

	/**
 	 * Hide a list of nodes
 	 *
 	 * @param nodeList the node to hide
 	 * @param view the view to use to get the location information
 	 * @param nodeAttributes our node attributes
 	 * @param hiddenNodes an empty array that will contain the list of nodes that are
 	 *                    already hidden
 	 */
	public static Dimension hideNodes(CyGroup metaGroup, CyNetworkView view, 
	                                  CyAttributes nodeAttributes, List<CyNode> hiddenNodes) {
		List<CyNode> nodeList = metaGroup.getNodes();
		int nNodes = nodeList.size();

		// This is a two-pass algorithm, unfortunately.  First, we need to find
		// the center point for the group of nodes, then we can hide them and
		// update their offsets
		double xSum = 0;
		double ySum = 0;
		for (CyNode node: nodeList) {
			// If one of these nodes is a MetaNode, and it's not collapsed, collapse it first
			MetaNode mn = MetaNodeManager.getMetaNode(node);
			if (mn != null && !mn.isCollapsed()) {
				mn.getCyGroup().setState(MetaNodePlugin2.COLLAPSED);
			}
			Dimension pos = getPosition(node, view);
			if (pos == null) {
				// System.out.println("Adding node "+node+" to hidden nodes");
				nNodes--;
				hiddenNodes.add(node);
				continue; // Hidden (possibly by another metanode)
			}

			xSum += pos.getWidth();
			ySum += pos.getHeight();
		}

		Dimension center = new Dimension();
		center.setSize(xSum/nNodes,ySum/nNodes);

		for (CyNode node: nodeList) {
			// Get the position
			Dimension pos = getPosition(node, view);
			if (pos == null)
				continue; // Hidden (possibly by another metanode)

			// Calculate the offset
			Dimension offset = getOffset(center, pos);

			// Update the attributes
			updateAttributes(node, offset, nodeAttributes);

			// Hide it
			metaGroup.getNetwork().hideNode(node);
		}
		
		return center;
	}

	/**
 	 * Restore all of the nodes in a group
 	 *
 	 * @param metaGroup the meta-group we're restoring the nodes for
 	 * @param net the CyNetwork we're restoring to
 	 * @param view the CyNetworkView
 	 * @param position the new center position of the group of nodes
 	 * @param nodeAttributes our node attributes
 	 * @param hiddenNodes the nodes that were already hidden
 	 * @return the bounding box of all of the nodes
 	 */
	public static Dimension restoreNodes(CyGroup metaGroup, CyNetwork net, CyNetworkView view, 
	                                     Dimension position, CyAttributes nodeAttributes,
	                                     List<CyNode> hiddenNodes) {
		double centerX = position.getWidth();
		double centerY = position.getHeight();
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		for (CyNode node: metaGroup.getNodes()) {
			if (hiddenNodes != null && hiddenNodes.contains(node)) {
				// System.out.println("Skipping node: "+node);
				continue;
			}
			Dimension offset = getAttributes(node, nodeAttributes);
			// offset might be null if we've just added the node
			if (offset != null) {
				// System.out.println("Offset for "+node.toString()+" is "+offset.getWidth()+"x"+offset.getHeight());
				net.restoreNode(node);
				NodeView nView = view.getNodeView(node);
				double nodeWidth = 0.0;
				double nodeHeight = 0.0;
				if (nView != null) {
					nodeWidth = nView.getWidth()/2;
					nodeHeight = nView.getHeight()/2;
				}
				minX = Math.min(minX, offset.getWidth()-nodeWidth);
				maxX = Math.max(maxX, offset.getWidth()+nodeWidth);
				minY = Math.min(minY, offset.getHeight()-nodeHeight);
				maxY = Math.max(maxY, offset.getHeight()+nodeHeight);
				offset.setSize(centerX-offset.getWidth(), centerY-offset.getHeight());
				setPosition(node, view, offset);
			}
		}
		// OK, not get the size of the bounding box and return it
		Dimension bb = new Dimension();
		bb.setSize(maxX-minX, maxY-minY);
		return bb;
	}

	/**
 	 * Restore a set of edges for a group
 	 *
 	 * @param metaGroup the meta-group we're restoring the edges for
 	 * @param edgeList the list of edges we're restoring
 	 * @param view the CyNetworkView
 	 */
	public static void restoreEdges(CyGroup metaGroup, Collection<CyEdge>edgeList, CyNetworkView view) {
		CyNetwork network = metaGroup.getNetwork();
		for (CyEdge edge: edgeList) {
			// System.out.println("Restoring edge "+edge.getIdentifier());
			if (network.containsNode(edge.getSource()) && network.containsNode(edge.getTarget())) {
				network.restoreEdge(edge);
				EdgeView eView = view.getEdgeView(edge);
				if (eView != null)
					Cytoscape.getVisualMappingManager().vizmapEdge(eView, view);
			}
		}
	}

	/**
 	 * Hide a set of edges for a group
 	 *
 	 * @param metaGroup the meta-group we're restoring the edges for
 	 * @param edgeList the list of edges we're restoring
 	 * @param view the CyNetworkView
 	 */
	public static void hideEdges(CyGroup metaGroup, Collection<CyEdge>edgeList, CyNetworkView view) {
		CyNetwork network = metaGroup.getNetwork();
		for (CyEdge edge: edgeList) {
			if (network.containsEdge(edge))
				network.hideEdge(edge);
		}
	}

	/**
 	 * Set the node opacity
 	 *
 	 * @param node the node we're setting the opacity for
 	 * @param view the view we're dealing with
 	 * @param opacity the actual opacity
 	 */
	public static void setOpacity(CyNode node, CyNetworkView view, double opacity) {
		NodeView nView = view.getNodeView(node);
		if (nView != null) {
			final Color oldPaint = (Color) nView.getUnselectedPaint();
			Integer tp = oldPaint.getAlpha();
			Integer newTp = new Integer((int)opacity);
			if (tp != newTp) {
				nView.setUnselectedPaint(new Color(oldPaint.getRed(), oldPaint.getGreen(),
				                                   oldPaint.getBlue(), newTp));
				// Should we set the node.opacity override?
			}
		}
	}

	/**
 	 * Set the node size
 	 *
 	 * @param node the node we're setting the size for
 	 * @param view the view we're dealing with
 	 * @param size the actual size
 	 */
	public static void setNodeSize(CyNode node, CyNetworkView view, Dimension size) {
		double width = size.getWidth();
		double height = size.getHeight();
		NodeView nView = view.getNodeView(node);
		if (nView == null) 
			return;
		nView.setWidth(width/2);
		nView.setHeight(height/2);
	}

	/**
 	 * Return the X,Y location of a node
 	 *
 	 * @param node the node we're interested in
 	 * @param view the network view
 	 * @return the x,y location as a Dimension
 	 */
	private static Dimension getPosition(CyNode node, CyNetworkView view) {
		NodeView nView = view.getNodeView(node);
		if (nView == null) return null;
		Dimension dim = new Dimension();
		dim.setSize(nView.getXPosition(), nView.getYPosition());
		return dim;
	}


	/**
 	 * Set the X,Y position of a node
 	 *
 	 * @param node the node we're moving
 	 * @param view the view this is operating in
 	 * @param position the X,Y position we're moving this to
 	 */
	private static void setPosition(CyNode node, CyNetworkView view, Dimension position) {
		NodeView nView = view.getNodeView(node);
		if (nView == null) 
			return;
		nView.setXPosition(position.getWidth());
		nView.setYPosition(position.getHeight());
		Cytoscape.getVisualMappingManager().vizmapNode(nView, view);
	}

	/**
 	 * Return the offset of a node from the center of a group of nodes
 	 *
 	 * @param center the center
 	 * @param position the position of a node
 	 * @return the x,y offset as a Dimension of this node from the center
 	 */
	private static Dimension getOffset(Dimension center, Dimension position) {
		double xOffset = center.getWidth() - position.getWidth();
		double yOffset = center.getHeight() - position.getHeight();
		Dimension offset = new Dimension();
		offset.setSize(xOffset, yOffset);
		return offset;
	}

	public static final String X_HINT_ATTR = "__metanodeHintX";
	public static final String Y_HINT_ATTR = "__metanodeHintY";

	private static void updateAttributes(CyNode node, Dimension offset, CyAttributes nodeAttributes) {
		nodeAttributes.setAttribute(node.getIdentifier(), X_HINT_ATTR, offset.getWidth());
		nodeAttributes.setAttribute(node.getIdentifier(), Y_HINT_ATTR, offset.getHeight());
		// Hide them
		nodeAttributes.setUserVisible(X_HINT_ATTR, false);
		nodeAttributes.setUserVisible(Y_HINT_ATTR, false);
	}

	private static Dimension getAttributes(CyNode node, CyAttributes nodeAttributes) {
		Double xOffset = nodeAttributes.getDoubleAttribute(node.getIdentifier(), X_HINT_ATTR);
		Double yOffset = nodeAttributes.getDoubleAttribute(node.getIdentifier(), Y_HINT_ATTR);
		if (xOffset == null || yOffset == null) return null;
		Dimension offset = new Dimension();
		offset.setSize(xOffset, yOffset);
		return offset;
	}

}
