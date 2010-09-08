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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.groups.CyGroup;
import cytoscape.view.CyNetworkView;

import metaNodePlugin2.MetaNodePlugin2;


/**
 * The MetaNode class provides a wrapper for a CyGroup that
 * maintains the additional state information we want to keep
 * to allow the MetaNode abstraction (expand/contract) to work
 * more efficiently
 */
public class MetaNodeManager {
	// Static variables
	private static Map<CyNode,MetaNode> metaMap = new HashMap<CyNode, MetaNode>();
	protected static boolean hideMetanodeDefault = true;
	protected static double metanodeOpacityDefault = 255.;
	protected static boolean useNestedNetworksDefault = false;

	public static final String X_HINT_ATTR = "__metanodeHintX";
	public static final String Y_HINT_ATTR = "__metanodeHintY";
	public static final String CHILDREN_ATTR = "NumChildren";
	public static final String DESCENDENTS_ATTR = "NumDescendents";

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

	static public MetaNode createMetaNode(CyGroup metaGroup) {
		MetaNode mn = new MetaNode(metaGroup);
		metaMap.put(metaGroup.getGroupNode(), mn);
		mn.setUseNestedNetworks(useNestedNetworksDefault);
		mn.setHideMetaNode(hideMetanodeDefault);
		mn.setMetaNodeOpacity(metanodeOpacityDefault);
		return mn;
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

	/**
	 * Remove a MetaNode from our internal map
	 *
	 * @param groupNode the CyNode that maps to the metaNode
	 */
	static public void removeMetaNode(CyNode groupNode) {
		// Remove the metanode
		if (metaMap.containsKey(groupNode))
			metaMap.remove(groupNode);
	}

	/**
	 * Remove a MetaNode from our internal map
	 *
	 * @param metaNode the metaNode to remove
	 */
	static public void removeMetaNode(MetaNode metaNode) {
		if (metaNode == null) return;
		removeMetaNode(metaNode.getCyGroup().getGroupNode());
	}

	/**
 	 * Expand all MetaNodes
 	 */
	static public void expandAll() {
		CyNetworkView nView = Cytoscape.getCurrentNetworkView();
		Collection<MetaNode> metaNodes = metaMap.values();
		for (MetaNode mNode: metaNodes) {
			if (mNode.isHidden())
				continue;
			if (mNode.isCollapsed())
				mNode.getCyGroup().setState(MetaNodePlugin2.EXPANDED);
		}
		// VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		// vizmapper.applyAppearances();
		nView.updateView();
	}

	/**
 	 * Collapse all MetaNodes
 	 */
	static public void collapseAll() {
		CyNetworkView nView = Cytoscape.getCurrentNetworkView();
		Collection<MetaNode> metaNodes = metaMap.values();
		for (MetaNode mNode: metaNodes) {
			if (!mNode.isCollapsed())
				mNode.getCyGroup().setState(MetaNodePlugin2.COLLAPSED);
		}
		// VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		// vizmapper.applyAppearances();
		nView.updateView();
	}

	/**
	 * Sets whether or not we hide the metnode when we expand the
	 * network.
	 *
	 * @param hide if 'true' we hide the metanode upon expansion
	 */
	static public void setHideMetaNodeDefault(boolean hide) {
		MetaNodeManager.hideMetanodeDefault = hide;
	}

	/**
	 * Sets the opacity of a metanode if we don't hide on expansion.
	 *
	 * @param opacity the opacity (between 0 and 100)
	 */
	static public void setExpandedOpacityDefault(double opacity) {
		MetaNodeManager.metanodeOpacityDefault = opacity;
	}

	/**
	 * Returns 'true' if we hide the metnode when we expand the
	 * network.
	 *
	 * @return 'true' if we hide the metanode upon expansion
	 */
	static public boolean getHideMetaNodeDefault() {
		return MetaNodeManager.hideMetanodeDefault;
	}

	/**
	 * Sets whether or not we size the metnode to the bounding box
	 * of all of the children when we expand the network.  NOTE:
	 * this only makes sense if hideMetanode is false.
	 *
	 * @param useNestedNetworks if 'true' we use nexted networks when we collapse
	 */
	static public void setUseNestedNetworksDefault(boolean useNestedNetworks) {
		MetaNodeManager.useNestedNetworksDefault = useNestedNetworks;
	}

	/**
	 * Returns 'true' if we hide the metnode when we expand the
	 * network.
	 *
	 * @return 'true' if we hide the metanode upon expansion
	 */
	static public boolean getUseNestedNetworksDefault() {
		return MetaNodeManager.useNestedNetworksDefault;
	}
}
