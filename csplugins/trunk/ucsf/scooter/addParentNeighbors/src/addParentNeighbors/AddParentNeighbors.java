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
package addParentNeighbors;

// System imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;

import ding.view.NodeContextMenuListener;
import giny.view.NodeView;

/**
 * This plugin is a quick addition to allow users to add the neighbors of the current node
 * from a parent network to this network.
 */
public class AddParentNeighbors extends CytoscapePlugin 
                                implements NodeContextMenuListener,
                                           PropertyChangeListener,
                                           ActionListener {

	// For each network, keep track of it's parent
	HashMap<CyNetwork,CyNetwork> parentMap = null;
	// For each network, keep track of it's children
	HashMap<CyNetwork,List<CyNetwork>> childMap = null;

	JMenuItem pluginMenu;
	CyLogger myLogger = null;

  /**
   * Create our action and add it to the node context menu
   */
	public AddParentNeighbors() {
		myLogger = CyLogger.getLogger(AddParentNeighbors.class);
		// Listen for changes to the network so we can build our maps
		try {
			// Listen for network and session load events
			Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_TITLE_MODIFIED, this);

			// Add ourselves to the network view created change list
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			          .addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			          .addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			          .addPropertyChangeListener( Cytoscape.NETWORK_CREATED, this);
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			          .addPropertyChangeListener( Cytoscape.NETWORK_DESTROYED, this);
		} catch (ClassCastException e) {
			myLogger.error(e.getMessage());
		}

		// Create our maps
		parentMap = new HashMap();
		childMap = new HashMap();

		// Add our menu
		pluginMenu = new JMenuItem("Add neighbors from parent");
		pluginMenu.setEnabled(false);
		pluginMenu.addActionListener(this);

    JMenu cyPluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
                                .getMenu("Plugins");
    cyPluginMenu.add(pluginMenu);

	}

	// PropertyChange support

	/**
	 * Implements propertyChange
	 *
	 * @param e the property change event
	 */
	public void propertyChange (PropertyChangeEvent e) {
		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			// ((CyNetworkView)e.getNewValue()).addNodeContextMenuListener(this);
		} else if (e.getPropertyName() == Cytoscape.NETWORK_CREATED) {
			addNetwork((String) e.getNewValue(), (String) e.getOldValue());
		} else if (e.getPropertyName() == Cytoscape.NETWORK_DESTROYED) {
			removeNetwork((String) e.getNewValue());
		} else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED) {
			// If this network has a parent, enable our menu
			CyNetwork network = Cytoscape.getNetwork(e.getNewValue().toString());
			if (parentMap.containsKey(network))
				pluginMenu.setEnabled(true);
			else
				pluginMenu.setEnabled(false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		// Get our current network
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		// Get our parent network
		if (!parentMap.containsKey(currentNetwork))
			return;

		CyNetwork parentNetwork = parentMap.get(currentNetwork);
		CyNetworkView parentView = Cytoscape.getNetworkView(parentNetwork.getIdentifier());
		CyNetworkView currentView = Cytoscape.getCurrentNetworkView();

		// Get all of the selected nodes
		Set<CyNode>selectedNodes = currentNetwork.getSelectedNodes();
		for (CyNode node: selectedNodes) {
			// Find all of the neighboring nodes in our parent
			int nodeIndex = node.getRootGraphIndex();
			int[] connectingEdges = parentNetwork.getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);
			if (connectingEdges == null || connectingEdges.length == 0) continue;
			for (int index = 0; index < connectingEdges.length; index++) {
				int edgeIndex = connectingEdges[index];
				// Add them (and their edges) to our current network
				int edgeSourceIndex = parentNetwork.getEdgeSourceIndex(edgeIndex);
				int edgeTargetIndex = parentNetwork.getEdgeTargetIndex(edgeIndex);
				int nodeToCopy = edgeSourceIndex;
				if (edgeSourceIndex == nodeIndex)
					nodeToCopy = edgeTargetIndex;

				currentNetwork.addNode(nodeToCopy);
				currentNetwork.addEdge(edgeIndex);

				currentView.getNodeView(nodeToCopy)
				               .setOffset(parentView.getNodeView(nodeToCopy).getXPosition(),
				                          parentView.getNodeView(nodeToCopy).getYPosition());

				currentView.applyVizMap(currentView.getNodeView(nodeToCopy));
				currentView.applyVizMap(currentView.getEdgeView(edgeIndex));
			}
		}
	}

	/**
	 * Implements addNodeContextMenuItems
	 *
	 * @param nodeView the views to add this to
	 * @param menu the menu to add
	 */
	public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu) {
		if (menu == null) {
			menu = new JPopupMenu();
		}
		menu.add(getNodePopupMenu(nodeView));
	}

	/**
	 * return the context menu to popup for a node.  The menu depends not
	 * only on the context of the node it's over, but also the number of
	 * other selected items, etc.
	 *
	 */
	private JMenu getNodePopupMenu(NodeView nodeView) {
		// JMenu menu = new JMenu("Metanode operations");
		// menu.addMenuListener(new MetanodeMenuListener(nodeView));
		return null;
	}

	private void addNetwork(String newId, String oldId) {
		// If newId exists, remove it
		removeNetwork(newId);
		CyNetwork newNetwork = Cytoscape.getNetwork(newId);
		CyNetwork oldNetwork = Cytoscape.getNetwork(oldId);

		if (oldNetwork == Cytoscape.getNullNetwork())
			return;
		
		List<CyNetwork>childList = null;
		if (!childMap.containsKey(oldNetwork))
			childList = new ArrayList();
		else
			childList = childMap.get(oldNetwork);

		childList.add(newNetwork);
		childMap.put(oldNetwork,childList);
		parentMap.put(newNetwork,oldNetwork);
	}

	private void removeNetwork(String id) {
		CyNetwork network = Cytoscape.getNetwork(id);
		CyNetwork parent = null;
		
		// Get our parent (if we have one)
		if (parentMap.containsKey(network)) {
			parent = parentMap.get(network);
			parentMap.remove(network);
		}

		// If we have any children, assign them to our parent
		if (childMap.containsKey(network)) {
			List<CyNetwork>childList = childMap.get(network);
			childMap.remove(network);
			if (parent != null) {
				List<CyNetwork>newChildList = childMap.get(parent); // We need to remove ourselves first
				newChildList.remove(network);
				newChildList.addAll(childList);
				childMap.put(parent,newChildList);
			}
		}
	}
}
