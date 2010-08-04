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

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;

import namedSelection.NamedSelection;

/**
 * The ObjectRenderer class is used to provide special rendering
 * capabilities for each row of the tree.
 */
class ObjectRenderer extends DefaultTreeCellRenderer {

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
		// Call the DefaultTreeCellRender's method to do most of the work
		Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
		if (userObject instanceof CyNode) {
			userObject = ((CyNode)userObject).getIdentifier();
		} else if (userObject instanceof CyNetwork) {
			CyNetwork network = (CyNetwork)userObject;
			if (Cytoscape.getNullNetwork().equals(network))
				userObject = NamedSelection.GLOBAL_GROUPS;
			else
				userObject = ((CyNetwork)userObject).getTitle();
			sel = false;
		} else if (userObject instanceof CyGroup) {
			userObject = ((CyGroup)userObject).getGroupNode().getIdentifier();
		} else {
			sel = false;
		}

		super.getTreeCellRendererComponent(tree, userObject, sel,
                           						 expanded, leaf, row,
                           						 hasFocus);
		return this;
	}
}
