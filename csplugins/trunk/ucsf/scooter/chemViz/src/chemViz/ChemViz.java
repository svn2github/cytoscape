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

package chemViz;

import giny.model.Node;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Properties;

import javax.swing.JMenu;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import giny.model.GraphObject;

import chemViz.commands.ChemVizCommandHandler;
import chemViz.menus.ChemVizMenu;
import chemViz.menus.ChemVizContextMenu;
import chemViz.tasks.CreateNodeGraphicsTask;
import chemViz.ui.ChemInfoSettingsDialog;

/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class ChemViz extends CytoscapePlugin implements PropertyChangeListener {
	
	static public CyLogger logger = CyLogger.getLogger(ChemViz.class);

	private ChemInfoSettingsDialog settingsDialog = null; 
	private boolean sessionLock = false;
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public ChemViz() {
		try {
			settingsDialog = new ChemInfoSettingsDialog();

			JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
			JMenu menu = new JMenu("Cheminformatics Tools");
			menu.addMenuListener(new ChemVizMenu(settingsDialog));
			pluginMenu.add(menu);
		
		} catch (Exception e) {
			logger.error("Unable to initialize menus: "+e.getMessage(), e);
		}

		try {
			// Set ourselves up to listen for new networks, and protect against session loading issues
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			         .addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_CREATED, this);

			Cytoscape.getPropertyChangeSupport()
			         .addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);

			Cytoscape.getPropertyChangeSupport()
			         .addPropertyChangeListener(Integer.toString(Cytoscape.SESSION_OPENED), this);

			((DGraphView) Cytoscape.getCurrentNetworkView())
					.addNodeContextMenuListener(new ChemVizContextMenu(settingsDialog));
			((DGraphView) Cytoscape.getCurrentNetworkView())
					.addEdgeContextMenuListener(new ChemVizContextMenu(settingsDialog));

		} catch (ClassCastException ccex) {
			logger.error("Unable to setup network listeners: "+ccex.getMessage(), ccex);
			return;
		}

		// Finally, register our commands
		new ChemVizCommandHandler(settingsDialog);
	}

	/**
	 * Detect that a new network view has been created and add our node context
	 * menu listener to nodes within this network
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Integer.toString(Cytoscape.SESSION_OPENED))) {
			sessionLock = true;
		} else if (evt.getPropertyName() == Cytoscape.SESSION_LOADED) {
			sessionLock = false;
		} else if (evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			CyNetworkView view = (CyNetworkView)evt.getNewValue();
			updateNetworkView(view);
		}
	}

	private void updateNetworkView(CyNetworkView view) {
		// Add menu to the context dialog
		view.addNodeContextMenuListener(new ChemVizContextMenu(settingsDialog));
		view.addEdgeContextMenuListener(new ChemVizContextMenu(settingsDialog));
		// Check to see if this view has custom graphics
		if (CreateNodeGraphicsTask.hasCustomGraphics(view.getNetwork())) {
			List<GraphObject> selection = 
			  CreateNodeGraphicsTask.getCustomGraphicsNodes(view);

			CreateNodeGraphicsTask loader = null;
			loader = CreateNodeGraphicsTask.getCustomGraphicsTask(view);
			if (loader == null) {
				loader = new CreateNodeGraphicsTask(selection, settingsDialog, false);
			} else {
				loader.setSelection(selection);
				loader.setRemove(false);
			}
			if (!sessionLock)
				TaskManager.executeTask(loader, loader.getDefaultTaskConfig());
			else {
				loader.setTaskMonitor(null);
				loader.run();
			}
		}
	}

}
