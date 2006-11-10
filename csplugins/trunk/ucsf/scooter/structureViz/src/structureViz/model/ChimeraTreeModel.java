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

public class ChimeraTreeModel extends DefaultTreeModel {
	private Chimera chimeraObject;
	private JTree navigationTree;
	private int residueDisplay = ChimeraResidue.THREE_LETTER;

	public ChimeraTreeModel (Chimera chimeraObject, JTree tree) {
		super(new DefaultMutableTreeNode());
		this.chimeraObject = chimeraObject;
		this.navigationTree = tree;
		DefaultMutableTreeNode rootNode = buildTree();
		this.setRoot(rootNode);
	}

	public void setResidueDisplay(int newDisplay) {
		this.residueDisplay = newDisplay;
	}
		
	public void reload() {
		// First, rebuild the tree with the new data
		DefaultMutableTreeNode rootNode = buildTree();
		this.setRoot(rootNode);

		// Now let the superclass do all of the work
		super.reload();
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

	public void rebuildTree() {
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
			addResidues(chimeraChain.getResidues(), chain, chainPath);
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
}
