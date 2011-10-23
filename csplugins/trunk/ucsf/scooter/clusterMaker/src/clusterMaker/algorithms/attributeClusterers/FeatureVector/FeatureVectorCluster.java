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
package clusterMaker.algorithms.attributeClusterers.FeatureVector;

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
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.AbstractAttributeClusterer;
import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.algorithms.attributeClusterers.Matrix;
import clusterMaker.ui.ClusterViz;

// clusterMaker imports

public class FeatureVectorCluster extends AbstractAttributeClusterer implements TunableListener {

	boolean createNewNetwork = true;
	double edgeCutoff = 0.001;
	String dataAttributes = null;
	String edgeAttribute = null;
	final static String interaction = "distance";

	public FeatureVectorCluster() {
		super();
		logger = CyLogger.getLogger(FeatureVectorCluster.class);
		initializeProperties();
	}

	public String getShortName() {return "featurevector";};
	public String getName() {return "Create Correlation Network from Node Attributes";};

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = getNodeAttributes();
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
		                                  (Object)Matrix.distanceTypes, (Object)null, 0));

		clusterProperties.add(new Tunable("attributeListGroup",
		                                  "Source for array data",
		                                  Tunable.GROUP, new Integer(1)));

		// The attribute to use to get the weights
		attributeArray = getNodeAttributes();
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
		                                  Tunable.BOOLEAN, new Boolean(selectedOnly)));

		// For expression data, we might want to exclude missing data
		clusterProperties.add(new Tunable("ignoreMissing",
		                                  "Ignore nodes/edges with no data",
		                                  Tunable.BOOLEAN, new Boolean(ignoreMissing)));

		// Whether to create a new network or add values to existing network
		Tunable t = new Tunable("createNewNetwork",
		                        "Create a new network",
		                        Tunable.BOOLEAN, new Boolean(createNewNetwork));
		t.addTunableValueListener(this);
		clusterProperties.add(t);

		// If we're creating edges, do we have a cut-off value?
		t = new Tunable("edgeCutoff", "Only create edges if nodes are closer than this",
		                Tunable.DOUBLE, new Double(edgeCutoff));
		if (!createNewNetwork)
			t.setImmutable(true);
		clusterProperties.add(t);

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
		                                  Tunable.BOOLEAN, new Boolean(zeroMissing)));

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void tunableChanged(Tunable tunable) {
		if (tunable.getName().equals("createNewNetwork")) {
			createNewNetwork = ((Boolean) tunable.getValue()).booleanValue();
			Tunable t = clusterProperties.get("edgeCutoff");
			if (createNewNetwork)
				t.setImmutable(false);
			else
				t.setImmutable(true);
		}
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);

		Tunable t = clusterProperties.get("dMetric");
		if ((t != null) && (t.valueChanged() || force))
			distanceMetric = Matrix.distanceTypes[((Integer) t.getValue()).intValue()];

		t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("ignoreMissing");
		if ((t != null) && (t.valueChanged() || force))
			ignoreMissing = ((Boolean) t.getValue()).booleanValue();

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

		t = clusterProperties.get("createNewNetwork");
		if ((t != null) && (t.valueChanged() || force))
			createNewNetwork = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("edgeCutoff");
		if ((t != null) && (t.valueChanged() || force))
			edgeCutoff = ((Double) t.getValue()).doubleValue();

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

		if (monitor != null) {
			monitor.setPercentCompleted(0);
			monitor.setStatus("Initializaing");
		}

		// Create the matrix
		Matrix matrix = new Matrix(attributeArray, false, ignoreMissing, selectedOnly);

		if (monitor != null) {
			monitor.setPercentCompleted(1);
			monitor.setStatus("Calculating edge distances");
			if (canceled) return;
		}

		// Create a weight vector of all ones (we don't use individual weighting, yet)
		matrix.setUniformWeights();

		// Handle special cases
		if (zeroMissing)
			matrix.setMissingToZero();

		int nNodes = matrix.nRows();

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		// For each node, get the distance to all other nodes
		double maxdistance = Double.MIN_VALUE;
		double mindistance = Double.MAX_VALUE;

		double distanceMatrix[][] = new double[nNodes][nNodes];
		for (int i = 0; i < nNodes; i++) {
			for (int j = i+1; j < nNodes; j++) {
 				double distance = distanceMetric.getMetric(matrix, matrix, matrix.getWeights(), i, j);
				maxdistance = Math.max(maxdistance, distance);
				mindistance = Math.min(mindistance, distance);
				distanceMatrix[i][j] = distance;
			}
			if (monitor != null) {
				if (canceled) return;
				monitor.setPercentCompleted((int)(25 * (double)i/(double)nNodes));
			}
		}

		if (monitor != null) {
			monitor.setStatus("Assigning values to edges");
		}

		int[] nodeArray = new int[2];
		List<CyEdge> edgeList = new ArrayList<CyEdge>();
		double scale = maxdistance - mindistance;

		/* Performance tuning -- what's taking all the time!
		long time = System.currentTimeMillis();
		long nodeFetchTime = 0L;
		long edgeFetchTime = 0L;
		long edgeCreateTime = 0L;
		long networkAddTime = 0L;
		long setAttributeTime = 0L;
		int newEdges = 0;
		*/

		for (int i = 0; i < nNodes; i++) {
			for (int j = i+1; j < nNodes; j++) {
				// time = System.currentTimeMillis();
				double distance = (distanceMatrix[i][j]-mindistance)/scale;
				CyNode source = Cytoscape.getCyNode(matrix.getRowLabel(i));
				CyNode target = Cytoscape.getCyNode(matrix.getRowLabel(j));
				nodeArray[0] = source.getRootGraphIndex();
				nodeArray[1] = target.getRootGraphIndex();

				// nodeFetchTime += System.currentTimeMillis()-time;

				if (createNewNetwork == true && distance > edgeCutoff)
					continue;

				// time = System.currentTimeMillis();
				CyEdge edge;
				if (createNewNetwork == true) {
					edge = myCreateEdge(source, target, edgeAttributes);
					edgeList.add(edge);
				} else {
					int[] edgeArray = network.getConnectingEdgeIndicesArray(nodeArray);
					// edgeFetchTime += System.currentTimeMillis()-time;
					// time = System.currentTimeMillis();
					if (edgeArray == null || edgeArray.length == 0) {
						continue;
					} else {
						edge = (CyEdge)network.getEdge(edgeArray[0]);
						// edgeFetchTime += System.currentTimeMillis()-time;
					}
				}
				if (edge == null) continue;
				// time = System.currentTimeMillis();
				edgeAttributes.setAttribute(edge.getIdentifier(), edgeAttribute, distance);
				// setAttributeTime += System.currentTimeMillis()-time;
			}
			if (monitor != null)  {
				if (canceled) return;
				monitor.setPercentCompleted((int)(25 + (75 * (double)i/(double)nNodes)));
			}
		}

		// If we're supposed to, create the new network
		if (createNewNetwork) {
			List nodeList = network.nodesList();
			CyNetwork net = Cytoscape.createNetwork(nodeList, edgeList, network.getTitle()+"--clustered",network,false);
			// Create the network view
			CyNetworkView view = Cytoscape.createNetworkView(net);
			// If available, do a force-directed layout
			CyLayoutAlgorithm alg = CyLayouts.getLayout("force-directed");
			if (alg != null)
				view.applyLayout(alg);
		}

		/*
		System.out.println("Created "+newEdges+" edges");
		System.out.println("Edge creation time: "+edgeCreateTime+"ms");
		System.out.println("Network add time: "+networkAddTime+"ms");
		System.out.println("Edge fetch time: "+edgeFetchTime+"ms");
		System.out.println("Node fetch time: "+nodeFetchTime+"ms");
		System.out.println("Set attribute time: "+setAttributeTime+"ms");
		*/

		if (monitor != null)
			monitor.setStatus("Complete");

		// Tell any listeners that we're done
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}

	public boolean isAvailable() {
		return false;
	}

	/**
 	 * Cytoscape doesn't provide us with an easy way to create an edge without searching
 	 * for it first.  Since we've already searched for it, we are absolutely certain
 	 * by this point that the edge doesn't exist, so we can save significant time by
 	 * just going ahead and creating it.
 	 */
	private CyEdge myCreateEdge(CyNode source, CyNode target, CyAttributes edgeAttributes) {
		int rootEdge = Cytoscape.getRootGraph().createEdge(source, target);
		CyEdge edge = (CyEdge) Cytoscape.getRootGraph().getEdge(rootEdge);

		// create the edge id
		String edge_name = CyEdge.createIdentifier(source.getIdentifier(),
		                                           interaction,
		                                           target.getIdentifier());
		edge.setIdentifier(edge_name);

		edgeAttributes.setAttribute(edge_name, Semantics.INTERACTION, interaction);
		edgeAttributes.setAttribute(edge_name, Semantics.CANONICAL_NAME, edge_name);
		return edge;
	}
}
