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
import java.util.Properties;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;

import chemViz.model.Compound;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.tasks.CreateCompoundTableTask;
import chemViz.tasks.CreatePopupTask;


/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class StructureMenus extends ChemVizAbstractMenu implements ActionListener {

	Object context = null;
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public StructureMenus(JMenu menu, Properties systemProps, 
	       ChemInfoSettingsDialog settingsDialog, Object context) {
		super(systemProps, settingsDialog);

		this.context = context;
		JMenu clear = new JMenu(systemProps.getProperty("chemViz.menu.clearstructures"));
		if (context == null) {
			addEdgeStructureMenus(clear, "chemViz.menu.clearstructures", null);
			addNodeStructureMenus(clear, "chemViz.menu.clearstructures", null);
		} else if (context instanceof NodeView) {
			addNodeStructureMenus(clear, "chemViz.menu.clearstructures", (NodeView)context);
		} else {
			addEdgeStructureMenus(clear, "chemViz.menu.clearstructures", (EdgeView)context);
		}
		menu.add(clear);

		JMenu reload = new JMenu(systemProps.getProperty("chemViz.menu.reloadstructures"));
		if (context == null) {
			addEdgeStructureMenus(reload, "chemViz.menu.reloadstructures", null);
			addNodeStructureMenus(reload, "chemViz.menu.reloadstructures", null);
		} else if (context instanceof NodeView) {
			addNodeStructureMenus(reload, "chemViz.menu.reloadstructures", (NodeView)context);
		} else {
			addEdgeStructureMenus(reload, "chemViz.menu.reloadstructures", (EdgeView)context);
		}
		menu.add(reload);
	}

	/**
	 * Builds the popup menu for edge depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param edgeContext the EdgeView this menu is for
	 */
	private void addEdgeStructureMenus(JMenu menu, String prefix, EdgeView edgeContext) {
		// Check and see if we have any edge attributes
		Collection<CyEdge> selectedEdges = Cytoscape.getCurrentNetwork().getSelectedEdges();

		if (edgeContext == null) {
			// Populating main menu
			if (selectedEdges != null && selectedEdges.size() > 0) {
				JMenuItem item2 = buildMenuItem(prefix+".selectedEdges", prefix+".selectedEdges");
				if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
					item2.setEnabled(false);
				}
				menu.add(item2);
			}
			return;
		}

		menu.add(buildMenuItem(prefix+".thisEdge",
		                         prefix+".thisEdge"));

		if (selectedEdges == null) selectedEdges = new ArrayList();

		if (!selectedEdges.contains(edgeContext.getEdge()))
			selectedEdges.add((CyEdge)edgeContext.getEdge());

		menu.add(buildMenuItem(prefix+".selectedEdges",
		                         prefix+".selectedEdges"));

		if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
			menu.setEnabled(false);
		}
		return;
	}

	/**
	 * Builds the popup menu for node depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param nodeContext the NodeView this menu is for
	 */
	private void addNodeStructureMenus(JMenu menu, String prefix, NodeView nodeContext) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		if (nodeContext == null) {
			// Populating main menu
			JMenuItem item = buildMenuItem(prefix+".all", prefix+".all");
			if (!settingsDialog.hasNodeCompounds(null))
				item.setEnabled(false);

			menu.add(item);
			if (selectedNodes != null && selectedNodes.size() > 0) {
				JMenuItem item2 = buildMenuItem(prefix+".selectedNodes", prefix+".selectedNodes");
				if (!settingsDialog.hasNodeCompounds(selectedNodes))
					item2.setEnabled(false);
				menu.add(item2);
			}
			return;
		}

		// Populating popup menu
		menu.add(buildMenuItem(prefix+".thisNode", prefix+".thisNode"));

		if (selectedNodes == null) selectedNodes = new ArrayList();

		if (!selectedNodes.contains(nodeContext.getNode()))
			selectedNodes.add((CyNode)nodeContext.getNode());

		menu.add(buildMenuItem(prefix+".selectedNodes", prefix+".selectedNodes"));

		if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
			menu.setEnabled(false);
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
		if (context instanceof NodeView)
			obj = ((NodeView)context).getNode();
		else
			obj = ((EdgeView)context).getEdge();
		
		if (cmd.equals("chemViz.menu.clearstructures.thisNode")) {
			Compound.clearStructures(obj);
		} else if (cmd.equals("chemViz.menu.clearstructures.selectedNodes")) {
			Set<CyNode> nodes = (Set<CyNode>)Cytoscape.getCurrentNetwork().getSelectedNodes();
			for (CyNode node: nodes) Compound.clearStructures(node);
		} else if (cmd.equals("chemViz.menu.clearstructures.all")) {
			Compound.clearStructures(null);
		} else if (cmd.equals("chemViz.menu.clearstructures.thisEdge")) {
			Compound.clearStructures(obj);
		} else if (cmd.equals("chemViz.menu.clearstructures.selectedEdges")) {
			Set<CyEdge> edges = (Set<CyEdge>)Cytoscape.getCurrentNetwork().getSelectedEdges();
			for (CyEdge edge: edges) Compound.clearStructures(edge);
		} else if (cmd.equals("chemViz.menu.reloadstructures.thisNode")) {
			Compound.reloadStructures(obj);
		} else if (cmd.equals("chemViz.menu.reloadstructures.selectedNodes")) {
			Set<CyNode> nodes = (Set<CyNode>)Cytoscape.getCurrentNetwork().getSelectedNodes();
			for (CyNode node: nodes) Compound.reloadStructures(node);
		} else if (cmd.equals("chemViz.menu.reloadstructures.all")) {
			Compound.reloadStructures(null);
		} else if (cmd.equals("chemViz.menu.reloadstructures.thisEdge")) {
			Compound.reloadStructures(obj);
		} else if (cmd.equals("chemViz.menu.reloadstructures.selectedEdges")) {
			Set<CyEdge> edges = (Set<CyEdge>)Cytoscape.getCurrentNetwork().getSelectedEdges();
			for (CyEdge edge: edges) Compound.reloadStructures(edge);
		} 
	}
}
