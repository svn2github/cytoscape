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
package clusterMaker.algorithms.AP;

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
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.DistanceMatrix;
import clusterMaker.algorithms.EdgeAttributeHandler;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;

// clusterMaker imports

public class APCluster extends AbstractClusterAlgorithm  {
	
	double lambda = .5;
	int rNumber = 8;
	double preference = -1;
	boolean createMetaNodes = false;
	EdgeAttributeHandler edgeAttributeHandler = null;

	TaskMonitor monitor = null;
	CyLogger logger = null;
	RunAP runAP = null;

	public APCluster() {
		super();
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_AP_cluster";
		logger = CyLogger.getLogger(APCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "AP";};
	public String getName() {return "Affinity Propagation cluster";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		edgeAttributeHandler.updateAttributeList();

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
		                                  "AP Tuning",
		                                  Tunable.GROUP, new Integer(3)));

		// Lambda Parameter
		clusterProperties.add(new Tunable("lambda",
		                                  "Lambda Parameter",
		                                  Tunable.DOUBLE, new Double(.5),
		                                  (Object)null, (Object)null, 0));
		// Clustering Threshold
		clusterProperties.add(new Tunable("preference",
		                                  "Preference Parameter (Set to Avg Edge Weight if < 0)",
		                                  Tunable.DOUBLE, new Double(-1),
		                                  (Object)null, (Object)null, 0));

		// Number of iterations
		clusterProperties.add(new Tunable("rNumber",
		                                  "Number of iterations",
		                                  Tunable.INTEGER, new Integer(10),
		                                  (Object)null, (Object)null, 0));

	       
		// Use the standard edge attribute handling stuff....
		edgeAttributeHandler = new EdgeAttributeHandler(clusterProperties, true);

		clusterProperties.add(new Tunable("options_panel2",
		                                  "Results options",
		                                  Tunable.GROUP, new Integer(1)));

		// Whether or not to create a new network from the results
		clusterProperties.add(new Tunable("createMetaNodes","Create meta nodes for clusters",
		                                  Tunable.BOOLEAN, new Boolean(false)));

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

		Tunable t = clusterProperties.get("lambda");
		if ((t != null) && (t.valueChanged() || force))
			lambda = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("preference");
		if ((t != null) && (t.valueChanged() || force))
			preference = ((Double) t.getValue()).doubleValue();

		//t = clusterProperties.get("maxResidual");
		//if ((t != null) && (t.valueChanged() || force))
		//	maxResidual = ((Double) t.getValue()).doubleValue();

		t = clusterProperties.get("rNumber");
		if ((t != null) && (t.valueChanged() || force))
			rNumber = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("createMetaNodes");
		if ((t != null) && (t.valueChanged() || force))
			createMetaNodes = ((Boolean) t.getValue()).booleanValue();

		edgeAttributeHandler.updateSettings(force);
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;

		DistanceMatrix matrix = edgeAttributeHandler.getMatrix();

		//Cluster the nodes
		runAP = new RunAP(clusterAttributeName, matrix, lambda, preference, 
		                    rNumber,logger);

		if (createMetaNodes)
			runAP.createMetaNodes();

		//runAP.setDebug(debug);

		runAP.run(monitor);

		// Set up the appropriate attributes
		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		netAttr.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                     ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, "AP");
		netAttr.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                     ClusterMaker.CLUSTER_ATTRIBUTE, clusterAttributeName);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public void halt() {
		runAP.halt();
	}
}
