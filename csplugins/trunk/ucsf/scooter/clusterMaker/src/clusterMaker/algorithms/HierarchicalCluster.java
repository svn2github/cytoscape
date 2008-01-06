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
package clusterMaker.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.awt.GridLayout;
import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.task.TaskMonitor;

// clusterMaker imports

public class HierarchicalCluster extends AbstractClusterAlgorithm {
	/**
	 * Linkage types
	 */
	ClusterMethod[] linkageTypes = { ClusterMethod.AVERAGE_LINKAGE,
	                                 ClusterMethod.SINGLE_LINKAGE,
	                                 ClusterMethod.MAXIMUM_LINKAGE,
	                                 ClusterMethod.CENTROID_LINKAGE };

	DistanceMetric[] distanceTypes = { DistanceMetric.EUCLIDEAN,
	                                   DistanceMetric.CITYBLOCK,
	                                   DistanceMetric.CORRELATION,
	                                   DistanceMetric.ABS_CORRELATION,
	                                   DistanceMetric.UNCENTERED_CORRELATION,
	                                   DistanceMetric.ABS_UNCENTERED_CORRELATION,
	                                   DistanceMetric.SPEARMANS_RANK,
	                                   DistanceMetric.KENDALLS_TAU };
	String[] attributeArray = new String[1];

	ClusterMethod clusterMethod =  ClusterMethod.AVERAGE_LINKAGE;
	DistanceMetric distanceMetric = DistanceMetric.EUCLIDEAN;
	boolean transposeMatrix = false;
	String dataAttribute = null;
	TaskMonitor monitor = null;

	public HierarchicalCluster() {
		super();
		initializeProperties();
	}

