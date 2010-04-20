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

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XXX FIXME XXX Description 
 */
public class NodeNamespace extends AbstractCommandHandler {

	// Commands
	private static String DESELECT = "deselect";
	private static String EXPORT = "export attributes";
	private static String FIND = "find";
	private static String GETATTR = "get attribute";
	private static String GETSEL = "get selected";
	private static String IMPORTATTR = "import attributes";
	private static String SELECT = "select";
	private static String SETATTR = "set attribute";
	private static String LISTATTR = "list attributes";

	// Settings
	private static String ATTRIBUTE = "attribute";
	private static String EXPRESSION = "expression";
	private static String FILE = "file";
	private static String NAME = "name";
	private static String NETWORK = "network";
	private static String NODE = "node";
	private static String NODELIST = "nodelist";
	private static String TYPE = "type";
	private static String VALUE = "value";

	public NodeNamespace(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addDescription(DESELECT, "Deselect nodes.  If no node(s) are provided, all nodes are deselected");
		addArgument(DESELECT, NODE);
		addArgument(DESELECT, NODELIST);

		// addArgument(EXPORT, FILE);
		// addArgument(EXPORT, ATTRIBUTE);
		//
		// addArgument(FIND, EXPRESSION);

		addDescription(GETATTR, "Returns node attributes");
		addArgument(GETATTR, NODE);
		addArgument(GETATTR, NODELIST);
		addArgument(GETATTR, NAME);

		addDescription(GETSEL, "Lists the currently selected nodes");
		addArgument(GETSEL, NETWORK, "current");

		addDescription(IMPORTATTR, "Import node attributes from a file");
		addArgument(IMPORTATTR, FILE);

		addDescription(LISTATTR, "List node attributes");
		addArgument(LISTATTR);

		addDescription(SELECT, "Select nodes.  If no node(s) are provided, all nodes are selected");
		addArgument(SELECT, NODE);
		addArgument(SELECT, NODELIST);

		addDescription(SETATTR, "Set node attributes to a new value");
		addArgument(SETATTR, NODE);
		addArgument(SETATTR, NODELIST);
		addArgument(SETATTR, NAME);
		addArgument(SETATTR, VALUE);
		addArgument(SETATTR, TYPE);
	}

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		// Import node attributes from a file
		if (IMPORTATTR.equals(command)) {
			String fileName = getArg(command, FILE, args);
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
		// } else if ("export attributes".equals(command)) {

		// Select some ndoes
		} else if (SELECT.equals(command)) {
			CyNetwork net = getNetwork(command, args);
			List<CyNode> nodeList = NodeListUtils.getNodeList(net, result, args);
			if (nodeList == null)
				throw new CyCommandException("node: nothing to select");
			net.setSelectedNodeState(nodeList, true);
			result.addMessage("node: selected "+nodeList.size()+" nodes");
			if (net == Cytoscape.getCurrentNetwork()) {
				Cytoscape.getCurrentNetworkView().updateView();
			}

		// de-select some ndoes
		} else if (DESELECT.equals(command)) {
			CyNetwork net = getNetwork(command, args);
			try {
				List<CyNode> nodeList = NodeListUtils.getNodeList(net, result, args);
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
		} else if (GETSEL.equals(command)) {
			CyNetwork net = getNetwork(command, args);
			Set<CyNode>nodes = net.getSelectedNodes();
			result.addMessage("node: returned "+nodes.size()+" selected nodes");
			result.addResult("nodes", makeNodeList(nodes));

		// Get attribute values
		} else if (GETATTR.equals(command)) {
			CyNetwork net = getNetwork(command, args);
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			String attrName = getArg(command, NAME, args);
			if (attrName == null)
				throw new CyCommandException("node: attribute 'name' is required");
			else if (nodeAttributes.getType(attrName) == CyAttributes.TYPE_UNDEFINED)
				throw new CyCommandException("node: attribute 'name' does not exist");

			List<CyNode> nodeList = NodeListUtils.getNodeList(net, result, args);
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
		} else if (SETATTR.equals(command)) {
			CyNetwork net = getNetwork(command, args);
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			String attrName = getArg(command, NAME, args);
			String value = getArg(command, VALUE, args);
			if (attrName == null || value == null)
				throw new CyCommandException("node: attribute 'name' and 'value' are required");

			List<CyNode> nodeList = NodeListUtils.getNodeList(net, result, args);
			if (nodeList == null)
				nodeList = net.nodesList();

			String typeName = getArg(command, TYPE, args);
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
		} else if ("find".equals(command)) {

		// list node attributes
		} else if (LISTATTR.equals(command)) {
			CyNetwork net = getNetwork(command, args);
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			String[] attrNames = nodeAttributes.getAttributeNames();
			List<String>attrList = Arrays.asList(attrNames);
			result.addResult(attrList);
			result.addMessage("Node attributes: "+AttributeUtils.attributeNamesToList(nodeAttributes, attrList));
		}

		return result;
	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		return new NodeNamespace(CyCommandManager.reserveNamespace(namespace));
	}

	private CyNetwork getNetwork(String command, Map<String, Object> args) throws CyCommandException {
		String netName = getArg(command, "network", args);
		if (netName == null || netName.equals("current"))
			return Cytoscape.getCurrentNetwork();

		CyNetwork net = Cytoscape.getNetwork(netName);
		if (net == Cytoscape.getNullNetwork())
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
