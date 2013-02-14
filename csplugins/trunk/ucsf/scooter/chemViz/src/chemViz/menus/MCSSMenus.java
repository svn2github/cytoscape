/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package chemViz.menus;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;

import chemViz.model.Compound;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.tasks.CreateMCSSTask;
import chemViz.tasks.CreatePopupTask;


/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class MCSSMenus extends ChemVizAbstractMenu implements ActionListener {

	Object context = null;
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public MCSSMenus(JMenu menu, ChemInfoSettingsDialog settingsDialog, Object context) {
		super(settingsDialog);

		this.context = context;

		if (context == null) {
			JMenu mcss = new JMenu("Show Maximum Common SubStructure (MCSS)");
			addNodeMCSSMenus(mcss, null);
			addEdgeMCSSMenus(mcss, null);
			menu.add(mcss);
		} else if (context instanceof NodeView) {
			addNodeMCSSMenus(menu, (NodeView)context);
		} else {
			addEdgeMCSSMenus(menu, (EdgeView)context);
		}

	}

	/**
	 * Builds the popup menu for edge depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param edgeContext the EdgeView this menu is for
	 */
	private void addEdgeMCSSMenus(JMenu menu, EdgeView edgeContext) {
		// Check and see if we have any edge attributes
		Collection<CyEdge> selectedEdges = Cytoscape.getCurrentNetwork().getSelectedEdges();

		if (edgeContext == null) {
			// Populating main menu
			JMenuItem item = buildMenuItem("of all edges", "allEdges");
			menu.add(item);
			if (selectedEdges != null && selectedEdges.size() > 0) {
				JMenuItem item2 = buildMenuItem("of selected edges", "selectedEdges");
				if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
					item2.setEnabled(false);
				}
				menu.add(item2);
			}
			return;
		}

		JMenuItem item = buildMenuItem("Show Maximum Common SubStructure of selected edges", "selectedEdges");
		if (selectedEdges == null) 
			selectedEdges = new ArrayList<CyEdge>(Collections.singletonList((CyEdge)edgeContext));

		if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
			item.setEnabled(false);
		}
		menu.add(item);
		return;
	}

	/**
	 * Builds the popup menu for node depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param nodeContext the NodeView this menu is for
	 */
	private void addNodeMCSSMenus(JMenu menu, NodeView nodeContext) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		if (nodeContext == null) {
			JMenuItem item = buildMenuItem("of all nodes", "allNodes");
			menu.add(item);
			if (selectedNodes != null && selectedNodes.size() > 0) {
				JMenuItem item2 = buildMenuItem("of selected nodes", "selectedNodes");
				if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
					item2.setEnabled(false);
				}
				menu.add(item2);
			}
			return;
		}

		// Populating popup menu
		JMenuItem item = buildMenuItem("Show Maximum Common SubStructure of selected nodes", "selectedNodes");
		if (selectedNodes == null) 
			selectedNodes = new ArrayList<CyNode>(Collections.singletonList((CyNode)nodeContext));

		if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
			item.setEnabled(false);
		}
		menu.add(item);

		if (selectedNodes.size() > 1 && settingsDialog.hasNodeCompounds(selectedNodes)) {
			item = buildMenuItem("Create MCSS group from selected nodes", "selectedNodesGroup");
			menu.add(item);
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		GraphObject obj = null;
		List<GraphObject> gObjList = new ArrayList<GraphObject>();
		CyAttributes attributes = null;
		boolean showResult = true;
		boolean createGroup = false;

		if (cmd.equals("selectedNodes")) {
			gObjList.addAll(Cytoscape.getCurrentNetwork().getSelectedNodes());
			attributes = Cytoscape.getNodeAttributes();
		} else if (cmd.equals("allNodes")) {
			gObjList.addAll(Cytoscape.getCurrentNetwork().nodesList());
			attributes = Cytoscape.getNodeAttributes();
		} else if (cmd.equals("selectedEdges")) {
			gObjList.addAll(Cytoscape.getCurrentNetwork().getSelectedEdges());
			attributes = Cytoscape.getEdgeAttributes();
		} else if (cmd.equals("allEdges")) {
			gObjList.addAll(Cytoscape.getCurrentNetwork().edgesList());
			attributes = Cytoscape.getEdgeAttributes();
		} else if (cmd.equals("selectedNodesGroup")) {
			gObjList.addAll(Cytoscape.getCurrentNetwork().getSelectedNodes());
			attributes = Cytoscape.getNodeAttributes();
			showResult = false;
			createGroup = true;
		}

		CreateMCSSTask task = new CreateMCSSTask(gObjList, attributes, settingsDialog, showResult, createGroup);
		TaskManager.executeTask(task, task.getDefaultTaskConfig());
	}
}
