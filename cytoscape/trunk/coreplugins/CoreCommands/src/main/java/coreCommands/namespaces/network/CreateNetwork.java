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
package coreCommands.namespaces.network;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;

import cytoscape.data.Semantics;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;

import coreCommands.namespaces.AbstractGraphObjectHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XXX FIXME XXX Description 
 */
public class CreateNetwork extends AbstractGraphObjectHandler {
	static String NETWORK = "network";

	// Commands
	static String CREATE = "create";

	// Arguments
	static String CREATEVIEW = "createview";
	static String DIRECTED = "directed";
	static String EDGETYPE = "edgetype";
	static String NAME = "name";
	static String PARENT = "parent";
	static String SOURCELIST = "sourcelist";
	static String TARGETLIST = "targetlist";

	public CreateNetwork(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addDescription(CREATE, "Create a new network");
		addArgument(CREATE, NAME, "NewNetwork");
		addArgument(CREATE, CREATEVIEW, "true");
		addArgument(CREATE, PARENT);
		addArgument(CREATE, TARGETLIST);
		addArgument(CREATE, SOURCELIST);
		addArgument(CREATE, EDGETYPE, "pp");
		addArgument(CREATE, DIRECTED, "true");
	}

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		String netName = getArg(command, NAME, args);
		if (netName == null)
			throw new CyCommandException("network: need a network name for the new network");

		// Handle create view
		boolean createView = true;
		{
			String cv = getArg(command, CREATEVIEW, args);
			if (cv != null && (cv.equalsIgnoreCase("false") || cv.equalsIgnoreCase("no")))
				createView = false;
		}

		// Handle parent network name
		CyNetwork parent = null;
		{
			String pName = getArg(command, PARENT, args);
			if (pName != null) {
				parent = getNetwork(command, pName);
			}
		}

		CyNetwork net = Cytoscape.createNetwork(netName, parent, createView);
		if (net == null) {
			result.addError("network: unable to create new network "+netName);
			return result;
		}

		// See if we have source and target
		String sourceList = getArg(command, SOURCELIST, args);
		String targetList = getArg(command, TARGETLIST, args);
		String edgeType = getArg(command, EDGETYPE, args);
		boolean directed = Boolean.getBoolean(getArg(command, DIRECTED, args));
		if (sourceList != null || targetList != null || edgeType != null) {
			if (targetList != null && sourceList == null)
				throw new CyCommandException("network: if 'targetlist' is specified, 'sourcelist' must be specified");

			String[] sourceNodes = sourceList.split(",");
			String[] targetNodes = null;

			if (targetList != null)
				targetNodes = targetList.split(",");

			if (targetList != null && (sourceNodes.length != targetNodes.length))
				throw new CyCommandException("network: 'targetlist' and 'sourcelist' must have the same number of nodes");
			
			for (int nodeIndex = 0; nodeIndex < sourceNodes.length; nodeIndex++) {
				CyNode source = Cytoscape.getCyNode(sourceNodes[nodeIndex], true);
				net.addNode(source);

				if (targetNodes != null) {
					CyNode target = Cytoscape.getCyNode(targetNodes[nodeIndex], true);

					CyEdge edge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, edgeType, true, directed);
					net.addNode(target);
					net.addEdge(edge);
				}
			}

			result.addMessage("Created network "+netName+" with "+sourceNodes.length+" edges");
		}

		return result;
	}

}
