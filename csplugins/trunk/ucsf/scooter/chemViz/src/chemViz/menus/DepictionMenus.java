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

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;

import chemViz.model.Compound;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.tasks.CreateCompoundTableTask;
import chemViz.tasks.CreateNodeGraphicsTask;
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
	public DepictionMenus(JMenu menu, ChemInfoSettingsDialog settingsDialog, Object context) {
		super(settingsDialog);

		this.context = context;
		addDepictionMenus(menu, "Show compound table", "for", context, "Table");
		addDepictionMenus(menu, "Show structure window", "for", context, "Window");
		addDepictionMenus(menu, "Paint structures", "on", context, "Graphics");
		addDepictionMenus(menu, "Remove structures", "from", context, "ClearGraphics");
	}

	private void addDepictionMenus(JMenu parent, String menu, String prep, Object context, String type) {
		String contextString = menu+" "+prep;

		if (context == null) {
			JMenu depict = new JMenu(menu);
			addNodeDepictionMenus(depict, prep, type);
			addEdgeDepictionMenus(depict, prep, type);
			parent.add(depict);
		} else if (context instanceof NodeView) {
			JMenuItem item = addNodeContextDepictionMenu(contextString+" selected nodes", 
			                                             "selectedNodes"+type, (NodeView)context);
			parent.add(item);
		} else {
			if (type.equals("Graphics") || type.equals("ClearGraphics"))
				return;
			JMenuItem item = addEdgeContextDepictionMenu(contextString+" selected edges", 
			                                             "selectedEdges"+type, (EdgeView)context);
			parent.add(item);
		}
	}

	/**
	 * Builds the popup menu for edge depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param edgeContext the EdgeView this menu is for
	 */
	private void addEdgeDepictionMenus(JMenu menu, String prep, String type) {
		// Check and see if we have any edge attributes
		Collection<CyEdge> selectedEdges = Cytoscape.getCurrentNetwork().getSelectedEdges();
		if (type.equals("Graphics") || type.equals("ClearGraphics"))
			return;

		// Populating main menu
		JMenuItem item = buildMenuItem(prep+" all edges", "allEdges"+type);
		if (!settingsDialog.hasEdgeCompounds(null))
			item.setEnabled(false);
		menu.add(item);
		if (selectedEdges != null && selectedEdges.size() > 0) {
			item = buildMenuItem(prep+" selected edges", "selectedEdges"+type);
			if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
				item.setEnabled(false);
			}
			menu.add(item);
		}
	}

	private JMenuItem addEdgeContextDepictionMenu(String menu, String command, EdgeView context) {
		Collection<CyEdge> selectedEdges = Cytoscape.getCurrentNetwork().getSelectedEdges();
		JMenuItem item = buildMenuItem(menu, command);
		if (!settingsDialog.hasEdgeCompounds(selectedEdges)) {
			item.setEnabled(false);
		}
		return item;
	}

	/**
	 * Builds the popup menu for node depiction
	 * 
	 * @param menu the menu we're going add our items to
	 * @param nodeContext the NodeView this menu is for
	 */
	private void addNodeDepictionMenus(JMenu menu, String prep, String type) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		// Populating main menu
		JMenuItem item = buildMenuItem(prep+" all nodes", "allNodes"+type);
		if (!settingsDialog.hasNodeCompounds(null))
			item.setEnabled(false);
		menu.add(item);
		if (selectedNodes != null && selectedNodes.size() > 0) {
			item = buildMenuItem(prep+" selected nodes", "selectedNodes"+type);
			if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
				item.setEnabled(false);
			}
			menu.add(item);
		}
		return;
	}

	private JMenuItem addNodeContextDepictionMenu(String menu, String command, NodeView context) {
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
		JMenuItem item = buildMenuItem(menu, command);
		if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
			item.setEnabled(false);
		}
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		
		if (cmd.equals("selectedNodesWindow")) {
			// Bring up a grid-style pop-up
			createPopup(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog);
		} else if (cmd.equals("selectedNodesTable")) {
			// Bring up the compound table
			createTable(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog);
		} else if (cmd.equals("selectedNodesGraphics")) {
			createNodeGraphics((Collection<GraphObject>)Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog, false);
		} else if (cmd.equals("selectedNodesClearGraphics")) {
			createNodeGraphics((Collection<GraphObject>)Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog, true);
		} else if (cmd.equals("allNodesWindow")) {
			createPopup((Collection<GraphObject>)Cytoscape.getCurrentNetwork().nodesList(), settingsDialog);
		} else if (cmd.equals("allNodesTable")) {
			createTable((Collection<GraphObject>)Cytoscape.getCurrentNetwork().nodesList(), settingsDialog);
		} else if (cmd.equals("allNodesGraphics")) {
			createNodeGraphics((Collection<GraphObject>)Cytoscape.getCurrentNetwork().nodesList(), settingsDialog, false);
		} else if (cmd.equals("allNodesClearGraphics")) {
			createNodeGraphics((Collection<GraphObject>)Cytoscape.getCurrentNetwork().nodesList(), settingsDialog, true);
		} else if (cmd.equals("selectedEdgesWindow")) {
			// Bring up a grid display
			createPopup(Cytoscape.getCurrentNetwork().getSelectedEdges(), settingsDialog);
		} else if (cmd.equals("selectedEdgesTable")) {
			// Bring up the compound table
			createTable(Cytoscape.getCurrentNetwork().getSelectedEdges(), settingsDialog);
		} else if (cmd.equals("allEdgesTable")) {
			createTable((Collection<GraphObject>)Cytoscape.getCurrentNetwork().edgesList(), settingsDialog);
		} else if (cmd.equals("allEdgesWindow")) {
			createPopup((Collection<GraphObject>)Cytoscape.getCurrentNetwork().edgesList(), settingsDialog);
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
    CreatePopupTask loader = new CreatePopupTask(null, new ArrayList(selection), dialog, null, dialog.getMaxCompounds());
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

	private void createNodeGraphics(Collection<GraphObject>selection, ChemInfoSettingsDialog dialog, boolean remove) {
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CreateNodeGraphicsTask loader = null;
		loader = CreateNodeGraphicsTask.getCustomGraphicsTask(view);
		if (loader == null) {
			loader = new CreateNodeGraphicsTask(selection, settingsDialog, remove);
		} else {
			loader.setSelection(selection);
			loader.setRemove(remove);
		}
		TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
	}
}
