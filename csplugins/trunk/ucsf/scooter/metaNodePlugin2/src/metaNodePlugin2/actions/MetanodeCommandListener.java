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
package metaNodePlugin2.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

import metaNodePlugin2.model.MetaNode;
import metaNodePlugin2.MetaNodeGroupViewer;
import metaNodePlugin2.MetaNodePlugin2;
import metaNodePlugin2.MetaNodePlugin2.Command;

/**
 * This class gets attached to the menu item.
 */
public class MetanodeCommandListener implements ActionListener {
	private static final long serialVersionUID = 1;
	private Command command;
	private CyGroup group = null; // The group we care about
	private CyNode node = null; // The node this is refering to
	private MetaNodeGroupViewer groupViewer = null;

	/**
	 * The main constructor for the command listener.
	 *
	 * @param command the command to execute
	 * @param group the group to apply the command to
	 * @param node the node to apply the command to
	 */
	public MetanodeCommandListener(Command command, CyGroup group, CyNode node, MetaNodeGroupViewer groupViewer) {
		this.command = command;
		this.group = group;
		this.node = node;
		this.groupViewer = groupViewer;
	}

   /**
    * This method is called when the user selects the menu item.
    */
   public void actionPerformed(ActionEvent ae) {

		String label = ae.getActionCommand();
		if (command == Command.COLLAPSE) {
			collapse();
		} else if (command == Command.EXPAND) {
			expand();
		} else if (command == Command.EXPANDNEW) {
			createNetworkFromGroup();
		} else if (command == Command.NEW) {
			newGroup();
		} else if (command == Command.REMOVE) {
			removeGroup();
		} else if (command == Command.ADD) {
			addToGroup(node);
		} else if (command == Command.DELETE) {
			removeFromGroup(node);
		} else if (command == Command.EXPANDALL) {
			expandAll();
		} else if (command == Command.COLLAPSEALL) {
			collapseAll();
		} else if (command == Command.SETTINGS) {
			// Bring up the settings dialog
			groupViewer.getSettingsDialog().setVisible(true);
		}
	}

	/**
	 * Create a new group.  
	 */
	private void newGroup() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		List<CyGroup> groupList = CyGroupManager.getGroupList();
		String groupName = JOptionPane.showInputDialog("Please enter a name for this metanode");
		if (groupName == null) return;
		for (CyGroup group: groupList) {
			if (groupName.equals(group.getGroupName())) {
				// Oops -- already have a group named groupName!
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
					"There is already a group named "+groupName,"GroupError",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// Careful!  If one of the nodes is an expanded (but not hidden) metanode,
		// we need to collapse it first or this gets messy fast
		for (CyNode node: (List<CyNode>)new ArrayList(network.getSelectedNodes())) {
			MetaNode mn = MetaNode.getMetaNode(node);
			if (mn == null) continue;
			// Is this an expanded metanode?
			if (mn.getCyGroup().getState() == MetaNodePlugin2.EXPANDED) {
				// Yes, collapse it
				mn.collapse(false, false, true, Cytoscape.getCurrentNetworkView());
			}
		}

		// OK, now get the selected nodes again
		List<CyNode> currentNodes = new ArrayList(network.getSelectedNodes());

		CyGroup group = CyGroupManager.createGroup(groupName, currentNodes, MetaNodePlugin2.viewerName, network);
		if (group == null) {
			// Oops -- something didn't happen right!
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
				"Unable to create group "+groupName,"GroupError",
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		MetaNode newNode = new MetaNode(group);
		groupViewer.groupCreated(group);
		newNode.collapse(false, false, true, Cytoscape.getCurrentNetworkView());
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
		if (node != null) {
			node.addToGroup(group);  // NOTE: this will trigger a groupChanged callback
		} else {
			// Get the currently selected nodes and add
			// them one-by-one
			CyNetwork network = Cytoscape.getCurrentNetwork();
			List<CyNode> currentNodes = new ArrayList(network.getSelectedNodes());
			for (CyNode selNode: currentNodes) {
				selNode.addToGroup(group);
			}
		}
	}

	/**
	 * Remove a node from a group
	 *
	 * @param node the node to remove from this group
	 */
	private void removeFromGroup(CyNode node) {
		if (node != null) {
			if (group.contains(node))
				node.removeFromGroup(group);  // NOTE: this will trigger a groupChanged callback
		} else {
			// Get the currently selected nodes and add
			// them one-by-one
			CyNetwork network = Cytoscape.getCurrentNetwork();
			List<CyNode> currentNodes = new ArrayList(network.getSelectedNodes());
			for (CyNode selNode: currentNodes) {
				if (group.contains(selNode))
					selNode.removeFromGroup(group);
			}
		}
	}

	/**
	 * Perform the action associated with a select menu selection
	 */
	private void collapse() {
		MetaNode mNode = MetaNode.getMetaNode(group);
		if (mNode == null) {
			mNode = new MetaNode(group);
			if (mNode == null) return;
			groupViewer.groupCreated(group);
		}
		mNode.collapse(false, false, true, Cytoscape.getCurrentNetworkView());
	}

	/**
	 * Perform the action associated with an unselect menu selection
	 */
	private void expand() {
		MetaNode mNode = MetaNode.getMetaNode(group);
		if (mNode == null) {
			mNode = new MetaNode(group);
			groupViewer.groupCreated(group);
		}
		mNode.expand(false, Cytoscape.getCurrentNetworkView(), true);
	}

	/**
 	 * Create a new network from the currently collapsed group
 	 */
	private void createNetworkFromGroup() {
		MetaNode mNode = MetaNode.getMetaNode(group);
		if (mNode == null) {
			mNode = new MetaNode(group);
			groupViewer.groupCreated(group);
		}
		mNode.createNetworkFromGroup();
	}

	/**
 	 * Expand all metanodes
 	 */
	private void expandAll() {
		MetaNode.expandAll();
	}

	/**
 	 * Collapse all metanodes
 	 */
	private void collapseAll() {
		MetaNode.collapseAll();
	}
}
