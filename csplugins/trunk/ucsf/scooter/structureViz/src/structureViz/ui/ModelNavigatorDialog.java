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
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.WindowConstants.*;

import java.awt.*;
import java.awt.event.*;

import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraChain;

import structureViz.Chimera;

public class ModelNavigatorDialog extends JDialog implements TreeSelectionListener {
	private Chimera ChimeraObject;
	private boolean status;

	// Dialog components
	private JLabel titleLabel;
	private JTree navigationTree;
	private DefaultTreeModel treeModel;
	private int residueDisplay = ChimeraResidue.THREE_LETTER;

	public ModelNavigatorDialog (Frame parent, Chimera object) {
		super(parent, false);
		ChimeraObject = object;
		initComponents();
		status = false;
	}

	public void modelChanged() {
		// Something significant changed in the model (new open/closed structure?)
		rebuildTree();
	}

	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] paths = navigationTree.getSelectionPaths();
		DefaultMutableTreeNode node = null;
		String selSpec = "sel ";

		if (paths == null) return;

		for (int i = 0; i < paths.length; i++) {
			node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
			Object nodeInfo = node.getUserObject();
			if (nodeInfo.getClass() == ChimeraModel.class) {
				// Select the model
				selSpec = selSpec.concat(((ChimeraModel)nodeInfo).toSpec());
			} else if (nodeInfo.getClass() == ChimeraChain.class) {
				// Select the chain
				selSpec = selSpec.concat(((ChimeraChain)nodeInfo).toSpec());
			} else if (nodeInfo.getClass() == ChimeraResidue.class) {
				// Select the residue
				selSpec = selSpec.concat(((ChimeraResidue)nodeInfo).toSpec());
			}
			if (i < paths.length-1) selSpec.concat("|");
			// Add the model to be selected (if it's not already)
		}
		try {
			ChimeraObject.command(selSpec);
		} catch (java.io.IOException ex) {}
	}

	private void initComponents() {
		this.setTitle("Cytoscape Molecular Structure Navigator");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		titleLabel = new JLabel();
		titleLabel.setFont(new Font("Dialog", 1, 14));
		titleLabel.setText("Cytoscape Molecular Structure Navigator");

		// Initialize the menus
		JMenuBar menuBar = new JMenuBar();
		JMenu viewMenu = new JMenu("View");
		JMenu viewResidues = new JMenu("Residues as..");
		JMenuItem vrItem = new JMenuItem("single letter");
		{
			ViewActionListener va = new ViewActionListener(ChimeraResidue.SINGLE_LETTER);
			vrItem.addActionListener(va);
		}
		viewResidues.add(vrItem);
		vrItem = new JMenuItem("three letters");
		{
			ViewActionListener va = new ViewActionListener(ChimeraResidue.THREE_LETTER);
			vrItem.addActionListener(va);
		}
		viewResidues.add(vrItem);
		vrItem = new JMenuItem("full name");
		{
			ViewActionListener va = new ViewActionListener(ChimeraResidue.FULL_NAME);
			vrItem.addActionListener(va);
		}
		viewResidues.add(vrItem);
		viewMenu.add(viewResidues);
		menuBar.add(viewMenu);
		setJMenuBar(menuBar);

		// Initialize the tree
		int modelCount = ChimeraObject.getChimeraModels().size();
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

	private DefaultMutableTreeNode buildTree() {
		int modelCount = ChimeraObject.getChimeraModels().size();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(modelCount+" Open Chimera Models");

		DefaultMutableTreeNode model = null;

		// Add all of the Chimera models
		Iterator modelIter = ChimeraObject.getChimeraModels().iterator();
		while (modelIter.hasNext()) {
			ChimeraModel chimeraModel = (ChimeraModel)modelIter.next();
			model = new DefaultMutableTreeNode(chimeraModel);
			addChainNodes(chimeraModel, model);
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

	private void addChainNodes(ChimeraModel chimeraModel, DefaultMutableTreeNode treeModel) {
		DefaultMutableTreeNode chain = null;
		ChimeraChain chimeraChain = null; 

		Set chainNames = chimeraModel.getChainNames();

		if (chainNames.size() == 0) {
			// No chains!  Just add the residues
			addResidues(chimeraModel.getResidues(), treeModel);	
			return;
		}

		Iterator chainIter = chainNames.iterator();
		while (chainIter.hasNext()) {
			String chainName = (String)chainIter.next();
			chimeraChain = chimeraModel.getChain(chainName);
			chain = new DefaultMutableTreeNode(chimeraChain);
			addResidues(chimeraChain.getResidueList(), chain);
			treeModel.add(chain);	
		}
	}

	private void addResidues(ArrayList residues, DefaultMutableTreeNode treeModel) {
		DefaultMutableTreeNode residue = null;
		Iterator resIter = residues.iterator();

		while (resIter.hasNext()) {
			ChimeraResidue res = (ChimeraResidue)resIter.next();
			res.setDisplayType(this.residueDisplay);
			residue = new DefaultMutableTreeNode(res);
			treeModel.add(residue);
		}
	}	

	class ViewActionListener extends AbstractAction {
		int type;

		public ViewActionListener (int type) { this.type = type; }

		public void actionPerformed(ActionEvent e) {
			residueDisplay = type;
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

			JPopupMenu menu = new ActionPopupMenu(ChimeraObject,navigationTree,models,chains,residues);
			menu.setVisible(true);
			menu.show(navigationTree, ev.getX(), ev.getY());

		}
	}
}

