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
package structureViz.model;

// System imports
import java.util.Collection;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.Color;

// Cytoscape imports

// StructureViz imports
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraChain;

import structureViz.actions.Chimera;

/**
 * The ChimeraTreeModel class provides the underlying model
 * for the navigation tree in the ModelNavigatorDialog.
 *
 * @author scooter
 * @see ModelNavigatorDialog
	 */
public class ChimeraTreeModel extends DefaultTreeModel {
	private Chimera chimeraObject;
	private JTree navigationTree;
	private int residueDisplay = ChimeraResidue.THREE_LETTER;

	/**
	 * Constructor for the ChimeraTreeModel.
	 *
	 * @param chimeraObject the Chimera object that this tree represents
	 * @param tree the JTree used to display the object
	 * @see Chimera
	 */
	public ChimeraTreeModel (Chimera chimeraObject, JTree tree) {
		super(new DefaultMutableTreeNode());
		this.chimeraObject = chimeraObject;
		this.navigationTree = tree;
		DefaultMutableTreeNode rootNode = buildTree();
		this.setRoot(rootNode);
	}

	/**
	 * Set the display type for the residues.  The display type
	 * must be one of:
	 *
	 *	ChimeraResidue.THREE_LETTER
	 *	ChimeraResidue.SINGLE_LETTER
	 *	ChimeraResidue.FULL_NAME
	 *
	 * @param newDisplay the display type
	 * @see ChimeraResidue
	 */
	public void setResidueDisplay(int newDisplay) {
		this.residueDisplay = newDisplay;
	}
		
	/**
	 * This method is called to rebuild the tree model "from scratch"
	 */
	public void reload() {
		// First, rebuild the tree with the new data
		DefaultMutableTreeNode rootNode = buildTree();
		this.setRoot(rootNode);

		// Now let the superclass do all of the work
		super.reload();
	}

	/**
	 * Rebuild an existing tree
	 */
	public void rebuildTree() {
		DefaultMutableTreeNode rootNode = buildTree();
		DefaultTreeModel model = (DefaultTreeModel)navigationTree.getModel();
		model.setRoot(rootNode);
		model.reload();
	}

	/**
	 * build the tree from the current chimera data
	 *
	 * @return DefaultMutableTreeNode that represents the currently loaded Chimera models
	 */
	private DefaultMutableTreeNode buildTree() {
		int modelCount = chimeraObject.getChimeraModels().size();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(modelCount+" Open Chimera Models");
		TreePath rootPath = new TreePath(rootNode);

		TreePath path = null;
		DefaultMutableTreeNode model = null;

		// Add all of the Chimera models
		for (ChimeraModel chimeraModel: chimeraObject.getChimeraModels()) {
			model = new DefaultMutableTreeNode(chimeraModel);
			path = rootPath.pathByAddingChild(model);
			chimeraModel.setUserData(path);
			addChainNodes(chimeraModel, model, path);
			rootNode.add(model);
		}
		return rootNode;
	}

	/**
	 * add chains to a tree model
	 *
	 * @param chimeraModel the ChimeraModel to get the chains from
	 * @param treeModel the tree model to add the chains to
	 * @param treePath the tree path where the chains should be added
	 */
	private void addChainNodes(ChimeraModel chimeraModel, 
														 DefaultMutableTreeNode treeModel,
														 TreePath treePath) {
		DefaultMutableTreeNode chain = null;
		TreePath chainPath = null;

		// Get the list of chains
		Collection<ChimeraChain> chainList = chimeraModel.getChains();

		if (chainList.size() == 0) {
			// No chains!  Just add the residues
			addResidues(chimeraModel.getResidues(), treeModel, treePath);	
			return;
		}

		// Iterate over the chains and add the chain and all of
		// the chain's residues
		for (ChimeraChain chimeraChain: chainList) {
			chain = new DefaultMutableTreeNode(chimeraChain);
			chainPath = treePath.pathByAddingChild(chain);
			chimeraChain.setUserData(chainPath);
			addResidues(chimeraChain.getResidues(), chain, chainPath);
			treeModel.add(chain);	
		}
	}

	/**
	 * add residues to a tree model
	 *
	 * @param residues the residues to add
	 * @param treeModel the tree model to add the residues to
	 * @param treePath the tree path where the residues should be added
	 */
	private void addResidues(Collection<ChimeraResidue> residues, 
													 DefaultMutableTreeNode treeModel,
													 TreePath treePath) {
		DefaultMutableTreeNode residue = null;
		TreePath residuePath = null;

		// Iterate over all residues & add them to the tree
		for (ChimeraResidue res: residues) {
			res.setDisplayType(this.residueDisplay);
			residue = new DefaultMutableTreeNode(res);
			residuePath = treePath.pathByAddingChild(residue);
			res.setUserData(residuePath);
			treeModel.add(residue);
		}
	}	
}
