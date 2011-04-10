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

import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import clusterMaker.ui.ClusterViz;

import java.beans.PropertyChangeSupport;
import java.lang.Math;
import java.util.Arrays;

import javax.swing.JPanel;

// clusterMaker imports

public interface ClusterAlgorithm {

	// Property change
	public static String CLUSTER_COMPUTED = "CLUSTER_COMPUTED";

	/**
 	 * Get the short name of this algorithm
 	 *
 	 * @return short-hand name for algorithm
 	 */
	public String getShortName();

	/**
 	 * Get the name of this algorithm
 	 *
 	 * @return name for algorithm
 	 */
	public String getName();

	/**
 	 * Get the settings panel for this algorithm
 	 *
 	 * @return settings panel
 	 */
	public JPanel getSettingsPanel();

	/**
	 * This method is used to ask the algorithm to revert its settings
	 * to some previous state.  It is called from the settings dialog
	 * when the user presses the "Cancel" button.
	 *
	 * NOTE: ClusterAlgorithmBase implements this on behalf of all its subclasses
	 * by using Java Preferences.
	 */
	public void revertSettings();

  /**
	 * This method is used to ask the algorithm to get its settings
	 * from the settings dialog.  It is called from the settings dialog
	 * when the user presses the "Done" or the "Execute" buttons.
	 *
	 * NOTE: ClusterAlgorithmBase implements this on behalf of all its subclasses
	 * by using Java Preferences.
	 */
	public void updateSettings();
	public void updateSettings(boolean force);

  /**
	 * This method is used to ask the algorithm to get all of its tunables
	 * and return them to the caller.
	 *
	 * @return the cluster properties for this algorithm
	 *
	 */
	public ClusterProperties getSettings();

	/**
 	 * This method is used to re-initialize the properties for an algorithm.  This
 	 * might be used, for example, by an external command, or when a new network
 	 * is loaded.
 	 */
	public void initializeProperties();

	/**
	 * This method is used to signal a running cluster algorithm to stop
	 *
	 */
	public void halt();

	/**
	 * This is the main interface to trigger a cluster to compute
	 *
	 * @param monitor a TaskMonitor
	 */
	public void doCluster(TaskMonitor monitor);

	/**
	 * This call returns a JTaskConfig option
	 *
	 * @return the JTaskconfig
	 */
	public JTaskConfig getDefaultTaskConfig();

	/**
 	 * Hooks for the visualizer
 	 *
 	 * @return the visualizer or null if one doesn't exist
 	 */
	public ClusterViz getVisualizer();

	/**
 	 * Hooks for the results.  This is so results can
 	 * be returned to commands.
 	 *
 	 * @return cluster results.
 	 */
	public ClusterResults getResults();

	/**
 	 * Returns 'true' if this algorithm has already been run on this network
 	 *
 	 * @return true if the algorithm attributes exist
 	 */
	public boolean isAvailable();

	/**
 	 * This is a hook to notify interested parties that we have finished
 	 * computing a cluster.  The major use is for clusters with visualizers
 	 * to inform UI components that the visualizer can now be launched.
 	 */
	public PropertyChangeSupport getPropertyChangeSupport();

}
