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
package clusterMaker.algorithms.attributeClusterers.hierarchical;

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
import clusterMaker.algorithms.attributeClusterers.AbstractAttributeClusterer;
import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.algorithms.attributeClusterers.Matrix;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.TreeView;

// clusterMaker imports

public class HierarchicalCluster extends AbstractAttributeClusterer {
	/**
	 * Linkage types
	 */
	ClusterMethod[] linkageTypes = { ClusterMethod.AVERAGE_LINKAGE,
	                                 ClusterMethod.SINGLE_LINKAGE,
	                                 ClusterMethod.MAXIMUM_LINKAGE,
	                                 ClusterMethod.CENTROID_LINKAGE };


	ClusterMethod clusterMethod =  ClusterMethod.AVERAGE_LINKAGE;
	TreeView treeView = null;

	public HierarchicalCluster() {
		super();
		logger = CyLogger.getLogger(HierarchicalCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "hierarchical";};
	public String getName() {return "Hierarchical cluster";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);

		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		// if (treeView == null)
		// 	treeView = new TreeView();

		return new TreeView();
	}

	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */

		// The linkage to use
		clusterProperties.add(new Tunable("linkage",
		                                  "Linkage",
		                                  Tunable.LIST, new Integer(0),
		                                  (Object)linkageTypes, (Object)null, 0));

		// The distance metric to use
		clusterProperties.add(new Tunable("dMetric",
		                                  "Distance Metric",
		                                  Tunable.LIST, new Integer(0),
		                                  (Object)Matrix.distanceTypes, (Object)null, 0));

		clusterProperties.add(new Tunable("attributeListGroup",
		                                  "Source for array data",
		                                  Tunable.GROUP, new Integer(1)));

		// The attribute to use to get the weights
		attributeArray = getAllAttributes();
		clusterProperties.add(new Tunable("attributeList",
		                                  "Array sources",
		                                  Tunable.LIST, "",
		                                  (Object)attributeArray, (Object)null, Tunable.MULTISELECT));

		clusterProperties.add(new Tunable("parametersGroup",
		                                  "Clustering Parameters",
		                                  Tunable.GROUP, new Integer(6)));

		// Whether or not to only cluster selected nodes/edges
		clusterProperties.add(new Tunable("selectedOnly",
		                                  "Only use selected nodes/edges for cluster",
		                                  Tunable.BOOLEAN, new Boolean(selectedOnly)));

		// Whether or not to cluster attributes as well as nodes
		clusterProperties.add(new Tunable("clusterAttributes",
		                                  "Cluster attributes as well as nodes", 
		                                  Tunable.BOOLEAN, new Boolean(clusterAttributes)));

		// For expression data, we might want to exclude missing data
		clusterProperties.add(new Tunable("ignoreMissing",
		                                  "Ignore nodes/edges with no data",
		                                  Tunable.BOOLEAN, new Boolean(ignoreMissing)));

		clusterProperties.add(new Tunable("advancedParametersGroup",
		                                  "Advanced Parameters",
		                                  Tunable.GROUP, new Integer(2), 
		                                  new Boolean(true), null,
		                                  Tunable.COLLAPSABLE));

		// How to handle missing data
		clusterProperties.add(new Tunable("zeroMissing",
		                                  "Set missing data to zero (not common)",
		                                  Tunable.BOOLEAN, new Boolean(zeroMissing)));

		// Adjust loops
		clusterProperties.add(new Tunable("adjustDiagonals",
		                                  "Adjust loops (not common)",
		                                  Tunable.BOOLEAN, new Boolean(adjustDiagonals)));

		// Whether or not to create groups
		clusterProperties.add(new Tunable("createGroups",
		                                  "Create groups from clusters", 
		                                  Tunable.BOOLEAN, new Boolean(createGroups)));

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);

		Tunable t = clusterProperties.get("linkage");
		if ((t != null) && (t.valueChanged() || force))
			clusterMethod = linkageTypes[((Integer) t.getValue()).intValue()];

		t = clusterProperties.get("dMetric");
		if ((t != null) && (t.valueChanged() || force))
			distanceMetric = Matrix.distanceTypes[((Integer) t.getValue()).intValue()];

		t = clusterProperties.get("clusterAttributes");
		if ((t != null) && (t.valueChanged() || force))
			clusterAttributes = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("createGroups");
		if ((t != null) && (t.valueChanged() || force))
			createGroups = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("ignoreMissing");
		if ((t != null) && (t.valueChanged() || force))
			ignoreMissing = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			dataAttributes = (String) t.getValue();
		}

		t = clusterProperties.get("zeroMissing");
		if ((t != null) && (t.valueChanged() || force))
			zeroMissing = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("adjustDiagonals");
		if ((t != null) && (t.valueChanged() || force)) 
			adjustDiagonals = ((Boolean) t.getValue()).booleanValue();
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		// Sanity check all of our settings
		if (debug)
			logger.debug("Performing hierarchical cluster with method: "+clusterMethod+" using "+distanceMetric+" and attributes: "+dataAttributes);
		if (dataAttributes == null || dataAttributes.length() == 0) {
			if (monitor != null) {
				logger.warning("Must have an attribute list to use for cluster weighting");
				monitor.setException(null, "Error: no attribute list selected");
			} else
				logger.error("Must have an attribute list to use for cluster weighting");
			return;
		}

		// Get our attributes we're going to use for the cluster
		String attributeArray[] = getAttributeArray(dataAttributes);
		// To make debugging easier, sort the attribute array
		Arrays.sort(attributeArray);

		// If we've got node attributes, there must be at least two
		if (attributeArray.length == 1 && attributeArray[0].startsWith("node.")) {
			if (monitor != null) {
				logger.warning("Must have at least two node attributes for cluster weighting");
				monitor.setException(null, "Error: not enough attributes selected");
			} else
				logger.error("Must have at least two node attributes for cluster weighting");
			return;
		}

		if (monitor != null)
			monitor.setStatus("Initializing");

		// Create a new clusterer
		EisenCluster algorithm = new EisenCluster(attributeArray, distanceMetric, clusterMethod,
			                                        logger, monitor);
		// Set our parameters
		setParameters(algorithm);

		// Cluster the attributes, if requested
		if (clusterAttributes && attributeArray.length > 1) {
			if (monitor != null)
				monitor.setStatus("Clustering attributes");

			algorithm.cluster(0, 0, true);
		}

		if (monitor != null)
			monitor.setStatus("Clustering nodes");

		// Cluster the nodes
		algorithm.cluster(0, 0, false);

		if (monitor != null)
			monitor.setStatus("Done");

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	private void setParameters(EisenCluster algorithm) {
		algorithm.setCreateGroups(createGroups);
		algorithm.setIgnoreMissing(ignoreMissing);
		algorithm.setSelectedOnly(selectedOnly);
		algorithm.setAdjustDiagonals(adjustDiagonals);
		algorithm.setZeroMissing(zeroMissing);
	}

}
