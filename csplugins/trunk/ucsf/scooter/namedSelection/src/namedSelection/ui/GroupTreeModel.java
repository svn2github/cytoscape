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
 * The GroupTreeModel implements the model for the JTree
 */
public class GroupTreeModel extends DefaultTreeModel {
	GroupTree navTree = null;
	TreePath[] ta = new TreePath[1];
	List<CyGroupViewer> viewerList = null;
	Map<CyNode,List<TreePath>>nodeMap = null;
	int maxDepth = 1;
	CyNetwork nullNetwork = Cytoscape.getNullNetwork();

	/**
	 * GroupTreeModel constructor
	 *
	 * @param tree the JTree whose model we are implementing
	 */
	public GroupTreeModel (GroupTree tree) {
		super(new DefaultMutableTreeNode());
		this.navTree = tree;
		// DefaultMutableTreeNode rootNode = buildTree();
		// this.setRoot(rootNode);
	}

	public void setViewerList(List<CyGroupViewer> viewerList) {
		this.viewerList = viewerList;
	}

	/**
	 * Reload the tree model
	 */
	public void reload() {
		navTree.setUpdates(false);
		DefaultMutableTreeNode rootNode = buildTree();
		this.setRoot(rootNode);
		TreeSelectionModel treeSelectionModel = navTree.getSelectionModel();

		super.reload();

		for (CyGroupViewer viewer: viewerList) {
			// Update our selection based on the currently selection nodes, etc.
			List<CyGroup> groupList = CyGroupManager.getGroupList(viewer);
			if (groupList == null || groupList.size() == 0)
				continue;
			for (CyGroup group: groupList) {
				CyNode groupNode = group.getGroupNode();
				if (group.getViewer() != null && group.getViewer().equals(NamedSelection.viewerName) &&
				    group.getState() == NamedSelection.SELECTED && nodeMap.containsKey(groupNode)) {
					treeSelectionModel.addSelectionPaths(nodeMap.get(groupNode).toArray(ta));
				}
			}
		}
		navTree.setUpdates(true);
	}

	public int getMaxDepth() { return maxDepth; }

	/**
	 * Select a group of nodes in our tree.  This is primiarily used by the
	 * GroupTree's graphViewChanged method to reflect the nodes selected in the
	 * network in our group tree.
	 *
	 * TODO: Handle promotion/demotion
	 *
	 * @param nodes the array of nodes that was changed
	 * @param select whether the nodes were selected or deselected
	 */
	public void selectTreeNodes(Node[] nodes, boolean select) {
		for (Node node: nodes) {
			// Get the path we want to select
			if (nodeMap.containsKey((CyNode)node)) {
				List<TreePath> pathList = nodeMap.get((CyNode)node);
				for (TreePath path: pathList) {
					GroupTreeUtils.selectTreeNode(navTree, path, select);
				}
			}
		}
	}

