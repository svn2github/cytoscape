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
package structureViz.ui;

// System imports
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.Position;
import javax.swing.WindowConstants.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.*;

// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import giny.view.NodeView;

// StructureViz imports
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraChain;

import structureViz.Chimera;

public class ModelNavigatorDialog extends JDialog implements TreeSelectionListener {
	private Chimera chimeraObject;
	private boolean status;
	private static final int EXIT = 1;
	private static final int REFRESH = 2;
	private static ArrayList selectedList = null;
	private boolean ignoreSelection = false;

	// Dialog components
	private JLabel titleLabel;
	private JTree navigationTree;
	private DefaultTreeModel treeModel;
	private int residueDisplay = ChimeraResidue.THREE_LETTER;

	public ModelNavigatorDialog (Frame parent, Chimera object) {
		super(parent, false);
		chimeraObject = object;
		initComponents();
		status = false;
		selectedList = new ArrayList();
	}

	public void modelChanged() {
		// Something significant changed in the model (new open/closed structure?)
		rebuildTree();
	}

	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] paths = navigationTree.getSelectionPaths();
		if (paths == null) return;

		DefaultMutableTreeNode node = null;
		String selSpec = "sel ";
		HashMap modelsToSelect = new HashMap();

		for (int i = 0; i < paths.length; i++) {
			node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
			ChimeraStructuralObject nodeInfo = (ChimeraStructuralObject)node.getUserObject();
			ChimeraModel model = nodeInfo.getChimeraModel();
			selSpec = selSpec.concat(nodeInfo.toSpec());
			modelsToSelect.put(model,model);
			if (i < paths.length-1) selSpec.concat("|");
			// Add the model to be selected (if it's not already)
		}
		if (!ignoreSelection)
			chimeraObject.select(selSpec);

		selectCytoscapeNodes(modelsToSelect);
	}

	public void updateSelection(List selectionList) {
		navigationTree.clearSelection();
		Iterator selectionIter = selectionList.iterator();
		this.ignoreSelection = true;
		while (selectionIter.hasNext()) {
			ChimeraStructuralObject selectedObject = (ChimeraStructuralObject)selectionIter.next();
			TreePath path = (TreePath)selectedObject.getUserData();
			navigationTree.expandPath(path);
			navigationTree.addSelectionPath(path);
		}
		this.ignoreSelection = false;
	}

	private void selectCytoscapeNodes(HashMap modelsToSelect) {
		CyNetworkView networkView = chimeraObject.getNetworkView();
		CyNetwork network = networkView.getNetwork();

		Iterator modelIter = chimeraObject.getChimeraModels().iterator();
		while (modelIter.hasNext()) {
			ChimeraModel model = (ChimeraModel)modelIter.next();
			CyNode node = model.getStructure().node();
			NodeView nodeView = networkView.getNodeView(node);

			if (modelsToSelect.containsKey(model)) {
				System.out.println("Selecting node "+node.getIdentifier());
				// Get the current selection state
				if (!nodeView.isSelected()) {
					// Not selected, mark the fact that we're selecting it.
					selectedList.add(nodeView);
					nodeView.setSelected(true);
				} 
				nodeView.setSelectedPaint(java.awt.Color.GREEN);
			} else {
				System.out.println("Deselecting node "+node.getIdentifier());
				// Did we select it?
				if (nodeView.isSelected() && selectedList.contains(nodeView)) {
					// Yes, deselect it
					nodeView.setSelected(false);
					selectedList.remove(nodeView);
				} else {
					// No, just change the color
					nodeView.setSelectedPaint(java.awt.Color.YELLOW);
				}
			}
		}

		networkView.updateView();

	}

	private void initComponents() {
		this.setTitle("Cytoscape Molecular Structure Navigator");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		titleLabel = new JLabel();
		titleLabel.setFont(new Font("Dialog", 1, 14));
		titleLabel.setText("Cytoscape Molecular Structure Navigator");

		// Initialize the menus
		JMenuBar menuBar = new JMenuBar();

		// Chimera menu
		JMenu chimeraMenu = new JMenu("Chimera");
		addMenuItem(chimeraMenu, "Exit", EXIT);
		menuBar.add(chimeraMenu);

		// View menu
		JMenu viewMenu = new JMenu("View");
		addMenuItem(viewMenu, "Refresh", REFRESH);

		JMenu viewResidues = new JMenu("Residues as..");
		addMenuItem(viewResidues, "single letter", ChimeraResidue.SINGLE_LETTER);
		addMenuItem(viewResidues, "three letters", ChimeraResidue.THREE_LETTER);
		addMenuItem(viewResidues, "full name", ChimeraResidue.FULL_NAME);
		viewMenu.add(viewResidues);
		menuBar.add(viewMenu);

		// Select menu
		JMenu selectMenu = new JMenu("Select");
		addMenuSelectCommand(selectMenu, "Ligand", "select ligand");
		addMenuSelectCommand(selectMenu, "Ions", "select ions");
		addMenuSelectCommand(selectMenu, "Solvent", "select solvent");
		JMenu secondaryMenu = new JMenu("Secondary Structure");
		addMenuSelectCommand(secondaryMenu, "Helix", "select helix");
		addMenuSelectCommand(secondaryMenu, "Strand", "select strand");
		addMenuSelectCommand(secondaryMenu, "Turn", "select turn");
		selectMenu.add(secondaryMenu);
		addMenuSelectCommand(selectMenu, "Invert selection", "select invert");
		addMenuSelectCommand(selectMenu, "Clear selection", "~select");
		menuBar.add(selectMenu);

		setJMenuBar(menuBar);

		// Initialize the tree
		int modelCount = chimeraObject.getChimeraModels().size();
		DefaultMutableTreeNode rootNode = buildTree();
		treeModel = new DefaultTreeModel(rootNode);

		navigationTree = new JTree(treeModel);
		navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		navigationTree.addTreeSelectionListener(this);
		navigationTree.setShowsRootHandles(true);

		navigationTree.addMouseListener(new PopupMenuListener());

		JScrollPane treeView = new JScrollPane(navigationTree);

		//add(titleLabel);
		//add(treeView);
		setContentPane(treeView);
	}

	private JMenuItem addMenuItem (JMenu menu, String label, int command) {
		JMenuItem menuItem = new JMenuItem(label);
		{
			MenuActionListener va = new MenuActionListener(command);
			menuItem.addActionListener(va);
		}
		menu.add(menuItem);
		return menuItem;
	}

	private JMenuItem addMenuSelectCommand (JMenu menu, String label, String command) {
		JMenuItem menuItem = new JMenuItem(label);
		{
			MenuActionSelectListener va = new MenuActionSelectListener(command);
			menuItem.addActionListener(va);
		}
		menu.add(menuItem);
		return menuItem;
	}

	private DefaultMutableTreeNode buildTree() {
		int modelCount = chimeraObject.getChimeraModels().size();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(modelCount+" Open Chimera Models");
		TreePath rootPath = new TreePath(rootNode);

		TreePath path = null;
		DefaultMutableTreeNode model = null;

		// Add all of the Chimera models
		Iterator modelIter = chimeraObject.getChimeraModels().iterator();
		while (modelIter.hasNext()) {
			ChimeraModel chimeraModel = (ChimeraModel)modelIter.next();
			model = new DefaultMutableTreeNode(chimeraModel);
			path = rootPath.pathByAddingChild(model);
			chimeraModel.setUserData(path);
			addChainNodes(chimeraModel, model, path);
			rootNode.add(model);
		}
		return rootNode;
	}

	private void rebuildTree() {
		DefaultMutableTreeNode rootNode = buildTree();
		DefaultTreeModel model = (DefaultTreeModel)navigationTree.getModel();
		model.setRoot(rootNode);
		model.reload();
	}

	private void addChainNodes(ChimeraModel chimeraModel, 
														 DefaultMutableTreeNode treeModel,
														 TreePath treePath) {
		DefaultMutableTreeNode chain = null;
		TreePath chainPath = null;
		ChimeraChain chimeraChain = null; 

		Collection chainList = chimeraModel.getChains();

		if (chainList.size() == 0) {
			// No chains!  Just add the residues
			addResidues(chimeraModel.getResidues(), treeModel, treePath);	
			return;
		}

		Iterator chainIter = chainList.iterator();
		while (chainIter.hasNext()) {
			chimeraChain = (ChimeraChain)chainIter.next();
			chain = new DefaultMutableTreeNode(chimeraChain);
			chainPath = treePath.pathByAddingChild(chain);
			chimeraChain.setUserData(chainPath);
			addResidues(chimeraChain.getResidueList(), chain, chainPath);
			treeModel.add(chain);	
		}
	}

	private void addResidues(Collection residues, 
													 DefaultMutableTreeNode treeModel,
													 TreePath treePath) {
		DefaultMutableTreeNode residue = null;
		TreePath residuePath = null;
		Iterator resIter = residues.iterator();

		while (resIter.hasNext()) {
			ChimeraResidue res = (ChimeraResidue)resIter.next();
			res.setDisplayType(this.residueDisplay);
			residue = new DefaultMutableTreeNode(res);
			residuePath = treePath.pathByAddingChild(residue);
			res.setUserData(residuePath);
			treeModel.add(residue);
		}
	}	

	class MenuActionSelectListener extends AbstractAction {
		String command;

		public MenuActionSelectListener (String command) {
			this.command = command;
		}

		public void actionPerformed(ActionEvent ev) {
			chimeraObject.select(command);
			// Special case for clearing the selection
			if (command.equals("~select")) {
				navigationTree.clearSelection();
			}
		}
	}

	class MenuActionListener extends AbstractAction {
		int type;

		public MenuActionListener (int type) { this.type = type; }

		public void actionPerformed(ActionEvent ev) {
			if (type == EXIT) {
				chimeraObject.exit();
				setVisible(false);
				return;
			} else if (type == REFRESH) {
				chimeraObject.refresh();
			} else {
				residueDisplay = type;
			}
			rebuildTree();
		}
	}

	class PopupMenuListener implements MouseListener {
		public PopupMenuListener() {}

		public void mouseEntered(MouseEvent ev) {return;}
		public void mouseClicked(MouseEvent ev) {return;}
		public void mouseExited(MouseEvent ev) {return;}

		public void mousePressed(MouseEvent ev) {
			if (ev.isPopupTrigger())
				createPopupMenu(ev);
		}

		public void mouseReleased(MouseEvent ev) {
			if (ev.isPopupTrigger())
				createPopupMenu(ev);
		}

		private void createPopupMenu(MouseEvent ev) {
			// Create our popup menu -- depends on what's selected.  If we
			// have things selected at multiple levels of the hierarchy, see
			// if the menu was over a specific type
			TreePath overPath = navigationTree.getPathForLocation(ev.getX(), ev.getY());
			TreePath[] paths = navigationTree.getSelectionPaths();
			Object userObject = null;

			if (paths == null && overPath == null) {
				System.out.println("No context");
			} else if (paths == null) {
				paths = new TreePath[1];
				paths[0] = overPath;
			}

			if (overPath != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) overPath.getLastPathComponent();
				userObject = node.getUserObject();
			}

			// Create lists of types
			ArrayList models = new ArrayList();
			ArrayList chains = new ArrayList();
			ArrayList residues = new ArrayList();
			boolean modelContext = false;
			boolean chainContext = false;
			boolean residueContext = false;
			boolean multipleContexts = false;
			for (int i = 0; i < paths.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
				Object nodeInfo = node.getUserObject();
				if (nodeInfo.getClass() == ChimeraModel.class) {
					models.add(nodeInfo);
					modelContext = true;
					if (chainContext || residueContext) multipleContexts = true;
				} else if (nodeInfo.getClass() == ChimeraChain.class) {
					chains.add(nodeInfo);
					chainContext = true;
					if (modelContext || residueContext) multipleContexts = true;
				} else if (nodeInfo.getClass() == ChimeraResidue.class) {
					residues.add(nodeInfo);
					residueContext = true;
					if (modelContext || chainContext) multipleContexts = true;
				}
			}

			if (multipleContexts) {
				// See if overPath can help us
				if (modelContext && userObject.getClass() == ChimeraModel.class && models.contains(userObject)) {
					chainContext = false;
					chains.clear();
					residueContext = false;
					residues.clear();
					multipleContexts = false;
				} else if (chainContext && userObject.getClass() == ChimeraChain.class && chains.contains(userObject)) {
					modelContext = false;
					models.clear();
					residueContext = false;
					residues.clear();
					multipleContexts = false;
				} else if (residueContext && userObject.getClass() == ChimeraResidue.class && residues.contains(userObject)) {
					modelContext = false;
					models.clear();
					chainContext = false;
					chains.clear();
					multipleContexts = false;
				}
			}

			if (userObject != null) {
				// Get the component
				System.out.println("Clicked over "+userObject.toString());
			}

			if (modelContext) {
				System.out.println("Model context");
			}
			if (chainContext) {
				System.out.println("Chain context");
			}
			if (residueContext) {
				System.out.println("Residue context");
			}
			if (multipleContexts) {
				System.out.println("Generic context");
			}

			JPopupMenu menu = new ActionPopupMenu(chimeraObject,navigationTree,models,chains,residues);
			menu.setVisible(true);
			menu.show(navigationTree, ev.getX(), ev.getY());

		}
	}
}

