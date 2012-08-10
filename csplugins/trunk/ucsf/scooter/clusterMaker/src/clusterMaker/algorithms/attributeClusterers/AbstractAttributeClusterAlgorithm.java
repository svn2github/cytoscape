/* vim: set ts=2: */
/**
 * Copyright (c) 2011 The Regents of the University of California.
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
package clusterMaker.algorithms.attributeClusterers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.silhouette.SilhouetteCalculator;
import clusterMaker.algorithms.attributeClusterers.silhouette.Silhouettes;

/**
 * This abstract class is the base class for all of the attribute clusterers provided by
 * clusterMaker.  Fundamentally, an attribute clusterer is an algorithm which functions to
 * partition either nodes or attributes based on the similarity between them.
 */
public abstract class AbstractAttributeClusterAlgorithm {
	// Instance variables common to all algorithms
	protected List<String>attrList;
	protected CyLogger logger;
	protected Matrix matrix;
	protected DistanceMetric metric;
	protected TaskMonitor monitor;
	protected Integer[] rowOrder;
	protected String weightAttributes[] = null;
	protected int kMax = -1;
	protected boolean initializeNearCenter = false;
	private Silhouettes[] silhouetteResults = null;

	protected boolean adjustDiagonals = false;
	protected boolean debug = false;
	protected boolean createGroups = false;
	protected boolean ignoreMissing = true;
	protected boolean interimRun = false;
	protected boolean selectedOnly = false;
	protected boolean zeroMissing = false;
	protected boolean useSilhouette = false;
	protected AbstractClusterAlgorithm clusterAlgorithm = null;

	public Matrix getMatrix() { return matrix; }
	public DistanceMetric getMetric() { return metric; }

	public void setCreateGroups(boolean val) { createGroups = val; }
	public void setIgnoreMissing(boolean val) { ignoreMissing = val; }
	public void setSelectedOnly(boolean val) { selectedOnly = val; }
	public void setAdjustDiagonals(boolean val) { adjustDiagonals = val; }
	public void setZeroMissing(boolean val) { zeroMissing = val; }
	public void setDebug(boolean val) { debug = val; }
	public void setUseSilhouette(boolean val) { useSilhouette = val; }
	public void setKMax(int val) { kMax = val; }
	public void setClusterInterface(AbstractClusterAlgorithm alg) { clusterAlgorithm = alg; }
	public void setInitializeNearCenter(boolean val) { initializeNearCenter = val; }

	/**
 	 * This method is called by all of the attribute cluster algorithms to update the
 	 * results attributes in the network.
 	 *
 	 * @param cluster_type the cluster type to indicate write into the CLUSTER_TYPE_ATTRIBUTE
 	 */
	protected void updateAttributes(String cluster_type) {
		// Update the network attribute and make it hidden
		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		String netID = Cytoscape.getCurrentNetwork().getIdentifier();
		List<String>params = new ArrayList<String>();

		if (zeroMissing)
			params.add("zeroMissing");

		netAttr.setAttribute(netID, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, cluster_type);

		if (matrix.isTransposed()) {
			netAttr.setListAttribute(netID, ClusterMaker.CLUSTER_ATTR_ATTRIBUTE, attrList);
		} else {
			netAttr.setListAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE, attrList);
			if (matrix.isSymmetrical()) {
				netAttr.setListAttribute(netID, ClusterMaker.CLUSTER_ATTR_ATTRIBUTE, attrList);
				netAttr.setAttribute(netID, ClusterMaker.CLUSTER_EDGE_ATTRIBUTE, weightAttributes[0]);
				if (adjustDiagonals) {
					params.add("diagonals="+matrix.getValue(0,0));
				}
			}
		}
		netAttr.setListAttribute(netID, ClusterMaker.CLUSTER_PARAMS_ATTRIBUTE, params);

		String[] rowArray = matrix.getRowLabels();
		ArrayList<String> orderList = new ArrayList<String>();

		String[] columnArray = matrix.getColLabels();
		ArrayList<String>columnList = new ArrayList<String>(columnArray.length);

		for (int i = 0; i < rowOrder.length; i++) {
			orderList.add(rowArray[rowOrder[i]]);
			if (matrix.isSymmetrical())
				columnList.add(rowArray[rowOrder[i]]);
		}

