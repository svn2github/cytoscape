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
package metaNodePlugin2.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import metaNodePlugin2.actions.MetanodeCommandListener;
import metaNodePlugin2.model.MetaNode;
import metaNodePlugin2.model.MetaNodeManager;
import metaNodePlugin2.MetaNodeGroupViewer;
import metaNodePlugin2.MetaNodePlugin2;
import metaNodePlugin2.MetaNodePlugin2.Command;

import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import giny.view.NodeView;

/**
 * The MetanodeMenuListener provides the interface to the metanode
 * Node context menu and the plugin menu.
 */
public class MetanodeMenuListener implements MenuListener {
	private NodeView overNode = null;
	private CyNode contextNode = null;
	private MetaNodeGroupViewer groupViewer = null;
	private CyLogger logger = null;

	/**
	 * Create the metaNode menu listener
	 *
	 * @param nv the Cytoscape NodeView the mouse was over
	 */
	public MetanodeMenuListener(MetaNodeGroupViewer groupViewer, CyLogger logger, NodeView nv) {
		this.overNode = nv;
		if (nv != null)
			this.contextNode = (CyNode)overNode.getNode();

		this.groupViewer = groupViewer;
		this.logger = logger;
	}

  public void menuCanceled (MenuEvent e) {
		JMenu m = (JMenu)e.getSource();
		// Clear the menu
		m.removeAll();
	}

	public void menuDeselected (MenuEvent e) {
		JMenu m = (JMenu)e.getSource();
		// Clear the menu
		m.removeAll();
	}

	/**
	 * Process the selected menu
	 *
	 * @param e the MenuEvent for the selected menu
	 */
	public void menuSelected (MenuEvent e)
	{
		JMenu m = (JMenu)e.getSource();
		m.removeAll();

		CyNetwork network = Cytoscape.getCurrentNetwork();
		Set currentNodes = network.getSelectedNodes();
		List<CyGroup>groupList = CyGroupManager.getGroupList(groupViewer);

		// Add our menu items
		{
		  JMenuItem item = new JMenuItem("Create new metanode");
			MetanodeCommandListener l = new MetanodeCommandListener(Command.NEW, null,null,groupViewer);
			item.addActionListener(l);
			if (currentNodes.size() > 0) {
				item.setEnabled(true);
			} else {
				item.setEnabled(false);
			}
			m.add(item);
		}
		m.add(new JSeparator());
		{
			groupList = sortList(groupList);
			addMenuItem(m, Command.EXPAND, groupList, contextNode, "Expand metanode");
			addMenuItem(m, Command.COLLAPSE, groupList, contextNode, "Collapse metanode");
			addMenuItem(m, Command.EXPANDNEW, groupList, contextNode, "Expand metanode into new network");
			addMenuItem(m, Command.REMOVE, groupList, contextNode, "Remove metanode");
			addMenuItem(m, Command.ADD, groupList, contextNode, "Add node to metanode");
			addMenuItem(m, Command.DELETE, groupList, contextNode, "Remove node from metanode");
			addMenuItem(m, Command.EXPANDALL, groupList, null, "Expand all metanodes");
			addMenuItem(m, Command.COLLAPSEALL, groupList, null, "Collapse all metanodes");
		}
		m.add(new JSeparator());
		{
		  JMenuItem item = new JMenuItem("Metanode Settings...");
			MetanodeCommandListener l = new MetanodeCommandListener(Command.SETTINGS, null,null,groupViewer);
			item.addActionListener(l);
			m.add(item);
		}
	}

