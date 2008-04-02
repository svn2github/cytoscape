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
import javax.swing.JPanel;
import java.util.List;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;

import giny.view.*;
import giny.model.Node;

/**
 * The GroupPanel is the implementation for the Cytopanel that presents
 * the named selection mechanism to the user.
 */
public class GroupPanel extends JPanel implements TreeSelectionListener,
                                                  TreeExpansionListener,
	                                                ActionListener,
                                                  GraphViewChangeListener {
	List<CyGroupViewer> viewerList = null;
	JTree navTree = null;
	GroupTreeModel treeModel = null;
	TreeSelectionModel treeSelectionModel = null;
	HashMap<CyNode,List<TreePath>>nodeMap = null;
	boolean updateSelection = true;
	boolean updateTreeSelection = true;
	TreePath[] ta = new TreePath[1];
	ButtonGroup depthGroup = null;
	JButton deleteButton = null;
	JPanel depthBox = null;
	int treeDepth = 1;

	/**
	 * Construct a group panel
	 *
	 * @param viewer the CyGroupViewer that created us
	 */
	public GroupPanel (CyGroupViewer viewer) {
		super();
		viewerList = new ArrayList();
		viewerList.add(viewer);

		setLayout(new BorderLayout());

		// Create a separate JPanel for our various controls
		JPanel controlPanel = new JPanel();
		// Set up our layout
		BoxLayout layout = new BoxLayout(controlPanel, BoxLayout.Y_AXIS);
		controlPanel.setLayout(layout);

		// Create a button box at the top 
		JPanel buttonBox = new JPanel();
		// Create clear selection button
		buttonBox.add(createButton("Clear Selection", "clear", true));

		// Create new group button
		buttonBox.add(createButton("New Group", "new", true));

		// Create delete group button
		deleteButton = createButton("Delete Group", "delete", false);
		buttonBox.add(deleteButton);

		// Border it
		buttonBox.setBorder(BorderFactory.createEtchedBorder());
		controlPanel.add(buttonBox);

		depthBox = new JPanel();
		depthGroup = new ButtonGroup();
		addDepthButtons(treeDepth);

		// Border it
		Border depthBorder = BorderFactory.createEtchedBorder();
		TitledBorder dTitleBorder = BorderFactory.createTitledBorder(depthBorder, "Expansion Depth");
		dTitleBorder.setTitlePosition(TitledBorder.LEFT);
		dTitleBorder.setTitlePosition(TitledBorder.TOP);
		depthBox.setBorder(dTitleBorder);
		controlPanel.add(depthBox);

		add(controlPanel, BorderLayout.NORTH);

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
		// this.setPreferredSize(new Dimension(-1, 400));
		// navTree.setPreferredSize(new Dimension(-1, 800));

		navTree.addTreeExpansionListener(this);

		add(treeView, BorderLayout.CENTER);

	}

	/**
 	 * Tell the GroupPanel that we have an additional viewer who
 	 * wants to use us
 	 *
 	 * @param viewer the new viewer
 	 */
	public void addViewer(CyGroupViewer viewer) {
		if (viewerList.contains(viewer))
			return;
		viewerList.add(viewer);
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
		// System.out.println("valueChanged");

		if (!updateTreeSelection) {
			return;
		}

		// Close our "ears" to selection updates from Cytoscape
		updateSelection = false;

		List<CyNode>clearNodes = new ArrayList();
		List<TreePath>clearPaths = new ArrayList();

		for (int i = cPaths.length-1; i >= 0; i--) {
			// System.out.println(cPaths[i]);
			DefaultMutableTreeNode treeNode = 
			     (DefaultMutableTreeNode) cPaths[i].getLastPathComponent();
			    
			if (!CyNode.class.isInstance(treeNode.getUserObject()))
				continue;

			updateTreeSelection = false;
			CyNode node = (CyNode)treeNode.getUserObject();
			if (e.isAddedPath(cPaths[i])) {
				// See if this node has multiple paths
				if (nodeMap.containsKey(node) && nodeMap.get(node).size() > 1) {
					treeSelectionModel.addSelectionPaths(nodeMap.get(node).toArray(ta));
				} else {
					// System.out.println("Adding "+cPaths[i]);
					treeSelectionModel.addSelectionPath(cPaths[i]);
				}

				if (CyGroupManager.isaGroup(node)) {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, true);
					CyGroup group = CyGroupManager.getCyGroup(node);
					Cytoscape.getCurrentNetwork().setSelectedNodeState(group.getNodes(), true);
					group.setState(NamedSelection.SELECTED);
					// Update the Cytoscape selections
					List<CyNode>nodes = updateNodes(group);
					Cytoscape.getCurrentNetwork().setSelectedNodeState(nodes, true);
					// Update the JTree
					List<TreePath>paths = getPathList(nodes);
					treeSelectionModel.addSelectionPaths(paths.toArray(cPaths));
				} else {
					Cytoscape.getCurrentNetwork().setSelectedNodeState(node, true);
					// Do we need to promote?
					checkUpdateGroups(node);
				}
			} else {
				clearNodes.add(node);
				clearPaths.add(cPaths[i]);
				if (CyGroupManager.isaGroup(node)) {
					CyGroup group = CyGroupManager.getCyGroup(node);
					group.setState(NamedSelection.UNSELECTED);
					// Update the Cytoscape selections
					clearNodes.addAll(updateNodes(group));
				}
				for (TreePath path: nodeMap.get(node)) {
					// Get the parent of this selection
					if (path.getPathCount() > 1) {
						if (path != cPaths[i])
							clearPaths.add(path);
						TreePath parentPath = path.getParentPath();
						clearPaths.add(parentPath);
						// Get the group and mark it as unselected
						Object userObject = ((DefaultMutableTreeNode)parentPath.getLastPathComponent()).getUserObject();
						if (CyNode.class.isInstance(userObject)) {
							CyGroup parentGroup = CyGroupManager.getCyGroup((CyNode)userObject);
							parentGroup.setState(NamedSelection.UNSELECTED);
						}
					}
				}
			}
			updateTreeSelection = true;
		}
		if (clearPaths.size() > 0) {
			updateTreeSelection = false;
			treeSelectionModel.removeSelectionPaths(clearPaths.toArray(cPaths));
			Cytoscape.getCurrentNetwork().setSelectedNodeState(clearNodes, false);
			updateTreeSelection = true;
		}
		Cytoscape.getCurrentNetworkView().updateView();

		updateSelection = true;
	}

	public void actionPerformed(ActionEvent e) {
		if ("clear".equals(e.getActionCommand())) {
			updateSelection = false;
			updateTreeSelection = false;
			Cytoscape.getCurrentNetwork().unselectAllNodes();
			Cytoscape.getCurrentNetworkView().updateView();
			navTree.clearSelection();
			updateSelection = true;
			updateTreeSelection = true;
		} else if ("new".equals(e.getActionCommand())) {
			// Check and see if anything is selected
			Set nodeSet = Cytoscape.getCurrentNetwork().getSelectedNodes();
			if (nodeSet != null && nodeSet.size() > 0) {
				// Yes, create the group
				ArrayList<CyNode>currentNodes = new ArrayList(nodeSet);
				GroupCreationDialog dd = new GroupCreationDialog(Cytoscape.getDesktop(), currentNodes, viewerList);
			} else {
				// No, tell the user
				JOptionPane.showMessageDialog(this, "You must select a set of nodes to be part of the group", 
				                              "No nodes", JOptionPane.ERROR_MESSAGE);
			}
		} else if ("delete".equals(e.getActionCommand())) {
			// Do we have a path to a group?
			TreePath[] pathArray = navTree.getSelectionPaths();
			
			// Make sure we're pointing to groups
			ArrayList<CyGroup>groupList = new ArrayList();
			if (pathArray != null) {
				for (int path = 0; path < pathArray.length; path++) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)pathArray[path].getLastPathComponent();
					CyNode nodeObject = (CyNode)node.getUserObject();
					if (nodeObject.isaGroup()) {
						groupList.add(CyGroupManager.getCyGroup(nodeObject));
					}
				}
			}

			if (groupList.size() == 0) {
					JOptionPane.showMessageDialog(this, "No groups are selected", "No groups", JOptionPane.ERROR_MESSAGE);
					return;
			}
			// Yes, do the appropriate "are you sure"
			int ans = JOptionPane.showConfirmDialog(this, "You are deleting "+groupList.size()+" groups.  Are you sure?", 
			                                        "Confirm group delete", JOptionPane.YES_NO_OPTION);
			if (ans == 0) {
				// Delete them
				updateSelection = false;
				updateTreeSelection = false;
				for (CyGroup group: groupList) {
					CyGroupManager.removeGroup(group);
				}
				updateSelection = true;
				updateTreeSelection = true;
				treeModel.reload();
			}
		} else if ("1".equals(e.getActionCommand())) {
			setTreeDepth(1, navTree.getPathForRow(0));
		} else if ("2".equals(e.getActionCommand())) {
			setTreeDepth(2, navTree.getPathForRow(0));
		} else if ("3".equals(e.getActionCommand())) {
			setTreeDepth(3, navTree.getPathForRow(0));
		} else if ("4".equals(e.getActionCommand())) {
			setTreeDepth(4, navTree.getPathForRow(0));
		} else if ("5".equals(e.getActionCommand())) {
			setTreeDepth(5, navTree.getPathForRow(0));
		} else if ("6".equals(e.getActionCommand())) {
			setTreeDepth(6, navTree.getPathForRow(0));
		} else if ("7".equals(e.getActionCommand())) {
			setTreeDepth(7, navTree.getPathForRow(0));
		} else if ("8".equals(e.getActionCommand())) {
			setTreeDepth(8, navTree.getPathForRow(0));
		}
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
			CyGroupViewer groupViewer = CyGroupManager.getGroupViewer(group.getViewer());
			if (groupViewer == null || !groupList.contains(groupViewer)) 
				continue;
			Iterator<CyNode> nodeIter = group.getNodeIterator();
			boolean allSelected = true;
			while (nodeIter.hasNext()) {
				CyNode nodeMember = nodeIter.next();
				if (nodeMap.containsKey(nodeMember)) {
					// For our purposes, if one is selected, both are selected
					TreePath path = (TreePath)(nodeMap.get(nodeMember).get(0));
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
				treeSelectionModel.addSelectionPaths(nodeMap.get(group.getGroupNode()).toArray(ta));
				group.setState(NamedSelection.SELECTED);
			}
		}
	}

	/**
	 * Update the selection state of all of the nodes for this group
	 *
	 * @param group the group whose nodes we need to check
	 */
	private List<CyNode> updateNodes(CyGroup group) {
		ArrayList<CyNode>list = new ArrayList();
		for (CyNode node: group.getNodes()) {
			if (nodeMap.containsKey(node)) {
				list.add(node);
			}
			if (node.isaGroup()) {
				CyGroup childGroup = CyGroupManager.getCyGroup(node);
				list.addAll(updateNodes(childGroup));
			}
		}
		return list;
	}

	private List<TreePath> getPathList(List<CyNode>nodeList) {
		ArrayList<TreePath> pathList = new ArrayList(nodeList.size());
		for (CyNode node: nodeList) {
			if (nodeMap.containsKey(node)) {
				for (TreePath path: nodeMap.get(node)) {
					if (navTree.isVisible(path))
						pathList.add(path);
				}
			}
		}
		return pathList;
	}

	private void setTreeDepth(int depth, TreePath path) {
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
				// System.out.println ("Path[0] = "+nodeMap.get(node).get(0));
				treeSelectionModel.addSelectionPaths(nodeMap.get(node).toArray(ta));
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
				treeSelectionModel.removeSelectionPaths(nodeMap.get(node).toArray(ta));
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
		int j = 0;
		for (int i = 0; i < nodeList.length; i++) {
			CyNode node = (CyNode)nodeList[i];

			// System.out.print(node.getIdentifier()+", ");
			if (nodeMap.containsKey(node)) {
				if (select) {
					treeSelectionModel.addSelectionPaths(nodeMap.get(node).toArray(ta));
				} else {
					treeSelectionModel.removeSelectionPaths(nodeMap.get(node).toArray(ta));
				}
			}
		}
		checkGroupSelection(nodeList,select);
		// System.out.println(" ");
	}

	private void checkGroupSelection(Node nodeList[], boolean select) {
		// First, get a list of groups
		ArrayList<CyGroup> groupList = new ArrayList();
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
					treeSelectionModel.addSelectionPaths(nodeMap.get(groupNode).toArray(ta));
				}
			} else {
				updateTreeSelection = false;
				treeSelectionModel.removeSelectionPaths(nodeMap.get(groupNode).toArray(ta));
				group.setState(NamedSelection.UNSELECTED);
				updateTreeSelection = true;
			}
		}
	}

	private JButton createButton(String label, String command, boolean enabled) {
		JButton newButton = new JButton("<html><span style='font-size: 80%;'>"+label+"</span></html>");
		newButton.setActionCommand(command);
		newButton.addActionListener(this);
		newButton.setEnabled(enabled);
		return newButton;
	}

	private void addDepthButtons(int depth) {
		int maxDepth = depth;
		if (maxDepth > 8) maxDepth = 8;

		// Get the number of buttons currently in the group
		int buttonCount = depthGroup.getButtonCount();
		if (buttonCount > maxDepth) {
			for (Enumeration <AbstractButton> buttons = depthGroup.getElements(); buttons.hasMoreElements() ;) {
				AbstractButton b = buttons.nextElement();
				String command = b.getActionCommand();
				if (Integer.parseInt(command) > maxDepth) {
					depthGroup.remove(b);
					depthBox.remove(b);
				}
			}
		} else {
			for (int count = buttonCount+1; count < maxDepth+1; count++) {
				JRadioButton depthButton = new JRadioButton("<html><span style='font-size: 70%'>"+count+"</span></html>");
				depthButton.setActionCommand(""+count);
				depthButton.addActionListener(this);
				depthGroup.add(depthButton);
				depthBox.add(depthButton);
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
			addDepthButtons(treeDepth);
			this.setRoot(rootNode);
			updateTreeSelection = true;
		}

		/**
		 * Reload the tree model
		 */
		public void reload() {
			updateTreeSelection = false;
			DefaultMutableTreeNode rootNode = buildTree();
			addDepthButtons(treeDepth);
			this.setRoot(rootNode);

			super.reload();

			for (CyGroupViewer viewer: viewerList) {
				// Update our selection based on the currently selection nodes, etc.
				List<CyGroup> groupList = CyGroupManager.getGroupList(viewer);
				if (groupList == null || groupList.size() == 0)
					continue;
				for (CyGroup group: groupList) {
					CyNode groupNode = group.getGroupNode();
					if (group.getState() == NamedSelection.SELECTED && nodeMap.containsKey(groupNode)) {
						treeSelectionModel.addSelectionPaths(nodeMap.get(groupNode).toArray(ta));
					}
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
			nodeMap = new HashMap<CyNode,List<TreePath>>();
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Named Selections (Groups)");
			TreePath rootPath = new TreePath(rootNode);

			deleteButton.setEnabled(false);
			for (CyGroupViewer viewer: viewerList) {
				List<CyGroup> groupList = CyGroupManager.getGroupList(viewer);
				if (groupList == null || groupList.size() == 0) {
					continue;
				}

				deleteButton.setEnabled(true);

				for (CyGroup group: groupList) {
					// Only add root groups
					if (isRootGroup(group, viewer))
						rootNode.add(addGroupToTree(group, rootNode, rootPath));
				}
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
		                                               TreePath parentPath) {
			// Get the node
			CyNode groupNode = group.getGroupNode();

			// Create the tree
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(groupNode);
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

			// Now, add all of our children
			for (CyNode node: group.getNodes()) {
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
					treeNode.add(addGroupToTree(childGroup, treeNode, path));
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
			}
			treeDepth++;
			return treeNode;
		}

		private void removeGroupFromTree(CyGroup childGroup, DefaultMutableTreeNode treeModel,
		                                 TreePath path) {
			TreePath parentPath = path.getParentPath();
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();
			DefaultMutableTreeNode thisNode = (DefaultMutableTreeNode)path.getLastPathComponent();
			parentNode.remove(thisNode);
		}

		private boolean isRootGroup(CyGroup group, CyGroupViewer viewer) {
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
			if (row == 0) sel = false;
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
