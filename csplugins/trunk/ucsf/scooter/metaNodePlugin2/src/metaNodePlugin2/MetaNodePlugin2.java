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
import javax.swing.JOptionPane;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

// giny imports
import giny.view.NodeView;
import ding.view.*;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

// our imports
import metaNodePlugin2.model.MetaNode;

/**
 * The MetaNodePlugin2 class provides the primary interface to the
 * Cytoscape plugin mechanism.  This class also implements the 
 * CyGroupViewer for the metaNode viewer.
 */
public class MetaNodePlugin2 extends CytoscapePlugin 
                             implements CyGroupViewer, 
                                        NodeContextMenuListener,
                                        PropertyChangeListener {

	public static final String viewerName = "metaNode";
	public static final double VERSION = 0.1;
	public static final int NONE = 0;
	public static final int COLLAPSE = 1;
	public static final int EXPAND = 2;
	public static final int NEW = 3;
	public static final int REMOVE = 4;
	public static final int ADD = 5;
	public static final int DELETE = 6;

	// Controlling variables
	public static boolean multipleEdges = false;
	public static boolean recursive = true;

	// State values
	public static final int EXPANDED = 1;
	public static final int COLLAPSED = 2;

	private static CyGroupViewer groupViewer = null;

	/**
	 * The main constructor
	 */
	public MetaNodePlugin2() {
		// Listen for network changes (so we can add our context menu)
		try {
			// Add ourselves to the network view created change list
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			          .addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this);
			// Add our context menu
			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(this);
		} catch (ClassCastException e) {
			System.out.println(e.getMessage());
		}

		// Register with CyGroup
		CyGroupManager.registerGroupViewer(this);
		this.groupViewer = this; // this makes it easier to get at from inner classes
		System.out.println("metaNodePlugin2 "+VERSION+" initialized");
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
		if (MetaNode.getMetaNode(group) == null) {
			MetaNode newNode = new MetaNode(group);
		}
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
	}

	/**
	 * This is called when a group we care about has been
	 * changed (usually node added or deleted).
	 *
	 * @param group the CyGroup that has changed
	 * @param node the CyNode that caused the change
	 * @param change the change that occured
	 */
	public void groupChanged(CyGroup group, CyNode node, ChangeType change) { 
		MetaNode mn = MetaNode.getMetaNode(group);
		if (mn == null) return;

		if (change == ChangeType.NODE_ADDED)
			mn.nodeAdded(node);
		else if (change == ChangeType.NODE_REMOVED)
			mn.nodeRemoved(node);
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
		JMenu menu = new JMenu("Metanode operations");
		menu.addMenuListener(new MetanodeMenuListener(nodeView));
		return menu;
	}


	/**
	 * The MetanodeMenuListener provides the interface to the metanode
	 * Node context menu and the plugin menu.
	 */
	public class MetanodeMenuListener implements MenuListener {
		private MetanodeCommandListener staticHandle;
		private NodeView overNode = null;

		/**
		 * Create the metaNode menu listener
		 *
		 * @param nv the Cytoscape NodeView the mouse was over
		 */
		MetanodeMenuListener(NodeView nv) {
			this.staticHandle = new MetanodeCommandListener(NONE,null,null);
			this.overNode = nv;
		}

	  public void menuCanceled (MenuEvent e) {};
		public void menuDeselected (MenuEvent e) {};

		/**
		 * Process the selected menu
		 *
		 * @param e the MenuEvent for the selected menu
		 */
		public void menuSelected (MenuEvent e)
		{
			JMenu m = (JMenu)e.getSource();
			// Clear the menu
			Component[] subMenus = m.getMenuComponents();
			for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }

			CyNetwork network = Cytoscape.getCurrentNetwork();
			Set currentNodes = network.getSelectedNodes();
			List<CyGroup>groupList = CyGroupManager.getGroupList(groupViewer);

			// Add our menu items
			{
			  JMenuItem item = new JMenuItem("Create new metanode");
				MetanodeCommandListener l = new MetanodeCommandListener(NEW, null,null);
				item.addActionListener(l);
				if (currentNodes.size() > 0) {
					item.setEnabled(true);
				} else {
					item.setEnabled(false);
				}
				m.add(item);
			}

			if (groupList != null && groupList.size() > 0) {
			  JMenu item = new JMenu("Remove metanode");
				if (addGroupMenu(item, REMOVE, groupList, null))
					m.add(item);
			} else {
			  JMenuItem item = new JMenuItem("Remove metanode");
				item.setEnabled(false);
				m.add(item);
			}

			if (groupList != null && groupList.size() > 0) {
			  JMenu item = new JMenu("Collapse metanode");
				if (addGroupMenu(item, COLLAPSE, groupList, null))
					m.add(item);
			} else {
			  JMenuItem item = new JMenuItem("Collapse metanode");
				item.setEnabled(false);
				m.add(item);
			}

			if (groupList != null && groupList.size() > 0) {
			  JMenu item = new JMenu("Expand metanode");
				if (addGroupMenu(item, EXPAND, groupList, null)) {
					m.add(item);
				}
			} else {
			  JMenuItem item = new JMenuItem("Expand metanode");
				item.setEnabled(false);
				m.add(item);
			}

			if (overNode != null) {
				CyNode contextNode = (CyNode)overNode.getNode();
				if (groupList != null && groupList.size() > 0) {
					JMenu item = new JMenu("Add node to metanode");
					if (addGroupMenu(item, ADD, groupList, contextNode))
						m.add(item);
				}

				List<CyGroup>nodeGroups = contextNode.getGroups();
				if (nodeGroups != null && nodeGroups.size() > 0) {
 					JMenu item = new JMenu("Remove node from metanode");
					// Figure out what groups this node is part of
					if (addGroupMenu(item, DELETE, nodeGroups, (CyNode)overNode.getNode()))
						m.add(item);
				}
			}
		}

		/**
		 * Add all groups to a menu, as appropriate.
		 *
		 * @param menu the JMenu to add our JMenuItems to
		 * @param command the command we will be executing
		 * @param groupList the list of CyGroups to add
		 * @param node the CyNode this menu refers to
		 * @return true if the menu should be added
		 */
		private boolean addGroupMenu(JMenu menu, int command, List<CyGroup>groupList,
		                             CyNode node) {
			List<CyGroup>nodeGroups = null;
			boolean foundItem = false;
			if (groupList == null) return false;

			if (command == ADD) {
				nodeGroups = node.getGroups();
			}
			// List current named selections
			Iterator iter = groupList.iterator();
			while (iter.hasNext()) {
				CyGroup group = (CyGroup)iter.next();
				CyNode groupNode = group.getGroupNode();
				List<CyGroup> parents = groupNode.getGroups();
				if (group.getViewer().equals(groupViewer.getViewerName())) {
					// Only present reasonable choices to the user
					if ((command == COLLAPSE && group.getState() == COLLAPSED) ||
					    (command == EXPAND && group.getState() == EXPANDED)) 
						continue;
					// If command is expand and we're a child of a group that isn't
					// yet expanded, don't give this as an option
					if ((command == EXPAND) && (parents != null) && (parents.size() > 0)) {
						Iterator<CyGroup> grIter = parents.iterator();
						boolean parentCollapsed = false;
						while (grIter.hasNext()) {
							CyGroup parent = grIter.next();
							if (groupList.contains(parent) && (parent.getState() == COLLAPSED)) {
								parentCollapsed = true;
								break;
							}
						}
						if (parentCollapsed) continue;
					}

					if (command == ADD) {
						MetaNode metaNode = null;
						// Are we already in this group?
						if ((nodeGroups != null) && (nodeGroups.contains(group)))
							continue;
						// Are we this group?
						if (((metaNode = MetaNode.getMetaNode(node)) != null) && 
						    (metaNode.getCyGroup() == group)) {
							continue;
						}
					}

					foundItem = true;
					addSubMenu(menu, group.getGroupName(), command, group, node);
				}
			}
			return foundItem;
		}

		/**
		 * Add a submenu item to an existing menu
		 *
		 * @param menu the JMenu to add the new submenu to
		 * @param label the label for this menu
		 * @param command the comment this menu refers to
		 * @param group the group node
		 * @param node the node
		 */
		private void addSubMenu(JMenu menu, String label, int command, CyGroup group, CyNode node) {
			JMenuItem item = new JMenuItem(label);
			MetanodeCommandListener l = new MetanodeCommandListener(command, group, node);
			item.addActionListener(l);
		  menu.add(item);
		}
	}
	
  /**
   * This class gets attached to the menu item.
   */
  private class MetanodeCommandListener implements ActionListener {
  	private static final long serialVersionUID = 1;
		private int command;
		private CyGroup group = null; // The group we care about
		private CyNode node = null; // The node this is refering to

		/**
		 * The main constructor for the command listener.
		 *
		 * @param command the command to execute
		 * @param group the group to apply the command to
		 * @param node the node to apply the command to
		 */
		MetanodeCommandListener(int command, CyGroup group, CyNode node) {
			this.command = command;
			this.group = group;
			this.node = node;
		}

    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
			String label = ae.getActionCommand();
			if (command == COLLAPSE) {
				collapse();
			} else if (command == EXPAND) {
				expand();
			} else if (command == NEW) {
				newGroup();
			} else if (command == REMOVE) {
				removeGroup();
			} else if (command == ADD) {
				addToGroup(node);
			} else if (command == DELETE) {
				removeFromGroup(node);
			}
		}

		/**
		 * Create a new group.  Eventually, this should be replaced by a more
		 * pleasing dialog that allows the user to choose their own name.
		 */
		private void newGroup() {
			CyNetwork network = Cytoscape.getCurrentNetwork();
			List<CyNode> currentNodes = new ArrayList(network.getSelectedNodes());
			List<CyGroup> groupList = CyGroupManager.getGroupList();
			String groupName = JOptionPane.showInputDialog("Please enter a name for this metanode");
			if (groupName == null) return;
			CyGroup group = CyGroupManager.createGroup(groupName, currentNodes, viewerName);
			MetaNode newNode = new MetaNode(group);
			groupCreated(group);
			newNode.collapse(recursive, multipleEdges, true);
		}

		/**
		 * Remove a group.
		 */
		private void removeGroup() {
			// We need to make sure the group is expanded, first
			expand();
			CyGroupManager.removeGroup(group);
		}

		/**
		 * Add a node to a group
		 *
		 * @param node the node to add to this group
		 */
		private void addToGroup(CyNode node) {
			node.addToGroup(group);  // NOTE: this will trigger a groupChanged callback
		}

		/**
		 * Remove a node from a group
		 *
		 * @param node the node to remove from this group
		 */
		private void removeFromGroup(CyNode node) {
			node.removeFromGroup(group);  // NOTE: this will trigger a groupChanged callback
		}

		/**
		 * Perform the action associated with a select menu selection
		 */
		private void collapse() {
			MetaNode mNode = MetaNode.getMetaNode(group);
			mNode.collapse(recursive, multipleEdges, true);
		}

		/**
		 * Perform the action associated with an unselect menu selection
		 */
		private void expand() {
			MetaNode mNode = MetaNode.getMetaNode(group);
			mNode.expand(recursive);
		}
	}
}
