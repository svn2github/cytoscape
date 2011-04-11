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
package clusterMaker.ui;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandResult;
import clusterMaker.algorithms.ClusterProperties;

import java.util.Arrays;
import java.lang.Math;
import javax.swing.JPanel;

// clusterMaker imports

public interface ClusterViz {

	/**
 	 * Get the short name of this Visualizer
 	 *
 	 * @return short-hand name for Visualizer
 	 */
	public String getShortName();

	/**
 	 * Get the name of this visualizer
 	 *
 	 * @return name for visualizer
 	 */
	public String getName();

	/**
 	 * Get the settings panel for this visualizer
 	 *
 	 * @return settings panel
 	 */
	public JPanel getSettingsPanel();

	/**
	 * This method is used to ask the visualizer to revert its settings
	 * to some previous state.  It is called from the settings dialog
	 * when the user presses the "Cancel" button.
	 *
	 */
	public void revertSettings();

  /**
	 * This method is used to ask the visualizer to get its settings
	 * from the settings dialog.  It is called from the settings dialog
	 * when the user presses the "Done" or the "Execute" buttons.
	 *
	 */
	public void updateSettings();
	public void updateSettings(boolean force);

  /**
	 * This method is used to ask the visualizer to get all of its tunables
	 * and return them to the caller.
	 *
	 * @return the properties for this visualizer
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
	 * This is the main interface to trigger a ui to display
	 *
	 * @param monitor a TaskMonitor
	 */
	public CyCommandResult startViz() throws CyCommandException;

	/**
 	 * Returns true if the data is available to visualize
 	 */
	public boolean isAvailable();
}
