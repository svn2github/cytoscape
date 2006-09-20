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
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.*;

// Cytoscape imports
import cytoscape.Cytoscape;
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
import structureViz.actions.Chimera;
import structureViz.ui.PopupMenuListener;

public class ModelNavigatorDialog extends JDialog implements TreeSelectionListener {
	private Chimera chimeraObject;
	private boolean status;
	// These must be > ChimeraResidue.FULL_NAME
	private static final int COMMAND = 10;
	private static final int EXIT = 11;
	private static final int REFRESH = 12;
	private static final int CLEAR = 13;
	private static final int ALIGN = 14;
	private boolean ignoreSelection = false;
	private int residueDisplay = ChimeraResidue.THREE_LETTER;
	private List selectedObjects = null;

	// Dialog components
	private JLabel titleLabel;
	private JTree navigationTree;
	private ChimeraTreeModel treeModel;
	private JMenuItem alignMenu;

	public ModelNavigatorDialog (Frame parent, Chimera object) {
		super(parent, false);
		chimeraObject = object;
		initComponents();
		status = false;
		selectedObjects = new ArrayList();
	}

	public void modelChanged() {
		// Something significant changed in the model (new open/closed structure?)
		treeModel.reload();
		int modelCount = chimeraObject.getChimeraModels().size();
		if (modelCount > 1)
			alignMenu.setEnabled(true);
		else
			alignMenu.setEnabled(false);
		// Re-select the paths
		chimeraObject.updateSelection();
	}

	public void valueChanged(TreeSelectionEvent e) {
		// Reset the state of any selected Objects
		clearSelectedObjects();

		TreePath[] paths = navigationTree.getSelectionPaths();
		if (paths == null) return;

		DefaultMutableTreeNode node = null;
		String selSpec = "sel ";
		HashMap modelsToSelect = new HashMap();

		for (int i = 0; i < paths.length; i++) {
			node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
			ChimeraStructuralObject nodeInfo = (ChimeraStructuralObject)node.getUserObject();
			nodeInfo.setSelected(true);
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
		TreePath path = null;
		navigationTree.clearSelection();
		Iterator selectionIter = selectionList.iterator();
		this.ignoreSelection = true;
		while (selectionIter.hasNext()) {
			ChimeraStructuralObject selectedObject = (ChimeraStructuralObject)selectionIter.next();
			path = (TreePath)selectedObject.getUserData();
			navigationTree.addSelectionPath(path);
			navigationTree.makeVisible(path);
		}
		int row = navigationTree.getMaxSelectionRow();
		navigationTree.scrollRowToVisible(row);
		this.ignoreSelection = false;
	}

	// Private methods
	private void initComponents() {
		int modelCount = chimeraObject.getChimeraModels().size();
		this.setTitle("Cytoscape Molecular Structure Navigator");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Initialize the menus
		JMenuBar menuBar = new JMenuBar();

		// Chimera menu
		JMenu chimeraMenu = new JMenu("Chimera");
		alignMenu = addMenuItem(chimeraMenu, "Align", ALIGN, null);
		if (modelCount > 1)
			alignMenu.setEnabled(true);
		else
			alignMenu.setEnabled(false);
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
		navigationTree = new JTree();
		treeModel = new ChimeraTreeModel(chimeraObject, navigationTree);

		navigationTree.setModel(treeModel);
		navigationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		navigationTree.addTreeSelectionListener(this);
		navigationTree.setShowsRootHandles(false);

		navigationTree.setCellRenderer(new ObjectRenderer());

		navigationTree.addMouseListener(new PopupMenuListener(chimeraObject, navigationTree));

		JScrollPane treeView = new JScrollPane(navigationTree);

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

	private void clearSelectedObjects () {
		Iterator iter = selectedObjects.iterator();
		while (iter.hasNext()) {
			((ChimeraStructuralObject)iter.next()).setSelected(false);
		}
		selectedObjects.clear();
	}

	// Embedded classes
	private class MenuActionListener extends AbstractAction {
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
				if (chimeraObject.getAlignDialog() != null)
					chimeraObject.getAlignDialog().setVisible(false);
				return;
			} else if (type == REFRESH) {
				chimeraObject.refresh();
			} else if (type == ALIGN) {
				launchAlignDialog();
			} else {
				residueDisplay = type;
				treeModel.setResidueDisplay(type);
			}
			modelChanged();
		}

		private void launchAlignDialog()
		{
			AlignStructuresDialog alDialog;
			if (chimeraObject.getAlignDialog() != null) {
				alDialog = chimeraObject.getAlignDialog();
				alDialog.setVisible(false);
				alDialog.dispose();
			}
			List structureList = new ArrayList();
			Iterator iter = chimeraObject.getChimeraModels().iterator();
			while (iter.hasNext()) {
				ChimeraModel model = (ChimeraModel)iter.next();
				structureList.add(model.getStructure());
			}	
			// Bring up the dialog
			alDialog = new AlignStructuresDialog(Cytoscape.getDesktop(), chimeraObject, structureList);
			alDialog.pack();
			alDialog.setVisible(true);
			chimeraObject.setAlignDialog(alDialog);
		}
	}

	private class ObjectRenderer extends DefaultTreeCellRenderer {

		public ObjectRenderer() {
		}

		public Component getTreeCellRendererComponent( JTree tree, Object value,
																									boolean sel, boolean expanded,
																									boolean leaf, int row, boolean hasFocus) 
		{
			// Call the DefaultTreeCellRender's method to do most of the work
			super.getTreeCellRendererComponent(tree, value, sel,
                            						 expanded, leaf, row,
                            						 hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object object = node.getUserObject();
			// If we're a model, use the model color as a border
			if (object.getClass() == ChimeraModel.class) {
				Color color = ((ChimeraModel)object).getModelColor();
				Border border = new LineBorder(color);
				setBorder(border);
			} else {
				setBorder(null);
			}

			return this;
		}
	}
}

