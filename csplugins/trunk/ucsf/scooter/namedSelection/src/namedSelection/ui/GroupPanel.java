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
import javax.swing.JPanel;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import namedSelection.*;

import cytoscape.*;

/**
 * The NamedSelection class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class GroupPanel extends JPanel implements TreeSelectionListener {
	CyGroupViewer viewer = null;
	JTree navTree = null;
	GroupTreeModel treeModel = null;

	/**
	 * Future version....
	 */
	public GroupPanel (CyGroupViewer viewer) {
		super();
		this.viewer = viewer;

		// Create a button box at the top for (New Group)

		// Create our JTree
		navTree = new JTree();
		treeModel = new GroupTreeModel(navTree);

		navTree.setModel(treeModel);
		DefaultTreeCellRenderer renderer = new ObjectRenderer();
		renderer.setBackgroundNonSelectionColor(Cytoscape.getDesktop().getBackground());
		navTree.setCellRenderer(renderer);
		navTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		navTree.addTreeSelectionListener(this);
		navTree.setShowsRootHandles(false);

		// navTree.addMouseListener(new PopupMenuListener(navTree));

		JScrollPane treeView = new JScrollPane(navTree);
		treeView.setBorder(BorderFactory.createEtchedBorder());
		navTree.setBackground(Cytoscape.getDesktop().getBackground());
		treeView.setBackground(Cytoscape.getDesktop().getBackground());
		this.setPreferredSize(new Dimension(240, 300));
		navTree.setPreferredSize(new Dimension(240, 300));

		add(treeView);

	}

	public void groupCreated(CyGroup group) {
		treeModel.reload();
	}

	public void groupRemoved(CyGroup group) {
		treeModel.reload();
	}

	public void groupChanged(CyGroup group) {
		treeModel.reload();
	}

	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] cPaths = e.getPaths();
		if (cPaths == null) return;

		for (int i = cPaths.length-1; i >= 0; i--) {
			DefaultMutableTreeNode treeNode = 
			     (DefaultMutableTreeNode) cPaths[i].getLastPathComponent();
			// Special case for "clear"
			if (String.class.isInstance(treeNode.getUserObject()) &&
			    e.isAddedPath(cPaths[i])) {
				String str = (String)treeNode.getUserObject();
				if (str.equals("Clear Selections")) {
					Cytoscape.getCurrentNetwork().unselectAllNodes();
				}
				continue;
			}
			    
			if (!CyNode.class.isInstance(treeNode.getUserObject()))
				continue;
			CyNode node = (CyNode)treeNode.getUserObject();
			if (e.isAddedPath(cPaths[i])) {
				if (CyGroup.isaGroup(node)) {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, true);
					// It's a group -- get the members
					CyGroup group = CyGroup.getCyGroup(node);
					Cytoscape.getCurrentNetwork().setSelectedNodeState(group.getNodes(), true);
					group.setState(NamedSelection.SELECTED);
				} else {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, true);
				}
			} else {
				if (CyGroup.isaGroup(node)) {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, false);
					CyGroup group = CyGroup.getCyGroup(node);
					group.setState(NamedSelection.UNSELECTED);
					Cytoscape.getCurrentNetwork().setSelectedNodeState(group.getNodes(), false);
				} else {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, false);
				}
			}
		}
		Cytoscape.getCurrentNetworkView().updateView();
	}

	public class GroupTreeModel extends DefaultTreeModel {
		JTree navTree = null;

		public GroupTreeModel (JTree tree) {
			super(new DefaultMutableTreeNode());
			this.navTree = tree;
			DefaultMutableTreeNode rootNode = buildTree();
			this.setRoot(rootNode);
		}

		public void reload() {
			DefaultMutableTreeNode rootNode = buildTree();
			this.setRoot(rootNode);

			super.reload();
		}

		DefaultMutableTreeNode buildTree() {
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Named Selections (Groups)");
			TreePath rootPath = new TreePath(rootNode);
			rootNode.add(addClearToTree("Clear Selections", rootNode, rootPath));
			List<CyGroup> groupList = CyGroup.getGroupList(viewer);
			if (groupList == null || groupList.size() == 0)
				return rootNode;

			Iterator<CyGroup> iter = groupList.iterator();
			while (iter.hasNext()) {
				rootNode.add(addGroupToTree(iter.next(), rootNode, rootPath));
			}
			return rootNode;
		}

		private DefaultMutableTreeNode addClearToTree (String message, DefaultMutableTreeNode treeModel,
		                                               TreePath parentPath) {
			// Create the tree
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(message);
			// Add it to our path
			TreePath path = parentPath.pathByAddingChild(treeNode);
			return treeNode;
		}

		private DefaultMutableTreeNode addGroupToTree (CyGroup group, DefaultMutableTreeNode treeModel,
		                                               TreePath parentPath) {
			// Get the node
			CyNode groupNode = group.getGroupNode();

			// Create the tree
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(groupNode);
			// Add it to our path
			TreePath path = parentPath.pathByAddingChild(treeNode);
			// Now, add all of our children
			Iterator<CyNode> nodeIter = group.getNodeIterator();
			while (nodeIter.hasNext()) {
				CyNode node = nodeIter.next();
				if (CyGroup.isaGroup(node)) {
					// Get the group
					CyGroup childGroup = CyGroup.getCyGroup(node);
					treeNode.add(addGroupToTree(childGroup, treeNode, path));
				} else {
					// Add the node to the tree
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);
					TreePath childPath = path.pathByAddingChild(childNode);
					treeNode.add(childNode);
				}
			}
			return treeNode;
		}
	}

	/**
	 * The ObjectRenderer class is used to provide special rendering
	 * capabilities for each row of the tree.
	 */
	private class ObjectRenderer extends DefaultTreeCellRenderer {

		/**
		 * Create a new ObjectRenderer
		 */
		public ObjectRenderer() {
		}

		/**
		 * This is the method actually called to render the tree cell
		 *
		 * @see DefaultTreeCellRenderer
		 */
		public Component getTreeCellRendererComponent(JTree tree, Object value,
																									boolean sel, boolean expanded,
																									boolean leaf, int row, 
																									boolean hasFocus) 
		{
			if (row == 0 || row == 1) sel = false;
			// Call the DefaultTreeCellRender's method to do most of the work
			super.getTreeCellRendererComponent(tree, value, sel,
                            						 expanded, leaf, row,
                            						 hasFocus);
			return this;
		}
	}
}
