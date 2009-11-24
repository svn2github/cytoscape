/* vim: set ts=2: */
/**
 * Copyright (c) 2009 The Regents of the University of California.
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
package coreCommands.namespaces;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.command.AbstractCommand;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.ding.DingNetworkView;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import coreCommands.namespaces.networkView.ExportNetworkView;

/**
 * XXX FIXME XXX Description 
 */
public class NetworkViewNamespace extends AbstractCommand {
	static String NETWORKVIEW = "networkview";

	static String CREATE = "create";
	static String GETCURRENT = "get current";
	static String FIT = "fit";
	static String FOCUS = "focus";
	static String LIST = "list";
	static String MAKECURRENT = "make current";
	static String UPDATE = "update";
	static String ZOOM = "zoom";

	static String NETWORK = "network";
	static String CURRENT = "current";
	static String NODELIST = "nodelist";
	static String FACTOR = "factor";
	static String SCALE = "scale";

	public NetworkViewNamespace(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addArgument(CREATE, NETWORK, CURRENT);
		addArgument(GETCURRENT);
		// addArgument("get size", "network", "current");
		addArgument(FIT, NETWORK, CURRENT);
		addArgument(FOCUS, NODELIST);
		addArgument(FOCUS, NETWORK, CURRENT);
		addArgument(LIST);
		addArgument(MAKECURRENT, NETWORK);
		// addArgument("set window", "network", "current");
		// addArgument("set window", "x");
		// addArgument("set window", "y");
		// addArgument("set window", "height");
		// addArgument("set window", "width");
		addArgument(UPDATE, NETWORK, CURRENT);
		addArgument(ZOOM, FACTOR, "2.0");
		addArgument(ZOOM, SCALE);
		addArgument(ZOOM, NETWORK, CURRENT);
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getHandlerName() { return NETWORKVIEW; }

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();
		Map<String, CyNetworkView> viewMap = Cytoscape.getNetworkViewMap();
		CyNetwork net = Cytoscape.getCurrentNetwork();

		if (command.equals(GETCURRENT)) {
			CyNetworkView current = Cytoscape.getCurrentNetworkView();
			result.addMessage("networkview: current network view is "+current.getIdentifier());
			result.addResult("currentview", current);
			return result;
		} else if (command.equals(LIST)) {
			result.addResult("views",viewMap.keySet());

			result.addMessage("networkview: current network views:");
			for (String key: viewMap.keySet()) {
				result.addMessage("  "+key);
			}
			return result;
		}

		String netName = getArg(command, NETWORK, args);
		if (netName == null) {
			throw new CyCommandException("networkview: "+command+" requires network argument");
		}
		if (!netName.equalsIgnoreCase(CURRENT)) {
			net = Cytoscape.getNetwork(netName);
			if (net == null)
				throw new CyCommandException("networkview: can't find network: "+netName);
			if (!viewMap.containsKey(netName) && !command.equals("create"))
				throw new CyCommandException("networkview: can't find view for network: "+netName);
		}

		if (command.equals(CREATE)) {
			CyNetworkView view = Cytoscape.createNetworkView(net);
			result.addResult("newview",view);
			result.addMessage("networkview: created view for :"+view.getIdentifier());

		} else if (command.equals(MAKECURRENT)) {
			Cytoscape.setCurrentNetworkView(net.getIdentifier());
			result.addMessage("networkview: set network view for "+net.getIdentifier()+" as current");
		} else if (command.equals("fit")) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			view.fitContent();
			result.addMessage("networkview: fit view to content for "+net.getIdentifier());

		} else if (command.equals(UPDATE)) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			view.updateView();
			result.addMessage("networkview: view '"+net.getIdentifier()+"' updated");

		} else if (command.equals(FOCUS)) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			String nodes = getArg(command, NODELIST, args);
			if (nodes == null || nodes.length() == 0) {
				((DingNetworkView) view).fitSelected();
				view.updateView();
				result.addMessage("networkview: focused '"+net.getIdentifier()+"' on selected nodes/edges");
			} else {
				// get the list of nodes
				List<CyNode> nodeList = NodeListUtils.getNodeList(net, result, args);

				// Remember our currently selected nodes and edges
				List<CyNode>selNodes = new ArrayList(net.getSelectedNodes());
				List<CyEdge>selEdges = new ArrayList(net.getSelectedEdges());

				// Select the desire nodes
				net.unselectAllEdges();
				net.unselectAllNodes();
				net.setSelectedNodeState(nodeList, true);

				// focus
				((DingNetworkView) view).fitSelected();

				// Reselect the previously selected nodes and edges
				net.setSelectedNodeState(nodeList, false);
				net.setSelectedNodeState(selNodes, true);
				net.setSelectedEdgeState(selEdges, true);

				view.updateView();
				result.addMessage("networkview: focused '"+net.getIdentifier()+"' on node(s)");
			}
		} else if (command.equals(ZOOM)) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			double factor = Double.parseDouble(getArg(command, FACTOR, args));
			String scale = getArg(command, SCALE, args);
			double zoom = view.getZoom();
			// If we have a scale, use that -- otherwise, use the factor
			if (scale != null) {
				view.setZoom(Double.parseDouble(scale));
			} else {
				view.setZoom(zoom*factor);
			}
			result.addMessage("networkview: network '"+net.getIdentifier()+"' zoom set to "+view.getZoom());
			result.addResult("scale", new Double(view.getZoom()));

		// } else if (command.equals("get size")) {

		// } else if (command.equals("set window")) {

		} else {
			throw new CyCommandException("networkview: unknown command "+command);
		}

		return result;
	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		CyCommandNamespace ns = CyCommandManager.reserveNamespace(namespace);

		// We'll handle the simple stuff ourselves
		CyCommandHandler h = new NetworkViewNamespace(ns);

		// Now register the export stuff
		new ExportNetworkView(ns);

		return h;
	}

}
