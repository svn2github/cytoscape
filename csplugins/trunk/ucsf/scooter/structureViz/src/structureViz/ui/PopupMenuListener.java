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
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.awt.event.*;

// Cytoscape imports

// StructureViz imports
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraTreeModel;
import structureViz.ui.ActionPopupMenu;
import structureViz.actions.Chimera;

/**
 * This class is used to implement the listener for the
 * context menu on the ModelNavigatorDialog
 */
class PopupMenuListener implements MouseListener {
	JTree navigationTree;
	Chimera chimeraObject;

	/**
	 * Create a new PopupMenuListener
	 *
	 * @param object the Chimera object
	 * @param tree the JTree to put this menu listener on
	 */
	public PopupMenuListener(Chimera object, JTree tree) {
		this.navigationTree = tree;
		this.chimeraObject = object;
	}

	public void mouseEntered(MouseEvent ev) {return;}
	public void mouseClicked(MouseEvent ev) {return;}
	public void mouseExited(MouseEvent ev) {return;}

	/**
	 * Process a mouse pressed event
	 *
	 * @param ev the MouseEvent that triggered this event
	 */
	public void mousePressed(MouseEvent ev) {
		if (ev.isPopupTrigger())
			createPopupMenu(ev);
	}

	/**
	 * Process a mouse released event
	 *
	 * @param ev the MouseEvent that triggered this event
	 */
	public void mouseReleased(MouseEvent ev) {
		if (ev.isPopupTrigger())
			createPopupMenu(ev);
	}

	/**
	 * Create the popup menu
	 *
	 * @param ev the MouseEvent that triggered the popup menu
	 */
	private void createPopupMenu(MouseEvent ev) {
		// Create our popup menu -- depends on what's selected.  If we
		// have things selected at multiple levels of the hierarchy, see
		// if the menu was over a specific type
		TreePath overPath = navigationTree.getPathForLocation(ev.getX(), ev.getY());
		TreePath[] paths = navigationTree.getSelectionPaths();
		Object userObject = null;

		if (paths == null && overPath == null) {
			// System.out.println("No context");
			return;
		} else if (paths == null) {
			paths = new TreePath[1];
			paths[0] = overPath;
		}

		if (overPath != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) overPath.getLastPathComponent();
			userObject = node.getUserObject();
		}

		// Create lists of types
		ArrayList<ChimeraModel>modelList = new ArrayList();
		ArrayList<ChimeraChain> chainList = new ArrayList();
		ArrayList<ChimeraResidue>residueList = new ArrayList();
		int context = 0;

		for (int i = 0; i < paths.length; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
			Object nodeInfo = node.getUserObject();
			if (nodeInfo.getClass() == ChimeraModel.class) {
				modelList.add((ChimeraModel)nodeInfo);
				if (modelList.contains(userObject)) {
					if (context == 0)
						context = ActionPopupMenu.MODEL_CONTEXT;
					else
						context = ActionPopupMenu.GENERIC_CONTEXT;
				}
			} else if (nodeInfo.getClass() == ChimeraChain.class) {
				chainList.add((ChimeraChain)nodeInfo);
				if (chainList.contains(userObject)) {
					if (context == 0)
						context = ActionPopupMenu.CHAIN_CONTEXT;
					else
						context = ActionPopupMenu.GENERIC_CONTEXT;
				}
			} else if (nodeInfo.getClass() == ChimeraResidue.class) {
				residueList.add((ChimeraResidue)nodeInfo);
				if (residueList.contains(userObject)) {
					if (context == 0)
						context = ActionPopupMenu.RESIDUE_CONTEXT;
					else
						context = ActionPopupMenu.GENERIC_CONTEXT;
				}
			}
		}

		JPopupMenu menu = new ActionPopupMenu(chimeraObject,navigationTree,
																					modelList,chainList,residueList,context);
		menu.show(navigationTree, ev.getX(), ev.getY());
		menu.setVisible(true);

	}
}
