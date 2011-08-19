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
package clusterMaker.algorithms.clusterFilters;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.algorithms.networkClusterers.AbstractNetworkClusterer;

/**
 * This abstract class is the base class for all of the network filters provided by
 * clusterMaker.  Fundamentally, a network filters is an algorithm which functions to
 * modify the results of a previous cluster algorithm by filtering the results.
 */
public abstract class AbstractNetworkFilter extends AbstractNetworkClusterer {
	protected String clusterAttribute = null;
	protected String[] attributeArray = null;
	protected CyLogger logger = null;

	/**
 	 * Return the list of attributes that might be considered cluster attributes.  Essentially,
 	 * this returns the list of INTEGER attributes.
 	 *
 	 * @return the list of INTEGER node attributes
 	 */
	public void clusterAttributes () {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String[] names = nodeAttributes.getAttributeNames();
		List<String> intList = new ArrayList<String>();
		for (String name: names) {
			if (nodeAttributes.getType(name) == CyAttributes.TYPE_INTEGER) {
				intList.add(name);
			}
		}
		attributeArray = intList.toArray(new String[1]);
	}

	/**
 	 * Return the attribute that is referenced by the last cluster run.
 	 *
 	 * @return the last attribute
 	 */
	public Integer defaultAttribute() {
		// Get the cluster type
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		String netId = network.getIdentifier();
		// System.out.println("Network = "+netId);
		if (!networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE)) {
			clusterAttribute = null;
			// System.out.println("No "+ClusterMaker.CLUSTER_TYPE_ATTRIBUTE+" attribute");
			return new Integer(0);
		}

		String cluster_type = networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE);
		// System.out.println("cluster type = "+cluster_type);
		if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_ATTRIBUTE)) {
			clusterAttribute = networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_ATTRIBUTE);
			for (int i = 0; i < attributeArray.length; i++) {
				if (clusterAttribute.equals(attributeArray[i]))
					return new Integer(i);
			}
		}
		clusterAttribute = null;
		return new Integer(0);
	}

	public void createAttributeTunable() {
		clusterAttributes();
		Integer defaultCluster = defaultAttribute();
		Tunable attrTunable = new Tunable("attribute",
		                                  "Cluster to filter",
		                                  Tunable.LIST, 0,
		                                  (Object)attributeArray, defaultCluster, 0);
		clusterProperties.add(attrTunable);
	}

	public void	updateAttributeList() {
		Tunable t = clusterProperties.get("attribute");
		clusterAttributes();
		t.setLowerBound((Object)attributeArray);
	}

	public void doCluster (TaskMonitor monitor) {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String networkID = network.getIdentifier();

		// get the cluster list
		List<List<CyNode>> clusterList = getNodeClusters(clusterAttribute);
		List<NodeCluster> newClusterList = new ArrayList<NodeCluster>();

		// Iterate over clusters and build a new clusterList
		for (List<CyNode>nodeList: clusterList) {
			NodeCluster newCluster = doFilter(network, nodeList);
			if (newCluster != null && newCluster.size() > 0)
				newClusterList.add(newCluster);
		}

		logger.info("Removing groups");

		// Remove any leftover groups from previous runs
		removeGroups(netAttributes, networkID);

		logger.info("Creating groups");
		monitor.setStatus("Creating groups");

		List<List<CyNode>> nodeClusters = 
		     createGroups(netAttributes, networkID, nodeAttributes, newClusterList);

		results = new ClusterResults(network, clusterList);
		ClusterResults results2 = new ClusterResults(network, nodeClusters);
		monitor.setStatus("Done.  Results:\n\nBefore Filter:\n"+results+"\n\nAfter Filter:\n"+results2);
		logger.info("Done.  Results:\n\nBefore Filter:\n"+results+"\n\nAfter Filter:\n"+results2);

	}

	abstract public NodeCluster doFilter(CyNetwork network, List<CyNode>nodeList);

	public void updateSettings(boolean force) {
		super.updateSettings(force);
		Tunable t = clusterProperties.get("attribute");
		if ((t != null) && (t.valueChanged() || force)) {
			int index = ((Integer) t.getValue()).intValue();
			if (index < 0) index = 0;
			clusterAttribute = attributeArray[index];
		}
	}

	
}