		if (!matrix.isSymmetrical()) {
			for (int col = 0; col < columnArray.length; col++) {
				columnList.add(columnArray[col]);
			}
		}
		// System.out.println("Order: "+orderList);

		if (matrix.isTransposed()) {
			// We did an Array cluster -- output the calculated array order
			// and the actual node order
			netAttr.setListAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE, orderList);

			// Don't override the columnlist if a node order already exists
			if (!netAttr.hasAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE))
				netAttr.setListAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE, columnList);
		} else {
			netAttr.setListAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE, orderList);
			// Don't override the columnlist if a node order already exists
			if (!netAttr.hasAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE))
				netAttr.setListAttribute(netID, ClusterMaker.ARRAY_ORDER_ATTRIBUTE, columnList);
		}

	}

	/**
 	 * This method resets (clears) all of the existing network attributes.
 	 */
	@SuppressWarnings("unchecked")
	protected void resetAttributes() {
		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		String netID = Cytoscape.getCurrentNetwork().getIdentifier();

		// Remove the attributes that are lingering
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
		if (netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_PARAMS_ATTRIBUTE))
			netAttr.deleteAttribute(netID, ClusterMaker.CLUSTER_PARAMS_ATTRIBUTE);

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
	}

	/**
 	 * This method is used to determine if the current network has data corresponding to a
 	 * particular cluster type.
 	 *
 	 * @param type the cluster type to check for
 	 * @return 'true' if this network has data for the designated type
 	 */
	public static boolean isAvailable(String type) {
		String netID = Cytoscape.getCurrentNetwork().getIdentifier();
		CyAttributes netAttr = Cytoscape.getNetworkAttributes();
		if (!netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE))
			return false;

		if (!netAttr.getStringAttribute(netID, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE).equals(type))
			return false;

		// OK, we need either a node list or an attribute list
		if (!netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_ATTR_ATTRIBUTE) && 
		    !netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE))
			return false;

		// Finally, we need to have the cluster attributes themselves
		if (!netAttr.hasAttribute(netID, ClusterMaker.NODE_ORDER_ATTRIBUTE) &&
		    !netAttr.hasAttribute(netID, ClusterMaker.CLUSTER_NODE_ATTRIBUTE))
			return false;

		return true;
	}

	/**
 	 * This protected method is called to create all of our groups (if desired).
 	 * It is used by all of the k-clustering algorithms.
 	 *
 	 * @param nClusters the number of clusters we created
 	 * @param cluster the list of values and the assigned clusters
 	 */
	protected void createGroups(int nClusters, int[] clusters) {
		if (matrix.isTransposed()) {
			return;
		}

		if (monitor != null) 
			monitor.setStatus("Creating groups");

		HashMap<String,List<CyNode>> groupMap = new HashMap<String,List<CyNode>>();
		attrList = new ArrayList<String>(matrix.nRows());
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


	/**
 	 * Common code for the k-cluster algorithms with silhouette
 	 */

	// This should be overridden by any k-cluster implementation
	protected int kcluster(int nClusters, int nIterations, Matrix matrix, 
	                       DistanceMetric metric, int[] clusters) {
		return 0;
	}
	/**
 	 * This is the common entry point for k-cluster algorithms.
 	 *
 	 * @param nClusters the number of clusters (k)
 	 * @param nIterations the number of iterations to use
 	 * @param transpose whether we're doing rows (GENE) or columns (ARRY)
 	 * @param algorithm the algorithm type
 	 * @return a string with all of the results
 	 */
	public String cluster(int nClusters, int nIterations, boolean transpose, String algorithm) {
		String keyword = "GENE";
		if (transpose) keyword = "ARRY";

		for (int att = 0; att < weightAttributes.length; att++)
			if (debug)
				logger.debug("Attribute: '"+weightAttributes[att]+"'");

		if (monitor != null) 
			monitor.setStatus("Creating distance matrix");

		// Create the matrix
		matrix = new Matrix(weightAttributes, transpose, ignoreMissing, selectedOnly);
		logger.info("cluster matrix has "+matrix.nRows()+" rows");

		// Create a weight vector of all ones (we don't use individual weighting, yet)
		matrix.setUniformWeights();

		if (monitor != null) 
			monitor.setStatus("Clustering...");

		if (useSilhouette) {
			TaskMonitor saveMonitor = monitor;
			monitor = null;

			silhouetteResults = new Silhouettes[kMax];

			int nThreads = Runtime.getRuntime().availableProcessors()-1;
			if (nThreads > 1)
				runThreadedSilhouette(kMax, nIterations, nThreads, saveMonitor);
			else
				runLinearSilhouette(kMax, nIterations, saveMonitor);

			if (halted()) return "Halted by user";

			// Now get the results and find our best k
			double maxSil = Double.MIN_VALUE;
			for (int kEstimate = 2; kEstimate < kMax; kEstimate++) {
				double sil = silhouetteResults[kEstimate].getMean();
				// System.out.println("Average silhouette for "+kEstimate+" clusters is "+sil);
				if (sil > maxSil) {
					maxSil = sil;
					nClusters = kEstimate;
				}
			}
			monitor = saveMonitor;
			// System.out.println("maxSil = "+maxSil+" nClusters = "+nClusters);
		}

		int[] clusters = new int[matrix.nRows()];

		if (halted()) return "Halted by user";

		// Cluster
		int nClustersFound = kcluster(nClusters, nIterations, matrix, metric, clusters);
		if (halted()) return "Halted by user";
		
		// TODO Change other algorithms s.t. the number of clusters found is returned
		if (nClusters == 0) nClusters = nClustersFound;

		// OK, now run our silhouette on our final result
		Silhouettes sResult = SilhouetteCalculator.calculate(matrix, metric, clusters);
		// System.out.println("Average silhouette = "+sResult.getAverageSilhouette());
		// SilhouetteUtil.printSilhouette(sResult, clusters);

		if (!matrix.isTransposed())
			createGroups(nClusters, clusters);

	/*
 		Ideally, we would sort our clusters based on size, but for some reason
		this isn't working...
		renumberClusters(nClusters, clusters);
	*/
		// NB  HOPACH clusters should not be re-ordered

		rowOrder = matrix.indexSort(clusters, clusters.length);
		// System.out.println(Arrays.toString(rowOrder));
		// Update the network attributes
		updateAttributes(algorithm);
		
		// FIXME For HOPACH, nClusters is determined by the algorithm, and is neither estimated nor predefined... 

		String resultString =  "Created "+nClusters+" clusters with average silhouette = "+sResult.getMean();
		logger.info(resultString);
		
		String s = "Clusters: ";
		for (int i = 0; i < clusters.length; ++i) {
			s += clusters[i] + ", ";
		}
		logger.info(s);
		
		return resultString;
	}
	
	protected int[] chooseRandomElementsAsCenters(int nElements, int nClusters) {
		int[] centers = new int[nClusters];

		for (int i = 0; i < nClusters; i++) {
			centers[i] = (int) Math.floor(Math.random() * nElements);
		}
		return centers;
	}

	protected int[] chooseCentralElementsAsCenters(int nElements, int nClusters, double[][] distances) {
		int[] centers = new int[nClusters];
		
		// calculate normalized distances
		double[][] normalized = new double[nElements][nElements];
		for (int i = 0; i < nElements; i++) {
			double sum = 0;
			for (int j = 0; j < nElements; j++) {
				double x = distances[i][j];
				normalized[i][j] = x;
				sum += x;
			}
			for (int j = 0; j < nElements; j++) {
				normalized[i][j] /= sum;
			}
		}
		
		// sum the normalized distances across all rows
		// setup key-value pairs with summed normalized distances as keys
		// and element indices as values
		KeyValuePair[] pairs = new KeyValuePair[nElements];
		for (int i = 0; i < nElements; i++) {
			pairs[i] = new KeyValuePair(0.0, i);
			for (int j = 0; j < nElements; j++) {
				pairs[i].key += normalized[i][j];
			}
		}
		
		// sort the summed normalized distances
		// for choosing the elements that are closest overall to all other elements
		Comparator<KeyValuePair> comparator = new KeyValuePairComparator();
		Arrays.sort(pairs, comparator);
		
		// initialize the centers
		for (int i = 0; i < nClusters; i++) {
			centers[i] = pairs[i].value;
			//System.out.println("i = " + i + ", center = " + centers[i]);
		}
		
		return centers;
	}

	private void renumberClusters(int nClusters, int [] clusters) {
		int[] clusterSizes = new int[nClusters];
		Arrays.fill(clusterSizes, 0);
		for (int row = 0; row < clusters.length; row++) {
			clusterSizes[clusters[row]] += 1;
		}

		Integer[] sortedClusters = new Integer[nClusters];
		for (int cluster = 0; cluster < nClusters; cluster++) {
			sortedClusters[cluster] = cluster;
		}


		// OK, now sort
		Arrays.sort(sortedClusters, new SizeComparator(clusterSizes));
		int[] clusterIndex = new int[nClusters];
		for (int cluster = 0; cluster < nClusters; cluster++) {
			clusterIndex[sortedClusters[cluster]] = cluster;
		}
		for (int row = 0; row < clusters.length; row++) {
			// System.out.println("Setting cluster for row "+ row+" to "+sortedClusters[clusters[row]]+" was "+clusters[row]);
			clusters[row] = clusterIndex[clusters[row]];
		}
		
	}

	private void runThreadedSilhouette(int kMax, int nIterations, int nThreads, TaskMonitor saveMonitor) {
		// Set up the thread pools
		ExecutorService[] threadPools = new ExecutorService[nThreads];
		for (int pool = 0; pool < threadPools.length; pool++)
			threadPools[pool] = Executors.newFixedThreadPool(1);

		// Dispatch a kmeans calculation to each pool
		for (int kEstimate = 2; kEstimate < kMax; kEstimate++) {
			int[] clusters = new int[matrix.nRows()];
			Runnable r = new RunKMeans(matrix, clusters, kEstimate, nIterations, saveMonitor);
			threadPools[(kEstimate-2)%nThreads].submit(r);
			// threadPools[0].submit(r);
		}

		// OK, now wait for each thread to complete
		for (int pool = 0; pool < threadPools.length; pool++) {
			threadPools[pool].shutdown();
			try {
				boolean result = threadPools[pool].awaitTermination(7, TimeUnit.DAYS);
			} catch (Exception e) {}
		}
	}

	private void runLinearSilhouette(int kMax, int nIterations, TaskMonitor saveMonitor) {
		for (int kEstimate = 2; kEstimate < kMax; kEstimate++) {
			int[] clusters = new int[matrix.nRows()];
			if (halted()) return;
			if (saveMonitor != null) saveMonitor.setStatus("Getting silhouette with a k estimate of "+kEstimate);
			int ifound = kcluster(kEstimate, nIterations, matrix, metric, clusters);
			silhouetteResults[kEstimate] = SilhouetteCalculator.calculate(matrix, metric, clusters);
		}
	}

	private boolean halted() {
		if (clusterAlgorithm != null)
			return clusterAlgorithm.halted();
		return false;
	}

	// private class pairing key and and value
	// abandon generic here and hard-code types, since arrays and generics do not work well in Java!
	private class KeyValuePair {
		public double key;
		public int value;
		
		public KeyValuePair(double key, int value) {
			this.key = key;
			this.value = value;
		}
	}
	
	// private class comparator for sorting key-value pairs
	private class KeyValuePairComparator implements Comparator<KeyValuePair> {
		public int compare(KeyValuePair a, KeyValuePair b) {
			if ((Double)a.key < (Double)b.key) {
				return -1;
			}
			return 1;
		}
	}

	private class SizeComparator implements Comparator <Integer> {
		int[] sizeArray = null;
		public SizeComparator(int[] a) { this.sizeArray = a; }

		public int compare(Integer o1, Integer o2) {
			if (sizeArray[o1] > sizeArray[o2]) return 1;
			if (sizeArray[o1] < sizeArray[o2]) return -1;
			return 0;
		}
	}

	private class RunKMeans implements Runnable {
		Matrix matrix;
		int[] clusters;
		int kEstimate;
		int nIterations;
		TaskMonitor saveMonitor = null;

		public RunKMeans (Matrix matrix, int[] clusters, int k, int nIterations, TaskMonitor saveMonitor) {
			this.matrix = matrix;
			this.clusters = clusters;
			this.kEstimate = k;
			this.nIterations = nIterations;
			this.saveMonitor = saveMonitor;
		}

		public void run() {
			int[] clusters = new int[matrix.nRows()];
			if (halted()) return;
			if (saveMonitor != null) saveMonitor.setStatus("Getting silhouette with a k estimate of "+kEstimate);
			int ifound = kcluster(kEstimate, nIterations, matrix, metric, clusters);
			try {
				silhouetteResults[kEstimate] = SilhouetteCalculator.calculate(matrix, metric, clusters);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}