	public void setTreeDepth(int depth, TreePath path) {
		DefaultMutableTreeNode treeNode = 
			     (DefaultMutableTreeNode) path.getLastPathComponent();
		navTree.expandPath(path);
		for (int child = 0; child < treeNode.getChildCount(); child++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)treeNode.getChildAt(child);
			CyNode node = (CyNode)childNode.getUserObject();
			if (!nodeMap.containsKey(node)) {
				continue;
			}

			// A node could be in mulitple paths, but we only want
			// the immediate descendant of "path"
			List<TreePath> pathsToNode = nodeMap.get(node);
			TreePath pathToNode = pathsToNode.get(0);
			for (int p = 1; p < pathsToNode.size(); p++) {
				if (pathsToNode.get(p).getParentPath().equals(path)) {
					pathToNode = pathsToNode.get(p);
					break;
				}
			}

			if (depth == 1) {
					navTree.collapsePath(pathToNode);
			} else {
				setTreeDepth(depth-1, pathToNode);
			}
		}
	}


	/**
	 * Build the model for the tree
	 *
	 * @return a DefaultMutableTreeNode that represents the root of the tree
	 */
	private DefaultMutableTreeNode buildTree() {
		int saveDepth;

		nodeMap = new HashMap<CyNode,List<TreePath>>();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Current Groups");
		TreePath rootPath = new TreePath(rootNode);

		maxDepth = 1;

		for (CyGroupViewer viewer: viewerList) {

			List<CyGroup> groupList = CyGroupManager.getGroupList(viewer);
			if (groupList == null || groupList.size() == 0) {
				continue;
			}

			Map<CyNetwork, List<CyGroup>> groupMap = GroupTreeUtils.sortListByNetwork(groupList);
			List<CyNetwork> netList = new ArrayList<CyNetwork>(groupMap.keySet());

			for (CyNetwork network: (List<CyNetwork>)GroupTreeUtils.sortNetworkList(netList)) {
				// System.out.println("Adding network: "+network.getTitle());
				List<CyGroup> netGroupList = groupMap.get(network);
				rootNode.add(addNetworkToTree(network, netGroupList, viewer, rootNode, rootPath, 2));
			}
		}
		return rootNode;
	}


	/**
	 * Add a network and all of its groups to the tree (including
	 * internal groups, recursively).
	 *
	 * @param network the network we're adding
	 * @param group the CyGroup we're adding to the tree
	 * @param treeModel the tree model
	 * @param parentPath the path we're going to add to
	 * @param depth the depth of this entry
	 */
	private DefaultMutableTreeNode addNetworkToTree (CyNetwork network, List<CyGroup> groupList, 
	                                                 CyGroupViewer viewer,
	                                                 DefaultMutableTreeNode treeModel,
	                                                 TreePath parentPath, int depth) {

		DefaultMutableTreeNode networkNode = new DefaultMutableTreeNode(network);

		// Add it to our path
		TreePath path = parentPath.pathByAddingChild(networkNode);

		for (CyGroup group: groupList) {
			// Only add root groups
			if (GroupTreeUtils.isRootGroup(group, viewer))
				networkNode.add(addGroupToTree(group, networkNode, path, 3));
		}
		return networkNode;
	}
	

	/**
	 * Add a pseudo-node that provides the user with an option clear all selections
	 *
	 * @param message the label for the item in the tree
	 * @param treeModel the tree model
	 * @param parentPath the path we're going to add to
	 */
	private DefaultMutableTreeNode addClearToTree (String message, DefaultMutableTreeNode treeModel,
	                                               TreePath parentPath) {
		// Create the tree
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(message);
		// Add it to our path
		TreePath path = parentPath.pathByAddingChild(treeNode);
		return treeNode;
	}

	/**
	 * Add a group and all of its nodes to the tree (including
	 * internal groups, recursively).
	 *
	 * @param group the CyGroup we're adding to the tree
	 * @param treeModel the tree model
	 * @param parentPath the path we're going to add to
	 */
	private DefaultMutableTreeNode addGroupToTree (CyGroup group, DefaultMutableTreeNode treeModel,
	                                               TreePath parentPath, int depth) {
		// Get the node
		CyNode groupNode = group.getGroupNode();

		// Create the tree
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(group);

		TreeSelectionModel treeSelectionModel = navTree.getSelectionModel();
		// Add it to our path
		TreePath path = parentPath.pathByAddingChild(treeNode);
		if (!nodeMap.containsKey(groupNode)) {
			nodeMap.put(groupNode, new ArrayList<TreePath>());
		}
		nodeMap.get(groupNode).add(path);
		// Is the group node selected?
		if (group.getState() == NamedSelection.SELECTED) {
			// System.out.println("Group "+group+" is selected");
			treeSelectionModel.addSelectionPath(path);
		}

		List<CyNode> nodeList = GroupTreeUtils.sortList(group.getNodes());

		// Now, add all of our children
		for (CyNode node: nodeList) {
			if (CyGroupManager.isaGroup(node)) {
				// Get the group
				CyGroup childGroup = CyGroupManager.getCyGroup(node);
				// See if it's already in the tree
				if (nodeMap.containsKey(childGroup.getGroupNode())) {
					// Yes!  If it's a root, we need to move it underneath us
					List<TreePath> childPathList = nodeMap.get(childGroup.getGroupNode());
					for (TreePath childPath: childPathList) {
						if (childPath.getPathCount() == 2) {
							// It's a root -- remove it
							removeGroupFromTree(childGroup, treeModel, childPath);
						}
					}
				}
				treeNode.add(addGroupToTree(childGroup, treeNode, path, depth+1));
			} else {
				// Add the node to the tree
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);
				TreePath childPath = path.pathByAddingChild(childNode);
				if (!nodeMap.containsKey(node)) {
					nodeMap.put(node, new ArrayList<TreePath>());
				}
				nodeMap.get(node).add(childPath);
				treeNode.add(childNode);
				if (Cytoscape.getCurrentNetwork().isSelected(node)) {
					treeSelectionModel.addSelectionPath(childPath);
				}
			}
			// Figure keep track of the maximum depth
		}
		maxDepth = Math.max(maxDepth, depth);
		return treeNode;
	}

	private void removeGroupFromTree(CyGroup childGroup, DefaultMutableTreeNode treeModel,
	                                 TreePath path) {
		TreePath parentPath = path.getParentPath();
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();
		DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode)path.getLastPathComponent();
		if (parentNode.isNodeChild(thisNode))
			parentNode.remove(thisNode);
	}

}
