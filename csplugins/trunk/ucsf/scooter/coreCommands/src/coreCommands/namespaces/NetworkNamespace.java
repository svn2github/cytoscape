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
import cytoscape.Cytoscape;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;

import coreCommands.namespaces.network.CreateNetwork;
import coreCommands.namespaces.network.ExportNetwork;
import coreCommands.namespaces.network.ImportNetwork;

import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XXX FIXME XXX Description 
 */
public class NetworkNamespace extends AbstractCommand {
	static String NETWORK = "network";

	// Commands
	static String CREATE = "create";
	static String DESTROY = "destroy";
	static String EXPORT = "export";
	static String GETCURRENT = "get current";
	static String IMPORT = "import";
	static String LIST = "list";
	static String MAKECURRENT = "make current";

	// Arguments
	static String NAME = "name";
	static String CREATEVIEW = "createview";
	static String CURRENT = "current";
	static String PARENT = "parent";
	static String FILE = "file";
	static String TYPE = "type";

	protected NetworkNamespace(CyCommandNamespace ns) {
		this.namespace = ns;

		// Define our subcommands
		settingsMap = new HashMap();
		addSetting(DESTROY, NAME);
		addSetting(GETCURRENT);
		addSetting(LIST);
		addSetting(MAKECURRENT, NAME);
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getHandlerName() { return NETWORK; }

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		if (command.equals(DESTROY)) {
			String netName = getArg(command, NAME, args);
			if (netName == null)
				throw new CyCommandException("network: need the name of the network to destroy");

			CyNetwork net = Cytoscape.getCurrentNetwork();
			if (!netName.equalsIgnoreCase(CURRENT))
				net = Cytoscape.getNetwork(netName);

			if (net == null)
				throw new CyCommandException("network: the network '"+netName+"' doesn't exist");

			Cytoscape.destroyNetwork(net);

		// Return the current network
		} else if (command.equals(GETCURRENT)) {
			CyNetwork current = Cytoscape.getCurrentNetwork();
			result.addMessage("network: current network is "+current.getIdentifier()+": "+current.getTitle());
			result.addResult("currentnetwork", current);

		// Make the designated network current
		} else if (command.equals(MAKECURRENT)) {
			String netName = getArg(command, NAME, args);
			if (netName == null)
				throw new CyCommandException("network: need a network name to make current");

			// Get the network
			CyNetwork net = Cytoscape.getNetwork(netName);
			if (net == null)
				throw new CyCommandException("network: network '"+netName+"' doesn't exist");

			Cytoscape.setCurrentNetwork(netName);
			result.addMessage("network: set current network to "+netName);

		// Return a list of all networks
		} else if (command.equals(LIST)) {
			Set<CyNetwork>networkList = Cytoscape.getNetworkSet();
			result.addMessage("network: network list:");
			result.addResult("networks",networkList);
			for (CyNetwork net: networkList) {
				result.addMessage("  "+net.getIdentifier()+": "+net.getTitle());
			}

		} else {
			throw new CyCommandException("network: unknown command "+command);
		}

		return result;
	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		CyCommandNamespace ns = CyCommandManager.reserveNamespace(namespace);

		// Handle the simple commands ourselves
		CyCommandHandler net = new NetworkNamespace(ns);

		// Now register the more complicated commands
		new CreateNetwork(ns);
		new ExportNetwork(ns);
		new ImportNetwork(ns);

		return net;
	}
}
