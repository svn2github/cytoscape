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
import cytoscape.Cytoscape;

import cytoscape.command.CyCommand;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
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

import java.net.URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * XXX FIXME XXX Description 
 */
public class NetworkCommand extends AbstractCommand {

	public NetworkCommand() {
		// Define our subcommands
		settingsMap = new HashMap();
		addSetting("create", "name", "NewNetwork");
		addSetting("create", "createview", "true");
		addSetting("create", "parent");
		addSetting("destroy", "name");
		addSetting("export", "name" , "current");
		addSetting("export", "file");
		addSetting("export", "type", "xgmml");
		addSetting("get current");
		addSetting("import", "file");
		addSetting("import", "createview", "true");
		addSetting("import", "parent");
		addSetting("list");
		addSetting("make current", "name");
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getCommandName() { return "network"; }

	public CyCommandResult execute(String subCommand, Map<String, String>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		// Import a network
		if (subCommand.equals("import")) {
			String fileName = getArg(subCommand, "file", args);
			if (fileName == null)
				throw new CyCommandException("network: 'file' must be specified for import");

			File file = new File(fileName);
			GraphReader reader = Cytoscape.getImportHandler().getReader(file.getAbsolutePath());
			URI uri = file.toURI();

			// Handle create view
			boolean createView = true;
			{
				String cv = getArg(subCommand, "createview", args);
				if (cv != null && (cv.equalsIgnoreCase("false") || cv.equalsIgnoreCase("no"))) 
					createView = false;
			}

			// Handle parent network name
			CyNetwork parent = null;
			{
				String pName = getArg(subCommand, "parent", args);
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

		// Destroy an existing network
		} else if (subCommand.equals("destroy")) {
			String netName = getArg(subCommand, "name", args);
			if (netName == null)
				throw new CyCommandException("network: need the name of the network to destroy");

			CyNetwork net = Cytoscape.getCurrentNetwork();
			if (!netName.equalsIgnoreCase("current"))
				net = Cytoscape.getNetwork(netName);

			if (net == null)
				throw new CyCommandException("network: the network '"+netName+"' doesn't exist");

			Cytoscape.destroyNetwork(net);

		// Export a network
		} else if (subCommand.equals("export")) {
			String netName = getArg(subCommand, "name", args);
			String type = getArg(subCommand, "type", args);
			String fileName = getArg(subCommand, "file", args);
			Object[] ret_val = new Object[3]; // For property change event

			if (fileName == null)
				throw new CyCommandException("network: 'file' must be specified for export");

			CyNetwork net = Cytoscape.getCurrentNetwork();
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			if (!netName.equalsIgnoreCase("current")) {
				net = Cytoscape.getNetwork(netName);
				view = Cytoscape.getNetworkView(netName);
			}
			if (net == null)
				throw new CyCommandException("network: the network '"+netName+"' doesn't exist");

			ret_val[0] = net;

			if (type.equalsIgnoreCase("xgmml")) {
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

			} else if (type.equalsIgnoreCase("gml")) {
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

			} else if (type.equalsIgnoreCase("sif")) {
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

		// Create a network
		} else if (subCommand.equals("create")) {
			String netName = getArg(subCommand, "name", args);
			if (netName == null)
				throw new CyCommandException("network: need a network name for the new network");

			// Handle create view
			boolean createView = true;
			{
				String cv = getArg(subCommand, "createview", args);
				if (cv != null && (cv.equalsIgnoreCase("false") || cv.equalsIgnoreCase("no")))
					createView = false;
			}

			// Handle parent network name
			CyNetwork parent = null;
			{
				String pName = getArg(subCommand, "parent", args);
				if (pName != null) {
					parent = Cytoscape.getNetwork(pName);
					if (parent == null)
						throw new CyCommandException("network: parent network "+pName+" doesn't exist");
				}
			}

			CyNetwork net = Cytoscape.createNetwork(netName, parent, createView);
			if (net == null)
				result.addError("network: unable to create new network "+netName);
			else
				result.addMessage("network: created new network "+netName);

		// Return the current network
		} else if (subCommand.equals("get current")) {
			CyNetwork current = Cytoscape.getCurrentNetwork();
			result.addMessage("network: current network is "+current.getIdentifier()+": "+current.getTitle());
			result.addResult("currentnetwork", current);

		// Make the designated network current
		} else if (subCommand.equals("make current")) {
			String netName = getArg(subCommand, "name", args);
			if (netName == null)
				throw new CyCommandException("network: need a network name to make current");

			// Get the network
			CyNetwork net = Cytoscape.getNetwork(netName);
			if (net == null)
				throw new CyCommandException("network: network '"+netName+"' doesn't exist");

			Cytoscape.setCurrentNetwork(netName);
			result.addMessage("network: set current network to "+netName);

		// Return a list of all networks
		} else if (subCommand.equals("list")) {
			Set<CyNetwork>networkList = Cytoscape.getNetworkSet();
			result.addMessage("network: network list:");
			result.addResult("networks",networkList);
			for (CyNetwork net: networkList) {
				result.addMessage("  "+net.getIdentifier()+": "+net.getTitle());
			}

		} else {
			throw new CyCommandException("network: unknown command "+subCommand);
		}

		return result;
	}
}
