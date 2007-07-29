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
package cytoscape.bubbleRouter;

import giny.model.Node;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

/**
 * The GroupPanel is the implementation for the Cytopanel that presents
 * the named selection mechanism to the user.
 */
@SuppressWarnings("serial")
public class GroupPanel extends JPanel implements TreeSelectionListener,
                                                  TreeExpansionListener,
                                                  GraphViewChangeListener {
	CyGroupViewer viewer = null;
	JTree navTree = null;
	GroupTreeModel treeModel = null;
	TreeSelectionModel treeSelectionModel = null;
	HashMap<Object, Object> nodeMap = null;
	boolean updateSelection = true;
	boolean updateTreeSelection = true;

	/**
	 * Construct a group panel
	 *
	 * @param viewer the CyGroupViewer that created us
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
		treeSelectionModel = navTree.getSelectionModel();
		treeSelectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		navTree.addTreeSelectionListener(this);
		navTree.setShowsRootHandles(false);

		// navTree.addMouseListener(new PopupMenuListener(navTree));

		JScrollPane treeView = new JScrollPane(navTree);
		treeView.setBorder(BorderFactory.createEtchedBorder());
		navTree.setBackground(Cytoscape.getDesktop().getBackground());
		treeView.setBackground(Cytoscape.getDesktop().getBackground());
		this.setPreferredSize(new Dimension(240, 300));
		navTree.setPreferredSize(new Dimension(240, 300));

		navTree.addTreeExpansionListener(this);

		add(treeView);

	}

	/**
	 * Update the JTree to reflect the creation of a new group
	 *
	 * @param group the CyGroup that just got created
	 */
	public void groupCreated(CyGroup group) {
		treeModel.reload();
	}

	/**
	 * Update the JTree to reflect the removal of a group
	 *
	 * @param group the CyGroup that just got removed
	 */
	public void groupRemoved(CyGroup group) {
		treeModel.reload();
	}

	/**
	 * Update the JTree to reflect the change of a group (node
	 * addition or deletion)
	 *
	 * @param group the CyGroup that just got changed
	 */
	public void groupChanged(CyGroup group) {
		treeModel.reload();
	}

	/**
	 * Respond to changes in the JTree
	 *
	 * @param e the TreeSelectionEvent we should respond to
	 */
	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] cPaths = e.getPaths();
		if (cPaths == null) return;

		if (!updateTreeSelection) {
			return;
		}

		// Close our "ears" to selection updates from Cytoscape
		updateSelection = false;

		for (int i = cPaths.length-1; i >= 0; i--) {
			// System.out.println(cPaths[i]);
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
				if (CyGroupManager.isaGroup(node)) {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, true);
					// It's a group -- get the members
					CyGroup group = CyGroupManager.getCyGroup(node);
					Cytoscape.getCurrentNetwork().setSelectedNodeState(group.getNodes(), true);
					group.setState(BubbleRouterPlugin.SELECTED);
					if (navTree.isExpanded(cPaths[i])) {
						updateNodes(group, true);
					}
				} else {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, true);
					// Do we need to promote?
					checkUpdateGroups(node);
				}
			} else {
				if (CyGroupManager.isaGroup(node)) {
					CyGroup group = CyGroupManager.getCyGroup(node);
					group.setState(BubbleRouterPlugin.UNSELECTED);
				} else {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, false);
				}
				// Get the parent of this selection
				TreePath path = cPaths[i].getParentPath();
				if (path.getPathCount() > 1) {
					treeSelectionModel.removeSelectionPath(path);
					// Get the group and mark it as unselected
					CyNode parent = (CyNode)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
					CyGroup parentGroup = CyGroupManager.getCyGroup(parent);
					parentGroup.setState(BubbleRouterPlugin.UNSELECTED);
				}
			}
		}
		Cytoscape.getCurrentNetworkView().updateView();

		updateSelection = true;
	}

	/**
	 * Check to see if we need to update the groups for this node
	 *
	 * @param node the node whose groups we need to check
	 */
	private void checkUpdateGroups(CyNode node) {
		List <CyGroup> groupList = node.getGroups();
		if (groupList == null) return;
		Iterator<CyGroup> iter = groupList.iterator();
		while (iter.hasNext()) {
			CyGroup group = iter.next();
			Iterator<CyNode> nodeIter = group.getNodeIterator();
			boolean allSelected = true;
			while (nodeIter.hasNext()) {
				CyNode nodeMember = nodeIter.next();
				if (nodeMap.containsKey(nodeMember)) {
					TreePath path = (TreePath)nodeMap.get(nodeMember);
					if (!navTree.isPathSelected(path)) {
						allSelected = false;
						break;
					}
				} else {
					allSelected = false;
					break;
				}
			}
			if (allSelected) {
				TreePath path = (TreePath)nodeMap.get(group.getGroupNode());
				treeSelectionModel.addSelectionPath(path);
				group.setState(BubbleRouterPlugin.SELECTED);
			}
		}
	}

	/**
	 * Update the selection state of all of the nodes for this group
	 *
	 * @param group the group whose nodes we need to check
	 */
	private void updateNodes(CyGroup group, boolean select) {
		Iterator<CyNode> nodeIter = group.getNodeIterator();
		while (nodeIter.hasNext()) {
			CyNode node = nodeIter.next();
			if (nodeMap.containsKey(node)) {
				TreePath path = (TreePath)nodeMap.get(node);
				if (select) {
					treeSelectionModel.addSelectionPath(path);
				} else {
					treeSelectionModel.removeSelectionPath(path);
				}
			}
		}
	}

	/**
	 * Respond to a tree expansion event
	 *
	 * @param event the TreeExpansionEvent that triggered us
	 */
	public void treeExpanded(TreeExpansionEvent event) {
		// Get the path
		TreePath path = event.getPath();
		// System.out.println("expanded "+path);
		// Is it selected?
		if (path.getPathCount() < 2) {
			// No, just return
			return;
		}
		// Get the group
		DefaultMutableTreeNode treeNode = 
			     (DefaultMutableTreeNode) path.getLastPathComponent();
		CyNode groupNode = (CyNode)treeNode.getUserObject();
		CyGroup group = CyGroupManager.getCyGroup(groupNode);
		// select them if they are selected in the graph
		Iterator<CyNode> nodeIter = group.getNodeIterator();
		CyNetwork network = Cytoscape.getCurrentNetwork();
		while (nodeIter.hasNext()) {
			CyNode node = nodeIter.next();
			if (nodeMap.containsKey(node) && network.isSelected(node)) {
				// System.out.println ("Node "+node+" is selected");
				TreePath nodePath = (TreePath)nodeMap.get(node);
				treeSelectionModel.addSelectionPath(nodePath);
			}
		}
	}

	/**
	 * Respond to a tree collapsed event
	 *
	 * @param event the TreeExpansionEvent that triggered us
	 */
	public void treeCollapsed(TreeExpansionEvent event) {
		// Get the path
		TreePath path = event.getPath();
		// Is it selected already?
		// System.out.println("collapsed "+path);
		if (!navTree.isPathSelected(path)) {
			// OK, this is a little ugly.  By default, JTree will promote
			// selections to the parent.  This means that if a single child
			// is selected, the collapsed parent will also be selected.  For
			// groups, that makes no sense.  So...we turn off our listener, deselect
			// all children, then allow the collapse to procede.
			updateTreeSelection = false;
			DefaultMutableTreeNode treeNode = 
			     (DefaultMutableTreeNode) path.getLastPathComponent();
			Enumeration childEnum = treeNode.children();
			while (childEnum.hasMoreElements()) {
				DefaultMutableTreeNode tn = (DefaultMutableTreeNode)childEnum.nextElement();
				CyNode node = (CyNode)tn.getUserObject();
				// Create a path
				TreePath nodePath = (TreePath)nodeMap.get(node);
				// Deselect the path
				treeSelectionModel.removeSelectionPath(nodePath);
			}
			updateTreeSelection = true;
		}
	}

	/**
	 * Respond to a change in the graph perspective
	 *
	 * @param event the GraphPerspectiveChangeEvent that resulted in our being called
	 */
	public void graphViewChanged(GraphViewChangeEvent event) {
		boolean select = false;
		Node[] nodeList;
		if (!updateSelection)
			return;

		if (event.isNodesSelectedType()) {
			select = true;
			nodeList = event.getSelectedNodes();
		} else if (event.isNodesUnselectedType()) {
			select = false;
			nodeList = event.getUnselectedNodes();
		} else {
			return;
		}

/*
		if (select)
			System.out.print("graphViewChanged selecting "+nodeList.length+" nodes: ");
		else
			System.out.print("graphViewChanged unselecting "+nodeList.length+" nodes: ");
*/
		
		// Build a path list corresponding to the selection
		for (int i = 0; i < nodeList.length; i++) {
			CyNode node = (CyNode)nodeList[i];

			// System.out.print(node.getIdentifier()+", ");
			if (nodeMap.containsKey(node)) {
				TreePath path = (TreePath)nodeMap.get(node);
				if (select) {
					treeSelectionModel.addSelectionPath(path);
				} else {
					treeSelectionModel.removeSelectionPath(path);
				}
			}
		}
		checkGroupSelection(nodeList,select);
		// System.out.println(" ");
	}

	private void checkGroupSelection(Node nodeList[], boolean select) {
		// First, get a list of groups
		ArrayList<CyGroup> groupList = new ArrayList<CyGroup>();
		for (int i = 0; i < nodeList.length; i++) {
			CyNode node = (CyNode) nodeList[i];
			List<CyGroup> groups = node.getGroups();
			if (groups == null) 
				continue;

			Iterator<CyGroup> iter = groups.iterator();
			while (iter.hasNext()) {
				CyGroup group = iter.next();
				if (!groupList.contains(group)) 
					groupList.add(group);
			}
		}

		// Now check to see if all of the members of those groups are
		// selected.  If they are, select the group
		if (groupList.size() == 0)
			return;

		Iterator<CyGroup> grIter = groupList.iterator();
		while (grIter.hasNext()) {
			CyGroup group = grIter.next();
			CyNode groupNode = group.getGroupNode();
			if (!nodeMap.containsKey(groupNode))
				continue;

			if (select) {
				Iterator <CyNode> nodeIter = group.getNodeIterator();
				boolean allSelected = true;
				while (nodeIter.hasNext()) {
					CyNode node = nodeIter.next();
					if (!Cytoscape.getCurrentNetwork().isSelected(node)) {
						allSelected = false;
						break;
					}
				}
				if (allSelected) {
					TreePath path = (TreePath)nodeMap.get(groupNode);
					treeSelectionModel.addSelectionPath(path);
				}
			} else {
				TreePath path = (TreePath)nodeMap.get(groupNode);
				updateTreeSelection = false;
				treeSelectionModel.removeSelectionPath(path);
				group.setState(BubbleRouterPlugin.UNSELECTED);
				updateTreeSelection = true;
			}
		}
	}

	/**
	 * The GroupTreeModel implements the model for the JTree
	 */
	public class GroupTreeModel extends DefaultTreeModel {
		JTree navTree = null;

		/**
		 * GroupTreeModel constructor
		 *
		 * @param tree the JTree whose model we are implementing
		 */
		public GroupTreeModel (JTree tree) {
			super(new DefaultMutableTreeNode());
			this.navTree = tree;
			updateTreeSelection = false;
			DefaultMutableTreeNode rootNode = buildTree();
			this.setRoot(rootNode);
			updateTreeSelection = true;
		}

		/**
		 * Reload the tree model
		 */
		public void reload() {
			updateTreeSelection = false;
			DefaultMutableTreeNode rootNode = buildTree();
			this.setRoot(rootNode);

			super.reload();

			// Update our selection based on the currently selection nodes, etc.
			List<CyGroup> groupList = CyGroupManager.getGroupList(viewer);
			if (groupList == null || groupList.size() == 0)
				return;
			Iterator<CyGroup> iter = groupList.iterator();
			while (iter.hasNext()) {
				CyGroup group = iter.next();
				CyNode groupNode = group.getGroupNode();
				if (group.getState() == BubbleRouterPlugin.SELECTED && nodeMap.containsKey(groupNode)) {
					TreePath path = (TreePath)nodeMap.get(groupNode);
					treeSelectionModel.addSelectionPath(path);
				}
			}
			
			updateTreeSelection = true;
		}

		/**
		 * Build the model for the tree
		 *
		 * @return a DefaultMutableTreeNode that represents the root of the tree
		 */
		DefaultMutableTreeNode buildTree() {
			nodeMap = new HashMap<Object, Object>();
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Bubble Router Regions");
			TreePath rootPath = new TreePath(rootNode);
			rootNode.add(addClearToTree("Clear Selections", rootNode, rootPath));
			List<CyGroup> groupList = CyGroupManager.getGroupList(viewer);
			if (groupList == null || groupList.size() == 0)
				return rootNode;

			Iterator<CyGroup> iter = groupList.iterator();
			while (iter.hasNext()) {
				rootNode.add(addGroupToTree(iter.next(), rootNode, rootPath));
			}
			return rootNode;
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
			parentPath.pathByAddingChild(treeNode);
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
		                                               TreePath parentPath) {
			// Get the node
			CyNode groupNode = group.getGroupNode();

			// Create the tree
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(groupNode);
			// Add it to our path
			TreePath path = parentPath.pathByAddingChild(treeNode);
			nodeMap.put(groupNode, path);
			// Is the group node selected?
			if (group.getState() == BubbleRouterPlugin.SELECTED) {
				// System.out.println("Group "+group+" is selected");
				treeSelectionModel.addSelectionPath(path);
			}

			// Now, add all of our children
			Iterator<CyNode> nodeIter = group.getNodeIterator();
			while (nodeIter.hasNext()) {
				CyNode node = nodeIter.next();
				if (CyGroupManager.isaGroup(node)) {
					// Get the group
					CyGroup childGroup = CyGroupManager.getCyGroup(node);
					treeNode.add(addGroupToTree(childGroup, treeNode, path));
				} else {
					// Add the node to the tree
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);
					TreePath childPath = path.pathByAddingChild(childNode);
					nodeMap.put(node, childPath);
					treeNode.add(childNode);
					if (Cytoscape.getCurrentNetwork().isSelected(node)) {
						treeSelectionModel.addSelectionPath(childPath);
					}
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
			Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
			if (CyNode.class.isInstance(userObject)) {
				userObject = ((CyNode)userObject).getIdentifier();
			}

			super.getTreeCellRendererComponent(tree, userObject, sel,
                            						 expanded, leaf, row,
                            						 hasFocus);
			return this;
		}
	}
}
