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
package clusterMaker.algorithms.attributeClusterers.hopach;

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

import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.AbstractAttributeClusterer;
import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.KnnView;


/**
 * HopachCluster implements the HOPACH (Hierarchical Ordered Partitioning and Collapsing Hybrid) 
 * algorithm of van der Laan and Pollard. The algorithm can be basically outlined as:
 * while (minClusterSize &gt; 3)
 *		foreach (level)
 *			k = averageSilhouette(K);	// Get the average silhouette for k=2,...,K
 * 			partitionClusters(k);	// Partition the clusters with PAM
 * 			orderClusters(); 			// Order the new clusters
 *			collapseClusters(); 	// Collapse clusters
 *			level++;
 *
 * where partitionClusters() uses PAM (Partition About Medoids)
 * algorithm (we actually use our k-medoid implementation)
 *
 * User inputs: 
 *	K - the maximum number of levels in the tree (15)
 *	kmax - the maximum number of children at at each node in the tree (1-9)
 *	coll - collapse approach
 *	attributeList - the names of the features to cluster on
 *	metric - the distance metric (Euclidean distance is used in HOPACH)
 *
 * Output:
 *	Ordered cluster tree
 *
 *	Reference: 
 *		M.J. van der Laan, K.S. Pollard (2001). Hybrid clustering of gene expression 
 *		data with visualization and the bootstrap. Journal of Statistical Planning and 
 *		Inference, 2003, 117: 275-303
 *
 * TODO: Get R source code to figure out exact algorithm
 */
public class HopachClustererOld extends AbstractAttributeClusterer {

	String[] attributeArray = new String[1];

	TaskMonitor monitor = null;
	CyLogger logger = null;

	public HopachClustererOld() {
		super();
		logger = CyLogger.getLogger(HopachClustererOld.class);
		initializeProperties();
	}

	public String getShortName() {return "hopach";};
	public String getName() {return "HOPACH cluster";};

	public JPanel getSettingsPanel() {
		return null;
	}

	public ClusterViz getVisualizer() {
		return null;
	}

	public void initializeProperties() {
		super.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);

	}

	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
	}

	public boolean isAvailable() {
		return true;
	}

}
