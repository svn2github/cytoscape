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
		addSetting("import attributes", "file");
		addSetting("export attributes", "file");
		// addSetting("export attributes", "attribute");
		addSetting("select", "edge");
		addSetting("select", "edgeList");
		addSetting("deselect", "edge");
		addSetting("deselect", "edgeList");
		addSetting("find", "expression");
		addSetting("get selected", "network", "current");

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
			if (!haveKey(args, "file"))
				throw new CyCommandException("edge: filename is required to import attributes");
			String fileName = args.get("file");
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
			CyNetwork net = getNetwork(args);
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
			CyNetwork net = getNetwork(args);
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
			CyNetwork net = getNetwork(args);
			Set<CyEdge>edges = net.getSelectedEdges();
			result.addMessage("edge: returned "+edges.size()+" selected edges");
			result.addResult("edges", makeEdgeList(edges));

		// find edges based on an expression
		} else if ("find".equals(subCommand)) {
		}

		return result;
	}

	private CyNetwork getNetwork(Map<String, String> args) throws CyCommandException {
		if (!haveKey(args,"network"))
			return Cytoscape.getCurrentNetwork();

		String netName = args.get("network");
		CyNetwork net = Cytoscape.getNetwork(netName);
		if (net == null)
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

	private boolean haveKey(Map<String,String>map, String key) {
		if (map == null || map.size() == 0 || !map.containsKey(key))
			return false;
		return true;
	}
}
