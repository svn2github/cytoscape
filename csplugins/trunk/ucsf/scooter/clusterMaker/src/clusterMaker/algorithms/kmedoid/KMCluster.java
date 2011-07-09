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
package clusterMaker.algorithms.kmedoid;

import java.awt.GridLayout;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

// clusterMaker imports
import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.kmeans.KCluster;
import clusterMaker.algorithms.hierarchical.DistanceMetric;
import clusterMaker.algorithms.hierarchical.EisenCluster;
import clusterMaker.algorithms.hierarchical.Matrix;

public class KMCluster {
	CyLogger logger;
	TaskMonitor monitor;
	String weightAttributes[] = null;
	DistanceMetric metric;
	boolean createGroups = false;
	boolean ignoreMissing = false;
	boolean selectedOnly = false;
	boolean debug = true;
	Random random = null;

	public KMCluster(String weightAttributes[], DistanceMetric metric, CyLogger log, TaskMonitor monitor) {
		this.logger = log;
		this.weightAttributes = weightAttributes;
		this.metric = metric;
		this.monitor = monitor;
		resetAttributes();
	}

	public void setCreateGroups(boolean val) { createGroups = val; }
	public void setIgnoreMissing(boolean val) { ignoreMissing = val; }
	public void setSelectedOnly(boolean val) { selectedOnly = val; }
	public void setDebug(boolean val) { debug = val; }

	public String cluster(int nClusters, int nIterations, boolean transpose) {
		String keyword = "GENE";
		if (transpose) keyword = "ARRY";
		debug = true;

		for (int att = 0; att < weightAttributes.length; att++)
			if (debug)
				logger.debug("Attribute: '"+weightAttributes[att]+"'");

		if (monitor != null) 
			monitor.setStatus("Creating distance matrix");

		// Create the matrix
		Matrix matrix = new Matrix(weightAttributes, transpose, ignoreMissing, selectedOnly);
		logger.info("cluster matrix has "+matrix.nRows()+" rows");

		// Create a weight vector of all ones (we don't use individual weighting, yet)
		matrix.setUniformWeights();

		int[] clusters = new int[matrix.nRows()];

		if (monitor != null) 
			monitor.setStatus("Clustering...");

		// Cluster
		int ifound = kmedoid(nClusters, nIterations, matrix, metric, clusters);

		HashMap<String,List<CyNode>> groupMap = new HashMap<String,List<CyNode>>();
		ArrayList<String> attrList = new ArrayList<String>(matrix.nRows());
		// Create the attribute list
		for (int cluster = 0; cluster < nClusters; cluster++) {
			List<CyNode> memberList = new ArrayList<CyNode>();
			for (int i = 0; i < matrix.nRows(); i++) {
				if (clusters[i] == cluster) {
					attrList.add(matrix.getRowLabel(i)+"\t"+cluster);
					if (debug)
						logger.debug(matrix.getRowLabel(i)+"\t"+cluster);
					memberList.add(matrix.getRowNode(i));
				}
			}
			groupMap.put("Cluster_"+cluster, memberList);
		}

		if (!matrix.isTransposed()) {
			if (monitor != null) 
				monitor.setStatus("Creating groups");

			List<String> groupNames = new ArrayList<String>();

			if (createGroups) {
				// Create our groups
				CyGroup group = null;
				for (String clusterName: groupMap.keySet()) {
					List<CyNode> memberList = groupMap.get(clusterName);
					groupNames.add(clusterName);

					if (debug)
						logger.debug("Creating group: "+clusterName);

					// Create the group
					group = CyGroupManager.createGroup(clusterName, memberList, null);
					if (group != null) 
						CyGroupManager.setGroupViewer(group, "namedSelection", Cytoscape.getCurrentNetworkView(), false);
				}
				CyGroupManager.setGroupViewer(group, "namedSelection", Cytoscape.getCurrentNetworkView(), true);
			}

			CyAttributes netAttr = Cytoscape.getNetworkAttributes();
			String netID = Cytoscape.getCurrentNetwork().getIdentifier();
			netAttr.setListAttribute(netID, ClusterMaker.GROUP_ATTRIBUTE, groupNames);
		}

		// Sort the appropriate list (Nodes or Attributes)
		Integer rowOrder[] = matrix.indexSort(clusters, clusters.length);
		/*
    for (int i = 0; i < rowOrder.length; i++) {
      logger.debug(""+i+": "+matrix.getRowLabel(rowOrder[i].intValue()));
    }
		*/

		// Update the network attributes
		EisenCluster.updateAttributes(matrix, attrList, weightAttributes, rowOrder, "kmedoid", false, false);

		return "Complete";
	}

