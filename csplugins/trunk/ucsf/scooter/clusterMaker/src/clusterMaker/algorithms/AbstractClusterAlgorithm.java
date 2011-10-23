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

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.layout.Tunable;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;

import clusterMaker.ui.ClusterTask;

// clusterMaker imports

public abstract class AbstractClusterAlgorithm implements ClusterAlgorithm {
	// Common class values
	protected ClusterProperties clusterProperties = null;
	protected PropertyChangeSupport pcs;
	protected boolean debug = false;
	protected boolean createGroups = false;
	protected String clusterAttributeName = null;
	protected boolean canceled = false;
	protected ClusterResults results;
	protected String GROUP_ATTRIBUTE = "_cluster";

	public AbstractClusterAlgorithm() {
		pcs = new PropertyChangeSupport(new Object());
	}

	/************************************************************************
	 * Abstract inteface -- override these methods!                         *
	 ***********************************************************************/

	public abstract String getShortName();
	public abstract String getName();
	public abstract void updateSettings();
	public abstract JPanel getSettingsPanel();
	public abstract void doCluster(TaskMonitor monitor);

	/************************************************************************
	 * Convenience routines                                                 *
	 ***********************************************************************/

	public void initializeProperties() {
		clusterProperties = new ClusterProperties(getShortName());
	}

	protected void advancedProperties() {
		clusterProperties.add(new Tunable("advancedGroup", "Cytoscape Advanced Settings",
		                                  Tunable.GROUP, new Integer(3),
		                                  new Boolean(true), null, Tunable.COLLAPSABLE));
		clusterProperties.add(new Tunable("clusterAttrName", "Cluster attribute", 
		                                  Tunable.STRING, clusterAttributeName));
		clusterProperties.add(new Tunable("createGroups", "Create metanodes with results", 
		                                  Tunable.BOOLEAN, new Boolean(createGroups)));
		clusterProperties.add(new Tunable("debug", "Enable debugging", 
		                                   Tunable.BOOLEAN, new Boolean(debug))); 
	}

	public void updateSettings(boolean force) {
		Tunable t = clusterProperties.get("debug");
		if ((t != null) && (t.valueChanged() || force))
			debug = ((Boolean) t.getValue()).booleanValue();
		t = clusterProperties.get("clusterAttrName");
		if ((t != null) && (t.valueChanged() || force))
			clusterAttributeName = (String) t.getValue();
		t = clusterProperties.get("createGroups");
		if ((t != null) && (t.valueChanged() || force))
			createGroups = ((Boolean) t.getValue()).booleanValue();
	}

	public void revertSettings() {
		clusterProperties.revertProperties();
	}

	public ClusterProperties getSettings() {
		return clusterProperties;
	}

	public String toString() { return getName(); }

	public void halt() { canceled = true; }

	public boolean halted() { return canceled; }

	public ClusterResults getResults() { return results; }

	public PropertyChangeSupport getPropertyChangeSupport() {return pcs;}

	public JTaskConfig getDefaultTaskConfig() { return ClusterTask.getDefaultTaskConfig(false); }
	
	public static double mean(Double[] vector) {
		double result = 0.0;
		for (int i = 0; i < vector.length; i++) {
			result += vector[i].doubleValue();
		}
		return (result/(double)vector.length);
	}

	// Inefficient, but simple approach to finding the median
	public static double median(Double[] vector) {
		// Clone the input vector
		Double[] vectorCopy = new Double[vector.length];
		for (int i = 0; i < vector.length; i++) {
			vectorCopy[i] = new Double(vector[i].doubleValue());
		}
	
		// sort it
		Arrays.sort(vectorCopy);
	
		// Get the median
		int mid = vector.length/2;
		if (vector.length%2 == 1) {
			return (vectorCopy[mid].doubleValue());
		}
		return ((vectorCopy[mid-1].doubleValue()+vectorCopy[mid].doubleValue()) / 2);
	}
}
