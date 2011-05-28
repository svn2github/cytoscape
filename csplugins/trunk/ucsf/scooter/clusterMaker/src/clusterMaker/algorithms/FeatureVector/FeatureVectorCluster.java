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
package clusterMaker.algorithms.FeatureVector;

import java.awt.GridLayout;
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
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.hierarchical.DistanceMetric;
import clusterMaker.algorithms.hierarchical.EisenCluster;
import clusterMaker.algorithms.hierarchical.Matrix;
import clusterMaker.ui.ClusterViz;

// clusterMaker imports

public class FeatureVectorCluster extends AbstractClusterAlgorithm {
	/**
	 * Linkage types
	 */
	DistanceMetric[] distanceTypes = { DistanceMetric.EUCLIDEAN,
	                                   DistanceMetric.CITYBLOCK,
	                                   DistanceMetric.CORRELATION,
	                                   DistanceMetric.ABS_CORRELATION,
	                                   DistanceMetric.UNCENTERED_CORRELATION,
	                                   DistanceMetric.ABS_UNCENTERED_CORRELATION,
	                                   DistanceMetric.SPEARMANS_RANK,
	                                   DistanceMetric.KENDALLS_TAU,
	                                   DistanceMetric.VALUE_IS_CORRELATION };
	String[] attributeArray = new String[1];

	DistanceMetric distanceMetric = DistanceMetric.EUCLIDEAN;
	boolean ignoreMissing = true;
	boolean selectedOnly = false;
	boolean zeroMissing = false;
	boolean createEdges = false;
	String dataAttributes = null;
	String edgeAttribute = null;
	TaskMonitor monitor = null;
	CyLogger logger = null;

	public FeatureVectorCluster() {
		super();
		logger = CyLogger.getLogger(FeatureVectorCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "featurevector";};
	public String getName() {return "Create Edges from Node Attributes";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = EisenCluster.getNodeAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);

		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return null;
	}

	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */

		// The distance metric to use
		clusterProperties.add(new Tunable("dMetric",
		                                  "Distance Metric",
		                                  Tunable.LIST, new Integer(0),
		                                  (Object)distanceTypes, (Object)null, 0));

		clusterProperties.add(new Tunable("attributeListGroup",
		                                  "Source for array data",
		                                  Tunable.GROUP, new Integer(1)));

		// The attribute to use to get the weights
		attributeArray = EisenCluster.getNodeAttributes();
		clusterProperties.add(new Tunable("attributeList",
		                                  "Array sources",
		                                  Tunable.LIST, "",
		                                  (Object)attributeArray, (Object)null, Tunable.MULTISELECT));

		clusterProperties.add(new Tunable("parametersGroup",
		                                  "Clustering Parameters",
		                                  Tunable.GROUP, new Integer(5)));

