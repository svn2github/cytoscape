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

import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;
import java.util.Observable;

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
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

// Giny imports
import giny.model.Node;
import giny.view.GraphViewChangeListener;
import giny.view.GraphViewChangeEvent;

// ClusterMaker imports
import clusterMaker.algorithms.ClusterProperties;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.hierarchical.EisenCluster;

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
public class HeatMapView extends TreeViewApp implements Observer, GraphViewChangeListener, ClusterViz, ClusterAlgorithm {
	private URL codeBase = null;
	private ViewFrame viewFrame = null;
	protected TreeSelectionI geneSelection = null;
	protected TreeSelectionI arraySelection = null;
	protected TreeViewModel dataModel = null;
	protected CyNetworkView myView = null;
	protected CyNetwork myNetwork = null;
	protected ClusterProperties clusterProperties = null;
	protected String dataAttributes = null;
	protected boolean canceled = false;
	protected PropertyChangeSupport pcs;

	private	List<CyNode>selectedNodes;
	private	List<CyNode>selectedArrays;
	private CyLogger myLogger;
	private String[] attributeArray = new String[1];

	private static String appName = "ClusterMaker HeatMapView";

	public HeatMapView() {
		super();
		// setExitOnWindowsClosed(false);
		selectedNodes = new ArrayList();
		selectedArrays = new ArrayList();
		myLogger = CyLogger.getLogger(TreeView.class);
		clusterProperties = new ClusterProperties(getShortName());
		pcs = new PropertyChangeSupport(new Object());
		initializeProperties();
	}

	public HeatMapView(PropertyConfig propConfig) {
		super(propConfig);
		selectedNodes = new ArrayList();
		selectedArrays = new ArrayList();
		// setExitOnWindowsClosed(false);
		myLogger = CyLogger.getLogger(TreeView.class);
		clusterProperties = new ClusterProperties(getShortName());
		pcs = new PropertyChangeSupport(new Object());
		initializeProperties();
	}

	public void setVisible(boolean visibility) {
		if (viewFrame != null)
			viewFrame.setVisible(visibility);
	}

	public String getAppName() {
		return appName;
	}

	// ClusterViz methods
	public String getShortName() { return "heatMapView"; }

	public String getName() { return "HeatMapView (unclustered)"; }

	public void startViz() {
		startup();
	}

	public boolean isAvailable() {
		return true;
	}

	public void startup() {
		updateSettings();

		// Set up the global config
		setConfigDefaults(new PropertyConfig(globalConfigName(),"ProgramConfig"));

		myNetwork = Cytoscape.getCurrentNetwork();

		// Set the "cluster" attributes, as appropriate
		// Clear the type attribute
		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		if (netAttributes.hasAttribute(myNetwork.getIdentifier(),EisenCluster.CLUSTER_TYPE_ATTRIBUTE))
			netAttributes.deleteAttribute(myNetwork.getIdentifier(),EisenCluster.CLUSTER_TYPE_ATTRIBUTE);

		// Set the node order to the sorted list of nodes
		String [] nodeArray = new String[myNetwork.getNodeCount()];
		int index = 0;
		for (Object node:myNetwork.nodesList())
			nodeArray[index++] = ((CyNode)node).getIdentifier();
		
		Arrays.sort(nodeArray);
		netAttributes.setListAttribute(myNetwork.getIdentifier(),EisenCluster.NODE_ORDER_ATTRIBUTE,Arrays.asList(nodeArray));

		// Now, figure out what type of attributes
		String attributeArray[] = getAttributeArray(dataAttributes);
		// To make debugging easier, sort the attribute array
		Arrays.sort(attributeArray);

		// Edge attribute?
		if (attributeArray.length == 1 && attributeArray[0].startsWith("edge.")) {
			// Yes, symmetrical array
			netAttributes.setListAttribute(myNetwork.getIdentifier(),EisenCluster.ARRAY_ORDER_ATTRIBUTE,Arrays.asList(nodeArray));
			netAttributes.setAttribute(myNetwork.getIdentifier(),EisenCluster.CLUSTER_EDGE_ATTRIBUTE, attributeArray[0]);
		} else {
			for (int i = 0; i < attributeArray.length; i++) {
				attributeArray[i] = attributeArray[i].substring(5);
			}
			netAttributes.setListAttribute(myNetwork.getIdentifier(),EisenCluster.ARRAY_ORDER_ATTRIBUTE,Arrays.asList(attributeArray));
		}

		// Get our data model
		dataModel = new TreeViewModel(myLogger);

		// Set up our configuration
		PropertyConfig documentConfig = new PropertyConfig(getShortName(),"DocumentConfig");
		dataModel.setDocumentConfig(documentConfig);

		// Create our view frame
		TreeViewFrame frame = new TreeViewFrame(this);

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
		// myView.addGraphViewChangeListener(this);
	}

	// ClusterAlgorithm methods
	protected void initializeProperties() {
		// The attribute to use to get the weights
		attributeArray = getAllAttributes();
		clusterProperties.add(new Tunable("attributeList",
		                                  "Array sources",
		                                  Tunable.LIST, "",
		                                  (Object)attributeArray, (Object)null, Tunable.MULTISELECT));

		clusterProperties.initializeProperties();
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void updateSettings(boolean force) {
		clusterProperties.updateValues();

		Tunable t = clusterProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			dataAttributes = (String) t.getValue();
		}
	}

