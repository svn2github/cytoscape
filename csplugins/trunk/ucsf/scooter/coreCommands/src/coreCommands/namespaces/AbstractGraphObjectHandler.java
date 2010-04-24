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

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;

import cytoscape.data.Semantics;

import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XXX FIXME XXX Description 
 */
public abstract class AbstractGraphObjectHandler extends AbstractCommandHandler {
	public static final String NODELIST = "nodelist";
	public static final String NODE = "node";
	public static final String SELECTED = "selected";
	public static final String EDGELIST = "edgelist";
	public static final String EDGE = "edge";

	public AbstractGraphObjectHandler(CyCommandNamespace ns) {
		super(ns);
	}

	protected CyNetwork getNetwork(String command, Map<String, Object> args) throws CyCommandException {
		String netName = getArg(command, "network", args);
		if (netName == null || netName.equals("current"))
			return Cytoscape.getCurrentNetwork();

		CyNetwork net = Cytoscape.getNetwork(netName);
		if (net == Cytoscape.getNullNetwork())
			throw new CyCommandException(namespace.getNamespaceName()+": no such network "+netName);

		return net;
	}

	protected String makeNodeList(Collection<CyNode>nodes) {
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
	protected List<CyNode> getNodeList(CyNetwork net, CyCommandResult result, 
	                                          Map<String, Object> args) {
		if (args == null || args.size() == 0)
			return null;

		List<CyNode> retList = new ArrayList();
		if (args.containsKey(NODELIST)) {
			String[] nodes = args.get(NODELIST).toString().split(",");
			// Handle special case for "selected" nodes
			if (nodes[0].equals(SELECTED)) {
				Set<CyNode> selectedNodes = net.getSelectedNodes();
				for (CyNode node: selectedNodes) {
					addNode(net, node.getIdentifier(), retList, result);
				}
			} else {
				for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
					addNode(net, nodes[nodeIndex], retList, result);
				}
			}
		} else if (args.containsKey(NODE)) {
			String nodeName = args.get(NODE).toString();
			addNode(net, nodeName, retList, result);
		} else {
			return null;
		}
		return retList;
	}

	protected String formatNodeList(List<CyNode> nodeList) {
		if (nodeList == null || nodeList.size() == 0)
			return "(none)";
		String result = "";
		for (CyNode node: nodeList)
			result += node.getIdentifier()+", ";
		result = result.substring(0, result.length()-2);
		return result;
	}

	protected void addNode(CyNetwork net, String nodeName, List<CyNode> list, CyCommandResult result) {
		CyNode node = Cytoscape.getCyNode(nodeName, false);
		if (node == null) 
			result.addError(namespace.getNamespaceName()+": can't find node "+nodeName);
		else
			list.add(node);
		return;
	}


	/**
 	 * This method is used to handle both <b>edgelist</b> and <b>edge</b> parameters.
 	 *
 	 * @param net the network we are currently dealing with
 	 * @param result the CyCommandResult to store our values in
 	 * @param args the argument list we're use to look for <b>edgelist</b> and
 	 * <b>edge</b> arguments.
 	 * @return the list of CyEdge objects we found that matched the arguments
 	 */
	protected List<CyEdge> getEdgeList(CyNetwork net, CyCommandResult result, 
	                                          Map<String, Object> args) {
		if (args == null || args.size() == 0)
			return null;

		List<CyEdge> retList = new ArrayList();
		if (args.containsKey(EDGELIST)) {
			String[] edges = args.get(EDGELIST).toString().split(",");
			// Handle special case for "selected" edges
			if (edges[0].equals(SELECTED)) {
				Set<CyEdge> selectedEdges = net.getSelectedEdges();
				for (CyEdge edge: selectedEdges) {
					addEdge(net, edge.getIdentifier(), retList, result);
				}
			} else {
				for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++) {
					addEdge(net, edges[edgeIndex], retList, result);
				}
			}
		} else if (args.containsKey(EDGE)) {
			String edgeName = args.get(EDGE).toString();
			addEdge(net, edgeName, retList, result);
		} else {
			return null;
		}
		return retList;
	}

	protected void addEdge(CyNetwork net, String edgeName, List<CyEdge> list, CyCommandResult result) {
		CyEdge edge = getCyEdge(edgeName);
		if (edge == null) 
			result.addError(namespace.getNamespaceName()+": can't find edge "+edgeName);
		else
			list.add(edge);
		return;
	}

	protected CyEdge getCyEdge(String edgeName) {
		// An edge name consists of "source (type) destination"
		String comp[] = edgeName.split("[()]");
		// comp[0] = source, comp[2] = destination, and comp[1] = interaction
		CyNode source = Cytoscape.getCyNode(comp[0].trim(), false);
		if (source == null) return null;
		CyNode target = Cytoscape.getCyNode(comp[2].trim(), false);
		if (target == null) return null;
		CyEdge edge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, comp[1].trim(), false);
		return edge;
	}

	protected String makeEdgeList(Collection<CyEdge>edges) {
		String edgeList = "";
		if (edges == null || edges.size() == 0)
			return edgeList;

		for (CyEdge edge: edges) {
			edgeList += edge.getIdentifier()+",";
		}
		return edgeList.substring(0, edgeList.length()-1);
	}
}
