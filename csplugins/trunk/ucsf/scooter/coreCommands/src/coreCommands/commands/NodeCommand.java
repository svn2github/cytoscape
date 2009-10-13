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
public class NodeCommand extends AbstractCommand {

	public NodeCommand() {
		// Define our subcommands
		settingsMap = new HashMap();
		addSetting("deselect", "node");
		addSetting("deselect", "nodeList");
		// addSetting("export attributes", "file");
		// addSetting("export attributes", "attribute");
		// addSetting("find", "expression");
		addSetting("get attribute", "node");
		addSetting("get attribute", "nodelist");
		addSetting("get attribute", "name");
		addSetting("get selected", "network", "current");
		addSetting("import attributes", "file");
		addSetting("select", "node");
		addSetting("select", "nodeList");
		addSetting("set attribute", "node");
		addSetting("set attribute", "nodelist");
		addSetting("set attribute", "name");
		addSetting("set attribute", "value");
		addSetting("set attribute", "type");
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
			String fileName = getArg(subCommand, "file", args);
			if (fileName == null)
				throw new CyCommandException("node: filename is required to import attributes");
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
			CyNetwork net = getNetwork(subCommand, args);
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
			CyNetwork net = getNetwork(subCommand, args);
			try {
				List<CyNode> nodeList = getNodeList(net, result, args);
				if (nodeList == null)
					throw new CyCommandException("node: nothing to deselect");

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
			CyNetwork net = getNetwork(subCommand, args);
			Set<CyNode>nodes = net.getSelectedNodes();
			result.addMessage("node: returned "+nodes.size()+" selected nodes");
			result.addResult("nodes", makeNodeList(nodes));

		// Get attribute values
		} else if ("get attribute".equals(subCommand)) {
			CyNetwork net = getNetwork(subCommand, args);
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			String attrName = getArg(subCommand, "name", args);
			if (attrName == null)
				throw new CyCommandException("node: attribute 'name' is required");
			else if (nodeAttributes.getType(attrName) == CyAttributes.TYPE_UNDEFINED)
				throw new CyCommandException("node: attribute 'name' does not exist");

			List<CyNode> nodeList = getNodeList(net, result, args);
			if (nodeList == null)
				nodeList = net.nodesList();

			byte attributeType = nodeAttributes.getType(attrName);
			result.addResult("attribute type", attributeType);
			result.addMessage("node: values for '"+attrName+"' attribute:");
			for (CyNode node: nodeList) {
				if (nodeAttributes.hasAttribute(node.getIdentifier(), attrName)) {
					Object attr = nodeAttributes.getAttribute(node.getIdentifier(), attrName);
					result.addResult(node.getIdentifier(), attr);
					result.addMessage("   "+node.getIdentifier()+"='"+AttributeUtils.attributeToString(attr, attributeType)+"'");
				}
			}

		// Set attribute values
		} else if ("set attribute".equals(subCommand)) {
			CyNetwork net = getNetwork(subCommand, args);
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			String attrName = getArg(subCommand, "name", args);
			String value = getArg(subCommand, "value", args);
			if (attrName == null || value == null)
				throw new CyCommandException("node: attribute 'name' and 'value' are required");

			List<CyNode> nodeList = getNodeList(net, result, args);
			if (nodeList == null)
				nodeList = net.nodesList();

			String typeName = getArg(subCommand, "type", args);
			byte attributeType = nodeAttributes.getType(attrName);
			if (attributeType == CyAttributes.TYPE_UNDEFINED && typeName == null)
				attributeType = CyAttributes.TYPE_STRING;
			else if (attributeType == CyAttributes.TYPE_UNDEFINED && typeName != null) {
				attributeType = AttributeUtils.attributeStringToByte(typeName);
			}

			int count = 0;
			int nodeCount = nodeList.size();
			for (CyNode node: nodeList) {
				String id = node.getIdentifier();
				if (AttributeUtils.setAttribute(result, "node", nodeAttributes, attributeType, id, attrName, value))
					count++;
			}
			result.addMessage("node: set "+count+" attributes (out of "+nodeCount+")");

		// find nodes based on an expression
		} else if ("find".equals(subCommand)) {
		}

		return result;
	}

	private CyNetwork getNetwork(String subCommand, Map<String, String> args) throws CyCommandException {
		String netName = getArg(subCommand, "network", args);
		if (netName == null)
			return Cytoscape.getCurrentNetwork();

		CyNetwork net = Cytoscape.getNetwork(netName);
		if (net == null)
			throw new CyCommandException("node: no such network "+netName);
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
}
