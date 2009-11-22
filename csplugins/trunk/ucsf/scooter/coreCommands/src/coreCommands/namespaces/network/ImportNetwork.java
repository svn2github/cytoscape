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

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;

import cytoscape.data.readers.GMLParser;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GraphReader;

import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.io.File;
import java.io.FileWriter;

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coreCommands.namespaces.AbstractCommand;

/**
 * XXX FIXME XXX Description 
 */
public class ImportNetwork extends AbstractCommand {
	static String NETWORK = "network";

	// Commands
	static String IMPORT = "import";

	// Arguments
	static String NAME = "name";
	static String CREATEVIEW = "createview";
	static String PARENT = "parent";
	static String FILE = "file";
	static String TYPE = "type";

	// File types
	static String XGMML = "xgmml";
	static String GML = "gml";
	static String SIF = "sif";

	public ImportNetwork(CyCommandNamespace ns) {
		this.namespace = ns;

		// Define our subcommands
		settingsMap = new HashMap();
		addSetting(IMPORT, FILE);
		addSetting(IMPORT, CREATEVIEW, "true");
		addSetting(IMPORT, PARENT);
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getHandlerName() { return IMPORT; }

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		String fileName = getArg(command, FILE, args);
		if (fileName == null)
			throw new CyCommandException("network: 'file' must be specified for import");

		File file = new File(fileName);
		GraphReader reader = Cytoscape.getImportHandler().getReader(file.getAbsolutePath());
		URI uri = file.toURI();

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
				parent = Cytoscape.getNetwork(pName);
				if (parent == null)
					throw new CyCommandException("network: parent network "+pName+" doesn't exist");
			}
		}

		try {
			CyNetwork cyNetwork = Cytoscape.createNetwork(reader, createView, parent);
			result.addMessage("network: import complete.  Network has "+cyNetwork.getNodeCount()+
                         " nodes and "+cyNetwork.getEdgeCount()+" edges");
			result.addResult("nodecount",""+cyNetwork.getNodeCount());
			result.addResult("edgecount",""+cyNetwork.getEdgeCount());
		} catch (Exception e) {
			throw new CyCommandException("network: unable to import network from file '"+
			                             file.getAbsolutePath()+"': "+e.getMessage());
		}

		return result;
	}

}
