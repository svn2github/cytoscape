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

import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

import namedSelection.NamedSelection;

// System imports

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import giny.model.Node;

/**
 * The GroupTreeUtils implements the model for the JTree
 */
public class GroupTreeUtils {

	/**
	 * Handle promotion or demotion of parent nodes.  Promotion occurs when all of the children
	 * of a group are selected.
	 *
	 * @param navTree The tree that holds all the paths
	 * @param groupTreeNode The parent node
	 * @param path The path to the node that is being selected or deselected
	 * @param selected if true this path is being selected
	 */
	public static void handlePromotion(GroupTree navTree, DefaultMutableTreeNode groupTreeNode, 
	                            TreePath path, boolean selected) {
		if (!(groupTreeNode.getUserObject() instanceof CyGroup))
			return;

		CyGroup group = (CyGroup) groupTreeNode.getUserObject();
		// Don't promote selection for other viewers!
		if (!group.getViewer().equals(NamedSelection.viewerName))
			return;

		// Now, see if we need to promote (or demote) the selection
		if (checkForPromotion(navTree, groupTreeNode, selected)) {
			// System.out.println("Promoting");
			navTree.addSelectionPath(path.getParentPath());
			// System.out.println("Setting group "+group+"s state to selected");
			group.setState(NamedSelection.SELECTED);
		} else {
			// We don't want to promote, but we only want to demote if we're
			// deselecting and the group is selected
			if (selected || group.getState() != NamedSelection.SELECTED)
				return; // We were selecting, just ignore this...

			// OK, we were deselecting and our group was selected
			navTree.removeSelectionPath(path.getParentPath());
			
			// Unselect the group -- this will deselect all nodes
			// System.out.println("Setting group "+group+"s state to unselected");
			group.setState(NamedSelection.UNSELECTED);

			// Reselect the individual nodes based on our selected paths
			syncNodesToPath(navTree, path.getParentPath(), group);
		}
	}

	/**
	 * Check to see if a path should be promoted
	 *
	 * @param navTree The tree that holds all the paths
	 * @param groupTreeNode The parent node
	 * @param selected if true this path is being selected
	 */

	public static boolean checkForPromotion(GroupTree navTree, DefaultMutableTreeNode groupTreeNode, boolean selected) {
		CyGroup group = (CyGroup)groupTreeNode.getUserObject();
		TreePath groupPath = new TreePath(groupTreeNode.getPath());
		// First do the easy thing
		if (!selected) {
			return false;
		}

		// Now, we need to see if all of the nodes for this group are now selected
		// Since groups can be in multiple paths, we want to use the TreeNode to figure this out
		for (Enumeration<DefaultMutableTreeNode> e = groupTreeNode.children(); e.hasMoreElements();) {
			DefaultMutableTreeNode treeNode = e.nextElement();
			// Get the path
			TreePath path = new TreePath(treeNode.getPath());
			if (!navTree.isPathSelected(path))
				return false;
		}
		// If we got here, all children are selected.  Select the parent
		return true;
	}

	/**
	 * Select all of the children in a given path
	 *
	 * @param navTree the tree we're looking at
	 * @param groupPath the path we're selecting
	 */
	public static void selectAllChildren(GroupTree navTree, TreePath groupPath) {
		DefaultMutableTreeNode groupTreeNode = (DefaultMutableTreeNode)groupPath.getLastPathComponent();
		for (Enumeration<DefaultMutableTreeNode> e = groupTreeNode.children(); e.hasMoreElements();) {
			DefaultMutableTreeNode treeNode = e.nextElement();
			TreePath path = new TreePath(treeNode.getPath());
			navTree.addSelectionPath(path);
			// Recurse if necessary
			if (treeNode.getUserObject() instanceof CyGroup)
				selectAllChildren(navTree, path);
		}
	}