	public String getShortName() {return "hierarchical";};
	public String getName() {return "Hierarchical cluster";};

	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0,1));

		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attribute");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);

		panel.add(clusterProperties.getTunablePanel());

		return panel;
	}

	protected void initializeProperties() {
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
		                                  (Object)distanceTypes, (Object)null, 0));

		// The attribute to use to get the weights
		attributeArray = getAllAttributes();

		clusterProperties.add(new Tunable("attribute",
		                                  "Attribute to use to get the data values",
		                                  Tunable.LIST, new Integer(0),
		                                  (Object)attributeArray, (Object)null, 0));

		// Whether or not to transpose the matrix
		clusterProperties.add(new Tunable("transposeMatrix",
		                                  "Transpose matrix (if using node attributes)",
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

		Tunable t = clusterProperties.get("linkage");
		if ((t != null) && (t.valueChanged() || force))
			clusterMethod = linkageTypes[((Integer) t.getValue()).intValue()];

		t = clusterProperties.get("dMetric");
		if ((t != null) && (t.valueChanged() || force))
			distanceMetric = distanceTypes[((Integer) t.getValue()).intValue()];

		t = clusterProperties.get("transposeMatrix");
		if ((t != null) && (t.valueChanged() || force))
			transposeMatrix = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("attribute");
		if ((t != null) && (t.valueChanged() || force))
			dataAttribute = attributeArray[((Integer) t.getValue()).intValue()];
	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		// Sanity check all of our settings
		// OK, go for it!
		String summary = cluster(dataAttribute, distanceMetric, transposeMatrix);
	}

	public String cluster(String weightAttribute, 
	                      DistanceMetric metric, boolean transpose) {

		String keyword = "GENE";
		if (transpose) keyword = "ARRY";

		// Create the matrix
		Matrix matrix = new Matrix(weightAttribute, transpose);

		// Create a weight vector of all ones (we don't use individual weighting, yet)
		matrix.setUniformWeights();

		// Cluster
		TreeNode[] nodeList = treeCluster(matrix, metric);

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

		// Join the nodes
		double[] nodeOrder = new double[nodeList.length];
		int[] nodeCounts = new int[nodeList.length];
		String[] nodeID = new String[nodeList.length];

		for (int node = 0; node < nodeList.length; node++) {
			int min1 = nodeList[node].getLeft();
			int min2 = nodeList[node].getRight();

			double order1;
			double order2;
			int counts1;
			int counts2;
			String ID1;
			String ID2;
			nodeID[node] = "NODE"+(node+1)+"X";
			if (min1 < 0) {
				int index1 = -min1-1;
				order1 = nodeOrder[index1];
				counts1 = nodeCounts[index1];
				ID1 = nodeID[index1];
				nodeList[node].setDistance(Math.max(nodeList[node].getDistance(), nodeList[index1].getDistance()));
			} else {
				order1 = min1;
				counts1 = 1;
				ID1 = keyword+min1+"X"; // Shouldn't this be the name of the gene/condition?
			}

			if (min2 < 0) {
				int index2 = -min2-1;
				order2 = nodeOrder[index2];
				counts2 = nodeCounts[index2];
				ID2 = nodeID[index2];
				nodeList[node].setDistance(Math.max(nodeList[node].getDistance(), nodeList[index2].getDistance()));
			} else {
				order2 = min2;
				counts2 = 1;
				ID2 = keyword+min2+"X"; // Shouldn't this be the name of the gene/condition?
			}

			nodeCounts[node] = counts1 + counts2;
			nodeOrder[node] = (counts1*order1 + counts2*order2) / (counts1 + counts2);
		}

		// Now sort based on tree structure

		// Update the network attribute "HierarchicalCluster" and make it hidden
		return "Complete";
	}

	private TreeNode[] treeCluster(Matrix matrix, DistanceMetric metric) { 

		double[][] distanceMatrix = distanceMatrix(matrix, metric);
		TreeNode[] result = null;

		switch (clusterMethod) {
			case SINGLE_LINKAGE:
				System.out.println("Calculating single linkage hierarchical cluster");
				result = pslCluster(matrix, distanceMatrix, metric);
				break;

			case MAXIMUM_LINKAGE:
				System.out.println("Calculating maximum linkage hierarchical cluster");
				result = pmlcluster(matrix.nRows(), distanceMatrix);
				break;

			case AVERAGE_LINKAGE:
				System.out.println("Calculating average linkage hierarchical cluster");
				result = palcluster(matrix.nRows(), distanceMatrix);
				break;

			case CENTROID_LINKAGE:
				System.out.println("Calculating centroid linkage hierarchical cluster");
				result = pclcluster(matrix, distanceMatrix, metric);
				break;
		}
		return result;
	}

	private TreeNode[] pslCluster(Matrix matrix, double[][] distanceMatrix, DistanceMetric metric) {
		int nRows = matrix.nRows();
		int nNodes = nRows-1;

		int[] vector = new int[nNodes];
		TreeNode[] nodeList = new TreeNode[nNodes]; 
		// Initialize
		for (int i = 0; i < nNodes; i++) {
			vector[i] = i;
			nodeList[i] = new TreeNode(Double.MAX_VALUE);
		}

		int k;
		for (int row = 0; row < nRows; row++) {
			double[] temp = new double[nNodes];
			if (distanceMatrix != null) {
					for (int j = 0; j < row; j++) 
					temp[j] = distanceMatrix[row][j];
			} else {
				for (int j = 0; j < row; j++)
					temp[j] = metric.getMetric(matrix, matrix, matrix.getWeights(), row, j);
			}
			for (int j = 0; j < row; j++) {
				k = vector[j];
				double dist = nodeList[j].getDistance();
				if (dist >= temp[j]) {
					if (dist < temp[k]) temp[k] = dist;
					nodeList[j].setDistance(temp[j]);
					vector[j] = row;
				} else if (temp[j] < temp[k]) temp[k] = temp[j];
				for (j = 0; j < row; j++) {
					dist = nodeList[j].getDistance();
					if (dist > nodeList[vector[j]].getDistance()) vector[j] = row;	
				}
			}
		}
		for (int row = 0; row < nNodes; row++)
			nodeList[row].setLeft(row);

		Arrays.sort(nodeList, new NodeComparator());

		int[] index = new int[nRows];
		for (int i = 0; i < nRows; i++) index[i] = i;
		for (int i = 0; i < nNodes; i++) {
			int j = nodeList[i].getLeft();
			k = vector[j];
			nodeList[i].setLeft(index[j]);
			nodeList[i].setRight(index[i]);
			index[k] = -i-1;
		}

		return nodeList;
	}

	private TreeNode[] pclcluster(Matrix matrix, double[][] distanceMatrix, DistanceMetric metric) {
		return null;
	}

	private TreeNode[] pmlcluster(int rows, double[][] distanceMatrix) {
		return null;
	}

	private TreeNode[] palcluster(int rows, double[][] distanceMatrix) {
		return null;
	}

	private void getAttributesList(List<String>attributeList, CyAttributes attributes, 
	                              String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING ||
			    attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
				attributeList.add(prefix+names[i]);
			}
		}
	}

	private String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		attributeList.add("-- select attribute --");
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"node.");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(),"edge.");
		return attributeList.toArray(attributeArray);
	}
		

	class TreeNode {
		int left;
		int right;
		double distance;

		public TreeNode(int left, int right, double distance) {
			this.left = left;
			this.right = right;
			this.distance = distance;
		};

		public TreeNode(double distance) {
			this.distance = distance;
		}

		double getDistance() { return this.distance; }
		void setDistance(double dist) { this.distance = dist; }

		int getLeft() { return this.left; }
		void setLeft(int left) { this.left = left; }

		int getRight() { return this.right; }
		void setRight(int right) { this.right = right; }
	}

	enum ClusterMethod {
		SINGLE_LINKAGE("pairwise single-linkage"),
		MAXIMUM_LINKAGE("pairwise maximum-linkage"),
		AVERAGE_LINKAGE("pairwise average-linkage"),
		CENTROID_LINKAGE("pairwise centroid-linkage");
	
		private String keyword;
	
		ClusterMethod(String keyword) {
			this.keyword = keyword;
		}
	
		public String toString() {
			return this.keyword;
		}
	}

	class NodeComparator implements Comparator<TreeNode> {

		public int compare(TreeNode o1, TreeNode o2) {
			if (o1.getDistance() < o2.getDistance()) return -1;
			if (o1.getDistance() > o2.getDistance()) return 1;
			return 0;
		}
		boolean equals() { return false; };
	}

}
