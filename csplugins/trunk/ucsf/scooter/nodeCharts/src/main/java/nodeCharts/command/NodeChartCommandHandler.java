/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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
package nodeCharts.command;

// System imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.view.CyNetworkView;

// nodeChart imports
import nodeCharts.view.NodeChartViewer;
import nodeCharts.view.PieChart;
import nodeCharts.view.ViewUtils;

/**
 * 
 */
public class NodeChartCommandHandler extends AbstractCommandHandler {
	CyLogger logger;
	Map<String, NodeChartViewer> viewerMap;

	// Global commands
	public static final String ALL = "all";
	public static final String ATTRIBUTELIST = "attributelist";
	public static final String CLEAR = "clear";
	public static final String CURRENT = "current";
	public static final String LABELS = "labellist";
	public static final String NETWORK = "network";
	public static final String NODE = "node";
	public static final String NODELIST = "nodelist";
	public static final String POSITION = "position";
	public static final String SELECTED = "selected";
	public static final String VALUES = "valuelist";

	public NodeChartCommandHandler(String namespace, CyLogger logger) {
		super(CyCommandManager.reserveNamespace(namespace));

		this.logger = logger;
		viewerMap = new HashMap<String, NodeChartViewer>();

		// Register each command
		register(new PieChart());

		// Register our built-in commands
		addDescription(CLEAR, "Remove all charts from a node");
		addArgument(CLEAR, NODE);
		addArgument(CLEAR, NODELIST, SELECTED);
	}

