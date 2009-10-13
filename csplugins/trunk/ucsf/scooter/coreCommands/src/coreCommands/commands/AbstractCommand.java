/* vim: set ts=2: */
/**
 * Copyright (c) 2007 The Regents of the University of California.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommand;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;

/**
 * The layout command handles all requests to layout the current network.
 * For efficiency reasons, this should be done assuming we're in headless
 * mode.
 */
public abstract class AbstractCommand implements CyCommand {
	protected Map<String, List<Tunable>> settingsMap = null;

	/**
	 * Override if this command supports subcommands
	 */
	public List<String> getSubCommands() { return new ArrayList(settingsMap.keySet()); }

	/**
	 * Override to return the arguments supported for a specific sub-command
	 */
	public List<String> getArguments(String subCommand) { 
		if (!settingsMap.containsKey(subCommand)) {
			return null;
		}

		List<String> argList = new ArrayList();
		for (Tunable t: settingsMap.get(subCommand)) {
			argList.add(t.getName());
		}
		return argList;
	}

	/**
	 * Override to return the current values for a specific sub-command
	 */
	public Map<String, String> getSettings(String subCommand) { 
		Map<String, String> kvSettings = createKVSettings(subCommand);
		if (kvSettings != null)
			return kvSettings;
		return null;
	}

	/**
	 * Override to return the Tunables supported for a specific sub-command
	 */
	public Map<String, Tunable> getTunables(String subCommand) { 
		if (settingsMap.containsKey(subCommand)) {
			Map<String, Tunable> tunableMap = new HashMap();
			for (Tunable t: settingsMap.get(subCommand)) {
				tunableMap.put(t.getName(), t);
			}
			return tunableMap;
		}
		return null;
	}

	/**
	 * Override if the sub-commands support Tunables directly (recommended)
	 */
	public CyCommandResult execute(String subCommand, Collection<Tunable>args) throws CyCommandException { 
		return execute(subCommand, createKVMap(args));
	}

	protected void addSetting(String subCommand) {
		if (settingsMap == null)
			settingsMap = new HashMap();
		if (!settingsMap.containsKey(subCommand)) {
			settingsMap.put(subCommand, new ArrayList());
		}
	}

	protected void addSetting(String subCommand, String vKey) {
		addSetting(subCommand, vKey, null);
	}

	protected void addSetting(String subCommand, String vKey, String value) {
		Tunable t = new Tunable(vKey, vKey, Tunable.STRING, value);
		addSetting(subCommand, t);
	}

	protected void addSetting(String subCommand, Tunable t) {
		if (settingsMap == null)
			settingsMap = new HashMap();
		
		if (!settingsMap.containsKey(subCommand)) {
			settingsMap.put(subCommand, new ArrayList());
		}

		List<Tunable> tList = settingsMap.get(subCommand);
		tList.add(t);
	}

	/**
	 * This method is useful for converting from Tunable lists to key-value settings
	 */
	protected	Map<String, String> createKVSettings(String subCommand) {
		if (!settingsMap.containsKey(subCommand)) return null;
		return createKVMap(settingsMap.get(subCommand));
	}

	protected Map<String, String> createKVMap(Collection<Tunable> tList) {
		Map<String, String> kvSettings = new HashMap();
		for (Tunable t: tList) {
			Object v = t.getValue();
			if (v != null)
				kvSettings.put(t.getName(), v.toString());
			else
				kvSettings.put(t.getName(), null);
		}
		return kvSettings;
	}

	/**
 	 * Some additional utility routines
 	 */

	protected String getArg(String subCommand, String key, Map<String,String>args) {
		// Do we have the key in our settings map?
		String value = null;

		if (settingsMap.containsKey(subCommand)) {
			List<Tunable> tL = settingsMap.get(subCommand);
			for (Tunable t: tL) {
				if (t.getName().equals(key)) {
					Object v = t.getValue();
					if (v != null)
						value = v.toString();
					break;
				}
			}
		}

		if (args == null || args.size() == 0 || !args.containsKey(key))
			return value;

		return args.get(key);
	}

	protected static List<CyNode> getNodeList(CyNetwork net, CyCommandResult result, 
	                                          Map<String, String> args) {
		if (args == null || args.size() == 0)
			return null;

		List<CyNode> retList = new ArrayList();
		if (args.containsKey("nodelist")) {
			String[] nodes = args.get("nodelist").split(",");
			for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
				addNode(net, nodes[nodeIndex], retList, result);
			}
		} else if (args.containsKey("node")) {
			String nodeName = args.get("node");
			addNode(net, nodeName, retList, result);
		} else {
			return null;
		}
		return retList;
	}

	protected static void addNode(CyNetwork net, String nodeName, List<CyNode> list, CyCommandResult result) {
		CyNode node = Cytoscape.getCyNode(nodeName, false);
		if (node == null) 
			result.addError("node: can't find node "+nodeName);
		else
			list.add(node);
		return;
	}

}
