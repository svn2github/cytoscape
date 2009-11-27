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

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;

import cytoscape.data.readers.GMLParser;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GMLWriter;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.writers.InteractionWriter;
import cytoscape.data.writers.XGMMLWriter;

import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.io.File;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XXX FIXME XXX Description 
 */
public class ExportNetwork extends AbstractCommandHandler {
	static String NETWORK = "network";

	// Commands
	static String EXPORT = "export";

	// Arguments
	static String NAME = "name";
	static String CURRENT = "current";
	static String PARENT = "parent";
	static String FILE = "file";
	static String TYPE = "type";

	// File types
	static String XGMML = "xgmml";
	static String GML = "gml";
	static String SIF = "sif";

	public ExportNetwork(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addArgument(EXPORT, NAME , CURRENT);
		addArgument(EXPORT, FILE);
		addArgument(EXPORT, TYPE, XGMML);
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getHandlerName() { return EXPORT; }

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		String netName = getArg(command, NAME, args);
		String type = getArg(command, TYPE, args);
		String fileName = getArg(command, FILE, args);
		Object[] ret_val = new Object[3]; // For property change event

		if (fileName == null)
			throw new CyCommandException("network: 'file' must be specified for export");

		CyNetwork net = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		if (!netName.equalsIgnoreCase(CURRENT)) {
			net = Cytoscape.getNetwork(netName);
			view = Cytoscape.getNetworkView(netName);
		}
		if (net == null)
			throw new CyCommandException("network: the network '"+netName+"' doesn't exist");

		ret_val[0] = net;

		if (type.equalsIgnoreCase(XGMML)) {
			if (!fileName.endsWith(".xgmml"))
				fileName += fileName + ".xgmml";

			try {
				FileWriter fileWriter = new FileWriter(fileName);
				XGMMLWriter xgmmlWriter = new XGMMLWriter(net, view);
				xgmmlWriter.write(fileWriter);
				result.addMessage("network: exported network '"+
					                net.getIdentifier()+"' to '"+fileName);
				ret_val[2] = new Integer(Cytoscape.FILE_XGMML);
				fileWriter.close();
			} catch (Exception e) {
				result.addError("network: unable to export network '"+
				                net.getIdentifier()+"' to '"+fileName+": "+e.getMessage());
			}

		} else if (type.equalsIgnoreCase(GML)) {
			if (!fileName.endsWith(".gml"))
				fileName = fileName + ".gml";

			List list;
			GMLReader reader = (GMLReader) net.getClientData("GML");
			if (reader != null) {
				list = reader.getList();
			} else {
				list = new ArrayList();
			}

			try {
				FileWriter fileWriter = new FileWriter(fileName);
				GMLWriter gmlWriter = new GMLWriter();
				gmlWriter.writeGML(net, view, list);
				GMLParser.printList(list, fileWriter);
				fileWriter.close();
				result.addMessage("network: exported network '"+
					                net.getIdentifier()+"' to '"+fileName);

				ret_val[2] = new Integer(Cytoscape.FILE_GML);
			} catch (Exception e) {
				result.addError("network: unable to export network '"+
				                net.getIdentifier()+"' to '"+fileName+": "+e.getMessage());
			}

		} else if (type.equalsIgnoreCase(SIF)) {
			if (!fileName.endsWith(".sif"))
				fileName = fileName + ".sif";

			try {
				FileWriter fileWriter = new FileWriter(fileName);
				InteractionWriter.writeInteractions(net, fileWriter, null);
				fileWriter.close();
				result.addMessage("network: exported network '"+
					                net.getIdentifier()+"' to '"+fileName);

				ret_val[2] = new Integer(Cytoscape.FILE_SIF);
			} catch (Exception e) {
				result.addError("network: unable to export network '"+
				                net.getIdentifier()+"' to '"+fileName+": "+e.getMessage());
			}

		} else
			throw new CyCommandException("network: export type "+type+" is not supported");

		if (result.successful()) {
			ret_val[1] = new File(fileName).toURI();
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);
		}

		return result;
	}
}