	public void resetAttributes() {
		// Update the network attribute "HierarchicalCluster" and make it hidden
		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		String netID = Cytoscape.getCurrentNetwork().getIdentifier();

		// See if we have any old groups in this network
		if (netAttr.hasAttribute(netID, ClusterMaker.GROUP_ATTRIBUTE)) {
			List<String>clList = (List<String>)netAttr.getListAttribute(netID, ClusterMaker.GROUP_ATTRIBUTE);
			for (String groupName: clList) {
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group != null)
					CyGroupManager.removeGroup(group);
			}
			netAttr.deleteAttribute(netID, ClusterMaker.GROUP_ATTRIBUTE);
		}

		// Clear of the other attributes
		if (netAttr.hasAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE))
			netAttr.deleteAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE);
		if (netAttr.hasAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE))
			netAttr.deleteAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE);
		if (netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_ATTR_ATTRIBUTE))
			netAttr.deleteAttribute(netID, ClusterMaker.CLUSTER_ATTR_ATTRIBUTE);
		if (netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE))
			netAttr.deleteAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE);
		if (netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_EDGE_ATTRIBUTE))
			netAttr.deleteAttribute(netID, ClusterMaker.CLUSTER_EDGE_ATTRIBUTE);
		if (netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE))
			netAttr.deleteAttribute(netID, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE);
	}

	public int kmedoid(int nClusters, int nIterations, Matrix matrix, DistanceMetric metric, int[] clusterId) {

		if (monitor != null)
			monitor.setPercentCompleted(0);

		int iteration = 0;

		// Start by calculating the pairwise distances
		double[][] distances = new double[matrix.nRows()][matrix.nRows()];
		for (int i = 0; i < matrix.nRows(); i++) {
			for (int j = i; j < matrix.nRows(); j++) {
				distances[i][j] = metric.getMetric(matrix, matrix, matrix.getWeights(), i, j);
			}
		}

		int[] centers = chooseRandomElementsAsCenters(matrix, nClusters);
		int[] oldCenters = null;
		// outputCenters(centers);

		while (centersChanged(oldCenters, centers)) {
			oldCenters = centers;
			// outputClusterId(clusterId);
			assignPointsToClosestCenter(oldCenters, distances, clusterId);
			centers = calculateCenters(nClusters, matrix, metric, clusterId);
			// outputCenters(centers);

			if (iteration++ >= nIterations) break;
		}

		// System.out.println("ifound = "+ifound+", error = "+error);
  	return 1;
	}

	private int[] chooseRandomElementsAsCenters(Matrix matrix, int nClusters) {
		int[] centers = new int[nClusters];

		for (int i = 0; i < nClusters; i++) {
			centers[i] = (int) Math.floor(Math.random() * matrix.nRows());
		}
		return centers;
	}

	private void assignPointsToClosestCenter(int[] centers, double[][] distances, int[] clusterId) {
		for (int row = 0; row < distances.length; row++) {
			double minDistance = Double.MAX_VALUE;
			for (int cluster = 0; cluster < centers.length; cluster++) {
				double distance = distances[row][centers[cluster]];
				if (distance < minDistance) {
					clusterId[row] = cluster;
					minDistance = distance;
				}
			}
		}
	} 

	private int[] calculateCenters(int nClusters, Matrix matrix, DistanceMetric metric, int[] clusterId) {
		int[] newCenters = new int[nClusters];
		Matrix cData = new Matrix(nClusters, matrix.nRows());

		// Calculate all of the cluster centers
		KCluster.getClusterMeans(nClusters, matrix, cData, clusterId);

		// For each cluster, find the closest row
		for (int cluster = 0; cluster < nClusters; cluster++) {
			newCenters[cluster] = findMedoid(matrix, cData, cluster, clusterId);
		}
		return newCenters;
	}

	private int findMedoid(Matrix matrix, Matrix cdata, int cluster, int[] clusterid) {
		double minDistance = Double.MAX_VALUE;
		int medoid = -1;
		for (int row = 0; row < matrix.nRows(); row++) {
			if (clusterid[row] == cluster) {
				double distance = metric.getMetric(matrix, cdata, matrix.getWeights(), row, cluster);
				if (distance < minDistance)
					medoid = row;
			}
		}
		return medoid;
	}

	private boolean centersChanged(int[] oldCenters, int[] centers) {
		if (oldCenters == null || centers == null) return true;

		for (int i = 0; i < oldCenters.length; i++) {
			if (oldCenters[i] != centers[i]) return true;
		}
		return false;
	}

	private void outputCenters(int[] centers) {
		System.out.println("Centroid points: ");
		for (int i = 0; i < centers.length; i++) {
			System.out.println(" "+i+": "+centers[i]);
		}
	}

	private void outputClusterId(int[] clusterId) {
		System.out.println("Cluster IDs: ");
		for (int i = 0; i < clusterId.length; i++) {
			System.out.println(" "+i+": "+clusterId[i]);
		}
	}

	private double uniform() {
		if (random == null) {
			Date date = new Date();
			random = new Random(date.getTime());
		}
		return random.nextDouble();
	}
}
