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
import namedSelection.ui.GroupCreationDialog;

// System imports

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import giny.model.Node;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;

/**
 * The GroupPanel is the implementation for the Cytopanel that presents
 * the named selection mechanism to the user.
 */
public class GroupTree extends JTree implements TreeSelectionListener,
                                                TreeExpansionListener,
                                                GraphViewChangeListener {

	TreePath[] ta = new TreePath[1];
	GroupTreeModel treeModel = null;
	GroupPanel groupPanel = null;
	boolean updateSelection = true;
	boolean updateTreeSelection = true;

	/**
	 * Construct a group panel
	 *
	 * @param viewer the CyGroupViewer that created us
	 */
	public GroupTree (GroupPanel parent) {
		super();

		groupPanel = parent;
		treeModel = new GroupTreeModel(this);
		this.setModel(treeModel);

		DefaultTreeCellRenderer renderer = new ObjectRenderer();
		renderer.setBackgroundNonSelectionColor(Cytoscape.getDesktop().getBackground());

		setCellRenderer(renderer);
		getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		addTreeSelectionListener(this);
		addTreeExpansionListener(this);
		setShowsRootHandles(false);
		// setRootVisible(false);
		setToggleClickCount(0);	// Disable multi-click

		setBackground(Cytoscape.getDesktop().getBackground());
	}

	public int reload() {
		if (updateTreeSelection)
			treeModel.reload();
		int depth = treeModel.getMaxDepth();
		return depth;
	}

	public void setViewerList(List<CyGroupViewer> viewerList) {
		treeModel.setViewerList(viewerList);
	}

	public void setTreeDepth(int depth) {
		treeModel.setTreeDepth(depth, getPathForRow(0));
	}

	public void setUpdateSelection(boolean update) {updateSelection = update;}
	public void setUpdateTreeSelection(boolean update) {updateTreeSelection = update;}
	public void setUpdates(boolean update) {
		updateSelection = update;
		updateTreeSelection = update;
	}

	public void clearAll() {
		setUpdateSelection(false);
		Cytoscape.getCurrentNetwork().unselectAllNodes();
		Cytoscape.getCurrentNetworkView().updateView();
		clearSelection();
		setUpdateSelection(true);
	}

	public List<CyGroup>getSelectedGroups() {
		TreePath[] pathArray = getSelectionPaths();
		List<CyGroup>groupList = new ArrayList<CyGroup>();
		if (pathArray != null) {
			for (int path = 0; path < pathArray.length; path++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)pathArray[path].getLastPathComponent();
				CyNode nodeObject = (CyNode)node.getUserObject();
				if (nodeObject.isaGroup()) {
					groupList.add(CyGroupManager.getCyGroup(nodeObject));
				}
			}
		}
		return groupList;
	}

	/**
	 * Respond to changes in the JTree
	 *
	 * @param e the TreeSelectionEvent we should respond to
	 */
	public void valueChanged(TreeSelectionEvent e) {

		if (!updateTreeSelection)
			return;

		// System.out.println("valueChanged: "+e);

		setUpdates(false);

		// For each path
		for (TreePath path: e.getPaths()) {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)path.getLastPathComponent();
			Object userObject = treeNode.getUserObject();
			boolean selected = e.isAddedPath(path);

			// Get the type of object
			if (userObject instanceof CyNetwork) {
				// This is either the root, the global network or a network name
				CyNetwork network = (CyNetwork)userObject;
				if (!network.equals(Cytoscape.getNullNetwork())) {
					// select (make current) the network
					Cytoscape.setCurrentNetwork(network.getIdentifier());
					// And the network view
					Cytoscape.setCurrentNetworkView(network.getIdentifier());
				}
			} else if (userObject instanceof CyGroup) {
				// Group
				// System.out.println("Group");
				// select the group
				CyGroup group = (CyGroup) userObject;
				if (selected)
					group.setState(NamedSelection.SELECTED);
				else if (group.getState() == NamedSelection.SELECTED)
					group.setState(NamedSelection.UNSELECTED);
			} else if (userObject instanceof CyNode) {
				// Node
				CyNode node = (CyNode)userObject;

				// Get the group this node is part of
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)treeNode.getParent();
				if (!(parentNode.getUserObject() instanceof CyGroup)) {
					// Shouldn't happen!
					continue;
				}
				GroupTreeUtils.selectNetworkNode(this, parentNode, path, node, selected);
			}
		}
		setUpdates(true);
	}

	/**
	 * Respond to a tree expansion event
	 *
	 * @param event the TreeExpansionEvent that triggered us
	 */
	public void treeExpanded(TreeExpansionEvent event) {
		setUpdates(false);
		TreePath treePath = event.getPath();
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
		// Get the type of object
		Object userObject = treeNode.getUserObject();
		if (userObject instanceof CyGroup) {
			// This is the only expansion event we really care about
			// If we are selected, select all of our children
			if (isPathSelected(treePath)) {
				GroupTreeUtils.selectAllChildren(this, treePath);
				removeSelectionPath(treePath);
			}
		}
		setUpdates(true);
	}

	/**
	 * Respond to a tree collapsed event
	 *
	 * @param event the TreeExpansionEvent that triggered us
	 */
	public void treeCollapsed(TreeExpansionEvent event) {
		setUpdates(false);
		TreePath treePath = event.getPath();
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
		// Get the type of object
		Object userObject = treeNode.getUserObject();
		if (userObject instanceof CyGroup) {
			// Careful -- by default JTree will promote any selected children to imply the parent is
			// selected.  We only want to select the group if *all* of the children in that group are
			// selected.
			if (!GroupTreeUtils.checkForPromotion(this, treeNode, true))
				removeSelectionPath(treePath);
		}
		setUpdates(true);
	}

	/**
	 * Respond to a change in the graph perspective
	 *
	 * @param event the GraphPerspectiveChangeEvent that resulted in our being called
	 */
	public void graphViewChanged(GraphViewChangeEvent event) {
		if (!updateSelection)
			return;

		setUpdates(false);
		if (event.isNodesSelectedType()) {
			treeModel.selectTreeNodes(event.getSelectedNodes(), true);
			// System.out.println("selected "+nodeList.length+" nodes");
		} else if (event.isNodesUnselectedType()) {
			treeModel.selectTreeNodes(event.getUnselectedNodes(), false);
			// System.out.println("unselected "+nodeList.length+" nodes");
		}
		setUpdates(true);
	}

	/**
	 * Get the node from a userObject
	 *
	 * @param treeNode the treeNode we're interested in
	 * @return the CyNode or null if treeNode is not a node or a group
	 */
	private CyNode getCyNode(DefaultMutableTreeNode treeNode) {
		Object userObj = treeNode.getUserObject();
		if (userObj instanceof CyNode)
			return (CyNode)userObj;
		if (userObj instanceof CyGroup)
			return ((CyGroup)userObj).getGroupNode();
		return null;
	}
}
