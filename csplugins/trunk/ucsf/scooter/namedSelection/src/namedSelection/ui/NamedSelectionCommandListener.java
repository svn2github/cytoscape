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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

// our imports
import namedSelection.NamedSelection;
import namedSelection.ui.GroupPanel;
	
/**
 * This class gets attached to the menu item.
 */
class NamedSelectionCommandListener implements ActionListener {
 	private static final long serialVersionUID = 1;
	private int command;
	private CyGroup group = null; // The group we care about
	private CyNode node = null; // The node this is refering to
	private GroupPanel groupPanel = null;

	NamedSelectionCommandListener(int command, CyGroup group, CyNode node, GroupPanel groupPanel) {
		this.command = command;
		this.group = group;
		this.node = node;
		this.groupPanel = groupPanel;
	}

   /**
    * This method is called when the user selects the menu item.
    */
   public void actionPerformed(ActionEvent ae) {
			String label = ae.getActionCommand();
		if (command == NamedSelectionMenuListener.SELECT) {
			select();
		} else if (command == NamedSelectionMenuListener.UNSELECT) {
			unselect();
		} else if (command == NamedSelectionMenuListener.NEW) {
			newGroup();
		} else if (command == NamedSelectionMenuListener.REMOVE) {
			removeGroup();
		} else if (command == NamedSelectionMenuListener.ADD) {
			addToGroup(node);
		} else if (command == NamedSelectionMenuListener.DELETE) {
			removeFromGroup(node);
		}
	}

	/**
	 * Create a new group.
	 */
	private void newGroup() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		List<CyNode> currentNodes = new ArrayList(network.getSelectedNodes());
		List<CyGroup> groupList = CyGroupManager.getGroupList();

		// TODO: add a check-box for global networks
		String groupName = JOptionPane.showInputDialog("Please enter a name for this selection");
		if (groupName == null) return;

		// By using the empty lists, we inhibit all of the overhead associated with figuring
		// out internal and external edges (which we don't care about)
		CyGroup group = CyGroupManager.createGroup(groupName, currentNodes, new ArrayList<CyEdge>(), 
		                                           new ArrayList<CyEdge>(), 
		                                           NamedSelection.viewerName, network);
		group.setState(NamedSelection.SELECTED);
	}

	/**
	 * Remove a group.
	 */
	private void removeGroup() {
		CyGroupManager.removeGroup(group);
		groupPanel.groupRemoved(group);
	}

	/**
	 * Add a node to a group
	 */
	private void addToGroup(CyNode node) {
		node.addToGroup(group);
		groupPanel.groupChanged(group);
	}

	/**
	 * Remove a node to a group
	 */
	private void removeFromGroup(CyNode node) {
		node.removeFromGroup(group);
		groupPanel.groupChanged(group);
	}

	/**
	 * Perform the action associated with a select menu selection
	 */
	private void select() {
		group.setState(NamedSelection.SELECTED);
	}

	/**
	 * Perform the action associated with an unselect menu selection
	 */
	private void unselect() {
		group.setState(NamedSelection.UNSELECTED);
	}
}

