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
import cytoscape.CytoscapeInit;
import cytoscape.command.AbstractCommand;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * XXX FIXME XXX Description 
 */
public class PropertyNamespace extends AbstractCommand {
	static String PROPERTY = "property";

	static String SET = "set";
	static String GET = "get";
	static String CLEAR = "clear";

	static String NAME = "name";
	static String VALUE = "value";

	public PropertyNamespace(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addArgument(SET, NAME);
		addArgument(SET, VALUE);
		addArgument(GET, NAME);
		addArgument(CLEAR, NAME);
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getHandlerName() { return PROPERTY; }

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();
		Properties props = CytoscapeInit.getProperties();

		if (args == null || args.size() == 0 || !args.containsKey(NAME))
			throw new CyCommandException("property: no property name to "+command);

		String propertyName = args.get(NAME).toString();
		if (SET.equals(command)) {
			if (!args.containsKey(VALUE))
				throw new CyCommandException("property: no 'value' to set "+propertyName+" to");
			props.setProperty(propertyName, args.get(VALUE).toString());
			result.addMessage("property: set "+propertyName+" to "+args.get(VALUE).toString());

		} else if (GET.equals(command)) {
			String value = props.getProperty(propertyName);
			result.addMessage("property: "+propertyName+" = "+value);
			result.addResult(propertyName, value);

		} else if (CLEAR.equals(command)) {
			props.remove(propertyName);
			result.addMessage("property: cleared "+propertyName);
		}
		return result;
	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		return new PropertyNamespace(CyCommandManager.reserveNamespace(namespace));
	}
}
