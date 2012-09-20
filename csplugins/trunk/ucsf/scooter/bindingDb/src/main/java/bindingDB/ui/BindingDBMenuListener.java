/* vim: set ts=2: */
/**
 * Copyright (c) 2012 The Regents of the University of California.
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
package bindingDB.ui;

import java.util.Set;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.logger.CyLogger;

import bindingDB.actions.AnnotateNetworkAction;
import bindingDB.actions.AnnotateSelectedNodesAction;
import bindingDB.actions.ColorNetworkAction;
import bindingDB.actions.ExpandNodesAction;
import bindingDB.actions.AboutAction;

public class BindingDBMenuListener implements MenuListener {
	private CyLogger logger = null;
	private NodeView overNode = null;

	public BindingDBMenuListener (NodeView nv, CyLogger logger) {
		this.overNode = nv;
		this.logger = logger;
	}

	public void menuCanceled (MenuEvent e) {};
	public void menuDeselected (MenuEvent e) {};
	
	public void menuSelected (MenuEvent e)
	{
		JMenu m = (JMenu)e.getSource();

		// Clear the menu
		m.removeAll();

		// We can always annotate
		JMenuItem annotate = new JMenuItem(new AnnotateNetworkAction(logger));
		m.add(annotate);

		JMenuItem annotateSelected = new JMenuItem(new AnnotateSelectedNodesAction(logger));
		if (Cytoscape.getCurrentNetwork().getSelectedNodes().size() == 0)
			annotateSelected.setEnabled(false);
		m.add(annotateSelected);

		JMenuItem color = new JMenuItem(new ColorNetworkAction(logger));
		if (!isAnnotated(Cytoscape.getCurrentNetwork()))
			color.setEnabled(false);

		m.add(color);

		JMenuItem expand = new JMenuItem(new ExpandNodesAction(logger));
		if (!isAnnotated(Cytoscape.getCurrentNetwork()) || !nodesSelected(Cytoscape.getCurrentNetwork()))
			expand.setEnabled(false);
		m.add(expand);

		m.addSeparator();
		JMenuItem about = new JMenuItem(new AboutAction(logger));
		m.add(about);
	}

	boolean isAnnotated(CyNetwork network) {
		return true;
	}

	boolean nodesSelected(CyNetwork network) {
		Set nodeSet = network.getSelectedNodes();
		if (nodeSet != null && nodeSet.size() > 0)
			return true;
		return false;
	}
}
