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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cytoscape.CyNode;

/**
 * In it's simplist form, a Cluster is a group of nodes that represents the
 * nodes that are grouped together as the result of a clustering algorithm
 * of some sort.  A more complicated form of a cluster could include clusters
 * as part of the list, which complicates this class a little....
 */
public class NodeCluster extends ArrayList<CyNode> {
	int clusterNumber = 0;
	static int clusterCount = 0;
	static boolean hasScore = false;
	protected double score = 0.0;

	public NodeCluster() {
		super();
		clusterCount++;
		clusterNumber = clusterCount;
	}

	public NodeCluster(Collection<CyNode> collection) {
		super(collection);
		clusterCount++;
		clusterNumber = clusterCount;
	}

	public boolean add(List<CyNode>nodeList, int index) {
		return add(nodeList.get(index));
	}

	public static void init() { clusterCount = 0; hasScore = false; }
	public static boolean hasScore() { return hasScore; }

	public int getClusterNumber() { return clusterNumber; }

	public void setClusterNumber(int clusterNumber) { 
		this.clusterNumber = clusterNumber; 
	}

	public void setClusterScore(double score) { 
		this.score = score; 
		hasScore = true;
	}

	public double getClusterScore() { return score; }


	public String toString() {
		String str = "("+clusterNumber+": ";
		for (Object i: this) 
			str += i.toString();
		return str+")";
	}

	public static List<NodeCluster> sortMap(Map<Integer, NodeCluster> map) {
		NodeCluster[] clusterArray = map.values().toArray(new NodeCluster[1]);
		Arrays.sort(clusterArray, new LengthComparator());
		return Arrays.asList(clusterArray);
	}

	public static List<NodeCluster> rankListByScore(List<NodeCluster> list) {
		NodeCluster[] clusterArray = list.toArray(new NodeCluster[1]);
		Arrays.sort(clusterArray, new ScoreComparator());
		for (int rank = 0; rank < clusterArray.length; rank++) {
			clusterArray[rank].setClusterNumber(rank+1);
		}
		return Arrays.asList(clusterArray);
	}

	static class LengthComparator implements Comparator {
		public int compare (Object o1, Object o2) {
			List c1 = (List)o1;
			List c2 = (List)o2;
			if (c1.size() > c2.size()) return -1;
			if (c1.size() < c2.size()) return 1;
			return 0;
		}
	}

	static class ScoreComparator implements Comparator {
		public int compare (Object o1, Object o2) {
			NodeCluster c1 = (NodeCluster)o1;
			NodeCluster c2 = (NodeCluster)o2;
			if (c1.getClusterScore() > c2.getClusterScore()) return -1;
			if (c1.getClusterScore() < c2.getClusterScore()) return 1;
			return 0;
		}
	}
}
