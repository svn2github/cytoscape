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
package clusterMaker.algorithms.attributeClusterers.kmeans;

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
import clusterMaker.ui.KnnView;

// clusterMaker imports

public class KMeansCluster extends AbstractAttributeClusterer {
	int rNumber = 0;
	KnnView knnView = null;

	public KMeansCluster() {
		super();
		logger = CyLogger.getLogger(KMeansCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "kmeans";};
	public String getName() {return "K-Means cluster";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);

		// We also want to update the number our "guestimate" for k and kMax
		updateKEstimates();

		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		// if (knnView == null)
		// 	knnView = new KnnView();

		return new KnnView();
	}

	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */

		// K
		addKTunables();

		// Number of iterations
		clusterProperties.add(new Tunable("iterations",
		                                  "Number of iterations",
		                                  Tunable.INTEGER, new Integer(50),
		                                  (Object)new Integer(rNumber), (Object)null, 0));

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

		// Whether or not to only cluster selected nodes/edges
		clusterProperties.add(new Tunable("selectedOnly",
		                                  "Only use selected nodes/edges for cluster",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		// Whether or not to cluster attributes as well as nodes
		clusterProperties.add(new Tunable("clusterAttributes",
		                                  "Cluster attributes as well as nodes", 
		                                  Tunable.BOOLEAN, new Boolean(false)));

/*
		// For expression data, we might want to exclude missing data
		clusterProperties.add(new Tunable("ignoreMissing",
		                                  "Ignore nodes with no data",
		                                  Tunable.BOOLEAN, new Boolean(true)));
*/

		// Whether or not to create groups
		clusterProperties.add(new Tunable("createGroups",
		                                  "Create groups from clusters", 
		                                  Tunable.BOOLEAN, new Boolean(true)));

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
		updateKTunables(false);
		
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);
		updateKTunables(force);

		Tunable t = clusterProperties.get("iterations");
		if ((t != null) && (t.valueChanged() || force))
			rNumber = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("dMetric");
		if ((t != null) && (t.valueChanged() || force))
			distanceMetric = Matrix.distanceTypes[((Integer) t.getValue()).intValue()];

		t = clusterProperties.get("clusterAttributes");
		if ((t != null) && (t.valueChanged() || force))
			clusterAttributes = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();

/*
		t = clusterProperties.get("ignoreMissing");
		if ((t != null) && (t.valueChanged() || force))
			ignoreMissing = ((Boolean) t.getValue()).booleanValue();
*/

		t = clusterProperties.get("createGroups");
		if ((t != null) && (t.valueChanged() || force))
			createGroups = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			dataAttributes = (String) t.getValue();
		}
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		// Sanity check all of our settings
		if (debug)
			logger.debug("Performing k-means cluster with k="+kNumber+" using "+
			             distanceMetric+" and attributes: "+dataAttributes);

		if (dataAttributes == null || dataAttributes.length() == 0) {
			if (monitor != null) {
				monitor.setException(null, "Error: no attribute list selected");
				logger.warning("Must have an attribute list to use for cluster weighting");
			} else
				logger.error("Must have an attribute list to use for cluster weighting");
			return;
		}

		// Get our attributes we're going to use for the cluster
		String attributeArray[] = getAttributeArray(dataAttributes);
		// To make debugging easier, sort the attribute array
		Arrays.sort(attributeArray);

		KCluster algorithm = new KCluster(attributeArray, distanceMetric, logger, monitor);
		algorithm.setCreateGroups(createGroups);
		algorithm.setIgnoreMissing(true);
		algorithm.setSelectedOnly(selectedOnly);
		algorithm.setDebug(debug);
		algorithm.setUseSilhouette(useSilhouette);
		algorithm.setKMax(kMax);
		algorithm.setClusterInterface(this);

		String resultsString = "K-Means results:";

		// Cluster the attributes, if requested
		if (clusterAttributes && attributeArray.length > 1) {
			if (monitor != null)
				monitor.setStatus("Clustering attributes");
			resultsString = "\nAttributes: " + algorithm.cluster(kNumber, rNumber, true, "kmeans");
		}

		// Cluster the nodes
		if (monitor != null)
			monitor.setStatus("Clustering nodes");
		resultsString = "\nNodes: "+algorithm.cluster(kNumber, rNumber, false, "kmeans");
		if (monitor != null)
			monitor.setStatus(resultsString);

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

}
