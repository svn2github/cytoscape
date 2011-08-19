/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.CyNode;

import giny.model.Edge;

/**
 * This class calculates a number of cluster statistics on a set of
 * of node clusters.  The node clusters passed as a list of lists of
 * CyNodes, where each list of CyNodes represents a cluster.  Currently
 * the calculated statistics include:
 *	Number of clusters
 *	Average cluster size
 *	Maximum cluster size
 *	Minimum cluster size
 *	Cluster coefficient (intra-cluster edges / total edges)
 */

public class ClusterResults {
	private List<List<CyNode>> clusters;
	private CyNetwork network;
	private int clusterCount;
	private double averageSize;
	private int maxSize;
	private int minSize;
	private double clusterCoefficient;
	private double modularity;
	private String extraText = null;

	public ClusterResults(CyNetwork network, List<List<CyNode>> cl, String extraInformation) { 
		this.network = network;
		clusters = cl; 
		extraText = extraInformation;
		calculate();
	}

	public ClusterResults(CyNetwork network, List<List<CyNode>> cl) { 
		this(network,cl,null);
	}

	public String toString() {
		NumberFormat nf = NumberFormat.getInstance();
		String result = "  Clusters: "+clusterCount+"\n";
		result += "  Average size: "+nf.format(averageSize)+"\n";
		result += "  Maximum size: "+maxSize+"\n";
		result += "  Minimum size: "+minSize+"\n";
		result += "  Modularity: "+nf.format(modularity);
		if (extraText != null)
			result += "  "+extraText;
		return result;
	}

	public List<List<CyNode>> getClusters() {
		return clusters;
	}

	private void calculate() {
		clusterCount = clusters.size();
		averageSize = 0.0;
		maxSize = -1;
		minSize = Integer.MAX_VALUE;
		clusterCoefficient = 0.0;
		modularity = 0.0;
		double edgeCount = (double)network.getEdgeCount();

		int clusterNumber = 0;
		for (List<CyNode> cluster: clusters) {
			averageSize += (double)cluster.size() / (double)clusterCount;
			maxSize = Math.max(maxSize, cluster.size());
			minSize = Math.min(minSize, cluster.size());
			double innerEdges = getInnerEdgeCount(cluster);
			double outerEdges = getOuterEdgeCount(cluster);
			clusterCoefficient += (innerEdges / (innerEdges+outerEdges)) / (double)(clusterCount);

			double percentEdgesInCluster = innerEdges/edgeCount;
			double percentEdgesTouchingCluster = (innerEdges+outerEdges)/edgeCount;
			modularity += percentEdgesInCluster - percentEdgesTouchingCluster*percentEdgesTouchingCluster;
			clusterNumber++;
		}
	}

	private double getInnerEdgeCount(List<CyNode> cluster) {
		return (double) network.getConnectingEdges(cluster).size();
	}

	private double getOuterEdgeCount(List<CyNode> cluster) {
		// Get all of the inner edges
		List<Edge> innerEdges = network.getConnectingEdges(cluster);

		// Make a map out of the inner edges
		Map<Edge,Edge> edgeMap = new HashMap<Edge,Edge>();
		for (Edge edge: innerEdges) {
			edgeMap.put(edge, edge);
		}

		int outerCount = 0;
		for (CyNode node: cluster) {
			List<Edge> edges = network.getAdjacentEdgesList(node, true, true, true);
			for (Edge edge: edges) {
				if (!edgeMap.containsKey(edge))
					outerCount++;
			}
		}
		return (double) outerCount;
	}

}
