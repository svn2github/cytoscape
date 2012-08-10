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

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
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
import clusterMaker.algorithms.networkClusterers.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.autosome.AutoSOMECluster;
import clusterMaker.algorithms.attributeClusterers.hierarchical.HierarchicalCluster;
import clusterMaker.algorithms.attributeClusterers.hopach.HopachPAMClusterer;
import clusterMaker.algorithms.attributeClusterers.kmeans.KMeansCluster;
import clusterMaker.algorithms.attributeClusterers.kmedoid.KMedoidCluster;
import clusterMaker.algorithms.attributeClusterers.pam.PAMClusterer;
import clusterMaker.algorithms.attributeClusterers.FeatureVector.FeatureVectorCluster;

import clusterMaker.algorithms.networkClusterers.TransClust.TransClustCluster;
import clusterMaker.algorithms.networkClusterers.MCL.MCLCluster;
import clusterMaker.algorithms.networkClusterers.MCODE.MCODECluster;
import clusterMaker.algorithms.networkClusterers.glay.GLayCluster;
import clusterMaker.algorithms.networkClusterers.ConnectedComponents.ConnectedComponentsCluster;
import clusterMaker.algorithms.networkClusterers.SCPS.SCPSCluster;
// import clusterMaker.algorithms.QT.QTCluster;
// import clusterMaker.algorithms.Spectral.SpectralCluster;
// import clusterMaker.algorithms.CP.CPCluster;
import clusterMaker.algorithms.networkClusterers.AP.APCluster;

import clusterMaker.algorithms.clusterFilters.AbstractNetworkFilter;
import clusterMaker.algorithms.clusterFilters.HairCutFilter;
import clusterMaker.algorithms.clusterFilters.CuttingEdgeFilter;
import clusterMaker.algorithms.clusterFilters.DensityFilter;

