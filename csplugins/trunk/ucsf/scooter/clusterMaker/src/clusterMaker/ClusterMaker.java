/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
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
package clusterMaker;

// System imports
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.util.List;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;

// clusterMaker imports
import clusterMaker.ui.ClusterSettingsDialog;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.hierarchical.HierarchicalCluster;
import clusterMaker.algorithms.MCL.MCLMenu;
import clusterMaker.algorithms.FORCE.CytoscapeFORCEmenu;
// import clusterMaker.algorithms.MCLCluster;
// import clusterMaker.algorithms.KMeansCluster;
// import clusterMaker.algorithms.SOMCluster;

/**
 * The ClusterMaker class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class ClusterMaker extends CytoscapePlugin {
	static final double VERSION = 0.1;

  /**
   * Create our action and add it to the plugins menu
   */
  public ClusterMaker() {

		JMenu menu = new JMenu("Cluster");
		addClusterAlgorithm(menu, new HierarchicalCluster());
		// At some point, we need to convert these over to be clusterAlgorithms.  In the meantime...
		menu.add(new MCLMenu());
		menu.add(new CytoscapeFORCEmenu());
		// addClusterAlgorithm(new MCLCLuster());
		// addClusterAlgorithm(new FORCECLuster());
		// addClusterAlgorithm(new KMeansCluster());
		// addClusterAlgorithm(new SOMCluster());
		
		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);
		System.out.println("clusterMaker "+VERSION+" initialized");

  }

	/**
 	 * addClusterAlgorithm does some basic inquiry of the algorithm to see what it
 	 * supports and constructs the appropriate menu, taking into account whether
 	 * the algorithm supports edge as well as node attributes, and whether the algorithm
 	 * can be restricted to selected edges/nodes only.
 	 *
 	 * @param menu the top-level menu we're going to attach to
 	 * @param algorithm the cluster algorithm itself
 	 */  
	private void addClusterAlgorithm(JMenu menu, ClusterAlgorithm algorithm) {
		JMenuItem item = new JMenuItem(algorithm.getName());
		item.addActionListener(new ClusterMakerCommandListener(algorithm));
		menu.add(item);
	}

	/**
	 * Add a submenu item to an existing menu
	 *
	 * @param menu the JMenu to add the new submenu to
	 * @param label the label for the submenu
	 * @param command the command to execute when selected
	 * @param userData data associated with the menu
	 */
	private void addSubMenu(JMenu menu, String label, int command, Object userData) {
		// ClusterMakerCommandListener l = new ClusterMakerCommandListener(command, userData);
		JMenuItem item = new JMenuItem(label);
		// item.addActionListener(l);
	  menu.add(item);
	}

	/**
	 * Inner class to handle commands
	 */
	class ClusterMakerCommandListener implements ActionListener {
		ClusterAlgorithm alg;

		public ClusterMakerCommandListener(ClusterAlgorithm algorithm) {
			this.alg = algorithm;
		}

		public void actionPerformed(ActionEvent e) {
			// Create the dialog
			ClusterSettingsDialog settingsDialog = new ClusterSettingsDialog(alg);
			// Pop it up
			settingsDialog.actionPerformed(e);
		}
	}
}
