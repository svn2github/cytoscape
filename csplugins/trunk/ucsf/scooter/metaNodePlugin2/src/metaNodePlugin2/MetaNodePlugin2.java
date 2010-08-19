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
package metaNodePlugin2;

// System imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.lang.reflect.Method;

import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

// giny imports
import ding.view.NodeContextMenuListener;
import giny.model.Node;
import giny.view.NodeView;
import giny.view.GraphView;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

// our imports
import metaNodePlugin2.model.MetaNode;
import metaNodePlugin2.model.MetaNodeManager;
import metaNodePlugin2.ui.MetanodeSettingsDialog;
import metaNodePlugin2.ui.MetanodeMenuListener;

/**
 * The MetaNodePlugin2 class provides the primary interface to the
 * Cytoscape plugin mechanism.  This class also implements the 
 * CyGroupViewer for the metaNode viewer.
 */
public class MetaNodePlugin2 extends CytoscapePlugin 
                             implements NodeContextMenuListener,
                                        PropertyChangeListener,
	                                      GraphViewChangeListener {

	public static final String viewerName = "metaNode";
	public static final double VERSION = 1.5;
	public CyLogger logger = null;
	public enum Command {
		NONE("none"),
		COLLAPSE("collapse"),
		EXPAND("expand"),
		NEW("new"),
		REMOVE("remove"),
		ADD("add"),
		DELETE("delete"),
		EXPANDALL("expandAll"),
		COLLAPSEALL("collapseAll"),
		EXPANDNEW("expandNew"),
		SETTINGS("settings");

		private String name;
		private Command(String s) { name = s; }
		public String toString() { return name; }
	}

	// Controlling variables
	// public static boolean multipleEdges = false;
	// public static boolean recursive = true;

	// State values
	public static final int EXPANDED = 1;
	public static final int COLLAPSED = 2;

	private static boolean registeredWithGroupPanel = false;

	private static boolean addedGraphViewChangeListener = false;

	private MetanodeSettingsDialog settingsDialog = null;
	private MetaNodeGroupViewer groupViewer = null;

	protected int descendents = 0;

	/**
	 * The main constructor
	 */
	public MetaNodePlugin2() {
		logger = CyLogger.getLogger(MetaNodePlugin2.class);
		// Listen for network changes (so we can add our context menu)
		try {
			// Add ourselves to the network view created change list
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			          .addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
			// We also want to add ourselves to the network view focused change list
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			          .addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);

			// Add our context menu
			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(this);
			Cytoscape.getCurrentNetworkView().addGraphViewChangeListener(this);
		} catch (ClassCastException e) {
			logger.error(e.getMessage());
		}

		// Register with CyGroup
		groupViewer = new MetaNodeGroupViewer(viewerName, logger);
		CyGroupManager.registerGroupViewer(groupViewer);

		// Create our main plugin menu
		JMenu menu = new JMenu("MetaNode Operations");
		menu.addMenuListener(new MetanodeMenuListener(groupViewer, logger, null));

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);

		logger.info("metaNodePlugin2 "+VERSION+" initialized");
	}

	// PropertyChange support

	/**
	 * Implements propertyChange
	 *
	 * @param e the property change event
	 */
	public void propertyChange (PropertyChangeEvent e) {
		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			((CyNetworkView)e.getNewValue()).addNodeContextMenuListener(this);
			// MetaNodeManager.newView((CyNetworkView)e.getNewValue());
			((CyNetworkView)e.getNewValue()).addGraphViewChangeListener(this);
			groupViewer.getSettingsDialog().updateAttributes();
		} else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED) {
			// Load the default aggregation values for this network
			groupViewer.getSettingsDialog().updateOverrides(Cytoscape.getCurrentNetwork());
			// MetaNodeManager.newView(Cytoscape.getCurrentNetworkView());
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
 	 * Implements the graphViewChanged required by GraphViewChangeListener
 	 *
 	 * @param event the event that triggered this change
 	 */
	public void graphViewChanged(GraphViewChangeEvent event) {
		if (event.getType() == GraphViewChangeEvent.NODES_SELECTED_TYPE) {
			// Get the selected nodes
			Node[] nodes = event.getSelectedNodes();
			// We only care about expanded metanodes
			for (int i=0; i < nodes.length; i++) {
				MetaNode n = MetaNodeManager.getMetaNode((CyNode)nodes[i]);
				if (n == null || n.getCyGroup().getState() == COLLAPSED) continue;
				// OK, so we have selected an expanded metanode.  This means that we are
				// not hiding the metanode, so we want to implicitly select all of the children
				Cytoscape.getCurrentNetwork().setSelectedNodeState(n.getCyGroup().getNodes(), true);
			}
		} else if (event.getType() == GraphViewChangeEvent.NODES_UNSELECTED_TYPE) {
			// Get the selected nodes
			Node[] nodes = event.getUnselectedNodes();
			// We only care about expanded metanodes
			for (int i=0; i < nodes.length; i++) {
				MetaNode n = MetaNodeManager.getMetaNode((CyNode)nodes[i]);
				if (n == null || n.getCyGroup().getState() == COLLAPSED) continue;
				// OK, so we have selected an expanded metanode.  This means that we are
				// not hiding the metanode, so we want to implicitly select all of the children
				Cytoscape.getCurrentNetwork().setSelectedNodeState(n.getCyGroup().getNodes(), false);
			}
		}
	}

	/**
	 * return the context menu to popup for a node.  The menu depends not
	 * only on the context of the node it's over, but also the number of
	 * other selected items, etc.
	 *
	 */
	private JMenu getNodePopupMenu(NodeView nodeView) {
		JMenu menu = new JMenu("Metanode operations");
		menu.addMenuListener(new MetanodeMenuListener(groupViewer, logger, nodeView));
		return menu;
	}
}
