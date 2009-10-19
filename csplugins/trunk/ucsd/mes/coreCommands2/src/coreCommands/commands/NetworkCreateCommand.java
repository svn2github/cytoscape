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
public class NetworkCreateCommand extends AbstractCommand {

	public NetworkCreateCommand() {
		super("network","create");

		addSetting("name", "NewNetwork");
		addSetting("createview", "true");
		addSetting("parent");
	}

	public CyCommandResult execute(Map<String, String>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

			String netName = getArg("name", args);
			if (netName == null)
				throw new CyCommandException("network: need a network name for the new network");

			// Handle create view
			boolean createView = true;
			{
				String cv = getArg("createview", args);
				if (cv != null && (cv.equalsIgnoreCase("false") || cv.equalsIgnoreCase("no")))
					createView = false;
			}

			// Handle parent network name
			CyNetwork parent = null;
			{
				String pName = getArg("parent", args);
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

		return result;
	}
}
