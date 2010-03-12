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
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XXX FIXME XXX Description 
 */
public class GroupNamespace extends AbstractCommandHandler {

	// Group commands
	static final String LIST = "list";
	static final String CREATE = "create";
	static final String DESTROY = "destroy";
	static final String GETGROUP = "getGroup";
	static final String ADD = "add";
	static final String REMOVE = "remove";
	static final String GETVIEWER = "getViewer";
	static final String SETVIEWER = "setViewer";
	static final String GETSTATE = "getState";
	static final String SETSTATE = "setState";
	static final String GETNODES = "getNodes";
	static final String GETOUTEREDGES = "getOuterEdges";
	static final String GETINNEREDGES = "getInnerEdges";

	// Arguments
	static final String NAME = "name";
	static final String NODE = "node";
	static final String NODELIST = "nodelist";
	static final String VIEWER = "viewer";
	static final String STATE = "state";

	public GroupNamespace(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addDescription(LIST, "List the current groups");
		addArgument(LIST);

		addDescription(CREATE, "Create a new group");
		addArgument(CREATE, NAME);
		addArgument(CREATE, NODE);
		addArgument(CREATE, NODELIST);
		addArgument(CREATE, VIEWER);

		addDescription(DESTROY, "Destroy (delete) a group");
		addArgument(DESTROY, NAME);

		addDescription(GETGROUP, "Get a group");
		addArgument(GETGROUP, NAME);

		addDescription(ADD, "Add a node (or nodes) to an existing group");
		addArgument(ADD, NAME);
		addArgument(ADD, NODE);
		addArgument(ADD, NODELIST);

		addDescription(REMOVE, "Remove a node (or nodes) from an existing group");
		addArgument(REMOVE, NAME);
		addArgument(REMOVE, NODE);
		addArgument(REMOVE, NODELIST);

		addDescription(GETVIEWER, "Get the current viewer for a group");
		addArgument(GETVIEWER, NAME);

		addDescription(SETVIEWER, "Set a viewer for a group");
		addArgument(SETVIEWER, NAME);
		addArgument(SETVIEWER, VIEWER);

		addDescription(GETSTATE, "Get the current state for a group");
		addArgument(GETSTATE, NAME);

		addDescription(SETSTATE, "Set the current state for a group");
		addArgument(SETSTATE, NAME);
		addArgument(SETSTATE, STATE);

		addDescription(GETNODES, "Get the current list of nodes for a group");
		addArgument(GETNODES, NAME);
		addDescription(GETOUTEREDGES, "Get the current list of outer edges for a group");
		addArgument(GETOUTEREDGES, NAME);
		addDescription(GETINNEREDGES, "Get the current list of inner edges for a group");
		addArgument(GETINNEREDGES, NAME);
	}

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		// Get the list of groups
		if (command.equals(LIST)) {
			List<CyGroup> groupList = CyGroupManager.getGroupList();
			result.addResult("groups",groupList);
			result.addMessage("group: group list:");
			for (CyGroup group: groupList) {
				List<CyNode> nodeList = group.getNodes();
				List<CyEdge> innerEdges = group.getInnerEdges();
				List<CyEdge> outerEdges = group.getOuterEdges();
				result.addMessage("   "+group.toString()+": "+nodeList.size()+" nodes, "+
				                  innerEdges.size()+" inner edges, and "+outerEdges.size()+" outer edges");
			}
			return result;
		}
		
		// Handle required arguments up front
		if (!args.containsKey(NAME))
			throw new CyCommandException("group: need the name of the group for "+command);

		String groupName = (String)args.get(NAME);
		if ((command.equals(CREATE) || command.equals(ADD) || command.equals(REMOVE)) &&
		    (!args.containsKey(NODE) && !args.containsKey(NODELIST)))
			throw new CyCommandException("group: need a node or list of nodes for "+command);

		// OK, here are the commands

		// Create a group
		if (command.equals(CREATE)) {
			CyGroup group = null;
			List<CyNode> nodeList = NodeListUtils.getNodeList(Cytoscape.getCurrentNetwork(),result, args);
			if (result.getErrors() != null && result.getErrors().size() > 0)
				return result;

			if (args.containsKey(VIEWER)) {
				String viewer = (String)args.get(VIEWER);
				group = CyGroupManager.createGroup(groupName, nodeList, viewer);
			} else {
				group = CyGroupManager.createGroup(groupName, nodeList, null);
			}
			result.addMessage("group: created group '"+groupName+"' with "+nodeList.size()+" nodes");
			result.addResult("group",group);
			return result;
		}