	/**
	 * This method selects tree nodes only and does not effect the network.
	 *
	 * @param navTree the tree we're looking at
	 * @param path the path to the node we want to select
	 * @param select whether to select or deselect the path
	 */
	public static void selectTreeNode(GroupTree navTree, TreePath path, boolean select) {
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)path.getLastPathComponent();
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)treeNode.getParent();
		if (select) {
			// System.out.println("Selecting "+path);
			navTree.addSelectionPath(path);
		} else {
			// System.out.println("Deselecting "+path);
			navTree.removeSelectionPath(path);
		}

		handlePromotion(navTree, parentNode, path, select);
	}

	/**
	 * This method selects a network node
	 *
	 * @param navTree the tree we're looking at
	 * @param groupNode the parent node of the node we're going to select
	 * @param node the node to select
	 * @param selected whether to select or deselect
	 */
	public static void selectNetworkNode(GroupTree navTree, DefaultMutableTreeNode groupNode, 
	                                     TreePath treePath, CyNode node, boolean selected) {
		// System.out.println("selectNetworkNode: "+node+"@"+treePath+": "+selected);
		CyGroup group = (CyGroup)groupNode.getUserObject();
		CyNetwork net = group.getNetwork();
		handlePromotion(navTree, groupNode, treePath, selected);
		if (net == null) {
			for (CyNetwork network: Cytoscape.getNetworkSet()) {
				// System.out.println("Setting node "+node+"s state to "+selected);
				network.setSelectedNodeState(node, selected);
				Cytoscape.getNetworkView(network.getIdentifier()).updateView();
			}
		} else {
			// System.out.println("Setting node "+node+"s state to "+selected);
			net.setSelectedNodeState(node, selected);
			Cytoscape.getNetworkView(net.getIdentifier()).updateView();
		}
	}

	public static void syncNodesToPath(GroupTree navTree, TreePath path, CyGroup group) {
		// System.out.println("Synching nodes to path: "+path);
		DefaultMutableTreeNode groupTreeNode = (DefaultMutableTreeNode)path.getLastPathComponent();
		for (Enumeration<DefaultMutableTreeNode> e = groupTreeNode.children(); e.hasMoreElements();) {
			DefaultMutableTreeNode treeNode = e.nextElement();
			TreePath childPath = new TreePath(treeNode.getPath());
			if (navTree.isPathSelected(childPath)) {
				if (treeNode.getUserObject() instanceof CyNode) {
					selectNetworkNode(navTree, groupTreeNode, childPath, (CyNode)treeNode.getUserObject(), true);
				} else if (treeNode.getUserObject() instanceof CyGroup) {
					selectNetworkNode(navTree, groupTreeNode, childPath, 
					                  ((CyGroup)treeNode.getUserObject()).getGroupNode(), true);
				}
			}
		}
	}

	public static boolean isRootGroup(CyGroup group, CyGroupViewer viewer) {
		CyNode groupNode = group.getGroupNode();
		List<CyGroup>groupList = groupNode.getGroups();
		if (groupList == null || groupList.size() == 0) {
			return true;
		}

		// Not a root, but might be a child of a different viewer
		for (CyGroup parent: groupList) {
			if (parent.getViewer() != null && parent.getViewer().equals(viewer.getViewerName()))
				return false;
		}
		return true;
	}

	public static List sortList(List listToSort) {
		Object[] array = listToSort.toArray();
		Arrays.sort(array, new ToStringComparator());
		return Arrays.asList(array);
	}

	public static List sortNetworkList(List listToSort) {
		Object[] array = listToSort.toArray();
		Arrays.sort(array, new ToNetworkComparator());
		return Arrays.asList(array);
	}

	public static Map<CyNetwork, List<CyGroup>> sortListByNetwork(List<CyGroup> listToSort) {
		// Create a map of networks and their groups
		Map<CyNetwork, List<CyGroup>> netMap = new HashMap<CyNetwork, List<CyGroup>>();

		for (CyGroup group: listToSort) {
			CyNetwork net = group.getNetwork();
			if (net == null) {
				net = Cytoscape.getNullNetwork();
			}
			if (!netMap.containsKey(net)) {
				netMap.put(net, new ArrayList<CyGroup>());
			}
			netMap.get(net).add(group);
		}

		// OK, now we have the map -- sort each network
		for (CyNetwork network: netMap.keySet()) {
			List<CyGroup> sortedList = sortList(netMap.get(network));
			netMap.put(network,sortedList);
		}
		return netMap;
	}

}
