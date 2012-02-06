/* vim: set ts=2: */
/**
 * Copyright (c) 20118 The Regents of the University of California.
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
import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.TunableListener;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;

import clusterMaker.algorithms.AbstractClusterAlgorithm;
import clusterMaker.ui.ClusterTask;

/**
 * This abstract class is the base class for all of the attribute clusterers provided by
 * clusterMaker.  Fundamentally, an attribute clusterer is an algorithm which functions to
 * partition nodes or node attributes based on properties of the attributes.
 */
public abstract class AbstractAttributeClusterer extends AbstractClusterAlgorithm implements TunableListener {
	// Common instance variables
	protected String[] attributeArray = new String[1];
	protected String dataAttributes = null;
	protected DistanceMetric distanceMetric = DistanceMetric.EUCLIDEAN;
	protected boolean clusterAttributes = false;
	protected boolean createGroups = false;
	protected boolean ignoreMissing = true;
	protected boolean selectedOnly = false;
	protected boolean adjustDiagonals = false;
	protected boolean zeroMissing = false;
	protected boolean useSilhouette = false;
	protected boolean initializeNearCenter = false;
	protected int kMax = 0;
	protected int kNumber = 0;
	protected TaskMonitor monitor = null;
	protected CyLogger logger = null;

	protected void getAttributesList(List<String>attributeList, CyAttributes attributes, String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING ||
			    attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
				attributeList.add(prefix+names[i]);
			}
		}
	}

	protected String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"node.");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(),"edge.");
		Collections.sort(attributeList);
		return attributeList.toArray(new String[1]);
	}

	protected String[] getNodeAttributes() {
		List<String> attributeList = new ArrayList<String>();
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"node.");
		Collections.sort(attributeList);
		return attributeList.toArray(new String[1]);
	}

	protected String[] getAttributeArray(String dataAttributes) {
		String indices[] = dataAttributes.split(",");
		String selectedAttributes[] = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			selectedAttributes[i] = attributeArray[Integer.parseInt(indices[i])];
		}
		return selectedAttributes;
	}

	public boolean isAvailable() {
		return AbstractAttributeClusterAlgorithm.isAvailable(getShortName());
	}

	// We don't want to autodispose our task monitors
	public JTaskConfig getDefaultTaskConfig() { return ClusterTask.getDefaultTaskConfig(true); }

	protected void addKTunables() {
		Tunable t = new Tunable("useSilhouette",
		                        "Estimate k using silhouette",
		                        Tunable.BOOLEAN, (Object)new Boolean(useSilhouette),
		                        (Object) null, (Object) null, 0);
		t.addTunableValueListener(this);
		clusterProperties.add(t);

		t = new Tunable("kMax",
		                "Maximum number of clusters",
		                Tunable.INTEGER, new Integer(kMax),
		                (Object)null, (Object)null, 0);
		if (!useSilhouette) t.setImmutable(true);
		clusterProperties.add(t);

		t = new Tunable("kNumber",
		                "Number of clusters (k)",
		                Tunable.INTEGER, new Integer(kNumber),
		                (Object)null, (Object)null, 0);
		if (useSilhouette) t.setImmutable(true);
		clusterProperties.add(t);

		// Whether to initialize cluster centers by choosing the most central elements
		clusterProperties.add(new Tunable("initializeNearCenter",
				                          "Initialize cluster centers from most central elements",
				                          Tunable.BOOLEAN, new Boolean(initializeNearCenter)));
	}

	protected void updateKTunables(boolean force) {
		Tunable t = clusterProperties.get("useSilhouette");
		if ((t != null) && (t.valueChanged() || force))
			useSilhouette = ((Boolean) t.getValue()).booleanValue();

		t = clusterProperties.get("kMax");
		if ((t != null) && (t.valueChanged() || force))
			kMax = ((Integer) t.getValue()).intValue();

		t = clusterProperties.get("kNumber");
		if ((t != null) && (t.valueChanged() || force))
			kNumber = ((Integer) t.getValue()).intValue();
		
		t = clusterProperties.get("initializeNearCenter");
		if ((t != null) && (t.valueChanged() || force)) {
			initializeNearCenter = ((Boolean) t.getValue()).booleanValue();
		}
	}

	protected void updateKEstimates() {
		// We also want to update the number our "guestimate" for k
		double nodeCount = (double)Cytoscape.getCurrentNetwork().getNodeCount();
		if (selectedOnly) {
			int selNodes = Cytoscape.getCurrentNetwork().getSelectedNodes().size();
			if (selNodes > 0) nodeCount = (double)selNodes;
		}

		Tunable kTunable = clusterProperties.get("kNumber");
		double kinit = Math.sqrt(nodeCount/2);
		if (kinit > 1)
			kTunable.setValue((int)kinit);
		else
			kTunable.setValue(1);

		Tunable kMaxTunable = clusterProperties.get("kMax");
		kMaxTunable.setValue((int)kinit*2); // Double our kNumber estimate
	}

	public void tunableChanged(Tunable t) {
		// System.out.println("Tunable changed");
		if (t.getName().equals("useSilhouette")) {
			useSilhouette = ((Boolean) t.getValue()).booleanValue();
			if (useSilhouette) {
				clusterProperties.get("kMax").setImmutable(false);
				clusterProperties.get("kNumber").setImmutable(true);
			} else {
				clusterProperties.get("kMax").setImmutable(true);
				clusterProperties.get("kNumber").setImmutable(false);
			}
		}
	}

}
