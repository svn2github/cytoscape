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

import giny.model.Node;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;

import chemViz.model.Compound;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.tasks.CreateNodeGraphicsTask;


/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class NodeGraphicsMenus extends ChemVizAbstractMenu implements ActionListener {

	private HashMap<CyNetworkView, CreateNodeGraphicsTask> customGraphicsMap = new HashMap();
	private NodeView nodeContext;
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public NodeGraphicsMenus(JMenu menu, Properties systemProps, ChemInfoSettingsDialog settingsDialog, 
	                         NodeView nodeContext) {

		super(systemProps, settingsDialog);
		this.nodeContext = nodeContext;
		addNodeGraphicsMenus(menu);
		addClearGraphicsMenus(menu);
	}

	private void addNodeGraphicsMenus(JMenu menu) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		if (nodeContext == null) {
			// Populating main menu
			JMenuItem item = buildMenuItem("chemViz.menu.nodegraphics.allNodes",
				                            "chemViz.menu.nodegraphics.allNodes");
			if (!settingsDialog.hasNodeCompounds(null))
				item.setEnabled(false);
			menu.add(item);
			if (selectedNodes != null && selectedNodes.size() > 0) {
				item = buildMenuItem("chemViz.menu.nodegraphics.selectedNodes",
			  	                   "chemViz.menu.nodegraphics.selectedNodes");
				if (!settingsDialog.hasNodeCompounds(selectedNodes))
					item.setEnabled(false);
				menu.add(item);
			}
			return;
		}

		// Populating popup menu
		JMenu depict = new JMenu(systemProps.getProperty("chemViz.menu.nodegraphics"));

		depict.add(buildMenuItem("chemViz.menu.nodegraphics.thisNode",
		                         "chemViz.menu.nodegraphics.thisNode"));

		if (selectedNodes == null) selectedNodes = new ArrayList();

		if (!selectedNodes.contains(nodeContext.getNode()))
			selectedNodes.add((CyNode)nodeContext.getNode());

		depict.add(buildMenuItem("chemViz.menu.nodegraphics.selectedNodes",
		                         "chemViz.menu.nodegraphics.selectedNodes"));

		if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
			depict.setEnabled(false);
		}
		menu.add(depict);

		return;
	}

	/**
	 * Builds the popup menu for the commands to clear the node graphics
	 * 
	 * @param menu the menu we're going add our items to
	 * @param nodeContext the NodeView this menu is for
	 */
	private void addClearGraphicsMenus(JMenu menu) {
		// Check and see if we have any node attributes
		Collection<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		if (nodeContext == null) {
			// Populating main menu
			JMenuItem item = buildMenuItem("chemViz.menu.clearnodegraphics.allNodes",
				                            "chemViz.menu.clearnodegraphics.allNodes");
			if (!settingsDialog.hasNodeCompounds(null))
				item.setEnabled(false);
			menu.add(item);
			if (selectedNodes != null && selectedNodes.size() > 0) {
				item = buildMenuItem("chemViz.menu.clearnodegraphics.selectedNodes",
			  	                   "chemViz.menu.clearnodegraphics.selectedNodes");
				if (!settingsDialog.hasNodeCompounds(selectedNodes))
					item.setEnabled(false);
				menu.add(item);
			}
			return;
		}

		// Populating popup menu
		JMenu depict = new JMenu(systemProps.getProperty("chemViz.menu.clearnodegraphics"));

		depict.add(buildMenuItem("chemViz.menu.clearnodegraphics.thisNode",
		                         "chemViz.menu.clearnodegraphics.thisNode"));

		if (selectedNodes == null) selectedNodes = new ArrayList();

		if (!selectedNodes.contains(nodeContext.getNode()))
			selectedNodes.add((CyNode)nodeContext.getNode());

		depict.add(buildMenuItem("chemViz.menu.clearnodegraphics.selectedNodes",
		                         "chemViz.menu.clearnodegraphics.selectedNodes"));

		if (!settingsDialog.hasNodeCompounds(selectedNodes)) {
			depict.setEnabled(false);
		}
		menu.add(depict);

		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		
		if (cmd.equals("chemViz.menu.nodegraphics.thisNode")) {
			// Bring up the popup-style of depiction
			List<Node>nl = new ArrayList();
			nl.add(nodeContext.getNode());
			addNodeGraphics(nl, settingsDialog);
		} else if (cmd.equals("chemViz.menu.nodegraphics.selectedNodes")) {
			// Bring up the compound table
			addNodeGraphics(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog);
		} else if (cmd.equals("chemViz.menu.nodegraphics.allNodes")) {
			addNodeGraphics(Cytoscape.getCurrentNetwork().nodesList(), settingsDialog);
		} else if (cmd.equals("chemViz.menu.clearnodegraphics.thisNode")) {
			// Bring up the popup-style of depiction
			List<Node>nl = new ArrayList();
			nl.add(nodeContext.getNode());
			removeNodeGraphics(nl, settingsDialog);
		} else if (cmd.equals("chemViz.menu.clearnodegraphics.selectedNodes")) {
			// Bring up the compound table
			removeNodeGraphics(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog);
		} else if (cmd.equals("chemViz.menu.clearnodegraphics.allNodes")) {
			removeNodeGraphics((List<Node>)null, settingsDialog);
		}
	}
	
	/**
 	 * Add 2D depictions as custom node graphics
 	 *
 	 * @param selection the nodes we're going to pull the compounds from
 	 * @param dialog the settings dialog
 	 */
	private void addNodeGraphics(Collection<Node>selection, ChemInfoSettingsDialog dialog) {
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CreateNodeGraphicsTask loader = null;
		loader = CreateNodeGraphicsTask.getCustomGraphicsTask(view);
		if (loader == null) {
			loader = new CreateNodeGraphicsTask(selection, settingsDialog, false);
		} else {
			loader.setSelection(selection);
			loader.setRemove(false);
		}
		TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
	}

	/**
 	 * Remove 2D depictions from custom node graphics
 	 *
 	 * @param selection the nodes we're going to remove custom graphics from.  If this is null,
 	 * all custom graphics are cleared
 	 * @param dialog the settings dialog
 	 */
	private void removeNodeGraphics(Collection<Node>selection, ChemInfoSettingsDialog dialog) {
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		CreateNodeGraphicsTask loader = null;
		loader = CreateNodeGraphicsTask.getCustomGraphicsTask(view);
		if (loader == null) {
			loader = new CreateNodeGraphicsTask(selection, settingsDialog, true);
		} else {
			loader.setSelection(selection);
			loader.setRemove(true);
		}
		TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
	}

}
