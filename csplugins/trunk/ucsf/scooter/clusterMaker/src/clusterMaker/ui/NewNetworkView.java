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
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
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
import cytoscape.visual.EdgeAppearanceCalculator;
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
import clusterMaker.algorithms.ClusterProperties;
import clusterMaker.algorithms.ClusterAlgorithm;

/**
 * The ClusterViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class NewNetworkView implements ClusterViz, ClusterAlgorithm {

	private static String appName = "ClusterMaker New Network View";
	private boolean selectedOnly = false;
	private boolean restoreEdges = false;
	private boolean checkForAvailability = false;
	private String clusterAttribute = null;
	private CyLogger myLogger = null;
	private ClusterProperties clusterProperties = null;
	private String[] attributeArray = new String[1];
	protected PropertyChangeSupport pcs;

	public NewNetworkView() {
		super();
		initialize();
		checkForAvailability = false;
	}

	public NewNetworkView(boolean available) {
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
	public String getShortName() { return "newNetworkView"; }

	public String getName() { 
		if (checkForAvailability) {
			return "Create New Network from Clusters";
		} else {
			return "Create New Network from Attribute"; 
		}
	}

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
		if (cluster_type != "MCL" && cluster_type != "GLay" && cluster_type != "AP" && cluster_type != "FORCE")
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
		TaskManager.executeTask( task, task.getDefaultTaskConfig() );
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

		clusterProperties.add(new Tunable("restoreEdges",
		                                  "Restore inter-cluster edges after layout",
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

		t = clusterProperties.get("restoreEdges");
		if ((t != null) && (t.valueChanged() || force))
			restoreEdges = ((Boolean) t.getValue()).booleanValue();
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
		CyNetwork net = Cytoscape.createNetwork(currentNetwork.getTitle()+"--clustered",currentNetwork,false);

		// Create the cluster Map
		HashMap<Integer, List<CyNode>> clusterMap = new HashMap();
		for (CyNode node: (List<CyNode>)currentNetwork.nodesList()) {
			// For each node -- see if it's in a cluster.  If so, add it to our map
			if (nodeAttributes.hasAttribute(node.getIdentifier(), clusterAttribute)) {
				Integer cluster = nodeAttributes.getIntegerAttribute(node.getIdentifier(), clusterAttribute);
				if (!clusterMap.containsKey(cluster)) {
					clusterMap.put(cluster, new ArrayList());
				}
				clusterMap.get(cluster).add(node);
				net.addNode(node);
			}
		}

		HashMap<CyEdge,CyEdge> edgeMap = new HashMap();

		for (Integer cluster: clusterMap.keySet()) {
			// Get the list of nodes
			List<CyNode> nodeList = clusterMap.get(cluster); 
			// Get the list of edges
			List<CyEdge> edgeList = currentNetwork.getConnectingEdges(nodeList);
			for (CyEdge edge: edgeList) { 
				net.addEdge(edge); 
				edgeMap.put(edge,edge);
				// Add the cluster attribute to the edge so we can style it later
				edgeAttributes.setAttribute(edge.getIdentifier(), clusterAttribute, new Integer(1));
			}
		}

		// Create the network view
		CyNetworkView view = Cytoscape.createNetworkView(net);

		// OK, now we need to explicitly remove any edges from our old network
		// that should not be in the new network (why is this necessary????)
		// for (CyEdge edge: edges) {
		// 	if (!edgeMap.containsKey(edge))
		// 		net.hideEdge(edge);
		// }

		// If available, do a force-directed layout
		CyLayoutAlgorithm alg = CyLayouts.getLayout("force-directed");

		if (alg != null)
			view.applyLayout(alg);

		// Get the current visual mapper
		VisualStyle vm = Cytoscape.getVisualMappingManager().getVisualStyle();

		// Now, if we're supposed to, restore the inter-cluster edges
		if (restoreEdges) {
			// Create new visual style
			vm = createNewStyle(clusterAttribute, "-cluster");

			// Add edge width and opacity descrete mappers
			for (CyEdge edge: (List<CyEdge>)currentNetwork.edgesList()) {
				if (!edgeMap.containsKey(edge)) {
					net.addEdge(edge);
					edgeAttributes.setAttribute(edge.getIdentifier(), clusterAttribute, new Integer(0));
				}
			}
		}

		view.applyVizmapper(vm);

		Cytoscape.setCurrentNetwork(net.getIdentifier());
		Cytoscape.setCurrentNetworkView(view.getIdentifier());
		return;
	}

	private VisualStyle createNewStyle(String attribute, String suffix) { 
		boolean newStyle = false;

		// Get our current vizmap
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calculatorCatalog = manager.getCalculatorCatalog();

		// Get the current style
		VisualStyle style = Cytoscape.getCurrentNetworkView().getVisualStyle();
		// Create a new vizmap
		Set<String> styles = calculatorCatalog.getVisualStyleNames();
		if (styles.contains(style.getName()+suffix))
			style = calculatorCatalog.getVisualStyle(style.getName()+suffix);
		else {
			style = new VisualStyle(style, style.getName()+suffix);
			newStyle = true;
		}

		// Set up our line width descrete mapper
		DiscreteMapping lineWidth = new DiscreteMapping(new Double(.5), attribute, ObjectMapping.EDGE_MAPPING);
		lineWidth.putMapValue(new Integer(0), new Double(1));
		lineWidth.putMapValue(new Integer(1), new Double(5));
   	Calculator widthCalculator = new BasicCalculator("Edge Width Calculator",
		                                                 lineWidth, VisualPropertyType.EDGE_LINE_WIDTH);

		DiscreteMapping lineOpacity = new DiscreteMapping(new Integer(50), attribute, ObjectMapping.EDGE_MAPPING);
		lineOpacity.putMapValue(new Integer(0), new Integer(50));
		lineOpacity.putMapValue(new Integer(1), new Integer(255));
   	Calculator opacityCalculator = new BasicCalculator("Edge Opacity Calculator",
		                                                 lineOpacity, VisualPropertyType.EDGE_OPACITY);

		
		EdgeAppearanceCalculator edgeAppCalc = style.getEdgeAppearanceCalculator();
   	edgeAppCalc.setCalculator(widthCalculator);
   	edgeAppCalc.setCalculator(opacityCalculator);
		style.setEdgeAppearanceCalculator(edgeAppCalc);
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

		public JTaskConfig getDefaultTaskConfig() {
			JTaskConfig result = new JTaskConfig();
	
			result.displayCancelButton(false);
			result.displayCloseButton(false);
			result.displayStatus(true);
			result.displayTimeElapsed(false);
			result.setAutoDispose(true);
			result.setModal(false);
			result.setOwner(Cytoscape.getDesktop());
	
			return result;
		}

	}

}
