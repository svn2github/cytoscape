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
import java.util.List;
import java.util.Properties;
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
	public MCSSMenus(JMenu menu, Properties systemProps, 
	       ChemInfoSettingsDialog settingsDialog, Object context) {
		super(systemProps, settingsDialog);

		this.context = context;
		JMenu clear = new JMenu(systemProps.getProperty("chemViz.menu.showmcss"));
		if (context == null) {
			addEdgeMCSSMenus(clear, "chemViz.menu.showmcss", null);
			addNodeMCSSMenus(clear, "chemViz.menu.showmcss", null);
		} else if (context instanceof NodeView) {
			addNodeMCSSMenus(clear, "chemViz.menu.showmcss", (NodeView)context);
		} else {
			addEdgeMCSSMenus(clear, "chemViz.menu.showmcss", (EdgeView)context);
		}
		menu.add(clear);

	}

	/**
	 * Builds the popup menu for edge depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param edgeContext the EdgeView this menu is for
	 */
	private void addEdgeMCSSMenus(JMenu menu, String prefix, EdgeView edgeContext) {
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
	private void addNodeMCSSMenus(JMenu menu, String prefix, NodeView nodeContext) {
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
		List<GraphObject> gObjList = new ArrayList<GraphObject>();
		CyAttributes attributes = null;

		if (cmd.equals("chemViz.menu.showmcss.thisNode") && (context instanceof NodeView)) {
			gObjList.add(((NodeView)context).getNode());
			attributes = Cytoscape.getNodeAttributes();
		} else if (cmd.equals("chemViz.menu.showmcss.thisEdge") && (context instanceof EdgeView)) {
			gObjList.add(((EdgeView)context).getEdge());
			attributes = Cytoscape.getEdgeAttributes();
		} else if (cmd.equals("chemViz.menu.showmcss.selectedNodes")) {
			gObjList.addAll(Cytoscape.getCurrentNetwork().getSelectedNodes());
			attributes = Cytoscape.getNodeAttributes();
		} else if (cmd.equals("chemViz.menu.showmcss.selectedEdges")) {
			gObjList.addAll(Cytoscape.getCurrentNetwork().getSelectedEdges());
			attributes = Cytoscape.getEdgeAttributes();
		} 

		CreateMCSSTask task = new CreateMCSSTask(gObjList, attributes, settingsDialog, true);
		TaskManager.executeTask(task, task.getDefaultTaskConfig());
	}
}
