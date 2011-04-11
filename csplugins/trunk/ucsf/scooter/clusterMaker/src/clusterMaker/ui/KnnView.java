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

// System imports
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

// Giny imports
import giny.model.Node;
import giny.view.GraphViewChangeListener;
import giny.view.GraphViewChangeEvent;

// ClusterMaker imports
import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.ClusterProperties;
import clusterMaker.algorithms.ClusterResults;

// TreeView imports
import clusterMaker.treeview.FileSet;
import clusterMaker.treeview.HeaderInfo;
import clusterMaker.treeview.KnnViewFrame;
import clusterMaker.treeview.LoadException;
import clusterMaker.treeview.PropertyConfig;
import clusterMaker.treeview.TreeSelectionI;
import clusterMaker.treeview.TreeViewApp;
import clusterMaker.treeview.ViewFrame;
import clusterMaker.treeview.model.KnnViewModel;

/**
 * The ClusterViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class KnnView extends TreeView {
	private CyLogger myLogger;

	private static String appName = "ClusterMaker KnnView";

	public KnnView() {
		super();
		myLogger = CyLogger.getLogger(KnnView.class);
	}

	public KnnView(PropertyConfig propConfig) {
		super(propConfig);
		myLogger = CyLogger.getLogger(KnnView.class);
	}

	public String getAppName() {
		return appName;
	}

	// ClusterViz methods
	public String getShortName() { return "knnview"; }

	public String getName() { return "Eisen KnnView"; }

	public ClusterResults getResults() { return null; }

	public void initializeProperties() {}

	public boolean isAvailable() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		String netId = network.getIdentifier();
		if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE) &&
		    !networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE).equals("kmeans")) {
			return false;
		}

		if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_NODE_ATTRIBUTE) ||
		    networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_ATTR_ATTRIBUTE)) {
			return true;
		}
		return false;
	}

	public CyCommandResult startViz() throws CyCommandException {
		CyCommandResult result = new CyCommandResult();
		if (isAvailable()) {
			KnnView kv = new KnnView();  // Clone ourselves
			kv.startup();
			result.addMessage("Knn View displayed");
		} else {
			throw new CyCommandException("No K-Means compatible cluster available");
		}
		return result;
	}

	protected void startup() {
		// Get our data model
		dataModel = new KnnViewModel(myLogger);

		// Set up the global config
		setConfigDefaults(new PropertyConfig(globalConfigName(),"ProgramConfig"));

		// Set up our configuration
		PropertyConfig documentConfig = new PropertyConfig(getShortName(),"DocumentConfig");
		dataModel.setDocumentConfig(documentConfig);

		// Create our view frame
		KnnViewFrame frame = new KnnViewFrame(this, appName);

		// Set the data model
		frame.setDataModel(dataModel);
		frame.setLoaded(true);
		frame.addWindowListener(this);
		frame.setVisible(true);
		geneSelection = frame.getGeneSelection();
		geneSelection.addObserver(this);
		arraySelection = frame.getArraySelection();
		arraySelection.addObserver(this);

		// Now set up to receive selection events
		myView = Cytoscape.getCurrentNetworkView();
		myNetwork = Cytoscape.getCurrentNetwork();
		myView.addGraphViewChangeListener(this);

		// Set up to listen for changes in the network view
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().
		                       addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUS, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().
		                       addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
	}
}
