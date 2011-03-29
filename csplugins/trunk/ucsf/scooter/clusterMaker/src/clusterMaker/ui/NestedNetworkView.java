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

import java.awt.Color;
import java.awt.Paint;

import java.beans.PropertyChangeSupport;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyEdgeView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;

import giny.view.EdgeView;

// ClusterMaker imports
import clusterMaker.ClusterMaker;
import clusterMaker.algorithms.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterProperties;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.ui.ClusterTask;

/**
 * The ClusterViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class NestedNetworkView implements ClusterViz, ClusterAlgorithm {

	private static String appName = "ClusterMaker Nested Network View";
	private boolean selectedOnly = false;
	private boolean restoreEdges = false;
	private boolean checkForAvailability = false;
	private String clusterAttribute = null;
	private CyLogger myLogger = null;
	private ClusterProperties clusterProperties = null;
	private String[] attributeArray = new String[1];
	protected PropertyChangeSupport pcs;

	public NestedNetworkView() {
		super();
		initialize();
		checkForAvailability = false;
	}

	public NestedNetworkView(boolean available) {
		super();
		initialize();
		checkForAvailability = available;
	}

	public void setVisible(boolean visibility) {
	}

	public String getAppName() {
		return appName;
	}

	// ClusterViz methods
	public String getShortName() { return "NestedNetworkView"; }

	public String getName() { 
		if (checkForAvailability) {
			return "Create New Network with nested clusters";
		} else {
			return "Create New Network with nested networks from attribute"; 
		}
	}

	public JTaskConfig getDefaultTaskConfig() { return ClusterTask.getDefaultTaskConfig(false); }

	public void startViz() {
		startup();
	}

	public boolean isAvailable() {
		if (!checkForAvailability)
			return true;
		
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		String netId = network.getIdentifier();
		if (!networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE)) {
			return false;
		}

		String cluster_type = networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_TYPE_ATTRIBUTE);
		ClusterMaker instance = ClusterMaker.getInstance();
		if (!(instance.getAlgorithm(cluster_type) instanceof AbstractNetworkClusterer))
			return false;

		if (networkAttributes.hasAttribute(netId, ClusterMaker.CLUSTER_ATTRIBUTE)) {
			clusterAttribute = networkAttributes.getStringAttribute(netId, ClusterMaker.CLUSTER_ATTRIBUTE);
			return true;
		}
		return false;
	}

	public void startup() {
		updateSettings();

		// Set up a new task
		CreateNetworkTask task = new CreateNetworkTask(clusterAttribute);
		TaskManager.executeTask( task, ClusterTask.getDefaultTaskConfig(false) );
	}

	protected void initialize() {
		myLogger = CyLogger.getLogger(TreeView.class);
		clusterProperties = new ClusterProperties(getShortName());
		pcs = new PropertyChangeSupport(new Object());
		initializeProperties();
	}

	// ClusterAlgorithm methods
	public void initializeProperties() {
		// The attribute to use to get the weights
		attributeArray = getAllAttributes();

		clusterProperties.add(new Tunable("attributeList",
		                                  "Cluster Attribute to Use",
		                                  Tunable.LIST, new Integer(0),
		                                  (Object)attributeArray, (Object)null, 0));

		clusterProperties.add(new Tunable("selectedOnly",
		                                  "Display only selected nodes (or edges)",
		                                  Tunable.BOOLEAN, new Boolean(false)));

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
			int val = ((Integer) t.getValue()).intValue();
			clusterAttribute = attributeArray[val];
		} else if (clusterAttribute == null && attributeArray.length > 0) {
			clusterAttribute = attributeArray[0];
		}

		t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();

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

	public void halt() { }

	public PropertyChangeSupport getPropertyChangeSupport() {return pcs;}

	private void createClusteredNetwork(String clusterAttribute) {
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		// Create the new network
		CyNetwork parentNet = Cytoscape.createNetwork(currentNetwork.nodesList(), currentNetwork.edgesList(), 
		                                              currentNetwork.getTitle()+"--clustered",currentNetwork,false);

		// Create a map for all of our clustered nodes
		HashMap<CyNode, CyNode> nodeMap = new HashMap();

		// Create the cluster Map
		HashMap<Integer, List<CyNode>> clusterMap = new HashMap();
		HashMap<Integer, CyNode> clusterNodeMap = new HashMap();
		for (CyNode node: (List<CyNode>)currentNetwork.nodesList()) {
			// For each node -- see if it's in a cluster.  If so, add it to our map
			if (nodeAttributes.hasAttribute(node.getIdentifier(), clusterAttribute)) {
				Integer cluster = nodeAttributes.getIntegerAttribute(node.getIdentifier(), clusterAttribute);
				if (!clusterMap.containsKey(cluster)) {
					// Create the map for this cluster
					clusterMap.put(cluster, new ArrayList());
					// Create the cluster node
					String title = currentNetwork.getTitle()+"--cluster "+cluster;
					CyNode clusterNode = Cytoscape.getCyNode(title, true);
					// Add it to the parent network
					parentNet.addNode(clusterNode);
					clusterNodeMap.put(cluster, clusterNode);
				}
				clusterMap.get(cluster).add(node);
				nodeMap.put(node, clusterNodeMap.get(cluster));
			}
		}

		HashMap<CyEdge,CyEdge> edgeMap = new HashMap();
		for (CyEdge edge: (List<CyEdge>)parentNet.edgesList()) {
			System.out.print("Edge: "+edge.getIdentifier());
			// Is this a non-clustered edge?
			if (!nodeMap.containsKey((CyNode)edge.getSource()) && !nodeMap.containsKey((CyNode)edge.getTarget())) {
				System.out.println(" Non-clustered");
				continue;
			}

			String interaction;

			// Is this an internal edge?
			if (nodeMap.containsKey((CyNode)edge.getSource()) && nodeMap.containsKey((CyNode)edge.getTarget())) {
				if (nodeMap.get(edge.getSource()) == nodeMap.get(edge.getTarget())) {
					System.out.println(" Internal only");
					continue;
				}
				System.out.println(" inter-cluster");
				interaction = "inter-cluster";
			} else {
				interaction = "cluster-"+edgeAttributes.getStringAttribute(edge.getIdentifier(), Semantics.INTERACTION);
			}

			// One end is in a cluster, or both are in different clusters
			CyNode source = (CyNode)edge.getSource();
			CyNode target = (CyNode)edge.getTarget();
			if (nodeMap.containsKey(edge.getSource())) {
				source = nodeMap.get(edge.getSource());
			}
			if (nodeMap.containsKey(edge.getTarget())) {
				target = nodeMap.get(edge.getTarget());
			}

			// Now create the new edge
			CyEdge newEdge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, interaction, true);
			System.out.println("Adding "+newEdge.getIdentifier()+" to parent network");

			// Add it to the parent network
			parentNet.addEdge(newEdge);

			// And remove the old one.
			parentNet.removeEdge(edge.getRootGraphIndex(), false);
		}

		// Finally, remove all of our clustered nodes
		for (CyNode node: nodeMap.keySet())
			parentNet.removeNode(node.getRootGraphIndex(), false);

		// Get our favorite layout algorithm
		CyLayoutAlgorithm alg = CyLayouts.getLayout("force-directed");
		if (alg == null)
			alg = CyLayouts.getDefaultLayout();

		for (Integer cluster: clusterMap.keySet()) {
			// Get the list of nodes
			List<CyNode> nodeList = clusterMap.get(cluster); 
			// Get the list of edges
			List<CyEdge> edgeList = currentNetwork.getConnectingEdges(nodeList);
			String title = currentNetwork.getTitle()+"--cluster "+cluster;
			CyNetwork net = Cytoscape.createNetwork(nodeList, edgeList, title, parentNet, false);

			// Create our view and lay it out
			CyNetworkView v = Cytoscape.createNetworkView(net, title, alg);

			// Get the clustered node
			CyNode node = Cytoscape.getCyNode(title, true);

			// Now, we need to add the nested network to this node
			node.setNestedNetwork(net);

		}

		// Set the maximize on create property
		String max = CytoscapeInit.getProperties().getProperty("maximizeViewOnCreate");
		CytoscapeInit.getProperties().setProperty("maximizeViewOnCreate", "true");

		// Create the network view
		CyNetworkView view = Cytoscape.createNetworkView(parentNet);
		view.applyLayout(alg);

		// Now create a reasonable visual map for our new network
		VisualStyle newStyle = createNewStyle(view.getVisualStyle(), "-nested");
		view.applyVizmapper(newStyle);

		// Focus it and fit it
		view.fitContent();

		// Reset back to the user's preference
		CytoscapeInit.getProperties().setProperty("maximizeViewOnCreate", max);

		Cytoscape.setCurrentNetwork(parentNet.getIdentifier());
		Cytoscape.setCurrentNetworkView(view.getIdentifier());
		return;
	}

	private VisualStyle createNewStyle(VisualStyle parent, String suffix) { 
		boolean newStyle = false;
		VisualStyle style = null;

		// Get our current vizmap
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calculatorCatalog = manager.getCalculatorCatalog();

		// Create a new vizmap
		Set<String> styles = calculatorCatalog.getVisualStyleNames();
		if (styles.contains(parent.getName()+suffix))
			style = calculatorCatalog.getVisualStyle(parent.getName()+suffix);
		else {
			style = new VisualStyle(parent, parent.getName()+suffix);
			newStyle = true;
		}

		// Set a new node size style
		DiscreteMapping nodeSize = new DiscreteMapping(new Double(35), "has_nested_network", ObjectMapping.NODE_MAPPING);
		nodeSize.putMapValue("yes", new Double(100));
   	Calculator sizeCalculator = new BasicCalculator("Node Size Calculator",
		                                                 nodeSize, VisualPropertyType.NODE_SIZE);
		// Set a new node shape style
		DiscreteMapping nodeShape = new DiscreteMapping(NodeShape.ELLIPSE, "has_nested_network", ObjectMapping.NODE_MAPPING);
		nodeShape.putMapValue("yes", NodeShape.ROUND_RECT);
   	Calculator shapeCalculator = new BasicCalculator("Node Shape Calculator",
		                                                 nodeShape, VisualPropertyType.NODE_SHAPE);

		// We don't want to mess up the user's color scheme, but it's probably safe to change
		// the default color...

		NodeAppearanceCalculator nodeAppCalc = style.getNodeAppearanceCalculator();
   	nodeAppCalc.setCalculator(sizeCalculator);
   	nodeAppCalc.setCalculator(shapeCalculator);
		style.setNodeAppearanceCalculator(nodeAppCalc);

		if (newStyle) {
			calculatorCatalog.addVisualStyle(style);
			manager.setVisualStyle(style);
		} 
		return style;
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
		attributeArray = new String[1];
		// Create the list by combining node and edge attributes into a single list
		List<String> attributeList = new ArrayList<String>();
		getAttributesList(attributeList, Cytoscape.getNodeAttributes(),"");
		String[] attrArray = attributeList.toArray(attributeArray);
		Arrays.sort(attrArray);
		return attrArray;
	}

	private class CreateNetworkTask implements Task {
		TaskMonitor monitor;
		String attribute;

		public CreateNetworkTask(String attribute) {
			this.attribute = attribute;
		}

		public void setTaskMonitor(TaskMonitor monitor) {
			this.monitor = monitor;
		}

		public void run() {
			createClusteredNetwork(attribute);
		}

		public void halt() {
		}

		public String getTitle() {
			return "Creating new network";
		}

	}

}
