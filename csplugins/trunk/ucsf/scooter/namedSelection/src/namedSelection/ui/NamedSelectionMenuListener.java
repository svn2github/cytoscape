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
package namedSelection.ui;

// System imports
import java.awt.Component;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

// our imports
import namedSelection.ui.GroupPanel;
import namedSelection.NamedSelection;

/**
 * The NamedSelectionMenuListener provides the interface to the structure viz
 * Node context menu and the plugin menu.
 */
public class NamedSelectionMenuListener implements MenuListener {
	private NamedSelectionCommandListener staticHandle;
	private NodeView overNode = null;
	private GroupPanel groupPanel = null;
	private CyGroupViewer groupViewer = null;

	public static final int NONE = 0;
	public static final int SELECT = 1;
	public static final int UNSELECT = 2;
	public static final int NEW = 3;
	public static final int REMOVE = 4;
	public static final int ADD = 5;
	public static final int DELETE = 6;

	/**
	 * Create the namedSelection menu listener
	 *
	 * @param nv the Cytoscape NodeView the mouse was over
		 */
	public NamedSelectionMenuListener(NodeView nv, GroupPanel groupPanel) {
		this.staticHandle = new NamedSelectionCommandListener(NONE,null,null,groupPanel);
		this.overNode = nv;
		this.groupPanel = groupPanel;
		groupViewer = CyGroupManager.getGroupViewer(NamedSelection.viewerName);
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
		  JMenuItem item = new JMenuItem("Create new named selection");
			NamedSelectionCommandListener l = new NamedSelectionCommandListener(NEW, null, null, groupPanel);
			item.addActionListener(l);
			if (currentNodes.size() > 0) {
				item.setEnabled(true);
				} else {
				item.setEnabled(false);
			}
			m.add(item);
		}

		if (groupList != null && groupList.size() > 0) {
		  JMenu item = new JMenu("Remove named selection");
			if (addGroupMenu(item, REMOVE, groupList, null))
				m.add(item);
		} else {
		  JMenuItem item = new JMenuItem("Remove named selection");
			item.setEnabled(false);
			m.add(item);
		}

		if (groupList != null && groupList.size() > 0) {
		  JMenu item = new JMenu("Select");
			if (addGroupMenu(item, SELECT, groupList, null))
				m.add(item);
		} else {
		  JMenuItem item = new JMenuItem("Select");
			item.setEnabled(false);
			m.add(item);
		}

		if (groupList != null && groupList.size() > 0) {
		  JMenu item = new JMenu("Unselect");
			if (addGroupMenu(item, UNSELECT, groupList, null))
				m.add(item);
		} else {
		  JMenuItem item = new JMenuItem("Unselect");
			item.setEnabled(false);
			m.add(item);
		}

		if (overNode != null) {
			CyNode contextNode = (CyNode)overNode.getNode();
			if (groupList != null && groupList.size() > 0) {
				JMenu item = new JMenu("Add node to");
				if (addGroupMenu(item, ADD, groupList, contextNode))
					m.add(item);
			}

			List<CyGroup>nodeGroups = contextNode.getGroups();
			if (nodeGroups != null && nodeGroups.size() > 0) {
 				JMenu item = new JMenu("Remove node from");
				// Figure out what groups this node is part of
				if (addGroupMenu(item, DELETE, nodeGroups, (CyNode)overNode.getNode()))
					m.add(item);
			}
		}
	}

	/**
	 * Add all groups to a menu
	 */
	private boolean addGroupMenu(JMenu menu, int command, List<CyGroup>groupList,
	                          CyNode node) {
		boolean foundItem = false;
		if (groupList == null) return false;
		// List current named selections
		for (CyGroup group: groupList) {
			String viewer = group.getViewer();
			if (viewer != null && viewer.equals(NamedSelection.viewerName)) {
				addSubMenu(menu, group.getGroupName(), command, group, node);
				foundItem = true;
			}
		}
		return foundItem;
	}

	/**
	 * Add a submenu item to an existing menu
	 *
	 * @param menu the JMenu to add the new submenu to
	 * @param group the group node
	 * @param node the node
	 */
	private void addSubMenu(JMenu menu, String label, int command, CyGroup group, CyNode node) {
		JMenuItem item = new JMenuItem(label);
		NamedSelectionCommandListener l = new NamedSelectionCommandListener(command, group, node, groupPanel);
		item.addActionListener(l);
	  menu.add(item);
	}
}
