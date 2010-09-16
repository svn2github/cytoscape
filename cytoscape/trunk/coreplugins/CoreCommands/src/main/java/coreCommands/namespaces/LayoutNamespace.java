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

import cytoscape.Cytoscape;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XXX FIXME XXX Description 
 */
public class LayoutNamespace extends AbstractCommandHandler {
	private static String LAYOUT = "layout";
	private static String GETCURRENT = "get current";
	private static String GETDEFAULT = "get default";
	private static String DEFAULT = "default";

	protected LayoutNamespace(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addDescription(GETCURRENT, "Return the name of the last layout performed on this network");
		addArgument(GETCURRENT);

		addDescription(GETCURRENT, "Return the name of the default layout");
		addArgument(GETDEFAULT);

		addDescription(DEFAULT, "Layout the current network with the default layout");
		addArgument(DEFAULT);

		// Get the list of layouts from the layout manager
		for (CyLayoutAlgorithm alg: CyLayouts.getAllLayouts()) {
			String layout = alg.getName();
			addDescription(layout, alg.toString());
			LayoutProperties props = alg.getSettings();
			if (props == null) {
				addArgument(layout);
				continue;
			}
			for (Tunable t: props.getTunables())
				addArgument(layout, t);
		}
	}

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		List<Tunable> tL = new ArrayList();
		if (args == null || args.size() == 0) {
			return execute(command, tL);
		}

		if (!getCommands().contains(command.toLowerCase()))
			throw new CyCommandException("layout: unknown algorithm: "+command.toLowerCase());

		if (command.equalsIgnoreCase(GETCURRENT) || 
		    command.equalsIgnoreCase(GETDEFAULT) ||
		    command.equalsIgnoreCase(DEFAULT)) {
			throw new CyCommandException("layout: "+command+" doesn't take any arguments");
		}

		Map<String, Tunable> tMap = getTunables(command);

		for (String key: args.keySet()) {
			if (tMap.containsKey(key)) {
				Tunable t = tMap.get(key);
				t.setValue(args.get(key));
				tL.add(t);
			} else {
				result.addError("layout: algorithm '"+command+"' doesn't have a '"+key+"' parameter");
			}	
		}
		if (result.successful())
			return execute(command, tL);

		return result;
	}

	public CyCommandResult execute(String command, List<Tunable> args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		if (command.equalsIgnoreCase(GETCURRENT)) {
			CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
			String networkID = Cytoscape.getCurrentNetwork().getIdentifier();

			if (networkAttributes.hasAttribute(networkID, "__layoutAlgorithm")) {
				String alg = (String)networkAttributes.getAttribute(networkID, "__layoutAlgorithm");
				result.addMessage("layout: last layout of network '"+networkID+"' was with "+alg);
			} else {
				result.addMessage("layout: last layout information is unavailable for '"+networkID+"'");
			}
		} else if (command.equalsIgnoreCase("get default")) {
			CyLayoutAlgorithm alg = CyLayouts.getDefaultLayout();
			result.addMessage("layout: default algorithm is "+alg.getName());
			result.addResult("algorithm", alg);
			result.addResult("name", alg.getName());
		} else if (command.equalsIgnoreCase("default")) {
			CyLayoutAlgorithm alg = CyLayouts.getDefaultLayout();
			alg.doLayout();
			result.addMessage("layout: laid current network out using "+alg.getName()+"(default) algorithm");
		} else {
			CyLayoutAlgorithm alg = CyLayouts.getLayout(command);
			LayoutProperties props = alg.getSettings();
			
			for (Tunable t: args) {
				Tunable layoutTunable = props.get(t.getName());
				if (layoutTunable == null) {
					result.addError("layout: algorithm '"+command+"' doesn't have a '"+t.getName()+"' parameter");
					continue;
				}
				layoutTunable.setValue(t.getValue());
				alg.updateSettings();
			}
			if (result.successful()) {
				alg.doLayout();
				result.addMessage("layout: laid current network out using "+alg.getName()+" algorithm");
			}
		}

		return result;
	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		return new LayoutNamespace(CyCommandManager.reserveNamespace(namespace));
	}
}
