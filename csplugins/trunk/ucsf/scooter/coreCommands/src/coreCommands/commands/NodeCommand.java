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

import cytoscape.CyNetwork;
import cytoscape.CyNode;
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
public class NodeCommand extends AbstractCommand {

	public NodeCommand() {
		// Define our subcommands
		settingsMap = new HashMap();
		addSetting("import attributes", "file");
		addSetting("export attributes", "file");
		// addSetting("export attributes", "attribute");
		addSetting("select", "node");
		addSetting("select", "nodeList");
		addSetting("deselect", "node");
		addSetting("deselect", "nodeList");
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
	public String getCommandName() { return "node"; }

	public CyCommandResult execute(String subCommand, Map<String, String>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		// Import node attributes from a file
		if ("import attributes".equals(subCommand)) {
			if (!haveKey(args, "file"))
				throw new CyCommandException("node: filename is required to import attributes");
			String fileName = args.get("file");
			try {
				File file = new File(fileName);
				Cytoscape.loadAttributes(new String[] { file.getAbsolutePath() },
				                         new String[] {});
				result.addMessage("node: attributes imported from "+file.getAbsolutePath());
			} catch (Exception e) {
				throw new CyCommandException("node: unable to import attributes: "+e.getMessage());
			}

		// Export node attributes to a file
		// } else if ("export attributes".equals(subCommand)) {

		// Select some ndoes
		} else if ("select".equals(subCommand)) {
			CyNetwork net = getNetwork(args);
			List<CyNode> nodeList = getNodeList(net, result, args);
			if (nodeList == null)
				throw new CyCommandException("node: nothing to select");
			net.setSelectedNodeState(nodeList, true);
			result.addMessage("node: selected "+nodeList.size()+" nodes");
			if (net == Cytoscape.getCurrentNetwork()) {
				Cytoscape.getCurrentNetworkView().updateView();
			}

		// de-select some ndoes
		} else if ("deselect".equals(subCommand)) {
			CyNetwork net = getNetwork(args);
			try {
				List<CyNode> nodeList = getNodeList(net, result, args);
				net.setSelectedNodeState(nodeList, false);
				result.addMessage("node: deselected "+nodeList.size()+" nodes");
			} catch (CyCommandException e) {
				// deselect everything
				net.unselectAllNodes();
				result.addMessage("node: deselected all nodes");
			} 

			if (net == Cytoscape.getCurrentNetwork()) {
				Cytoscape.getCurrentNetworkView().updateView();
			}

		// return the list of currently selected nodes
		} else if ("get selected".equals(subCommand)) {
			CyNetwork net = getNetwork(args);
			Set<CyNode>nodes = net.getSelectedNodes();
			result.addMessage("node: returned "+nodes.size()+" selected nodes");
			result.addResult("nodes", makeNodeList(nodes));

		// find nodes based on an expression
		} else if ("find".equals(subCommand)) {
		}

		return result;
	}

	private CyNetwork getNetwork(Map<String, String> args) throws CyCommandException {
		if (!haveKey(args, "network"))
			return Cytoscape.getCurrentNetwork();

		String netName = args.get("network");
		CyNetwork net = Cytoscape.getNetwork(netName);
		if (net == null)
			throw new CyCommandException("node: no such network "+netName);
		return net;
	}

	private List<CyNode> getNodeList(CyNetwork net, CyCommandResult result, Map<String, String> args) 
	                                 throws CyCommandException {
		if (args == null || args.size() == 0)
			throw new CyCommandException("node: either 'node' or 'nodeList' argument is required");

		List<CyNode> retList = new ArrayList();
		if (args.containsKey("nodelist")) {
			String[] nodes = args.get("nodelist").split(",");
			for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
				addNode(net, nodes[nodeIndex], retList, result);
			}
		} else if (args.containsKey("node")) {
			String nodeName = args.get("node");
			addNode(net, nodeName, retList, result);
		} else {
			throw new CyCommandException("node: either 'node' or 'nodeList' argument is required");
		}
		return retList;
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

	private void addNode(CyNetwork net, String nodeName, List<CyNode> list, CyCommandResult result) {
		CyNode node = Cytoscape.getCyNode(nodeName, false);
		if (node == null) 
			result.addError("node: can't find node "+nodeName);
		else
			list.add(node);
		return;
	}

	private boolean haveKey(Map<String,String>map, String key) {
		if (map == null || map.size() == 0 || !map.containsKey(key))
			return false;
		return true;
	}
}
