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
import java.util.HashMap;
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

// TreeView imports
import clusterMaker.treeview.FileSet;
import clusterMaker.treeview.HeaderInfo;
import clusterMaker.treeview.LoadException;
import clusterMaker.treeview.PropertyConfig;
import clusterMaker.treeview.TreeSelectionI;
import clusterMaker.treeview.TreeViewApp;
import clusterMaker.treeview.TreeViewFrame;
import clusterMaker.treeview.ViewFrame;
import clusterMaker.treeview.model.TreeViewModel;

/**
 * The ClusterViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class TreeView extends TreeViewApp implements Observer, 
                                                     GraphViewChangeListener, 
                                                     PropertyChangeListener, 
                                                     ClusterViz {
	private URL codeBase = null;
	private ViewFrame viewFrame = null;
	protected TreeSelectionI geneSelection = null;
	protected TreeSelectionI arraySelection = null;
	protected TreeViewModel dataModel = null;
	protected CyNetworkView myView = null;
	protected CyNetwork myNetwork = null;
	private	List<CyNode>selectedNodes;
	private	List<CyNode>selectedArrays;
	private CyLogger myLogger;
	private CyNetwork dataNetwork = null;

	private static String appName = "ClusterMaker TreeView";

	public TreeView() {
		super();
		// setExitOnWindowsClosed(false);
		selectedNodes = new ArrayList();
		selectedArrays = new ArrayList();
		myLogger = CyLogger.getLogger(TreeView.class);
	}

	public TreeView(PropertyConfig propConfig) {
		super(propConfig);
		selectedNodes = new ArrayList();
		selectedArrays = new ArrayList();
		// setExitOnWindowsClosed(false);
		myLogger = CyLogger.getLogger(TreeView.class);
	}

	public void setVisible(boolean visibility) {
		if (viewFrame != null)
			viewFrame.setVisible(visibility);
	}

	public String getAppName() {
		return appName;
	}

	// ClusterViz methods
	public String getShortName() { return "treeView"; }

	public String getName() { return "Eisen TreeView"; }

	public JPanel getSettingsPanel() { return null; }

	public void revertSettings() {}

	public void updateSettings() {}

	public void updateSettings(boolean force) {}

	public ClusterProperties getSettings() { return null; }

	public void startViz() {
		TreeView tv = new TreeView();  // Clone ourselves
		tv.startup();
		// startup();
	}

	public boolean isAvailable() {
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		String netId = network.getIdentifier();
		if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE) &&
		    !networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE).equals("hierarchical")) {
			return false;
		}

		if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_NODE_ATTRIBUTE) ||
		    networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_ATTR_ATTRIBUTE)) {
			return true;
		}
		return false;
	}

	public void startup() {
		// Get our data model
		dataModel = new TreeViewModel(myLogger);

		// Remember the network we were launched by
		dataNetwork = Cytoscape.getCurrentNetwork();

		// Set up the global config
		setConfigDefaults(new PropertyConfig(globalConfigName(),"ProgramConfig"));

		// Set up our configuration
		PropertyConfig documentConfig = new PropertyConfig(getShortName(),"DocumentConfig");
		dataModel.setDocumentConfig(documentConfig);

		// Create our view frame
		TreeViewFrame frame = new TreeViewFrame(this, appName);

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

	public void update(Observable o, Object arg) {
		if (o == geneSelection) {
			selectedNodes.clear();
			int[] selections = geneSelection.getSelectedIndexes();
			HeaderInfo geneInfo = dataModel.getGeneHeaderInfo();
			String [] names = geneInfo.getNames();
			for (int i = 0; i < selections.length; i++) {
				String nodeName = geneInfo.getHeader(selections[i])[0];
				CyNode node = Cytoscape.getCyNode(nodeName, false);
				// Now see if this network has this node
				if (node != null && !myNetwork.containsNode(node)) {
					// No, try dropping any suffixes from the node name
					String[] tokens = nodeName.split(" ");
					node = Cytoscape.getCyNode(tokens[0], false);
				}
				if (node != null)
					selectedNodes.add(node);
			}
			// System.out.println("Selecting "+selectedNodes.size()+" nodes");
			if (!dataModel.isSymmetrical() || selectedArrays.size() == 0) {
				myView.removeGraphViewChangeListener(this);
				myNetwork.unselectAllNodes();
				myNetwork.setSelectedNodeState(selectedNodes, true);
				myView.redrawGraph(false,false);
				myView.addGraphViewChangeListener(this);
				return;
			}
			return;
		} else if (o == arraySelection) {
			// We only care about array selection for symmetrical models
			if (!dataModel.isSymmetrical())
				return;
			selectedArrays.clear();
			int[] selections = arraySelection.getSelectedIndexes();
			if (selections.length == dataModel.nExpr())
				return;
			HeaderInfo arrayInfo = dataModel.getArrayHeaderInfo();
			String [] names = arrayInfo.getNames();
			for (int i = 0; i < selections.length; i++) {
				String nodeName = arrayInfo.getHeader(selections[i])[0];
				CyNode node = Cytoscape.getCyNode(nodeName, false);
				if (node != null && !myNetwork.containsNode(node)) {
					// No, try dropping any suffixes from the node name
					String[] tokens = nodeName.split(" ");
					node = Cytoscape.getCyNode(tokens[0], false);
				}
				if (node != null)
					selectedArrays.add(node);
			}
		}

		// If we've gotten here, we want to select edges
		myView.removeGraphViewChangeListener(this); // For efficiency reasons, remove our listener for now
		myNetwork.unselectAllEdges();

		HashMap<CyEdge,CyEdge>edgesToSelect = new HashMap();
		for (CyNode node1: selectedNodes) {
			int [] nodes = new int[2];
			nodes[0] = node1.getRootGraphIndex();
			for (CyNode node2: selectedArrays) {
				nodes[1] = node2.getRootGraphIndex();
				int edges[] = myNetwork.getConnectingEdgeIndicesArray(nodes);
				if (edges == null) {
					continue;
				}
				for (int i = 0; i < edges.length; i++) {
					CyEdge connectingEdge = (CyEdge)myNetwork.getEdge(edges[i]);
					edgesToSelect.put(connectingEdge, connectingEdge);
				}
			}
		}
		myNetwork.setSelectedEdgeState(edgesToSelect.keySet(), true);
		// myView.redrawGraph(false,false);
		// Add our listener back
		myView.addGraphViewChangeListener(this);
		selectedNodes.clear();
		selectedArrays.clear();
	}

	public void graphViewChanged(GraphViewChangeEvent event) {
		// System.out.println("graphViewChanged");
		if (event.getType() == GraphViewChangeEvent.NODES_UNSELECTED_TYPE || 
		    event.getType() == GraphViewChangeEvent.NODES_SELECTED_TYPE) {
			// We got a node selection event, but we're a symmetrical matrix -- not
			// sure what to do, so for now, just return
			if (dataModel.isSymmetrical()) return;
			Set<CyNode> nodes = myNetwork.getSelectedNodes();
			setNodeSelection(nodes, true);
		} else if ((event.getType() == GraphViewChangeEvent.EDGES_UNSELECTED_TYPE ||
		            event.getType() == GraphViewChangeEvent.EDGES_SELECTED_TYPE) &&
		            dataModel.isSymmetrical()) {
			// Got an edge selection (or unselection) event and we're a symmetrical matrix
			// We want to locate the bounding box that surrounds the set of edges and select
			// that range
			Set<CyEdge> edges = myNetwork.getSelectedEdges();
			setEdgeSelection(edges, true);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ( /* evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS || */
		     evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED ) {

			// The user has changed the network view -- we want to switch our
			// view to go along with it

			// See if we actually have a view
			CyNetworkView focusedView = Cytoscape.getNetworkView((String)evt.getNewValue());
			if (focusedView == Cytoscape.getNullNetworkView())
				return;

			// Remove our listener from this network
			myView.removeGraphViewChangeListener(this);

			// Switch to the new network view
			myView = focusedView;

			// Get the network for this view
			myNetwork = myView.getNetwork();

			// Add ourselves to the new network as a listener
			myView.addGraphViewChangeListener(this);
		}
	}

	private void setEdgeSelection(Set<CyEdge> edgeArray, boolean select) {
		HeaderInfo geneInfo = dataModel.getGeneHeaderInfo();
		HeaderInfo arrayInfo = dataModel.getArrayHeaderInfo();

		// Avoid loops -- delete ourselves as observers
		geneSelection.deleteObserver(this);
		arraySelection.deleteObserver(this);

		// Clear everything that's currently selected
		geneSelection.setSelectedNode(null);
		geneSelection.deselectAllIndexes();
		arraySelection.setSelectedNode(null);
		arraySelection.deselectAllIndexes();

		// Do the actual selection
		for (CyEdge cyEdge: edgeArray) {
			CyNode source = (CyNode)cyEdge.getSource();
			CyNode target = (CyNode)cyEdge.getTarget();
			int geneIndex = geneInfo.getHeaderIndex(source.getIdentifier());
			int arrayIndex = arrayInfo.getHeaderIndex(target.getIdentifier());
			geneSelection.setIndex(geneIndex, select);
			arraySelection.setIndex(arrayIndex, select);
		}

		// Notify all of the observers
		geneSelection.notifyObservers();
		arraySelection.notifyObservers();

		// OK, now we can listen again
		geneSelection.addObserver(this);
		arraySelection.addObserver(this);
	}

	// private void setNodeSelection(Node[] nodeArray, boolean select) {
	private void setNodeSelection(Set<CyNode> nodeArray, boolean select) {
		HeaderInfo geneInfo = dataModel.getGeneHeaderInfo();
		geneSelection.deleteObserver(this);
		geneSelection.setSelectedNode(null);
		geneSelection.deselectAllIndexes();
		for (CyNode cyNode: nodeArray) {
			int geneIndex = geneInfo.getHeaderIndex(cyNode.getIdentifier());
			// System.out.println("setting "+cyNode.getIdentifier()+"("+geneIndex+") to "+select);
			geneSelection.setIndex(geneIndex, select);
		}
		geneSelection.deleteObserver(this);
		geneSelection.notifyObservers();
		geneSelection.addObserver(this);
	}
}
