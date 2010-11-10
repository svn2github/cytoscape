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
package coreCommands.namespaces.edge;

import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import giny.model.GraphObject;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import coreCommands.namespaces.AbstractGraphObjectHandler;
import coreCommands.namespaces.AttributeUtils;

/**
 * XXX FIXME XXX Description 
 */
public class ExportEdgeAttributes extends AbstractGraphObjectHandler {

	// Commands
	private static String EXPORT = "export attributes";

	// Settings
	private static String ATTRIBUTE = "attributeList";
	private static String DELIMITER = "delimiter";
	private static String FILE = "file";
	private static String NETWORK = "network";
	private static String EDGELIST = "edgelist";

	public ExportEdgeAttributes(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addDescription(EXPORT, "Export edge attributes as a table");
		addArgument(EXPORT, FILE);
		addArgument(EXPORT, ATTRIBUTE);
		addArgument(EXPORT, DELIMITER);
		addArgument(EXPORT, NETWORK, "current");
		addArgument(EXPORT, EDGELIST);
	}

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		if (EXPORT.equals(command)) {
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			// Do we have a filename?
			String fileName = getArg(command, FILE, args);
			if (fileName == null)
				throw new CyCommandException("edge: filename is required to export attributes");

			// Get our delimiter
			String delim = getArg(command, DELIMITER, args);
			if (delim == null)
				delim = "\t";

			// Get the attribute list
			String[] attrArray;
			String attrStr = getArg(command, ATTRIBUTE, args);
			if (attrStr == null) {
				attrArray = edgeAttributes.getAttributeNames();
			} else {
				attrArray = attrStr.split(",");
			}

			List<String> attrList = Arrays.asList(attrArray);

			// Get the edge list
			CyNetwork net = getNetwork(command, args);
			List edgeList = getEdgeList(net, result, args);
			if (edgeList == null) 
				edgeList = net.edgesList();

			int lineCount = 0;
			try {
				File outputFile = new File(fileName);
				lineCount = AttributeUtils.exportAttributes(outputFile, edgeAttributes, edgeList, attrList, delim);
			} catch (IOException e) {
				throw new CyCommandException("edge: export failed: "+e.getMessage());
			}
			result.addMessage("edge: exported "+lineCount+" lines to "+fileName);
		}

		return result;
	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		return new ExportEdgeAttributes(CyCommandManager.reserveNamespace(namespace));
	}

}
