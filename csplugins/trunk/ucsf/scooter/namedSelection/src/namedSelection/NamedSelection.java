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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

// giny imports
import ding.view.DGraphView;
import giny.view.NodeView;
import ding.view.NodeContextMenuListener;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

// our imports
import namedSelection.commands.NamedSelectionCommandHandler;
import namedSelection.ui.GroupPanel;
import namedSelection.ui.NamedSelectionMenuListener;

/**
 * The NamedSelection class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class NamedSelection extends CytoscapePlugin 
                            implements NodeContextMenuListener,
                                       PropertyChangeListener {

	public static final String viewerName = "namedSelection";
	public static final double VERSION = 1.0;

	// State values
	public static final int SELECTED = 1;
	public static final int UNSELECTED = 2;

	// Name for the global groups
	public static final String GLOBAL_GROUPS = "Global groups";

	private static CyGroupViewer groupViewer = null;

	private static GroupPanel groupPanel = null;

	private CyLogger myLogger = null;

  /**
   * Create our action and add it to the plugins menu
   */
	public NamedSelection() {
		// Listen for network changes (so we can add our context menu)
		// Now that the group panel supports group creation, do we still
		// want to do this?
		try {
			// Listen for network and session load events
			Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_LOADED, this);
			Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );

			// Add ourselves to the current network context menu
			((DGraphView)Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(this);
		} catch (ClassCastException e) {
			myLogger.error(e.getMessage());
		}

		myLogger = CyLogger.getLogger(NamedSelection.class);

		// Add our interface to CytoPanel 1
		groupPanel = new GroupPanel();
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).add("Groups", groupPanel);

		// Create our main plugin menu
		JMenu menu = new JMenu("Named Selection Tool");
		menu.addMenuListener(new NamedSelectionMenuListener(null, groupPanel));
		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);

		// We want to listen for graph perspective changes (primarily SELECT/UNSELECT)
		Cytoscape.getCurrentNetworkView().addGraphViewChangeListener(groupPanel.getTree());

		// Register with CyGroup
		groupViewer = new NamedSelectionGroupViewer(groupPanel, myLogger);
		CyGroupManager.registerGroupViewer(groupViewer);
		groupPanel.addViewer(groupViewer);

		// Finally, register our commands (which are pretty sparse...)
		new NamedSelectionCommandHandler("namedselection", myLogger, groupPanel);

		myLogger.info("namedSelectionPlugin "+VERSION+" initialized");
	}

	// PropertyChange support

	/**
	 * Implements propertyChange
	 *
	 * @param e the property change event
	 */
	public void propertyChange (PropertyChangeEvent e) {
		if (e.getPropertyName() == Cytoscape.NETWORK_LOADED && 
		    e.getNewValue() != null) {
			// Get the name of the network we loaded
			Object[] ret_val = (Object []) e.getNewValue();
			CyNetwork network = (CyNetwork)ret_val[0];
			CyNetworkView netView = Cytoscape.getNetworkView(network.getIdentifier());
			if (!netView.equals(Cytoscape.getNullNetworkView())) {
				netView.addGraphViewChangeListener(groupPanel.getTree());
				netView.addNodeContextMenuListener(this);
			}
		} else if (e.getPropertyName() == Cytoscape.SESSION_LOADED &&
		           e.getNewValue() != null) {
			List<String> netList = (List<String>) e.getNewValue();
			for (String network: netList) {
				CyNetwork net = Cytoscape.getNetwork(network);
				CyNetworkView netView = Cytoscape.getNetworkView(net.getIdentifier());
				if (!netView.equals(Cytoscape.getNullNetworkView())) {
					netView.addGraphViewChangeListener(groupPanel.getTree());
					netView.addNodeContextMenuListener(this);
				}
			}
		} else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			// Add menu to the context dialog
			((CyNetworkView)e.getNewValue()).addNodeContextMenuListener(this);
		}

		// Update the tree
		groupPanel.reload();
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
	 * The named selection viewer can act as a "helper" for other viewers by
	 * allowing them to use the group panel.  This method will return a handle
	 * to the group panel for other viewers to use
	 */
	public void addViewerToGroupPanel(CyGroupViewer viewer) {
		groupPanel.addViewer(viewer);
	}

	/**
	 * Update the group panel when one of our clients gets updated.
	 */
	public void updateGroupPanel() {
		groupPanel.reload();
	}

	/**
	 * return the context menu to popup for a node.  The menu depends not
	 * only on the context of the node it's over, but also the number of
	 * other selected items, etc.
	 *
	 */
	private JMenu getNodePopupMenu(NodeView nodeView) {
		JMenu menu = new JMenu("Group operations");
		menu.addMenuListener(new NamedSelectionMenuListener(nodeView, groupPanel));
		return menu;
	}
}