/**
 * The ClusterMaker class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class ClusterMaker extends CytoscapePlugin implements PropertyChangeListener {
	static final double VERSION = 1.10;
	TreeMap<JMenuItem,ClusterViz> vizMenus;
	HashMap<String, ClusterViz> vizMap;
	HashMap<String, ClusterAlgorithm> algMap;
	HashMap<JMenuItem,ClusterAlgorithm> filterMenus;
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

		vizMenus = new TreeMap<JMenuItem, ClusterViz>(new JMenuComparator());
		vizMap = new HashMap<String, ClusterViz>();
		algMap = new HashMap<String, ClusterAlgorithm>();
		filterMenus = new HashMap<JMenuItem, ClusterAlgorithm>();
		menuList = new ArrayList<JMenuItem>();
		JMenu menu = new JMenu("Cluster");
		addCategory(menu, "-- Attribute Clustering Algorithms --");
		addClusterAlgorithm(menu, new AutoSOMECluster(true));
		addClusterAlgorithm(menu, new FeatureVectorCluster());
		addClusterAlgorithm(menu, new HierarchicalCluster());
		addClusterAlgorithm(menu, new KMeansCluster());
		addClusterAlgorithm(menu, new KMedoidCluster());
		addClusterAlgorithm(menu, new PAMClusterer());
		addClusterAlgorithm(menu, new HopachPAMClusterer());
		// addClusterAlgorithm(menu, new QTCluster());

		menu.addSeparator();
		addCategory(menu, "-- Network Clustering Algorithms --");
		addClusterAlgorithm(menu, new APCluster());
		addClusterAlgorithm(menu, new AutoSOMECluster(false));
		addClusterAlgorithm(menu, new ConnectedComponentsCluster());
		addClusterAlgorithm(menu, new GLayCluster());
		addClusterAlgorithm(menu, new MCODECluster());
		addClusterAlgorithm(menu, new MCLCluster());
		// addClusterAlgorithm(menu, new SpectralCluster());
		// addClusterAlgorithm(menu, new CPCluster());
		addClusterAlgorithm(menu, new SCPSCluster());
		addClusterAlgorithm(menu, new TransClustCluster());
		// addClusterAlgorithm(new HOPACHCluster());
		menu.addSeparator();
		addCategory(menu, "-- Post-cluster Filters --");
		addClusterFilter(menu, new CuttingEdgeFilter());
		addClusterFilter(menu, new DensityFilter());
		addClusterFilter(menu, new HairCutFilter());
		menu.addSeparator();
		addCategory(menu, "-- Visualizations --");

		HeatMapView hmViz = new HeatMapView();
		addVizBuiltIn(hmViz);
		vizMap.put(hmViz.getShortName(), hmViz);

		addVizBuiltIn(new NewNetworkView());

		// Add the nested network visualization
		NestedNetworkView viz3 = new NestedNetworkView();
		addVizBuiltIn(viz3);

		// Because this overlaps with the new network visualization, it doesn't show
		// up in our vizMap automatically -- add it here so it will show up in our
		// command list.
		vizMap.put(viz3.getShortName(), viz3);

		// Add the visualization menu items
		for (JMenuItem item: vizMenus.keySet()) {
			menu.add(item);
		}

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
			updateFilterMenus();
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

		algorithm.getPropertyChangeSupport().
				addPropertyChangeListener(ClusterAlgorithm.CLUSTER_COMPUTED, this);

		if (visualizer != null && !vizMap.containsKey(visualizer.getShortName())) {
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

	/**
 	 * addClusterFilter does some basic inquiry of the algorithm to see what it
 	 * supports and constructs the appropriate menu, taking into account whether
 	 * the algorithm supports edge as well as node attributes, and whether the algorithm
 	 * can be restricted to selected edges/nodes only.
 	 *
 	 * @param menu the top-level menu we're going to attach to
 	 * @param algorithm the cluster algorithm itself
 	 */  
	private void addClusterFilter(JMenu menu, ClusterAlgorithm algorithm) {
		algMap.put(algorithm.getShortName(), algorithm);

		JMenuItem item = new JMenuItem(algorithm.getName());
		item.addActionListener(new ClusterMakerCommandListener(algorithm));
		item.setEnabled(false);
		menuList.add(item);
		menu.add(item);
		filterMenus.put(item, algorithm);
	}

	private void updateVizMenus() {
		for (JMenuItem item: vizMenus.keySet()) {
			ClusterViz viz = vizMenus.get(item);
			if (!viz.isAvailable() && !menuList.contains(item))
				item.setEnabled(false);
			else
				item.setEnabled(true);
		}
	}

	private void updateFilterMenus() {
		boolean netClusterAvailable = false;
		// See if we have any network cluster results available
		for (ClusterAlgorithm alg: algMap.values()) {
			if (alg instanceof AbstractNetworkFilter) continue;
			if (alg instanceof AbstractNetworkClusterer && alg.isAvailable()) {
				netClusterAvailable = true;
				break;
			}
		}

		for (JMenuItem item: filterMenus.keySet()) {
			if (netClusterAvailable)
				item.setEnabled(true);
			else
				item.setEnabled(false);
		}
	}

	private void addVizBuiltIn(ClusterViz viz) {
		JMenuItem item = new JMenuItem(viz.getName());
		item.addActionListener(new ClusterMakerCommandListener((ClusterAlgorithm)viz));
		item.setEnabled(false);
		menuList.add(item);
		vizMenus.put(item, viz);
	}

	private void addCategory(JMenu menu, String category) {
		JLabel label = new JLabel(category, javax.swing.SwingConstants.CENTER);
		label.setEnabled(false);
		menu.add(label);
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
				try {
					viz.startViz();
				} catch (CyCommandException cce) {
					// Shouldn't happen
				}
			}
		}
	}

	class JMenuComparator implements Comparator<JMenuItem> {
		public int compare(JMenuItem o1, JMenuItem o2) {
			return o1.getText().compareTo(o2.getText());
		}

		public boolean equals(JMenuItem o1, JMenuItem o2) {
			return o1.getText().equals(o2.getText());
		}
	}
}
