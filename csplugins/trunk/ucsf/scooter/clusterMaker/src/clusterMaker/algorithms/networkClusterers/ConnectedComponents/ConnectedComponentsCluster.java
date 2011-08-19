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
package clusterMaker.algorithms.networkClusterers.ConnectedComponents;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.networkClusterers.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.algorithms.edgeConverters.EdgeAttributeHandler;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;

// clusterMaker imports

public class ConnectedComponentsCluster extends AbstractNetworkClusterer  {
	
	EdgeAttributeHandler edgeAttributeHandler = null;

	TaskMonitor monitor = null;
	CyLogger logger = null;

	public ConnectedComponentsCluster() {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_CC_cluster";
		logger = CyLogger.getLogger(ConnectedComponentsCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "connectedcomponents";};
	public String getName() {return "Connected Components cluster";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		edgeAttributeHandler.updateAttributeList();

		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return new NewNetworkView(true);
	}

	public void initializeProperties() {
		super.initializeProperties();

		// Use the standard edge attribute handling stuff....
		edgeAttributeHandler = new EdgeAttributeHandler(clusterProperties, false);

		super.advancedProperties();

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);

		edgeAttributeHandler.updateSettings(force);
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();

		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		DistanceMatrix matrix = edgeAttributeHandler.getMatrix();
		if (matrix == null) {
			logger.error("Can't get distance matrix: no attribute value?");
			return;
		}

		logger.info("Calculating Connected Components");
		monitor.setStatus("Calculating Connected Components");

		Map<Integer, List<CyNode>> components = matrix.findConnectedComponents();

		// Create the NodeClusters
		Map<Integer, NodeCluster> clusterMap = new HashMap<Integer, NodeCluster>();
		for (Integer cluster: components.keySet()) {
			clusterMap.put(cluster, new NodeCluster(components.get(cluster)));
		}

		// Now get the sorted cluster map
		int clusterNumber = 1;
		HashMap<NodeCluster,NodeCluster> cMap = new HashMap<NodeCluster, NodeCluster>();
		for (NodeCluster cluster: NodeCluster.sortMap(clusterMap)) {

			if (cMap.containsKey(cluster))
				continue;

			cMap.put(cluster,cluster);

			cluster.setClusterNumber(clusterNumber);
			clusterNumber++;
		}

		List<NodeCluster> clusters = new ArrayList<NodeCluster> (cMap.keySet());

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusters);

		results = new ClusterResults(network, nodeClusters);
		monitor.setStatus("Done.  Connected component results:\n"+results);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public void halt() {
	}
}