		// All of the other commands require a group -- get it now
		CyGroup group = CyGroupManager.findGroup(groupName);
		if (group == null) {
			result.addError("group: cannot find a group named '"+groupName);
			return result;
		}

		// Destroy a group
		if (command.equals(DESTROY)) {
			CyGroupManager.removeGroup(group);

		// Get a group
		} else if (command.equals(GETGROUP)) {
			result.addResult("group", group);
			result.addMessage(group.toString());

		// Add a node (or nodes) to a group
		} else if (command.equals(ADD)) {
			List<CyNode> nodeList = NodeListUtils.getNodeList(Cytoscape.getCurrentNetwork(),result, args);
			for (CyNode node: nodeList)
				group.addNode(node);
			result.addMessage("group: added "+nodeList.size()+" nodes to group: "+group);

		// Remove a node (or nodes) from a group
		} else if (command.equals(REMOVE)) {
			List<CyNode> nodeList = NodeListUtils.getNodeList(Cytoscape.getCurrentNetwork(),result, args);
			for (CyNode node: nodeList)
				group.removeNode(node);
			result.addMessage("group: removed "+nodeList.size()+" nodes from group: "+group);

		// Get the current viewer for a group
		} else if (command.equals(GETVIEWER)) {
			if (group.getViewer() != null)
				result.addMessage("group: viewer for group "+groupName+" is "+group.getViewer());
			else
				result.addMessage("group: no viewer for group "+groupName);
			result.addResult("viewer", group.getViewer());

		// Set the viewer for a group
		} else if (command.equals(SETVIEWER)) {
			if (!args.containsKey(VIEWER))
				throw new CyCommandException("group: need a viewer for "+command);

			String viewer = (String) args.get(VIEWER);
			CyGroupManager.setGroupViewer(group, viewer, Cytoscape.getCurrentNetworkView(), true);
			result.addMessage("group: set viewer for group '"+groupName+"' to '"+viewer+"'");

		// Get the current state for a group
		} else if (command.equals(GETSTATE)) {
			int state = group.getState();
			result.addResult("state", new Integer(state));
			result.addMessage("group: state for group '"+groupName+"' = "+state);

		// Set the current state for a group
		} else if (command.equals(SETSTATE)) {
			if (!args.containsKey(STATE))
				throw new CyCommandException("group: need a state for "+command);

			int state;
			try {
				state = Integer.parseInt((String)args.get(STATE));
			} catch (NumberFormatException e) {
				throw new CyCommandException("group: state nust be an integer for "+command);
			}
			group.setState(state);
			result.addMessage("group: set the state for group '"+groupName+"' to "+state);

		// Get the current list of nodes for a group
		} else if (command.equals(GETNODES)) {
			List<CyNode> nodeList = group.getNodes();
			result.addResult("nodeList", nodeList);
			result.addMessage("Nodes for group '"+groupName+"': ");
			result.addMessage("   "+NodeListUtils.formatNodeList(nodeList));

		// Get the current list of outer edges for a group
		} else if (command.equals(GETOUTEREDGES)) {
			List<CyEdge> edgeList = group.getOuterEdges();
			result.addResult("edgeList", edgeList);
			result.addMessage("Outer edges for group '"+groupName+"': ");
			result.addMessage("   "+formatEdgeList(edgeList));

		// Get the current list of inner edges for a group
		} else if (command.equals(GETINNEREDGES)) {
			List<CyEdge> edgeList = group.getInnerEdges();
			result.addResult("edgeList", edgeList);
			result.addMessage("Inner edges for group '"+groupName+"': ");
			result.addMessage("   "+formatEdgeList(edgeList));
		}

		return result;

	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		return new GroupNamespace(CyCommandManager.reserveNamespace(namespace));
	}

	private String formatEdgeList(List<CyEdge> edgeList) {
		if (edgeList == null || edgeList.size() == 0)
			return "(none)";
		String result = "";
		for (CyEdge edge: edgeList) 
			result += edge.getIdentifier()+", ";
		result = result.substring(0, result.length()-2);
		return result;
	}
}