		// Whether or not to only cluster selected nodes/edges
		clusterProperties.add(new Tunable("selectedOnly",
		                                  "Only use selected nodes/edges for cluster",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		// For expression data, we might want to exclude missing data
		clusterProperties.add(new Tunable("ignoreMissing",
		                                  "Ignore nodes/edges with no data",
		                                  Tunable.BOOLEAN, new Boolean(true)));

		// Whether to create a new network or add values to existing network
		clusterProperties.add(new Tunable("createEdges",
		                                  "Create edges if they don't exist",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		clusterProperties.add(new Tunable("advancedParametersGroup",
		                                  "Advanced Parameters",
		                                  Tunable.GROUP, new Integer(3), 
		                                  new Boolean(true), null,
		                                  Tunable.COLLAPSABLE));

		// Edge attribute to use for distances
		clusterProperties.add(new Tunable("edgeAttribute",
		                                  "Edge attribute to use for distance values",
		                                  Tunable.STRING, "FeatureDistance"));

		// How to handle missing data
		clusterProperties.add(new Tunable("zeroMissing",
		                                  "Set missing data to zero (not common)",
		                                  Tunable.BOOLEAN, new Boolean(false)));

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);

		Tunable t = clusterProperties.get("dMetric");
		if ((t != null) && (t.valueChanged() || force))
			distanceMetric = distanceTypes[((Integer) t.getValue()).intValue()];

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

		t = clusterProperties.get("edgeAttribute");
		if ((t != null) && (t.valueChanged() || force))
			edgeAttribute = (String) t.getValue();

		t = clusterProperties.get("createEdges");
		if ((t != null) && (t.valueChanged() || force))
			createEdges = ((Boolean) t.getValue()).booleanValue();

	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		CyNetwork network = Cytoscape.getCurrentNetwork();

		// Sanity check all of our settings
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

		if (monitor != null)
			monitor.setStatus("Initializaing");

		// Create the matrix
		Matrix matrix = new Matrix(attributeArray, false, ignoreMissing, selectedOnly);

		if (monitor != null)
			monitor.setStatus("Calculating edge distances");

		// Create a weight vector of all ones (we don't use individual weighting, yet)
		matrix.setUniformWeights();

		// Handle special cases
		if (zeroMissing)
			matrix.setMissingToZero();

		int nNodes = matrix.nRows();

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		// For each node, get the distance to all other nodes
		int[] nodeArray = new int[2];
		double maxweight = 0.0;
		double minweight = 0.0;
		Map<CyEdge, Double> edgeList = new HashMap<CyEdge, Double>();

		for (int i = 0; i < nNodes; i++) {
			for (int j = i+1; j < nNodes; j++) {
 				double weight = distanceMetric.getMetric(matrix, matrix, matrix.getWeights(), i, j);
				maxweight = Math.max(maxweight, weight);
				minweight = Math.min(minweight, weight);

				CyNode source = Cytoscape.getCyNode(matrix.getRowLabel(i));
				CyNode target = Cytoscape.getCyNode(matrix.getRowLabel(j));
				nodeArray[0] = source.getRootGraphIndex();
				nodeArray[1] = target.getRootGraphIndex();

				CyEdge edge;
				int[] edgeArray = network.getConnectingEdgeIndicesArray(nodeArray);
				if ((edgeArray == null || edgeArray.length == 0) && createEdges == true) {
					edge = Cytoscape.getCyEdge(source, target, "interaction", "distance", true);
					// System.out.println("Creating edge between "+source+" and "+target);
					if (edge != null) {
						// System.out.println("Adding edge "+edge.getIdentifier()+" to network");
						network.addEdge(edge);
					}
				} else if (edgeArray == null || edgeArray.length == 0) {
					continue;
				} else {
					edge = (CyEdge)network.getEdge(edgeArray[0]);
				}

				if (edge == null) continue;

				edgeList.put(edge, new Double(weight));
				
			}
		}

		double scale = maxweight - minweight;
		// System.out.println("scale: "+maxweight+"-"+minweight+" = "+scale);

		for (CyEdge edge: edgeList.keySet()) {
			edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttribute, edgeList.get(edge)/scale);
		}

/*
		if (metric == DistanceMetric.EUCLIDEAN || metric == DistanceMetric.CITYBLOCK) {
			// Normalize distances to between 0 and 1
			double scale = 0.0;
			for (int node = 0; node < nodeList.length; node++) {
				if (nodeList[node].getDistance() > scale) scale = nodeList[node].getDistance();
			}
			if (scale != 0.0) {
				for (int node = 0; node < nodeList.length; node++) {
					double dist = nodeList[node].getDistance();
					nodeList[node].setDistance(dist/scale);
				}
			}
		}
*/
		
		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public boolean isAvailable() {
		// return EisenCluster.isAvailable(getShortName());
		return false;
	}

	private String[] getAttributeArray(String dataAttributes) {
		String indices[] = dataAttributes.split(",");
		String selectedAttributes[] = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			selectedAttributes[i] = attributeArray[Integer.parseInt(indices[i])];
		}
		return selectedAttributes;
	}
}