  public CyCommandResult execute(String command, Collection<Tunable>args)
                                                      throws CyCommandException, RuntimeException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args)
                                                      throws CyCommandException, RuntimeException {

		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		return executeNodeChart(command, args, true, view);
	}


	private CyCommandResult executeNodeChart(String command, Map<String, Object>args, 
	                                         boolean saveCommand, CyNetworkView view)
                                                      throws CyCommandException, RuntimeException {
		CyCommandResult result = new CyCommandResult();

		if (!args.containsKey(NODE) && !args.containsKey(NODELIST)) {
			throw new CyCommandException("node or nodelist to map chart to must be specified");
		}

		CyNetwork network = getNetwork(command, args);
		List<CyNode> nodeList = getNodeList(network, result, args);
		if (nodeList == null)
			throw new CyCommandException("can't find node(s) or none specified");

		// Handle built-ins
		if (command.equals(CLEAR)) {
			for (CyNode node: nodeList) {
				ViewUtils.clearCustomGraphics(node, view);
				result.addMessage("Cleared  charts for node "+node.getIdentifier());
				clearCharts(node);
			}
			return result;
		}

		// Now, handle viewer commands
		if (!viewerMap.containsKey(command))
			throw new CyCommandException("unknown chart type: '"+command+"'");

		// Pick up all of our "Standard" attributes and handle them
		// for the viewer.  In particular, we want to pass the viewer
		// a list of values and a list of labels rather than having
		// eacy viewer try to determine where to get their values
		// and labels

		// First, sanity check our defaults
		if (!args.containsKey(VALUES) && !args.containsKey(ATTRIBUTELIST))
			throw new CyCommandException("nodecharts values or attributes list is mandatory");

		if (args.containsKey(VALUES) && args.containsKey(ATTRIBUTELIST))
			throw new CyCommandException("nodecharts can't handle both attributeslist and valuelist");

		if (!args.containsKey(LABELS) && !args.containsKey(ATTRIBUTELIST))
			throw new CyCommandException("nodecharts requires either a labels list or an attribute list");

		// Now get our actual viewer
		NodeChartViewer viewer = viewerMap.get(command);

		// OK, at this point, we've either got values or attributes
		List<Double> values = null;
		if (args.containsKey(VALUES)) {
			// Get our values.  convertData returns an array of values in degrees of arc
			values = ValueUtils.convertInputToDouble((String)args.get(VALUES));
		}

		List<String> labels = new ArrayList<String>();
		if (args.containsKey(LABELS)) {
			// Get our labels.  These may or may not be printed depending on options
			labels = ValueUtils.getStringList((String)args.get(LABELS));
		}

		// Get our position
		Object pos = null;
		if (args.containsKey(POSITION)) {
			String position = (String) args.get(POSITION);
			pos = ViewUtils.getPosition(position);
			if (pos == null)
				throw new CyCommandException("unknown position keyword or illegal position expression: "+position);
		}

		// OK, do it!
		for (CyNode node: nodeList) {
			// If we've got an attributelist, we need to get our values now since they will change based on
			// the node
			if (args.containsKey(ATTRIBUTELIST))
				values = ValueUtils.getDataFromAttributes (node, (String)args.get(ATTRIBUTELIST), labels);

			List<CustomGraphic> cgList = viewer.getCustomGraphics(args, values, labels, node, view, pos);
			ViewUtils.addCustomGraphics(cgList, node, view);
			result.addMessage("Created "+viewer.getName()+" chart for node "+node.getIdentifier());
			// If we succeeded, serialize the command and save it in the appropriate nodeAttribute
			if (saveCommand)
				saveChart(node, command, args);
		}
		return result;
	}

	private void register(NodeChartViewer viewer) {
		addDescription(viewer.getName(), viewer.getDescription());
		// Add our global commands
		addArgument(viewer.getName(), NETWORK, CURRENT);
		addArgument(viewer.getName(), NODE);
		addArgument(viewer.getName(), NODELIST);
		addArgument(viewer.getName(), POSITION);

		// Get the specific commands handled by the viewer
		Map<String,String> args = viewer.getOptions();
		for (String arg: args.keySet()) {
			if (args.get(arg) == "") {
				addArgument(viewer.getName(), arg);
			} else {
				addArgument(viewer.getName(), arg, args.get(arg));
			}
		}
		viewerMap.put(viewer.getName(), viewer);
	}

	/**************************************************************************************
 	 *                  Methods to save and restore chart information                     *
	 *************************************************************************************/
	private static final String CHARTLISTATTR = "__nodeChartList";
	private static final String CHARTATTR = "__nodeChart_%s_%d";

	public List<CyCommandResult> reloadCharts(CyNetworkView view) throws CyCommandException {
		List<CyCommandResult> resultList = new ArrayList<CyCommandResult>();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		// For each node:
		for (CyNode node: (List<CyNode>)view.getNetwork().nodesList()) {
			String nodeName = node.getIdentifier();
			// looking for our attribute
			if (nodeAttributes.hasAttribute(nodeName, CHARTLISTATTR)) {
				List<String> chartList = nodeAttributes.getListAttribute(nodeName, CHARTLISTATTR);
				// for each command in the command list
				for (String chart: chartList) {
					String command = chart.substring(12,chart.lastIndexOf('_'));
					// Get the command args
					Map<String,Object> args = nodeAttributes.getMapAttribute(nodeName, chart);
					// Execute it
					CyCommandResult comResult = executeNodeChart(command, args, false, view);
					resultList.add(comResult);
				}
			}
		}
		return resultList;
	}

	private void saveChart(CyNode node, String command, Map<String,Object>args) {
		// Get the list of charts we have so far
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String nodeName = node.getIdentifier();
		List<String> chartList = null;
		if (!nodeAttributes.hasAttribute(nodeName, CHARTLISTATTR)) {
			chartList = new ArrayList<String>();
		} else {
			chartList = nodeAttributes.getListAttribute(nodeName, CHARTLISTATTR);
		}
		int nCharts = chartList.size();

		// Create the new chart attribute
		String newChartAttribute = String.format(CHARTATTR, command, nCharts);
		Map<String,String> argMap = ValueUtils.serializeArgMap(args);
		nodeAttributes.setMapAttribute(nodeName, newChartAttribute, argMap);

		// Add it to the list
		chartList.add(newChartAttribute);

		// Save the updated list
		nodeAttributes.setListAttribute(nodeName, CHARTLISTATTR, chartList);
		nodeAttributes.setUserVisible(CHARTATTR, false);
		nodeAttributes.setUserVisible(newChartAttribute, false);
	}

	private void clearCharts(CyNode node) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String nodeName = node.getIdentifier();
		if (nodeAttributes.hasAttribute(nodeName, CHARTLISTATTR)) {
			List<String>chartList = nodeAttributes.getListAttribute(nodeName, CHARTLISTATTR);
			for (String chartAttr: chartList) {
				nodeAttributes.deleteAttribute(nodeName, chartAttr);
			}
			nodeAttributes.deleteAttribute(nodeName, CHARTLISTATTR);
		}
	}

	/**************************************************************************************
 	 *                  Methods for special command handling                              *
	 *************************************************************************************/
	private CyNetwork getNetwork(String command, Map<String, Object> args) throws CyCommandException {
		String netName = getArg(command, NodeChartCommandHandler.NETWORK, args);
		if (netName == null || netName.equals(NodeChartCommandHandler.CURRENT))
			return Cytoscape.getCurrentNetwork();

		CyNetwork net = Cytoscape.getNetwork(netName);
		if (net == Cytoscape.getNullNetwork())
			throw new CyCommandException(namespace.getNamespaceName()+": no such network "+netName);

		return net;
	}

	private String makeNodeList(Collection<CyNode>nodes) {
		String nodeList = "";
		if (nodes == null || nodes.size() == 0)
			return nodeList;

		for (CyNode node: nodes) {
			nodeList += node.getIdentifier()+",";
		}
		return nodeList.substring(0, nodeList.length()-1);
	}

	/**
 	 * This method is used to handle both <b>nodelist</b> and <b>node</b> parameters.
 	 *
 	 * @param net the network we are currently dealing with
 	 * @param result the CyCommandResult to store our values in
 	 * @param args the argument list we're use to look for <b>nodelist</b> and
 	 * <b>node</b> arguments.
 	 * @return the list of CyNode objects we found that matched the arguments
 	 */
	private List<CyNode> getNodeList(CyNetwork net, CyCommandResult result, 
	                                   Map<String, Object> args) {
		return getNodeList(net, result, args, false);
	}

	/**
 	 * This method is used to handle both <b>nodelist</b> and <b>node</b> parameters.
 	 *
 	 * @param net the network we are currently dealing with
 	 * @param result the CyCommandResult to store our values in
 	 * @param args the argument list we're use to look for <b>nodelist</b> and
 	 * <b>node</b> arguments.
 	 * @param create if true, create the node if it doesn't exist
 	 * @return the list of CyNode objects we found that matched the arguments
 	 */
	private List<CyNode> getNodeList(CyNetwork net, CyCommandResult result, 
	                                   Map<String, Object> args, boolean create) {
		if (args == null || args.size() == 0)
			return null;

		List<CyNode> retList = new ArrayList();
		if (args.containsKey(NODE)) {
			String nodeName = args.get(NODE).toString();
			addNode(net, nodeName, retList, result, create);
		} else if (args.containsKey(NODELIST)) {
			String[] nodes = args.get(NODELIST).toString().split(",");
			// Handle special case for "selected" nodes
			if (nodes[0].equals(SELECTED)) {
				Set<CyNode> selectedNodes = net.getSelectedNodes();
				for (CyNode node: selectedNodes) {
					addNode(net, node.getIdentifier(), retList, result, create);
				}
			// Handle special case for "all" nodes
			} else if (nodes[0].equals(ALL)) {
				for (CyNode node: (List<CyNode>)net.nodesList()) {
					addNode(net, node.getIdentifier(), retList, result, create);
				}
			// Handle node list
			} else {
				for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
					addNode(net, nodes[nodeIndex], retList, result, create);
				}
			}
		} else {
			return null;
		}
		return retList;
	}

	private String formatNodeList(List<CyNode> nodeList) {
		if (nodeList == null || nodeList.size() == 0)
			return "(none)";
		String result = "";
		for (CyNode node: nodeList)
			result += node.getIdentifier()+", ";
		result = result.substring(0, result.length()-2);
		return result;
	}

	private void addNode(CyNetwork net, String nodeName, List<CyNode> list, 
	                       CyCommandResult result, boolean create) {
		CyNode node = Cytoscape.getCyNode(nodeName, create);
		if (node == null) 
			result.addError(namespace.getNamespaceName()+": can't find node "+nodeName);
		else
			list.add(node);
		return;
	}
}
