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
public abstract class AbstractEdgeCommand extends AbstractCommand {

	public AbstractEdgeCommand(String commandName) {
		super("edge",commandName);
	}

	protected CyNetwork getNetwork(Map<String, String> args) throws CyCommandException {
		String netName = getArg("network", args);
		if (netName == null || netName.equals("current"))
			return Cytoscape.getCurrentNetwork();

		CyNetwork net = Cytoscape.getNetwork(netName);
		if (net == Cytoscape.getNullNetwork())
			throw new CyCommandException("edge: no such network "+netName);
		return net;
	}

	protected List<CyEdge> getEdgeList(CyNetwork net, CyCommandResult result, Map<String, String> args) 
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

	protected String makeEdgeList(Collection<CyEdge>edges) {
		String edgeList = "";
		if (edges == null || edges.size() == 0)
			return edgeList;

		for (CyEdge edge: edges) {
			edgeList += edge.getIdentifier()+",";
		}
		return edgeList.substring(0, edgeList.length()-1);
	}

	protected void addEdge(CyNetwork net, String edgeName, List<CyEdge> list, CyCommandResult result) {
		CyEdge edge = Cytoscape.getRootGraph().getEdge(edgeName);
		if (edge == null) 
			result.addError("edge: can't find edge "+edgeName);
		else
			list.add(edge);
		return;
	}
}
