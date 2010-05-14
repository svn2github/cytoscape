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

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.layout.Tunable;
import cytoscape.task.TaskMonitor;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;

import clusterMaker.ClusterMaker;

/**
 * This abstract class is the base class for all of the network clusterers provided by
 * clusterMaker.  Fundamentally, a network clusterer is an algorithm which functions to
 * partition a network based on properties of the relationships between nodes.
 */
public abstract class AbstractNetworkClusterer extends AbstractClusterAlgorithm {


	// For simple divisive clustering, these routines will do the group handling
	protected void removeGroups(CyAttributes netAttributes, String networkID) {
		// See if we already have groups defined (from a previous run?)
		if (netAttributes.hasAttribute(networkID, GROUP_ATTRIBUTE)) {
			List<String> groupList = (List<String>)netAttributes.getListAttribute(networkID, GROUP_ATTRIBUTE);
			for (String groupName: groupList) {
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group != null)
					CyGroupManager.removeGroup(group);
			}
		}
	}

	protected List<List<CyNode>> createGroups(CyAttributes netAttributes, 
	                                          String networkID,
	                                          CyAttributes nodeAttributes, 
	                                          List<NodeCluster> cMap) { 

		List<List<CyNode>> clusterList = new ArrayList(); // List of node lists
		List<String>groupList = new ArrayList(); // keep track of the groups we create
		CyGroup first = null;
		for (NodeCluster cluster: cMap) {
			int clusterNumber = cluster.getClusterNumber();
			String groupName = clusterAttributeName+"_"+clusterNumber;
			List<CyNode>nodeList = new ArrayList();

			for (CyNode node: cluster) {
				nodeList.add(node);
				nodeAttributes.setAttribute(node.getIdentifier(),
				                            clusterAttributeName, clusterNumber);
				if (NodeCluster.hasScore()) {
					nodeAttributes.setAttribute(node.getIdentifier(),
					                            clusterAttributeName+"_Score", cluster.getClusterScore());
				}
			}
			
			if (createGroups) {
				// Create the group
				CyGroup newgroup = CyGroupManager.createGroup(groupName, nodeList, null);
				if (newgroup != null) {
					first = newgroup;
					// Now tell the metanode viewer about it
					CyGroupManager.setGroupViewer(newgroup, "metaNode", 
					                              Cytoscape.getCurrentNetworkView(), false);
					// And, finally, set the score on the group itself
					nodeAttributes.setAttribute(newgroup.getGroupNode().getIdentifier(),
					                            clusterAttributeName, clusterNumber);
					if (NodeCluster.hasScore()) {
						nodeAttributes.setAttribute(newgroup.getGroupNode().getIdentifier(),
						                            clusterAttributeName+"_Score", cluster.getClusterScore());
					}
				}
			}
			clusterList.add(nodeList);
			groupList.add(groupName);
		}
		if (first != null)
			CyGroupManager.setGroupViewer(first, "metaNode", 
			                              Cytoscape.getCurrentNetworkView(), true);
		
		// Save the network attribute so we remember which groups are ours
		netAttributes.setListAttribute(networkID, GROUP_ATTRIBUTE, groupList);

		// Set up the appropriate attributes
		netAttributes.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                           ClusterMaker.CLUSTER_TYPE_ATTRIBUTE, getShortName());
		netAttributes.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                           ClusterMaker.CLUSTER_ATTRIBUTE, clusterAttributeName);
	
		return clusterList;
	}

	public boolean isAvailable() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		String netId = network.getIdentifier();
		if (!networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE)) {
			return false;
		}

		String cluster_type = networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE);
		if (cluster_type != getShortName())
			return false;

		if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_ATTRIBUTE)) {
			return true;
		}
		return false;
	}

	public List<List<CyNode>> getNodeClusters() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String netId = network.getIdentifier();

		List<List<CyNode>> clusterList = new ArrayList(); // List of node lists
		String clusterAttribute = networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_ATTRIBUTE);

		// Create the cluster Map
		HashMap<Integer, List<CyNode>> clusterMap = new HashMap();
		for (CyNode node: (List<CyNode>)network.nodesList()) {
			// For each node -- see if it's in a cluster.  If so, add it to our map
			if (nodeAttributes.hasAttribute(node.getIdentifier(), clusterAttribute)) {
				Integer cluster = nodeAttributes.getIntegerAttribute(node.getIdentifier(), clusterAttribute);
				if (!clusterMap.containsKey(cluster)) {
					List<CyNode> nodeList = new ArrayList();
					clusterMap.put(cluster, nodeList);
					clusterList.add(nodeList);
				}
				clusterMap.get(cluster).add(node);
			}
		}
		return clusterList;
	}

}