	/**
	 * Create the appropriate menu item
	 *
	 * @param menu the JMenu to add our JMenuItems to
	 * @param command the command we will be executing
	 * @param groupList the list of CyGroups to add
	 * @param contextNode the CyNode this menu refers to
	 * @param label the label for this menu item
	 */
	private void addMenuItem(JMenu menu, Command command, List<CyGroup>groupList,
	                            CyNode contextNode, String label) {

		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		if (groupList == null || groupList.size() == 0) {
			if (contextNode == null) {
		  	JMenuItem item = new JMenuItem(label);
				item.setEnabled(false);
				menu.add(item);
			}
		} else if (contextNode == null) {
			if (command == Command.EXPANDALL || command == Command.COLLAPSEALL) {
				addSubMenu(menu, label, command, null, null);
			} else if (command == Command.EXPANDNEW) {
		  	JMenu item = new JMenu("Expand metanode(s) into new network");
				if (addGroupMenu(item, command, groupList, contextNode))
					menu.add(item);
			} else if (command != Command.ADD && command != Command.DELETE) {
		  	JMenu item = new JMenu(label);
				if (addGroupMenu(item, command, groupList, contextNode))
					menu.add(item);
			} else if (command == Command.ADD) {
		  	JMenu item = new JMenu("Add node(s) to metanode");
				if (addGroupMenu(item, command, groupList, null))
					menu.add(item);
			} else if (command == Command.DELETE) {
		  	JMenu item = new JMenu("Remove node(s) from metanode");
				if (addGroupMenu(item, command, groupList, null))
					menu.add(item);
			}
		} else if (contextNode.isaGroup() && command == Command.EXPAND) {
			// Get the groups this group is a member of
			CyGroup group = CyGroupManager.findGroup(contextNode.getIdentifier());
			// Get the MetaNode
			MetaNode metaNode = MetaNodeManager.getMetaNode(group.getGroupNode());
			if (metaNode.isCollapsed()) {
				addSubMenu(menu, label+" "+group.getGroupName(), 
				           command, group, contextNode);
			}
		} else if (CyGroupManager.isaGroup(contextNode) && command == Command.EXPANDNEW) {
			CyGroup group = CyGroupManager.findGroup(contextNode.getIdentifier());
			MetaNode metaNode = MetaNodeManager.getMetaNode(group.getGroupNode());
			if (metaNode.isCollapsed()) {
				addSubMenu(menu, "Expand metanode "+group.getGroupName()+" into new network", 
				           command, group, contextNode);
			}
		} else if (command == Command.COLLAPSE) {
			List<CyGroup>nodeGroups = contextNode.getGroups();

			// Handle the case of an expanded group where we didn't hide the group node
			if (contextNode.isaGroup()) {
				CyGroup group = CyGroupManager.getCyGroup(contextNode);
				if (groupList.contains(group) && group.getState() == MetaNodePlugin2.EXPANDED) {
					if (nodeGroups == null) nodeGroups = new ArrayList();
					if (!nodeGroups.contains(group))
						nodeGroups.add(group);
				}
			}

			if (nodeGroups != null && nodeGroups.size() > 0) {
				if (nodeGroups.size() == 1) {
					CyGroup group = nodeGroups.get(0);
					addSubMenu(menu, label+" "+group.getGroupName(), 
					           command, group, contextNode);
				} else {
					JMenu item = new JMenu(label);
					if (addGroupMenu(item, command, nodeGroups, contextNode))
						menu.add(item);
				}
			}
		} else if (command == Command.ADD) {
			if (groupList.size() == 1 && 
			    !groupList.get(0).getGroupName().equals(contextNode.getIdentifier())) {
				CyGroup group = groupList.get(0);
				List<CyGroup>nodeGroups = contextNode.getGroups();
				if (nodeGroups == null || !nodeGroups.contains(group)) {
					addSubMenu(menu, label+" "+group.getGroupName(), 
				 	          command, group, contextNode);
				}
			} else {
				JMenu item = new JMenu(label);
				if (addGroupMenu(item, command, groupList, contextNode))
					menu.add(item);
			}
		} else if (command == Command.DELETE) {
			List<CyGroup>nodeGroups = contextNode.getGroups();
			if (nodeGroups != null && nodeGroups.size() > 0) {
				if (nodeGroups.size() == 1)  {
					CyGroup group = nodeGroups.get(0);
					addSubMenu(menu, label+" "+group.getGroupName(), 
					           command, group, contextNode);
				} else {
					JMenu item = new JMenu(label);
					if (addGroupMenu(item, command, nodeGroups, contextNode))
						menu.add(item);
				}
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
	private boolean addGroupMenu(JMenu menu, Command command, List<CyGroup>groupList,
	                             CyNode node) {
		List<CyGroup>nodeGroups = null;
		boolean foundItem = false;
		if (groupList == null) return false;

		CyNetworkView view = Cytoscape.getCurrentNetworkView();

		if (command == Command.ADD && node != null) {
			nodeGroups = node.getGroups();
		} 

		// List current metaNodes
		for (CyGroup group: groupList) {
			CyNode groupNode = group.getGroupNode();
			List<CyGroup> parents = groupNode.getGroups();
			if (group.getViewer() != null && group.getViewer().equals(MetaNodePlugin2.viewerName)) {
				MetaNode metaNode = MetaNodeManager.getMetaNode(group.getGroupNode());

				// Make sure we have a metanode object for this group
				if (metaNode == null) {
					// Make sure this group has a network assigned
					if (group.getNetwork() == null) {
						// Nope, make it the current network and issue a warning
						group.setNetwork(Cytoscape.getCurrentNetwork(), false);
						logger.warning("Metanodes can't handle global groups -- assinging group: "+
						                group+" to network "+Cytoscape.getCurrentNetwork().getIdentifier());
					}
					metaNode = MetaNodeManager.createMetaNode(group);
				}

				// Only present reasonable choices to the user
				if ((command == Command.COLLAPSE && metaNode.isCollapsed()) ||
				    (command == Command.EXPAND && !metaNode.isCollapsed())) 
					continue;

				// If command is expand and we're a child of a group that isn't
				// yet expanded, don't give this as an option
				if ((command == Command.EXPAND) && (parents != null) && (parents.size() > 0)) {
					if (metaNode.isHidden())
						continue;
				}

				if (command == Command.ADD) {
					metaNode = null;
					// Are we already in this group?
					if ((nodeGroups != null) && (nodeGroups.contains(group)))
						continue;
					// Are we this group?
					if (((metaNode = MetaNodeManager.getMetaNode(node)) != null) && 
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
	private void addSubMenu(JMenu menu, String label, Command command, CyGroup group, CyNode node) {
		JMenuItem item = new JMenuItem(label);
		MetanodeCommandListener l = new MetanodeCommandListener(command, group, node,groupViewer);
		item.addActionListener(l);
	  menu.add(item);
	}

	protected List sortList(List listToSort) {
		if (listToSort == null || listToSort.size() <= 1)
			return listToSort;
		Object[] array = listToSort.toArray();
		Arrays.sort(array, new ToStringComparator());
		return Arrays.asList(array);
	}

	private class ToStringComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			return o1.toString().compareToIgnoreCase(o2.toString());
		}
	}
}
