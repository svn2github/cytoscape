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
package clusterMaker.treeview.model;

// System imports
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

// TreeView imports
import clusterMaker.treeview.DataModel;
import clusterMaker.treeview.model.TVModel;

import clusterMaker.ClusterMaker;

/**
 * The ClusterVizModel provides the data that links the results of a cluster run
 * in Cytoscape with the Java TreeView code
 *
 */
public class TreeViewModel extends TVModel {
	// Keep track of gene to node references
	CyNetwork network;
	CyNetworkView networkView;
	boolean isSymmetrical = false;
	boolean zeroMissing = false;
	Double diagonalValue = null;
	List<String> clusterParams = null;
	CyLogger myLogger = null;
	private String [] clusterHeaders = {"NODEID", "LEFT", "RIGHT", "CORRELATION"};

	public TreeViewModel(CyLogger logger) {
		super();

		// Get our logger
		this.myLogger = logger;

		network = Cytoscape.getCurrentNetwork();
		networkView = Cytoscape.getCurrentNetworkView();

		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

		// Get the type of cluster
		String clusterType = null;
		if (networkAttributes.hasAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_TYPE_ATTRIBUTE))
			clusterType = networkAttributes.getStringAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_TYPE_ATTRIBUTE);

		if (networkAttributes.hasAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_PARAMS_ATTRIBUTE))
			clusterParams = networkAttributes.getListAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_PARAMS_ATTRIBUTE);

		if (clusterParams != null) {
			for (String param: clusterParams) {
				if (param.equals("zeroMissing"))
					zeroMissing = true;
				else if (param.startsWith("diagonals=")) {
					diagonalValue = Double.valueOf(param.substring(10));
				}
			}
		}
		
		// Gene annotations are just the list of node names
		List<String>geneList = networkAttributes.getListAttribute(network.getIdentifier(), ClusterMaker.NODE_ORDER_ATTRIBUTE);
		// System.out.println("geneList: "+geneList);
		String [][] gHeaders = new String[geneList.size()][4];
		int headerNumber = 0;
		for (String nodeName: geneList) {
			gHeaders[headerNumber][0] = nodeName;
			gHeaders[headerNumber][1] = nodeName;
			gHeaders[headerNumber][2] = nodeName;
			gHeaders[headerNumber++][3] = "1.0";
		}
		setGenePrefix(new String[] {"GID", "NODE","ORF","GWEIGHT"});
		setGeneHeaders(gHeaders);


		// Array annotations are the list of attributes we used (note: order matters)
		List<String>arrayList = networkAttributes.getListAttribute(network.getIdentifier(), ClusterMaker.ARRAY_ORDER_ATTRIBUTE);
		String [][] aHeaders = new String[arrayList.size()][2];
		headerNumber = 0;
		for (String attribute: arrayList) {
			aHeaders[headerNumber][0] = attribute;
			aHeaders[headerNumber++][1] = "1.0";
		}
		setArrayPrefix(new String[] {"AID", "EWEIGHT"});
		setArrayHeaders(aHeaders);

		int nGene = geneList.size();
		int nExpr = arrayList.size();
		// The CDT is the Gene x Array matrix
		double[] exprData = new double[nGene * nExpr];

		// Check for a symmetrical matrix
		if (geneList.get(0).equals(arrayList.get(0))) {

			// Matrix is symmetrical.  Get the edge attribute
			String attribute = networkAttributes.getStringAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_EDGE_ATTRIBUTE);
			attribute = attribute.substring(5);
			// System.out.println("Edge attribute is "+attribute);

			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

			// Initialize the data
			for (int row = 0; row < nGene; row++) {
				for (int col = 0; col < nExpr; col++) {
					int cell = row * nExpr + col;
					if (diagonalValue != null && row == col)
						exprData[cell] = diagonalValue;
					else if (zeroMissing)
						exprData[cell] = 0.0f;
					else
						exprData[cell] = DataModel.NODATA;
				}
			}

			byte attributeType = edgeAttributes.getType(attribute);
			Iterator edgeIterator = network.edgesIterator();
			while (edgeIterator.hasNext()) {
				CyEdge edge = (CyEdge)edgeIterator.next();
				CyNode source = (CyNode)edge.getSource();
				CyNode target = (CyNode)edge.getTarget();
				if (!geneList.contains(source.getIdentifier()) || !geneList.contains(target.getIdentifier()))
					continue;
				Double val = null;
				if (attributeType == CyAttributes.TYPE_FLOATING) {
					val = edgeAttributes.getDoubleAttribute(edge.getIdentifier(), attribute);
				} else if (attributeType == CyAttributes.TYPE_INTEGER) {
					Integer v = edgeAttributes.getIntegerAttribute(edge.getIdentifier(), attribute);
					if (v != null)
						val = Double.valueOf(v.toString());
				}
				int gene = geneList.indexOf(source.getIdentifier());
				int expr = geneList.indexOf(target.getIdentifier());
				// System.out.println("Edge "+source.getIdentifier()+"("+gene+") "+
				//                    target.getIdentifier()+"("+expr+") = "+val);
				if (val != null) {
					exprData[gene*nExpr + expr] = val;
					exprData[expr*nExpr + gene] = val;
				}
			}
			isSymmetrical = true;
		} else {
			// Get the data
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			int gene = 0;
			for (String nodeName: geneList) {
				int expr = 0;
				for (String attribute: arrayList) {
					byte attributeType = nodeAttributes.getType(attribute);
					Double val = null;
					if (attributeType == CyAttributes.TYPE_FLOATING) {
						val = nodeAttributes.getDoubleAttribute(nodeName, attribute);
					} else if (attributeType == CyAttributes.TYPE_INTEGER) {
						Integer v = nodeAttributes.getIntegerAttribute(nodeName, attribute);
						if (v != null)
							val = Double.valueOf(v.toString());
						else
							val = null;
					}

					if (val == null) {
						exprData[gene*nExpr + expr] = DataModel.NODATA;
					} else {
						exprData[gene*nExpr + expr] = val.doubleValue();
					}
					expr++;
				}
				gene++;
			}
		}

		setExprData(exprData);
		hashGIDs();
		hashAIDs();

		// Now, get the gene tree results (GTR) or array tree results (ATR) from Cytoscape, depending on
		// what we clustered.  Note that the way we put this together really depends on whether we've done
		// a som, kmeans, or hierarchical cluster

		if (networkAttributes.hasAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_NODE_ATTRIBUTE)) {
			List<String>groupList = networkAttributes.getListAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_NODE_ATTRIBUTE);
			setGtrPrefix(getClusterHeaders());
			String [][] gtrHeaders = new String[groupList.size()][getClusterHeaders().length];

			parseGroupHeaders(groupList, gtrHeaders);

			setGtrHeaders(gtrHeaders);
			hashGTRs();
			gidFound(true);
		}

		// If we're not a gene cluster, we need to transpose the matrix
		// when we save it
		if (networkAttributes.hasAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_ATTR_ATTRIBUTE)) {
			List<String>groupList = networkAttributes.getListAttribute(network.getIdentifier(), ClusterMaker.CLUSTER_ATTR_ATTRIBUTE);
			setAtrPrefix(getClusterHeaders());
			String [][] atrHeaders = new String[groupList.size()][getClusterHeaders().length];

			parseGroupHeaders(groupList, atrHeaders);

			setAtrHeaders(atrHeaders);
			hashATRs();
			aidFound(true);
		}


		// We don't use weights
		setEweightFound(false);
		setGweightFound(false);
	}

	public String getName() {
		return Cytoscape.getCurrentNetwork().getIdentifier()+" Clusters";
	}

	public String getSource() {
		return Cytoscape.getCurrentNetwork().getIdentifier();
	}

	public boolean isSymmetrical() {
		return isSymmetrical;
	}

	protected String[] getClusterHeaders() {
		return new String [] {"NODEID", "LEFT", "RIGHT", "CORRELATION"};
	}

	private void parseGroupHeaders (List<String>groupList, String [][] headers) {

		// Parse the group data: format is NAME\tID1\tID2\tdistance
		int headerNumber = 0;
		int headerLength = headers[0].length;
		for (String group: groupList) {
			String[] tokens = group.split("[\t]");
			for (int t = 0; t < headerLength; t++) {
				headers[headerNumber][t] = tokens[t];
			}
			headerNumber++;
		}
	}
}
