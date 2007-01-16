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
package csplugins.layout.algorithms.bioLayout;

// Cytoscape imports
import cytoscape.*;
import cytoscape.layout.*;
import cytoscape.plugin.*;
import cytoscape.view.*;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.ui.JTask;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

import csplugins.layout.AbstractLayout;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class EdgeWeightedLayoutMenu extends JMenu
{
	public EdgeWeightedLayoutMenu() {

		super("Edge-weighted");

		JMenu kkMenu = new JMenu("Edge-weighted Spring Embedded");
		kkMenu.addMenuListener(new WeightsMenuListener(BioLayoutActionListener.KK_ALGORITHM));
		this.add(kkMenu);

		JMenu frMenu = new JMenu("bioLayout");
		frMenu.addMenuListener(new WeightsMenuListener(BioLayoutActionListener.FR_ALGORITHM));
		this.add(frMenu);
	}

	protected class WeightsMenuListener implements MenuListener
	{
		private int algorithm;

		public WeightsMenuListener(int algorithm)
		{ 
			this.algorithm = algorithm; 
		}
		public void menuCanceled (MenuEvent e) {};
		public void menuDeselected (MenuEvent e) {};
		public void menuSelected (MenuEvent e)
		{
			JMenu m = (JMenu)e.getSource();
			// Clear the menu
			Component[] subMenus = m.getMenuComponents();
			for (int i = 0; i < subMenus.length; i++) { m.remove(subMenus[i]); }

			// Figure out if we have any nodes selected
			CyNetwork network = Cytoscape.getCurrentNetwork();
			Set selectedNodes = network.getSelectedNodes();
			if (selectedNodes != null && selectedNodes.size() > 0) {
				// We have some nodes selected, provide as an option
				JMenu allNodes = new JMenu("All Nodes");
				addWeightsMenus(allNodes, false);
				m.add(allNodes);
				JMenu selNodes = new JMenu("Selected Nodes Only");
				addWeightsMenus(selNodes, true);
				m.add(selNodes);
			} else {
				addWeightsMenus(m, false);
			}
		}

		private void addWeightsMenus(JMenu m, boolean selectedOnly) {
			// Add a sort-of default (No weight)
			JMenuItem unWeightedItem = new JMenuItem(BioLayoutActionListener.UNWEIGHTEDATTRIBUTE);
			{
				BioLayoutActionListener bl = new BioLayoutActionListener(algorithm,selectedOnly);
				unWeightedItem.addActionListener(bl);
			}
			m.add(unWeightedItem);

			// Get the edge attributes
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

			String[] attList = edgeAttributes.getAttributeNames();
			for (int i = 0; i < attList.length; i++) {
				byte type = edgeAttributes.getType(attList[i]);
				if (type == CyAttributes.TYPE_FLOATING || 
				    type == CyAttributes.TYPE_INTEGER) {
					JMenuItem newItem = new JMenuItem(attList[i]);
					{
						BioLayoutActionListener bl = new BioLayoutActionListener(algorithm,selectedOnly);
						newItem.addActionListener(bl);
					}
					m.add(newItem);
				}
			}
		}
	}
}

