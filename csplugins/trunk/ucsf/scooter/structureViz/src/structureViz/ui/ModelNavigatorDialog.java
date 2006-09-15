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
import structureViz.model.ChimeraTreeModel;
import structureViz.actions.CyChimera;
import structureViz.ui.PopupMenuListener;

import structureViz.Chimera;

public class ModelNavigatorDialog extends JDialog implements TreeSelectionListener {
	private Chimera chimeraObject;
	private boolean status;
	private static final int COMMAND = 0;
	private static final int EXIT = 1;
	private static final int REFRESH = 2;
	private static final int CLEAR = 3;
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
	}

	public void modelChanged() {
		// Something significant changed in the model (new open/closed structure?)
		treeModel.reload();
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

		CyChimera.selectCytoscapeNodes(chimeraObject.getNetworkView(), modelsToSelect, 
												 chimeraObject.getChimeraModels());
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

	// Private methods
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
		addMenuItem(chimeraMenu, "Exit", EXIT, null);
		menuBar.add(chimeraMenu);

		// View menu
		JMenu viewMenu = new JMenu("View");
		addMenuItem(viewMenu, "Refresh", REFRESH, null);

		JMenu viewResidues = new JMenu("Residues as..");
		addMenuItem(viewResidues, "single letter", ChimeraResidue.SINGLE_LETTER, null);
		addMenuItem(viewResidues, "three letters", ChimeraResidue.THREE_LETTER, null);
		addMenuItem(viewResidues, "full name", ChimeraResidue.FULL_NAME, null);
		viewMenu.add(viewResidues);
		menuBar.add(viewMenu);

		// Select menu
		JMenu selectMenu = new JMenu("Select");
		addMenuItem(selectMenu, "Ligand", COMMAND, "select ligand");
		addMenuItem(selectMenu, "Ions", COMMAND, "select ions");
		addMenuItem(selectMenu, "Solvent", COMMAND, "select solvent");
		JMenu secondaryMenu = new JMenu("Secondary Structure");
		addMenuItem(secondaryMenu, "Helix", COMMAND, "select helix");
		addMenuItem(secondaryMenu, "Strand", COMMAND, "select strand");
		addMenuItem(secondaryMenu, "Turn", COMMAND, "select turn");
		selectMenu.add(secondaryMenu);
		addMenuItem(selectMenu, "Invert selection", COMMAND, "select invert");
		addMenuItem(selectMenu, "Clear selection", CLEAR, null);
		menuBar.add(selectMenu);

		setJMenuBar(menuBar);

		// Initialize the tree
		int modelCount = chimeraObject.getChimeraModels().size();
		navigationTree = new JTree();
		treeModel = new ChimeraTreeModel(chimeraObject, navigationTree);

		navigationTree.setModel(treeModel);
		navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		navigationTree.addTreeSelectionListener(this);
		navigationTree.setShowsRootHandles(true);

		navigationTree.addMouseListener(new PopupMenuListener(chimeraObject, navigationTree));

		JScrollPane treeView = new JScrollPane(navigationTree);

		//add(titleLabel);
		//add(treeView);
		setContentPane(treeView);
	}

	private JMenuItem addMenuItem (JMenu menu, String label, int type, String command) {
		JMenuItem menuItem = new JMenuItem(label);
		{
			MenuActionListener va = new MenuActionListener(type, command);
			menuItem.addActionListener(va);
		}
		menu.add(menuItem);
		return menuItem;
	}

	// Embedded classes
	class MenuActionListener extends AbstractAction {
		int type;
		String command = null;

		public MenuActionListener (int type, String command) { 
			this.type = type; 
			this.command = command;
		}

		public void actionPerformed(ActionEvent ev) {
			if (type == COMMAND) {
				chimeraObject.select(command);
			} else if (type == CLEAR) {
				chimeraObject.select("~select");
				navigationTree.clearSelection();
			} else if (type == EXIT) {
				chimeraObject.exit();
				setVisible(false);
				return;
			} else if (type == REFRESH) {
				chimeraObject.refresh();
			} else {
				residueDisplay = type;
			}
			modelChanged();
		}
	}
}

