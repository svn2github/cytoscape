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
import java.util.List;
import java.awt.GridLayout;
import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.task.TaskMonitor;

// clusterMaker imports

public class EisenCluster {

	public static String cluster(String weightAttributes[], DistanceMetric metric, 
	                      ClusterMethod clusterMethod, boolean transpose) {

		String keyword = "GENE";
		if (transpose) keyword = "ARRY";

		// Create the matrix
		Matrix matrix = new Matrix(weightAttributes, transpose);

		// Create a weight vector of all ones (we don't use individual weighting, yet)
		matrix.setUniformWeights();

		// Cluster
		TreeNode[] nodeList = treeCluster(matrix, metric, clusterMethod);

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

	private static TreeNode[] treeCluster(Matrix matrix, DistanceMetric metric, ClusterMethod clusterMethod) { 

		double[][] distanceMatrix = matrix.getDistanceMatrix(metric);
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

	/**
 	 * The pslcluster routine performs single-linkage hierarchical clustering, using
 	 * either the distance matrix directly, if available, or by calculating the
 	 * distances from the data array. This implementation is based on the SLINK
 	 * algorithm, described in:
 	 * Sibson, R. (1973). SLINK: An optimally efficient algorithm for the single-link
 	 * cluster method. The Computer Journal, 16(1): 30-34.
 	 * The output of this algorithm is identical to conventional single-linkage
 	 * hierarchical clustering, but is much more memory-efficient and faster. Hence,
 	 * it can be applied to large data sets, for which the conventional single-
 	 * linkage algorithm fails due to lack of memory.
 	 *
 	 * @param matrix the data matrix containing the data and labels
 	 * @param distanceMatrix the distances that will be used to actually do the clustering.
 	 * @param metric the distance metric to be used.
 	 * @return the array of TreeNode's that describe the hierarchical clustering solution, or null if
 	 * it it files for some reason.
 	 **/

	private static TreeNode[] pslCluster(Matrix matrix, double[][] distanceMatrix, DistanceMetric metric) {
		int nRows = matrix.nRows();
		int nNodes = nRows-1;

		System.out.println("pslCluster: nRows = "+nRows+", nNodes = "+nNodes);
		if (distanceMatrix != null)
			System.out.println("pslCluster: distranceMatrix is not null");
		else
			System.out.println("pslCluster: distranceMatrix is null!");

		int[] vector = new int[nNodes];
		TreeNode[] nodeList = new TreeNode[nNodes]; 
		// Initialize
		for (int i = 0; i < nNodes; i++) {
			vector[i] = i;
			nodeList[i] = new TreeNode(Double.MAX_VALUE);
		}

		int k = 0;
		for (int row = 0; row < nNodes; row++) {
			double[] temp = new double[nNodes];
			if (distanceMatrix != null) {
				for (int j = 0; j < row; j++) temp[j] = distanceMatrix[row][j];
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
				System.out.println("pslCluster: nRows = "+nRows+", nNodes = "+nNodes+", j = "+j+", row="+row+", vector["+j+"] = "+vector[j]);
			}
			for (int j = 0; j < row; j++) {
				double dist = nodeList[j].getDistance();
				if (dist >= nodeList[vector[j]].getDistance()) vector[j] = row;	
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

	/**
 	 * The pclcluster routine performs clustering, using pairwise centroid-linking
 	 * on a given set of gene expression data, using the distrance metric given by metric.
 	 *
 	 * @param matrix the data matrix containing the data and labels
 	 * @param distanceMatrix the distances that will be used to actually do the clustering.
 	 * @param metric the distance metric to be used.
 	 * @return the array of TreeNode's that describe the hierarchical clustering solution, or null if
 	 * it it files for some reason.
 	 **/
	private static TreeNode[] pclcluster(Matrix matrix, double[][] distanceMatrix, DistanceMetric metric) {
		int nRows = matrix.nRows();
		int nColumns = matrix.nColumns();
		int nNodes = nRows-1;
		int mask[][] = new int[matrix.nRows()][matrix.nColumns()];

		System.out.println("pclCluster: nRows = "+nRows+", nNodes = "+nNodes);
		if (distanceMatrix != null)
			System.out.println("pclCluster: distranceMatrix is not null");
		else
			System.out.println("pclCluster: distranceMatrix is null!");

		TreeNode[] nodeList = new TreeNode[nNodes]; 

		// Initialize
		int distID[] = new int[nNodes];
		for (int row = 0; row < nNodes; row++) {
			distID[row] = row;
			for (int col = 0; col < nColumns; col++) {
				if (matrix.hasValue(row, col))
					mask[row][col] = 1;
				else
					mask[row][col] = 0;
			}
		}

		Matrix newData = new Matrix(matrix);
		int pair[] = new int[2];

		for (int inode = 0; inode < nNodes; inode++) {
			// find the pair with the shortest distance
			pair[0] = 1; pair[1] = 0;
			nodeList[inode].setDistance(findClosestPair(nNodes-inode, distanceMatrix, pair));

			int isInt = pair[0];
			int jsInt = pair[1];
			nodeList[inode].setLeft(distID[jsInt]);
			nodeList[inode].setRight(distID[isInt]);
	
			// make node js the new node
			for (int col = 0; col < nColumns; col++) {
				double jsValue = newData.doubleValue(jsInt, col);
				double isValue = newData.doubleValue(isInt, col);
				newData.setValue(jsInt, col, jsValue*mask[jsInt][col] + isValue*mask[isInt][col]);
				mask[jsInt][col] += mask[isInt][col];
				if (mask[jsInt][col] != 0) {
					newData.setValue(jsInt, col, newData.doubleValue(jsInt, col) / mask[jsInt][col]);
				}
			}

			for (int col = 0; col < nColumns; col++) {
				mask[isInt][col] = mask[nNodes-inode][col];
				newData.setValue(isInt, col, newData.getValue(nNodes-inode, col));
			}

			// Fix the distances
			distID[isInt] = distID[nNodes-inode];
			for (int i = 0; i < isInt; i++)
				distanceMatrix[isInt][i] = distanceMatrix[nNodes-inode][i];

			for (int i = jsInt+1; i < nNodes-inode; i++)
				distanceMatrix[i][isInt] = distanceMatrix[nNodes-inode][i];

			distID[jsInt] = -inode-1;

			for (int i = 0; i < jsInt; i++)
				distanceMatrix[jsInt][i] = metric.getMetric(newData, newData, newData.getWeights(), jsInt, i);
			for (int i = jsInt+1; i < nNodes-inode; i++)
				distanceMatrix[i][jsInt] = metric.getMetric(newData, newData, newData.getWeights(), jsInt, i);
		}

		return nodeList;
	}

	private static TreeNode[] pmlcluster(int rows, double[][] distanceMatrix) {
		return null;
	}

	private static TreeNode[] palcluster(int rows, double[][] distanceMatrix) {
		return null;
	}

	private static void getAttributesList(List<String>attributeList, CyAttributes attributes, 
	                              String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING ||
			    attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
				attributeList.add(prefix+names[i]);
			}
		}
	}

	private static String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		attributeList.add("-- select attribute --");
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"node.");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(),"edge.");
		return (String[])attributeList.toArray();
	}
		
	/**
 	 * This function searches the distance matrix to find the pair with the shortest
 	 * distance between them. The indices of the pair are returned in ip and jp; the
 	 * distance itself is returned by the function.
 	 *
 	 * n          (input) int
 	 * The number of elements in the distance matrix.
 	 *
 	 * distanceMatrix (input) double[][]
 	 * A ragged array containing the distance matrix. The number of columns in each
 	 * row is one less than the row index.
 	 *
 	 * pair         (output) int[2]
 	 * An array with two values representing the first and second indices of the pair
 	 * with the shortest distance.
 	 */
	private static double findClosestPair(int n, double[][] distanceMatrix, int[] pair) {
		int ip = 1;
		int jp = 0;
		double temp;
		double distance = distanceMatrix[1][0];
		for (int i = 1; i < n; i++) {
			for (int j = 0; j < i; j++) {
				temp = distanceMatrix[i][j];
				if (temp < distance) {
					distance = temp;
					ip = i;
					jp = j;
				}
			}
		}
		pair[0] = ip;
		pair[1] = jp;
		return distance;
	}
}
