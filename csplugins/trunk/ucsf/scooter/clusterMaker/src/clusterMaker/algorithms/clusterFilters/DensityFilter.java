/* vim: set ts=2: */
/**
 * Copyright (c) 201 The Regents of the University of California.
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

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import giny.model.GraphPerspective;

import clusterMaker.algorithms.ClusterResults;
import clusterMaker.algorithms.NodeCluster;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.NewNetworkView;

public class DensityFilter extends AbstractNetworkFilter {
	CyLogger logger;
	double minimumDensity;

	public DensityFilter() {
		super();
		logger = CyLogger.getLogger(HairCutFilter.class);
		clusterAttributeName = Cytoscape.getCurrentNetwork().getIdentifier()+"_DF_cluster";
		initializeProperties();
	}

	public String getShortName() { return "density"; }

	public String getName() { return "Density Filter"; }

	public void initializeProperties() {
		super.initializeProperties();

		/**
		 * Tuning values
		 */
		clusterProperties.add(new Tunable("tunables_panel",
		                                  "Density Filter Parameters",
		                                  Tunable.GROUP, new Integer(2)));

		super.createAttributeTunable();

		clusterProperties.add(new Tunable("minimumDensity",
		                                  "Minimum density",
		                                  Tunable.DOUBLE, new Double(.5)));

		// At the end of the day, this is similar to a cluster algorithm....
		super.advancedProperties();
		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);

		Tunable t = clusterProperties.get("minimumDensity");
		if ((t != null) && (t.valueChanged() || force))
			minimumDensity = ((Double) t.getValue()).doubleValue();
	}

	public JPanel getSettingsPanel() {
		super.updateAttributeList();
		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return new NewNetworkView(true);
	}

	public NodeCluster doFilter(CyNetwork network, List<CyNode>nodeList) {
		int[] indices = new int[nodeList.size()];
		for (int i = 0; i < nodeList.size(); i++) {
			indices[i] = nodeList.get(i).getRootGraphIndex();
		}
		// Create a new graph perspective with only the nodes in the cluster
		GraphPerspective gp = network.createGraphPerspective(indices);

		// Get the number of edges
		int edgeCount = gp.getEdgeCount();
		int nodeCount = indices.length;

		// Calculate the density
		double density = (double)(edgeCount*2)/(double)(nodeCount * (nodeCount-1));
		if (density >= minimumDensity)
			return new NodeCluster(nodeList);
		return null;
	}

}
