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

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.ui.ClusterViz;

// clusterMaker imports

public class MCLCluster extends AbstractClusterAlgorithm {
	
	double inflation_parameter = 2.0;
	int rNumber = 8;
	double clusteringThresh = 1e-15;
	boolean takeNegLOG = false;
	boolean createNewNetwork = false;
	boolean selectedOnly = false;
	double maxResidual = 0.001;
	String[] attributeArray = new String[1];

	String dataAttribute = null;
	TaskMonitor monitor = null;
	CyLogger logger = null;
	RunMCL runMCL = null;

	public MCLCluster() {
		super();
		logger = CyLogger.getLogger(MCLCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "MCL";};
	public String getName() {return "MCL cluster";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);
		if (attributeArray.length == 1)
			dataAttribute = attributeArray[0];

		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return null;
	}

	protected void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */

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

		//Whether or not take -LOG of Edge-Weights
		clusterProperties.add(new Tunable("takeNegLOG","Take the -LOG of Edge Weights in Network",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		// Whether or not to create a new network from the results
		clusterProperties.add(new Tunable("createNewNetwork","Create a new network with independent clusters",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		// Whether or not to create a new network from the results
		clusterProperties.add(new Tunable("selectedOnly","Cluster only selected nodes",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		clusterProperties.add(new Tunable("attributeListGroup",
		                                  "Source for array data",
		                                  Tunable.GROUP, new Integer(1)));

		// The attribute to use to get the weights
		attributeArray = getAllAttributes();
		clusterProperties.add(new Tunable("attributeList",
		                                  "Array sources",
		                                  Tunable.LIST, 0,
		                                  (Object)attributeArray, (Object)null, 0));
		if (attributeArray.length == 1)
			dataAttribute = attributeArray[0];

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

		t = clusterProperties.get("takeNegLOG");
		if ((t != null) && (t.valueChanged() || force))
			takeNegLOG = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("createNewNetwork");
		if ((t != null) && (t.valueChanged() || force))
			createNewNetwork = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();
		
		t = clusterProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			dataAttribute = attributeArray[((Integer) t.getValue()).intValue()];
		}
	}

	private void getAttributesList(List<String>attributeList, CyAttributes attributes) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING ||
			    attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
				attributeList.add(names[i]);
			}
		}
	}

	private String[] getAllAttributes() {
		attributeArray = new String[1];
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes());
		String[] attrArray = attributeList.toArray(attributeArray);
		if (attrArray.length > 1) 
			Arrays.sort(attrArray);
		return attrArray;
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		// Sanity check all of our settings
		if (debug)
			logger.debug("Performing MCL clustering with attributes: "+dataAttribute);

		//Cluster the nodes
		runMCL = new RunMCL("cluster", dataAttribute, inflation_parameter, 
		                           rNumber, clusteringThresh, maxResidual, 
		                           takeNegLOG, createNewNetwork, selectedOnly,
		                           logger);
		runMCL.run(monitor);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public void halt() {
		runMCL.halt();
	}
}