	public JPanel getSettingsPanel() {
		// Everytime we ask for the panel, we want to update our attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);

		return clusterProperties.getTunablePanel();
	}

	public ClusterViz getVisualizer() {
		return this;
	}

	public void doCluster(TaskMonitor monitor) {
		return;
	}

	public void revertSettings() {
		clusterProperties.revertProperties();
	}

	public ClusterProperties getSettings() {
		return clusterProperties;
	}

	public String toString() { return getName(); }

	public void halt() { canceled = true; }

	public PropertyChangeSupport getPropertyChangeSupport() {return pcs;}

	//
	public void update(Observable o, Object arg) {
		if (o == geneSelection) {
			selectedNodes = new ArrayList();
			int[] selections = geneSelection.getSelectedIndexes();
			HeaderInfo geneInfo = dataModel.getGeneHeaderInfo();
			String [] names = geneInfo.getNames();
			for (int i = 0; i < selections.length; i++) {
				String nodeName = geneInfo.getHeader(selections[i])[0];
				CyNode node = Cytoscape.getCyNode(nodeName, false);
				if (node != null) selectedNodes.add(node);
			}
			// myView.removeGraphViewChangeListener(this);
			// System.out.println("Selecting "+nodes.size()+" nodes");
			if (!dataModel.isSymmetrical() || selectedArrays.size() == 0) {
				myNetwork.unselectAllNodes();
				myNetwork.setSelectedNodeState(selectedNodes, true);
				return;
			}
			// myView.addGraphViewChangeListener(this);
		} else if (o == arraySelection) {
			// We only care about array selection for symmetrical models
			if (!dataModel.isSymmetrical())
				return;
			selectedArrays = new ArrayList();
			int[] selections = arraySelection.getSelectedIndexes();
			if (selections.length == dataModel.nExpr())
				return;
			HeaderInfo arrayInfo = dataModel.getArrayHeaderInfo();
			String [] names = arrayInfo.getNames();
			for (int i = 0; i < selections.length; i++) {
				String nodeName = arrayInfo.getHeader(selections[i])[0];
				CyNode node = Cytoscape.getCyNode(nodeName, false);
				if (node != null) selectedArrays.add(node);
			}
		}

		// If we've gotten here, we want to select edges
		myNetwork.unselectAllEdges();
		myNetwork.unselectAllNodes();
		List<CyEdge>edgesToSelect = new ArrayList();
		for (CyNode node1: selectedNodes) {
			int [] nodes = new int[2];
			nodes[0] = node1.getRootGraphIndex();
			for (CyNode node2: selectedArrays) {
				nodes[1] = node2.getRootGraphIndex();
				int edges[] = myNetwork.getConnectingEdgeIndicesArray(nodes);
				for (int i = 0; i < edges.length; i++)
					edgesToSelect.add((CyEdge)myNetwork.getEdge(edges[i]));
			}
		}
		myNetwork.setSelectedEdgeState(edgesToSelect, true);
	}

	public void graphViewChanged(GraphViewChangeEvent event) {
		// System.out.println("graphViewChanged");
		if (event.isNodesSelectedType()) {
			Node[] nodeArray = event.getSelectedNodes();
			// setSelection(nodeArray, true);
		} else if (event.isNodesUnselectedType()) {
			Node[] nodeArray = event.getUnselectedNodes();
			// setSelection(nodeArray, false);
		}
	}

	private void setSelection(Node[] nodeArray, boolean select) {
		HeaderInfo geneInfo = dataModel.getGeneHeaderInfo();
		geneSelection.deleteObserver(this);
		geneSelection.setSelectedNode(null);
		for (int index = 0; index < nodeArray.length; index++) {
			CyNode cyNode = (CyNode) nodeArray[index];
			// System.out.println("setting "+cyNode.getIdentifier()+" to "+select);
			int geneIndex = geneInfo.getIndex(cyNode.getIdentifier());
			geneSelection.setIndex(geneIndex, select);
		}
		geneSelection.notifyObservers();
		geneSelection.addObserver(this);
		arraySelection.setSelectedNode(null);
		arraySelection.selectAllIndexes();
		arraySelection.notifyObservers();
	}

	private void getAttributesList(List<String>attributeList, CyAttributes attributes, 
	                              String prefix) {
		String[] names = attributes.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (attributes.getType(names[i]) == CyAttributes.TYPE_FLOATING ||
			    attributes.getType(names[i]) == CyAttributes.TYPE_INTEGER) {
				attributeList.add(prefix+names[i]);
			}
		}
	}

	private String[] getAllAttributes() {
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"node.");
		getAttributesList(attributeList, Cytoscape.getEdgeAttributes(),"edge.");
		String[] attrArray = attributeList.toArray(attributeArray);
		Arrays.sort(attrArray);
		return attrArray;
	}

	private String[] getAttributeArray(String dataAttributes) {
		String indices[] = dataAttributes.split(",");
		String selectedAttributes[] = new String[indices.length];
		for (int i = 0; i < indices.length; i++) {
			selectedAttributes[i] = attributeArray[Integer.parseInt(indices[i])];
		}
		return selectedAttributes;
	}
}
