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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;
import cytoscape.view.CytoscapeDesktop;

// clusterMaker imports
import clusterMaker.ui.ClusterMakerLinkNetworks;
import clusterMaker.ui.ClusterSettingsDialog;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.HeatMapView;
import clusterMaker.ui.NewNetworkView;
import clusterMaker.ui.NestedNetworkView;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.hierarchical.HierarchicalCluster;
import clusterMaker.algorithms.kmeans.KMeansCluster;
import clusterMaker.algorithms.TransClust.TransClustCluster;
import clusterMaker.algorithms.FORCE.FORCECluster;
import clusterMaker.algorithms.MCL.MCLCluster;
import clusterMaker.algorithms.MCODE.MCODECluster;
import clusterMaker.algorithms.glay.GLayCluster;
import clusterMaker.algorithms.ConnectedComponents.ConnectedComponentsCluster;
import clusterMaker.algorithms.SCPS.SCPSCluster;
// import clusterMaker.algorithms.QT.QTCluster;
// import clusterMaker.algorithms.Spectral.SpectralCluster;
// import clusterMaker.algorithms.CP.CPCluster;
import clusterMaker.algorithms.AP.APCluster;
import clusterMaker.algorithms.autosome.AutoSOMECluster;

/**
 * The ClusterMaker class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class ClusterMaker extends CytoscapePlugin implements PropertyChangeListener {
	static final double VERSION = 1.9;
	HashMap<JMenuItem,ClusterViz> vizMenus;
	HashMap<String, ClusterViz> vizMap;
	HashMap<String, ClusterAlgorithm> algMap;
	List<JMenuItem> menuList;
	boolean menusEnabled = false;

	public final static String GROUP_ATTRIBUTE = "__clusterGroups";
	public final static String MATRIX_ATTRIBUTE = "__distanceMatrix";
	public final static String CLUSTER_NODE_ATTRIBUTE = "__nodeClusters";
	public final static String CLUSTER_ATTR_ATTRIBUTE = "__attrClusters";
	public final static String CLUSTER_EDGE_ATTRIBUTE = "__clusterEdgeWeight";
	public final static String NODE_ORDER_ATTRIBUTE = "__nodeOrder";
	public final static String ARRAY_ORDER_ATTRIBUTE = "__arrayOrder";
	public final static String CLUSTER_TYPE_ATTRIBUTE = "__clusterType";
	public final static String CLUSTER_ATTRIBUTE = "__clusterAttribute";
	public final static String CLUSTER_PARAMS_ATTRIBUTE = "__clusterParams";

	public static ClusterMaker clusterMakerInstance = null;

  /**
   * Create our action and add it to the plugins menu
   */
  public ClusterMaker() {

		if (ClusterMaker.clusterMakerInstance == null) 
			clusterMakerInstance = this;

		vizMenus = new HashMap<JMenuItem, ClusterViz>();
		vizMap = new HashMap<String, ClusterViz>();
		algMap = new HashMap<String, ClusterAlgorithm>();
		menuList = new ArrayList<JMenuItem>();
		JMenu menu = new JMenu("Cluster");
		addClusterAlgorithm(menu, new HierarchicalCluster());
		addClusterAlgorithm(menu, new KMeansCluster());
		addClusterAlgorithm(menu, new AutoSOMECluster(true));
		// addClusterAlgorithm(menu, new QTCluster());
		menu.addSeparator();
		addClusterAlgorithm(menu, new APCluster());
		addClusterAlgorithm(menu, new AutoSOMECluster(false));
		addClusterAlgorithm(menu, new ConnectedComponentsCluster());
		addClusterAlgorithm(menu, new GLayCluster());
		addClusterAlgorithm(menu, new MCODECluster());
		addClusterAlgorithm(menu, new MCLCluster());
		// addClusterAlgorithm(menu, new SpectralCluster());
		// addClusterAlgorithm(menu, new CPCluster());
		// addClusterAlgorithm(menu, new FORCECluster());
		addClusterAlgorithm(menu, new SCPSCluster());
		addClusterAlgorithm(menu, new TransClustCluster());
		// addClusterAlgorithm(new HOPACHCluster());
		menu.addSeparator();

		// Add the visualization menu items
		for (JMenuItem item: vizMenus.keySet()) {
			menu.add(item);
		}

		addVizBuiltIn(menu, new HeatMapView());
		addVizBuiltIn(menu, new NewNetworkView());

		// Add the nested network visualization
		NestedNetworkView viz3 = new NestedNetworkView();
		addVizBuiltIn(menu, viz3);

		// Because this overlaps with the new network visualization, it doesn't show
		// up in our vizMap automatically -- add it here so it will show up in our
		// command list.
		vizMap.put(viz3.getShortName(), viz3);

		// Finally, add the "Link networks" menu item.  Note that this is a little
		// different in that it's a boolean
		menu.addSeparator();
		menu.add(new ClusterMakerLinkNetworks());
		
		
		// Catch new network loaded and change events so we can update our visualization menus
		Cytoscape.getPropertyChangeSupport()
        .addPropertyChangeListener( Cytoscape.NETWORK_LOADED, this );
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);

		JMenu pluginMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
																.getMenu("Plugins");
		pluginMenu.add(menu);

		// Register our commands
		new clusterMaker.commands.ClusterCommandHandler(algMap);
		new clusterMaker.commands.VizCommandHandler(vizMap);

		CyLogger.getLogger(ClusterMaker.class).info("clusterMaker "+VERSION+" initialized");

  }

	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName() == Cytoscape.NETWORK_LOADED || 
		     evt.getPropertyName() == ClusterAlgorithm.CLUSTER_COMPUTED ||
		     evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS ||
		     evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED ){
			if (!menusEnabled) {
				menusEnabled = true;
				for (JMenuItem item: menuList) item.setEnabled(true);
			}
			updateVizMenus();
    }
	}

	public static ClusterMaker getInstance() {
		if (clusterMakerInstance == null)
			new ClusterMaker();

		return clusterMakerInstance;
	}

	public ClusterAlgorithm getAlgorithm(String algName) {
		if (algMap.containsKey(algName))
			return algMap.get(algName);
		return null;
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
		algMap.put(algorithm.getShortName(), algorithm);
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
		item.setEnabled(false);
		menuList.add(item);
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

	private void addVizBuiltIn(JMenu menu, ClusterViz viz) {
		JMenuItem item = new JMenuItem(viz.getName());
		item.addActionListener(new ClusterMakerCommandListener((ClusterAlgorithm)viz));
		item.setEnabled(false);
		menuList.add(item);
		menu.add(item);
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
				settingsDialog.showDialog();
			} else if (viz != null) {
				viz.startViz();
			}
		}
	}
}
