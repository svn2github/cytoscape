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
import cytoscape.command.AbstractCommand;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.data.writers.CytoscapeSessionWriter;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XXX FIXME XXX Description 
 */
public class SessionNamespace extends AbstractCommand {
	static String SESSION = "session";

	static String OPEN = "open";
	static String NEW = "new";
	static String SAVE = "save";

	static String FILE = "file";

	public SessionNamespace(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addArgument(OPEN, FILE);
		addArgument(NEW);
		addArgument(SAVE, FILE);
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getHandlerName() { return SESSION; }

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		if (command.equals(OPEN)) {
			// Load a session from a file
			String fileName = null;
			if (args.containsKey(FILE))
				fileName = args.get(FILE).toString();
			else
				throw new CyCommandException("session: need file argument to open a session");

			try {
				CytoscapeSessionReader reader = new CytoscapeSessionReader(fileName);
				reader.read();
			} catch (Exception e) {
				throw new CyCommandException("session: unable to open session file "+
				                             fileName+": "+e.getMessage());
			}
	
			result.addMessage("session: opened session: "+fileName);
			
			// TODO: figure some things out about the session
			// TODO: Get the number of networks
			// TODO: Get the current network

		} else if (command.equals(NEW)) {
			// Create a new session
			Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
			Cytoscape.createNewSession();
			Cytoscape.getDesktop().setTitle("Cytoscape Desktop (New Session)");
			Cytoscape.getDesktop().getNetworkPanel().repaint();
			Cytoscape.getDesktop().repaint();
			Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
			Cytoscape.getPropertyChangeSupport().firePropertyChange(Cytoscape.CYTOSCAPE_INITIALIZED, null, null);
			result.addMessage("session: created new session");

		} else if (command.equals(SAVE)) {
			// Save a session.  If no file argument is given, save the current session
			String fileName = null;
			if (args.containsKey(FILE))
				fileName = args.get(FILE).toString();
			else
				fileName = Cytoscape.getCurrentSessionFileName();

			if (!fileName.endsWith(".cys"))
				fileName = fileName + ".cys";
				
			try {
				CytoscapeSessionWriter writer = new CytoscapeSessionWriter(fileName);
				writer.writeSessionToDisk();
			} catch (Exception e) {
				throw new CyCommandException("session: unable to save session file "+
				                             fileName+": "+e.getMessage());
			}
			result.addMessage("session: saved session to file "+fileName);
		}
		return result;
	}

	public static CyCommandHandler register(String namespace) throws RuntimeException {
		// Get the namespace
		return new SessionNamespace(CyCommandManager.reserveNamespace(namespace));
	}
}
