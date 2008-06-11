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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.List;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;

// clusterMaker imports
import clusterMaker.ui.ClusterSettingsDialog;
import clusterMaker.ui.ClusterViz;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.hierarchical.HierarchicalCluster;
import clusterMaker.algorithms.kmeans.KMeansCluster;
import clusterMaker.algorithms.MCL.MCLMenu;
import clusterMaker.algorithms.FORCE.CytoscapeFORCEmenu;
// import clusterMaker.algorithms.MCLCluster;
// import clusterMaker.algorithms.KMeansCluster;
// import clusterMaker.algorithms.SOMCluster;

/**
 * The ClusterMaker class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class ClusterMaker extends CytoscapePlugin implements PropertyChangeListener {
	static final double VERSION = 0.1;
	HashMap<JMenuItem,ClusterViz> vizMenus;
	HashMap<String, ClusterViz> vizMap;

  /**
   * Create our action and add it to the plugins menu
   */
  public ClusterMaker() {

		vizMenus = new HashMap();
		vizMap = new HashMap();
		JMenu menu = new JMenu("Cluster");
		addClusterAlgorithm(menu, new HierarchicalCluster());
		addClusterAlgorithm(menu, new KMeansCluster());
		// At some point, we need to convert these over to be clusterAlgorithms.  In the meantime...
		menu.add(new MCLMenu());
		menu.add(new CytoscapeFORCEmenu());
		// addClusterAlgorithm(new MCLCLuster());
		// addClusterAlgorithm(new FORCECLuster());
		// addClusterAlgorithm(new KMeansCluster());
		// addClusterAlgorithm(new SOMCluster());
		menu.addSeparator();

		// Add the visualization menu items
		for (JMenuItem item: vizMenus.keySet()) {
			menu.add(item);
		}
		
		Cytoscape.getPropertyChangeSupport()
        .addPropertyChangeListener( Cytoscape.NETWORK_LOADED, this );

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);
		CyLogger.getLogger(ClusterMaker.class).info("clusterMaker "+VERSION+" initialized");

  }

	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName() == Cytoscape.NETWORK_LOADED || 
		     evt.getPropertyName() == ClusterAlgorithm.CLUSTER_COMPUTED){
			updateVizMenus();
    }
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
		ClusterViz visualizer = algorithm.getVisualizer();

		if (visualizer != null && !vizMap.containsKey(visualizer.getShortName())) {
			// We have a visualizer, so we're interested in any clusters that get completed
			algorithm.getPropertyChangeSupport().
					addPropertyChangeListener(ClusterAlgorithm.CLUSTER_COMPUTED, this);
			JMenuItem vizItem = new JMenuItem(visualizer.getName());
			vizMenus.put(vizItem, visualizer);
			vizMap.put(visualizer.getShortName(), visualizer);
			vizItem.addActionListener(new ClusterMakerCommandListener(visualizer));
			if (!visualizer.isAvailable())
				vizItem.setEnabled(false);
		}

		JMenuItem item = new JMenuItem(algorithm.getName());
		item.addActionListener(new ClusterMakerCommandListener(algorithm));
		menu.add(item);
	}

	private void updateVizMenus() {
		for (JMenuItem item: vizMenus.keySet()) {
			ClusterViz viz = vizMenus.get(item);
			if (!viz.isAvailable())
				item.setEnabled(false);
			else
				item.setEnabled(true);
		}
	}

	/**
	 * Inner class to handle commands
	 */
	class ClusterMakerCommandListener implements ActionListener {
		ClusterAlgorithm alg = null;
		ClusterViz viz = null;

		public ClusterMakerCommandListener(ClusterAlgorithm algorithm) {
			this.alg = algorithm;
		}

		public ClusterMakerCommandListener(ClusterViz vizualizer) {
			this.viz = vizualizer;
		}

		public void actionPerformed(ActionEvent e) {
			if (alg != null) {
				// Create the dialog
				ClusterSettingsDialog settingsDialog = new ClusterSettingsDialog(alg);
				// Pop it up
				settingsDialog.actionPerformed(e);
			} else if (viz != null) {
				viz.startViz();
			}
		}
	}
}
