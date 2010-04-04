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
package clusterMaker.algorithms.MCL;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
import clusterMaker.algorithms.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.EdgeAttributeHandler;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;

// clusterMaker imports

public class MCLCluster extends AbstractNetworkClusterer  {
	
	double inflation_parameter = 2.0;
	int rNumber = 8;
	double clusteringThresh = 1e-15;
	double maxResidual = 0.001;
	EdgeAttributeHandler edgeAttributeHandler = null;

	TaskMonitor monitor = null;
	CyLogger logger = null;
	RunMCL runMCL = null;

	public MCLCluster() {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_MCL_cluster";
		logger = CyLogger.getLogger(MCLCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "MCL";};
	public String getName() {return "MCL cluster";};

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

		/**
		 * Tuning values
		 */
		clusterProperties.add(new Tunable("tunables_panel",
		                                  "MCL Tuning",
		                                  Tunable.GROUP, new Integer(4)));

		// Inflation Parameter
		clusterProperties.add(new Tunable("inflation_parameter",
		                                  "Density Parameter",
		                                  Tunable.DOUBLE, new Double(2.5),
		                                  (Object)null, (Object)null, 0));
		// Clustering Threshold
		clusterProperties.add(new Tunable("clusteringThresh",
		                                  "Weak EdgeWeight Pruning Threshold",
		                                  Tunable.DOUBLE, new Double(1e-15),
		                                  (Object)null, (Object)null, 0));

		// Number of iterations
		clusterProperties.add(new Tunable("rNumber",
		                                  "Number of iterations",
		                                  Tunable.INTEGER, new Integer(8),
		                                  (Object)null, (Object)null, 0));

		// Number of iterations
		clusterProperties.add(new Tunable("maxResidual",
		                                  "The maximum residual value",
		                                  Tunable.DOUBLE, new Double(.001),
		                                  (Object)null, (Object)null, 0));

		// Use the standard edge attribute handling stuff....
		edgeAttributeHandler = new EdgeAttributeHandler(clusterProperties, true);

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

		Tunable t = clusterProperties.get("inflation_parameter");
		if ((t != null) && (t.valueChanged() || force))
			inflation_parameter = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("clusteringThresh");
		if ((t != null) && (t.valueChanged() || force))
			clusteringThresh = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("maxResidual");
		if ((t != null) && (t.valueChanged() || force))
			maxResidual = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("rNumber");
		if ((t != null) && (t.valueChanged() || force))
			rNumber = ((Integer) t.getValue()).intValue();

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

		//Cluster the nodes
		runMCL = new RunMCL(matrix, inflation_parameter, 
		                    rNumber, clusteringThresh, maxResidual, logger);

		runMCL.setDebug(debug);

		// results = runMCL.run(monitor);
		List<NodeCluster> clusters = runMCL.run(monitor);

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, clusters);

		ClusterResults results = new ClusterResults(network, nodeClusters);
		monitor.setStatus("Done.  MCL results:\n"+results);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public void halt() {
		runMCL.halt();
	}
}
