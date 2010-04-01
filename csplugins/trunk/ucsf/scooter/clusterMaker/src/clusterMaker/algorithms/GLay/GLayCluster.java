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
package clusterMaker.algorithms.glay;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import giny.model.Node;

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;

// clusterMaker imports

public class GLayCluster extends AbstractNetworkClusterer  {
	
	TaskMonitor monitor = null;
	CyLogger logger = null;
	FastGreedyAlgorithm fa = null;
	boolean selectedOnly = false;
	boolean createNewNetwork = false;
	boolean undirectedEdges = true;

	public GLayCluster() {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_GLay_cluster";
		logger = CyLogger.getLogger(GLayCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "GLay";};
	public String getName() {return "Community cluster (GLay)";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return new NewNetworkView(true);
	}

	protected void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */
		clusterProperties.add(new Tunable("tunables_panel",
		                                  "GLay Options",
		                                  Tunable.GROUP, new Integer(2)));

		// Whether or not to create a new network from the results
		clusterProperties.add(new Tunable("selectedOnly","Cluster only selected nodes",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		//Whether or not to assume the edges are undirected
		clusterProperties.add(new Tunable("undirectedEdges","Assume edges are undirected",
		                                  Tunable.BOOLEAN, new Boolean(true)));

		clusterProperties.add(new Tunable("results_panel",
		                                  "Results",
		                                  Tunable.GROUP, new Integer(2)));

		clusterProperties.add(new Tunable("modularity","Modularity",
		                                  Tunable.DOUBLE, new Double(0), Tunable.IMMUTABLE));

		clusterProperties.add(new Tunable("clusters","Number of clusters",
		                                  Tunable.INTEGER, new Integer(0), Tunable.IMMUTABLE));

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

		Tunable t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("undirectedEdges");
		if ((t != null) && (t.valueChanged() || force))
			undirectedEdges = ((Boolean) t.getValue()).booleanValue();
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();

		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// Sanity check all of our settings
		if (debug)
			logger.debug("Performing community clustering (GLay)");

    GSimpleGraphData simpleGraph = new GSimpleGraphData(network, selectedOnly, undirectedEdges);
		fa = new FastGreedyAlgorithm();
		//fa.partition(simpleGraph);
		fa.execute(simpleGraph, monitor);

		NumberFormat nf = NumberFormat.getInstance();
		String modularityString = nf.format(fa.getModularity());
		Tunable t = clusterProperties.get("modularity");
		t.setValue(new Double(modularityString));

		t = clusterProperties.get("clusters");
		t.setValue(new Integer(fa.getClusterNumber()));

		List<NodeCluster> clusterList = new ArrayList();
		for (int cluster = 0; cluster < fa.getClusterNumber(); cluster++) {
			clusterList.add(new NodeCluster());
		}

    int membership[] = fa.getMembership();
    for(int index=0; index < simpleGraph.graphIndices.length; index++){
      int cluster = membership[index];
      CyNode node = (CyNode) network.getNode(simpleGraph.graphIndices[index]);
      clusterList.get(cluster).add(node);
    }

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusterList);

		ClusterResults results = new ClusterResults(network, nodeClusters, "Modularity: "+modularityString);
		monitor.setStatus("Done.  Community Clustering results:\n"+results+"\n  Modularity: "+modularityString);

		// Set up the appropriate attributes
		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		netAttr.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                     ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, "glay");
		netAttr.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                     ClusterMaker.CLUSTER_ATTRIBUTE, clusterAttributeName);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public void halt() {
		if (fa != null)
			fa.halt();
	}
}
