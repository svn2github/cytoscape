/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
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
package namedSelection;

// System imports
import java.util.List;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupChangeEvent;
import cytoscape.groups.CyGroupChangeListener;
import cytoscape.groups.CyGroupViewer;
import cytoscape.groups.CyGroupViewer.ChangeType;

// our imports
import namedSelection.ui.GroupPanel;

/**
 * The NamedSelection class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class NamedSelectionGroupViewer implements CyGroupViewer,
                                                  CyGroupChangeListener { 

	public static final String viewerName = "namedSelection";
	public static final double VERSION = 1.0;

	// State values
	public static final int SELECTED = 1;
	public static final int UNSELECTED = 2;

	private boolean needReload = false;
	private CyLogger logger = null;
	private GroupPanel groupPanel;

	public NamedSelectionGroupViewer(GroupPanel groupPanel, CyLogger logger) {
		this.logger = logger;
		this.groupPanel = groupPanel;
		CyGroupManager.addGroupChangeListener(this);
	}

	// These are required by the CyGroupViewer interface

	/**
	 * Return the name of our viewer
	 *
	 * @return viewer name
	 */
	public String getViewerName() { return viewerName; }

	/**
	 * This is called when a new group has been created that
	 * we care about.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that was just created
	 */
	public void groupCreated(CyGroup group) { 
		groupPanel.groupCreated(group);
	}

	/**
	 * This is called when a new group has been created that
	 * we care about, but the network view may not be the
	 * current network view (e.g. during XGMML creation).
	 *
	 * @param group the CyGroup that was just created
	 * @param view the CyNetworkView that this group is being
	 * created under
	 */
	public void groupCreated(CyGroup group, CyNetworkView view) { 
		// Make sure we get rid of the group node
		CyNode node = group.getGroupNode();
		view.getNetwork().removeNode(node.getRootGraphIndex(), false);
		groupCreated(group);
	}

	/**
	 * This is called when a group we care about is about to 
	 * be deleted.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) { 
		groupPanel.groupRemoved(group);
	}

	/**
	 * This is called when a group we care about is changed.
	 *
	 * @param group the CyGroup that has changed
	 * @param node the CyNode that caused the change
	 * @param change the change that occured
	 */
	public void groupChanged(CyGroup group, CyNode node, ChangeType change) { 
		// At some point, this should be a little more granular.  Do we really
		// need to rebuild the tree when we have a simple node addition/removal?
		if (change == ChangeType.NODE_ADDED ||
		    change == ChangeType.NODE_REMOVED ||
		    change == ChangeType.NETWORK_CHANGED ) {
			groupPanel.groupChanged(group);
		} else if (change == ChangeType.STATE_CHANGED) {
			boolean selected = true;
			if (group.getState() == UNSELECTED)
				selected = false;
				
			List<CyNode> nodeList = group.getNodes();
			CyNetwork network = group.getNetwork();
			if (network == null) {
				// Global group -- select in all networks
				selectAll(group, selected);
			} else {
				network.setSelectedNodeState(nodeList, selected);
				Cytoscape.getNetworkView(network.getIdentifier()).updateView();
			}
			groupPanel.groupChanged(group);
		}
	}

	/**                                   
   */                                   
	public void groupChanged(CyGroup group, CyGroupChangeEvent change) {
		// Special-case for deleted groups
		if (change == CyGroupChangeEvent.GROUP_DELETED) {
			// System.out.println("Group deleted: "+group);
			groupPanel.groupChanged(null);
		} else if (change == CyGroupChangeEvent.GROUP_CREATED) {
			// System.out.println("Group added: "+group);
		}
	} 

	private void selectAll(CyGroup group, boolean selected) {
		for (CyNetwork network: Cytoscape.getNetworkSet()) {
			network.setSelectedNodeState(group.getNodes(), selected);
			Cytoscape.getNetworkView(network.getIdentifier()).updateView();
		}
	}
}
