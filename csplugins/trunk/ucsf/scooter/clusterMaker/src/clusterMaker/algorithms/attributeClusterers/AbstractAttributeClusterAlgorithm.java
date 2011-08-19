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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.ClusterMaker;

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

	protected boolean adjustDiagonals = false;
	protected boolean debug = false;
	protected boolean createGroups = false;
	protected boolean ignoreMissing = false;
	protected boolean interimRun = false;
	protected boolean selectedOnly = false;
	protected boolean zeroMissing = false;

	abstract public String cluster(int nClusters, int nIterations, boolean transpose);

	public Matrix getMatrix() { return matrix; }
	public DistanceMetric getMetric() { return metric; }

	public void setCreateGroups(boolean val) { createGroups = val; }
	public void setIgnoreMissing(boolean val) { ignoreMissing = val; }
	public void setSelectedOnly(boolean val) { selectedOnly = val; }
	public void setAdjustDiagonals(boolean val) { adjustDiagonals = val; }
	public void setZeroMissing(boolean val) { zeroMissing = val; }
	public void setDebug(boolean val) { debug = val; }
	public void setInterimRun(boolean val) { interimRun = val; }

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

}
