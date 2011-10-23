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
package clusterMaker.algorithms.networkClusterers.MCODE;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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

public class MCODECluster extends AbstractNetworkClusterer  {
	
	final static int FIRST_TIME = 0;
	final static int RESCORE = 1;
	final static int REFIND = 2;
	final static int INTERRUPTION = 3;
	int analyze = FIRST_TIME;

	boolean includeLoops = false;
	boolean haircut = true;
	boolean fluff = false;
	double scoreCutoff = 0.2;
	boolean selectedOnly = false;
	int degreeCutoff = 2;
	int kCore = 2;
	int maxDepth = 100;


	MCODEParameterSet currentParamsCopy;

	RunMCODE runMCODE;

	public MCODECluster() {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_MCODE_cluster";
		logger = CyLogger.getLogger(MCODECluster.class);
		currentParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy(null);
		currentParamsCopy.setDefaultParams();
		initializeProperties();
	}

	public String getShortName() {return "mcode";};
	public String getName() {return "MCODE cluster";};

	public JPanel getSettingsPanel() {
		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return new NewNetworkView(true);
	}

	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */
		clusterProperties.add(new Tunable("tunables_panel",
		                                  "MCODE Tuning",
		                                  Tunable.GROUP, new Integer(2)));
		{
			clusterProperties.add(new Tunable("selectedOnly",
			                                  "Cluster only selected nodes", 
			                                  Tunable.BOOLEAN, new Boolean(selectedOnly)));

			clusterProperties.add(new Tunable("advanced_panel",
			                                  "Advanced Tuning Options",
			                                  Tunable.GROUP, new Integer(2), new Boolean(true), 
			                                  null, Tunable.COLLAPSABLE));
			{

				clusterProperties.add(new Tunable("scoring_panel",
				                                  "Network Scoring",
				                                  Tunable.GROUP, new Integer(2), new Boolean(true), 
				                                  null, Tunable.COLLAPSABLE));

				{
					clusterProperties.add(new Tunable("includeLoops",
					                                  "Include loops", 
					                                  Tunable.BOOLEAN, new Boolean(includeLoops)));

					clusterProperties.add(new Tunable("degreeCutoff",
					                                  "Degree Cutoff", 
					                                  Tunable.INTEGER, new Integer(degreeCutoff)));
				}

				clusterProperties.add(new Tunable("cluster_panel",
				                                  "Cluster Finding",
				                                  Tunable.GROUP, new Integer(5), new Boolean(true), 
				                                  null, Tunable.COLLAPSABLE));

				{
					clusterProperties.add(new Tunable("haircut",
					                                  "Haircut", 
					                                  Tunable.BOOLEAN, new Boolean(haircut)));

					clusterProperties.add(new Tunable("fluff",
					                                  "Fluff", 
					                                  Tunable.BOOLEAN, new Boolean(fluff)));

					clusterProperties.add(new Tunable("scoreCutoff",
					                                  "Node Score Cutoff", 
					                                  Tunable.DOUBLE, new Double(scoreCutoff)));

					clusterProperties.add(new Tunable("kCore",
					                                  "K-Core", 
					                                  Tunable.INTEGER, new Integer(kCore)));

					clusterProperties.add(new Tunable("maxDepth",
					                                  "Max Depth", 
					                                  Tunable.INTEGER, new Integer(maxDepth)));

				}
			}
		}
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
		if ((t != null) && (t.valueChanged() || force)) {
			selectedOnly = ((Boolean) t.getValue()).booleanValue();
			if (selectedOnly)
				currentParamsCopy.setScope(MCODEParameterSet.SELECTION);
			else
				currentParamsCopy.setScope(MCODEParameterSet.NETWORK);
		}

		t = clusterProperties.get("includeLoops");
		if ((t != null) && (t.valueChanged() || force)) {
			includeLoops = ((Boolean) t.getValue()).booleanValue();
			currentParamsCopy.setIncludeLoops(includeLoops);
		}

		t = clusterProperties.get("degreeCutoff");
		if ((t != null) && (t.valueChanged() || force)) {
			degreeCutoff = ((Integer) t.getValue()).intValue();
			currentParamsCopy.setDegreeCutoff(degreeCutoff);
		}

		t = clusterProperties.get("haircut");
		if ((t != null) && (t.valueChanged() || force)) {
			haircut = ((Boolean) t.getValue()).booleanValue();
			currentParamsCopy.setHaircut(haircut);
		}

		t = clusterProperties.get("fluff");
		if ((t != null) && (t.valueChanged() || force)) {
			fluff = ((Boolean) t.getValue()).booleanValue();
			currentParamsCopy.setFluff(fluff);
		}

		t = clusterProperties.get("scoreCutoff");
		if ((t != null) && (t.valueChanged() || force)) {
			scoreCutoff = ((Double) t.getValue()).doubleValue();
			currentParamsCopy.setNodeScoreCutoff(scoreCutoff);
		}

		t = clusterProperties.get("kCore");
		if ((t != null) && (t.valueChanged() || force)) {
			kCore = ((Integer) t.getValue()).intValue();
			currentParamsCopy.setKCore(kCore);
		}

		t = clusterProperties.get("maxDepth");
		if ((t != null) && (t.valueChanged() || force)) {
			maxDepth = ((Integer) t.getValue()).intValue();
			currentParamsCopy.setMaxDepthFromStart(maxDepth);
		}

	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();

		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		NodeCluster.init();
		if(currentParamsCopy.getScope().equals(MCODEParameterSet.SELECTION)) {
			Set<CyNode> selectedNodes = network.getSelectedNodes();
			Integer[] selectedNodesRGI = new Integer[selectedNodes.size()];
			int c = 0;
			for (CyNode node: selectedNodes) 
				selectedNodesRGI[c++] = new Integer(node.getRootGraphIndex());
			currentParamsCopy.setSelectedNodes(selectedNodesRGI);
		}

		MCODECurrentParameters.getInstance().setParams(currentParamsCopy, "MCODE Result", network.getIdentifier());

		runMCODE = new RunMCODE(logger, RESCORE, clusterAttributeName, network);
		List<NodeCluster> clusters = runMCODE.run(monitor);
		if (canceled) {
			logger.info("Canceled by user");
			return;
		}

		// Now, sort our list of clusters by score
		clusters = NodeCluster.rankListByScore(clusters);

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusters);

		results = new ClusterResults(network, nodeClusters);
		monitor.setStatus("Done.  MCODE results:\n"+results);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public void halt() {
		canceled = true;
		runMCODE.halt();
	}

	public void setParams(List<String>params) {
		params.add("scope="+currentParamsCopy.getScope());
		params.add("includeLoops="+currentParamsCopy.isIncludeLoops());
		params.add("degreeCutoff="+currentParamsCopy.getDegreeCutoff());
		params.add("kCore="+currentParamsCopy.getKCore());
		params.add("maxDepth="+currentParamsCopy.getMaxDepthFromStart());
		params.add("nodeScoreCutoff="+currentParamsCopy.getNodeScoreCutoff());
		params.add("fluff="+currentParamsCopy.isFluff());
		params.add("haircut="+currentParamsCopy.isHaircut());
		super.setParams(params);
	}
}
