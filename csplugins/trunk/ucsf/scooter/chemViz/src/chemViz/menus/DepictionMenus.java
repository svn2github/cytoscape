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
public class DepictionMenus extends ChemVizAbstractMenu implements ActionListener {

	Object context = null;
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public DepictionMenus(JMenu menu, Properties systemProps, 
	       ChemInfoSettingsDialog settingsDialog, Object context) {
		super(systemProps, settingsDialog);

		this.context = context;
		JMenu depict = new JMenu(systemProps.getProperty("chemViz.menu.2ddepiction"));
		if (context == null) {
			addEdgeDepictionMenus(depict, null);
			addNodeDepictionMenus(depict, null);
		} else if (context instanceof NodeView) {
			addNodeDepictionMenus(depict, (NodeView)context);
		} else {
			addEdgeDepictionMenus(depict, (EdgeView)context);
		}
		menu.add(depict);
	}

	/**
	 * Builds the popup menu for edge depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param edgeContext the EdgeView this menu is for
	 */
	private void addEdgeDepictionMenus(JMenu menu, EdgeView edgeContext) {
		// Check and see if we have any edge attributes
		Collection<CyEdge> selectedEdges = Cytoscape.getCurrentNetwork().getSelectedEdges();

		if (edgeContext == null) {
			// Populating main menu
			JMenuItem item = buildMenuItem("chemViz.menu.2ddepiction.allEdges",
				                             "chemViz.menu.2ddepiction.allEdges");
			if (!settingsDialog.hasEdgeCompounds(null))
				item.setEnabled(false);
			menu.add(item);
			if (selectedEdges != null && selectedEdges.size() > 1) {
				item = buildMenuItem("chemViz.menu.2ddepiction.selEdges",
			  	                   "chemViz.menu.2ddepiction.selEdges");
				if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
					item.setEnabled(false);
				}
				menu.add(item);
			} else if (selectedEdges != null && selectedEdges.size() == 1) {
				item = buildMenuItem("chemViz.menu.2ddepiction.selectedEdges",
			  	                   "chemViz.menu.2ddepiction.selectedEdges");
				if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
					item.setEnabled(false);
				}
				menu.add(item);
			}
			return;
		}

		menu.add(buildMenuItem("chemViz.menu.2ddepiction.thisEdge",
		                         "chemViz.menu.2ddepiction.thisEdge"));

		if (selectedEdges == null) selectedEdges = new ArrayList();

		if (!selectedEdges.contains(edgeContext.getEdge()))
			selectedEdges.add((CyEdge)edgeContext.getEdge());

		if (selectedEdges.size() > 1) {
			menu.add(buildMenuItem("chemViz.menu.2ddepiction.selEdges",
		 		                       "chemViz.menu.2ddepiction.selEdges"));
		}

		menu.add(buildMenuItem("chemViz.menu.2ddepiction.selectedEdges",
		                         "chemViz.menu.2ddepiction.selectedEdges"));

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
	private void addNodeDepictionMenus(JMenu menu, NodeView nodeContext) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		if (nodeContext == null) {
			// Populating main menu
			JMenuItem item = buildMenuItem("chemViz.menu.2ddepiction.allNodes",
				                            "chemViz.menu.2ddepiction.allNodes");
			if (!settingsDialog.hasNodeCompounds(null))
				item.setEnabled(false);
			menu.add(item);
			if (selectedNodes != null && selectedNodes.size() > 1) {
				item = buildMenuItem("chemViz.menu.2ddepiction.selNodes",
			  	                   "chemViz.menu.2ddepiction.selNodes");
				if (!settingsDialog.hasNodeCompounds(selectedNodes))
					item.setEnabled(false);
				menu.add(item);
			} else if (selectedNodes != null && selectedNodes.size() == 1) {
				item = buildMenuItem("chemViz.menu.2ddepiction.selectedNodes",
			  	                   "chemViz.menu.2ddepiction.selectedNodes");
				if (!settingsDialog.hasNodeCompounds(selectedNodes))
					item.setEnabled(false);
				menu.add(item);
			}
			return;
		}

		// Populating popup menu
		menu.add(buildMenuItem("chemViz.menu.2ddepiction.thisNode",
		                         "chemViz.menu.2ddepiction.thisNode"));

		if (selectedNodes == null) selectedNodes = new ArrayList();

		if (!selectedNodes.contains(nodeContext.getNode()))
			selectedNodes.add((CyNode)nodeContext.getNode());

		if (selectedNodes.size() > 1) {
			menu.add(buildMenuItem("chemViz.menu.2ddepiction.selNodes",
		 		                       "chemViz.menu.2ddepiction.selNodes"));
		}

		menu.add(buildMenuItem("chemViz.menu.2ddepiction.selectedNodes",
		                         "chemViz.menu.2ddepiction.selectedNodes"));

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
		
		if (cmd.equals("chemViz.menu.2ddepiction.thisNode")) {
			// Bring up the popup-style of depiction
			createPopup(context, settingsDialog);
		} else if (cmd.equals("chemViz.menu.2ddepiction.selNodes")) {
			// Bring up a grid-style pop-up
			createPopup(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog);
		} else if (cmd.equals("chemViz.menu.2ddepiction.selectedNodes")) {
			// Bring up the compound table
			createTable(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog);
		} else if (cmd.equals("chemViz.menu.2ddepiction.allNodes")) {
			createTable((Collection<GraphObject>)Cytoscape.getCurrentNetwork().nodesList(), settingsDialog);
		} else if (cmd.equals("chemViz.menu.2ddepiction.thisEdge")) {
			createPopup(context, settingsDialog);
		} else if (cmd.equals("chemViz.menu.2ddepiction.selEdges")) {
			// Bring up a grid display
			createPopup(Cytoscape.getCurrentNetwork().getSelectedEdges(), settingsDialog);
		} else if (cmd.equals("chemViz.menu.2ddepiction.selectedEdges")) {
			// Bring up the compound table
			createTable(Cytoscape.getCurrentNetwork().getSelectedEdges(), settingsDialog);
		} else if (cmd.equals("chemViz.menu.2ddepiction.allEdges")) {
			createTable((Collection<GraphObject>)Cytoscape.getCurrentNetwork().edgesList(), settingsDialog);
		}
	}
	
	/**
 	 * Create a 2D popup dialog for this node or edge
 	 *
 	 * @param view the nodeView or edgeView we're going to pull the compounds from
 	 * @param dialog the settings dialog
 	 */
	private void createPopup(Object view, ChemInfoSettingsDialog dialog) {
    CreatePopupTask loader = new CreatePopupTask(view, dialog, dialog.getMaxCompounds());
		TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
  }

	/**
 	 * Create a 2D popup dialog from a group of nodes or edges
 	 *
 	 * @param selection the currently selected nodes or edges
 	 * @param dialog the settings dialog
 	 */
	private void createPopup(Collection<GraphObject>selection, ChemInfoSettingsDialog dialog) {
    CreatePopupTask loader = new CreatePopupTask(new ArrayList(selection), dialog, dialog.getMaxCompounds());
		TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
	}

	/**
 	 * Create a compound table for this group of nodes or edges
 	 *
 	 * @param selection the nodes or edges we're going to pull the compounds from
 	 * @param dialog the settings dialog
 	 */
	private void createTable(Collection<GraphObject>selection, ChemInfoSettingsDialog dialog) {
		CreateCompoundTableTask loader = new CreateCompoundTableTask(selection, dialog, dialog.getMaxCompounds());
		TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
	}

}
