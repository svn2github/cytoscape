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
package coreCommands.commands;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommand;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XXX FIXME XXX Description 
 */
public class EdgeCommand extends AbstractCommand {

	public EdgeCommand() {
		// Define our subcommands
		settingsMap = new HashMap();
		addSetting("deselect", "edge");
		addSetting("deselect", "edgeList");
		// addSetting("export attributes", "file");
		// addSetting("export attributes", "attribute");
		// addSetting("find", "expression");
		addSetting("get attribute", "edge");
		addSetting("get attribute", "edgelist");
		addSetting("get attribute", "name");
		addSetting("get selected", "network", "current");
		addSetting("import attributes", "file");
		addSetting("select", "edge");
		addSetting("select", "edgelist");
		addSetting("set attribute", "edge");
		addSetting("set attribute", "edgelist");
		addSetting("set attribute", "name");
		addSetting("set attribute", "value");

		// Handle table import????
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getCommandName() { return "edge"; }

	public CyCommandResult execute(String subCommand, Map<String, String>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		// Import edge attributes from a file
		if ("import attributes".equals(subCommand)) {
			String fileName = getArg("import attributes", "file", args);
			if (fileName == null)
				throw new CyCommandException("edge: filename is required to import attributes");

			try {
				File file = new File(fileName);
				Cytoscape.loadAttributes(new String[] { file.getAbsolutePath() },
				                         new String[] {});
				result.addMessage("edge: attributes imported from "+file.getAbsolutePath());
			} catch (Exception e) {
				throw new CyCommandException("edge: unable to import attributes: "+e.getMessage());
			}

		// Export edge attributes to a file
		// } else if ("export attributes".equals(subCommand)) {

		// Select some ndoes
		} else if ("select".equals(subCommand)) {
			CyNetwork net = getNetwork("select", args);
			List<CyEdge> edgeList = getEdgeList(net, result, args);
			if (edgeList == null)
				throw new CyCommandException("edge: nothing to select");
			net.setSelectedEdgeState(edgeList, true);
			result.addMessage("edge: selected "+edgeList.size()+" edges");
			if (net == Cytoscape.getCurrentNetwork()) {
				Cytoscape.getCurrentNetworkView().updateView();
			}

		// de-select some ndoes
		} else if ("deselect".equals(subCommand)) {
			CyNetwork net = getNetwork("deselect", args);
			try {
				List<CyEdge> edgeList = getEdgeList(net, result, args);
				net.setSelectedNodeState(edgeList, false);
				result.addMessage("edge: deselected "+edgeList.size()+" edges");
			} catch (CyCommandException e) {
				// deselect everything
				net.unselectAllEdges();
				result.addMessage("edge: deselected all edges");
			}
			if (net == Cytoscape.getCurrentNetwork()) {
				Cytoscape.getCurrentNetworkView().updateView();
			}

		// return the list of currently selected edges
		} else if ("get selected".equals(subCommand)) {
			CyNetwork net = getNetwork("get selected", args);
			Set<CyEdge>edges = net.getSelectedEdges();
			result.addMessage("edge: returned "+edges.size()+" selected edges");
			result.addResult("edges", makeEdgeList(edges));

		// Get attribute values
		} else if ("get attribute".equals(subCommand)) {
			CyNetwork net = getNetwork(subCommand, args);
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			String attrName = getArg(subCommand, "name", args);
			if (attrName == null)
				throw new CyCommandException("edge: attribute 'name' is required");
			else if (edgeAttributes.getType(attrName) == CyAttributes.TYPE_UNDEFINED)
				throw new CyCommandException("edge: attribute 'name' does not exist");

			List<CyEdge> edgeList = getEdgeList(net, result, args);
			if (edgeList == null)
				edgeList = net.edgesList();

			byte attributeType = edgeAttributes.getType(attrName);
			result.addResult("attribute type", attributeType);
			result.addMessage("edge: values for '"+attrName+"' attribute:");
			for (CyEdge edge: edgeList) {
				if (edgeAttributes.hasAttribute(edge.getIdentifier(), attrName)) {
					Object attr = edgeAttributes.getAttribute(edge.getIdentifier(), attrName);
					result.addResult(edge.getIdentifier(), attr);
					result.addMessage("   "+edge.getIdentifier()+"='"+AttributeUtils.attributeToString(attr, attributeType)+"'");
				}
			}

		// Set attribute values
		} else if ("set attribute".equals(subCommand)) {
			CyNetwork net = getNetwork(subCommand, args);
			CyAttributes edgeAttributes = Cytoscape.getNodeAttributes();
			String attrName = getArg(subCommand, "name", args);
			String value = getArg(subCommand, "value", args);
			if (attrName == null || value == null)
				throw new CyCommandException("edge: attribute 'name' and 'value' are required");

			List<CyEdge> edgeList = getEdgeList(net, result, args);
			if (edgeList == null)
				edgeList = net.edgesList();

			String typeName = getArg(subCommand, "type", args);
			byte attributeType = edgeAttributes.getType(attrName);
			if (attributeType == CyAttributes.TYPE_UNDEFINED && typeName == null)
				attributeType = CyAttributes.TYPE_STRING;
			else if (attributeType == CyAttributes.TYPE_UNDEFINED && typeName != null) {
				attributeType = AttributeUtils.attributeStringToByte(typeName);
			}

			int count = 0;
			int edgeCount = edgeList.size();
			for (CyEdge edge: edgeList) {
				String id = edge.getIdentifier();
				if (AttributeUtils.setAttribute(result, "edge", edgeAttributes, attributeType, id, attrName, value))
					count++;
			}
			result.addMessage("edge: set "+count+" attributes (out of "+edgeCount+")");

		// find edges based on an expression
		} else if ("find".equals(subCommand)) {
		}

		return result;
	}

	private CyNetwork getNetwork(String subCommand, Map<String, String> args) throws CyCommandException {
		String netName = getArg(subCommand, "network", args);
		if (netName == null || netName.equals("current"))
			return Cytoscape.getCurrentNetwork();

		CyNetwork net = Cytoscape.getNetwork(netName);
		if (net == Cytoscape.getNullNetwork())
			throw new CyCommandException("edge: no such network "+netName);
		return net;
	}

	private List<CyEdge> getEdgeList(CyNetwork net, CyCommandResult result, Map<String, String> args) 
                                   throws CyCommandException {
		if (args == null || args.size() == 0)
			throw new CyCommandException("edge: either 'edge' or 'edgeList' argument is required");

		List<CyEdge> retList = new ArrayList();
		if (args.containsKey("edgelist")) {
			String[] edges = args.get("edgelist").split(",");
			for (int edgeIndex = 0; edgeIndex < edges.length; edgeIndex++) {
				addEdge(net, edges[edgeIndex], retList, result);
			}
		} else if (args.containsKey("edge")) {
			String edgeName = args.get("edge");
			addEdge(net, edgeName, retList, result);
		} else {
			throw new CyCommandException("edge: either 'edge' or 'edgeList' argument is required");
		}
		return retList;
	}

	private String makeEdgeList(Collection<CyEdge>edges) {
		String edgeList = "";
		if (edges == null || edges.size() == 0)
			return edgeList;

		for (CyEdge edge: edges) {
			edgeList += edge.getIdentifier()+",";
		}
		return edgeList.substring(0, edgeList.length()-1);
	}

	private void addEdge(CyNetwork net, String edgeName, List<CyEdge> list, CyCommandResult result) {
		CyEdge edge = Cytoscape.getRootGraph().getEdge(edgeName);
		if (edge == null) 
			result.addError("edge: can't find edge "+edgeName);
		else
			list.add(edge);
		return;
	}
}
