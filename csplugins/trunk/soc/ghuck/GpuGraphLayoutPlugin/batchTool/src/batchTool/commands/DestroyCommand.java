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
package batchTool.commands;

import java.util.List;
import java.util.HashMap;

import cytoscape.data.writers.CytoscapeSessionWriter;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.logger.CyLogger;

import batchTool.commands.ParseException;

/**
 * The open command opens a Cytoscape session file
 */
public class DestroyCommand extends AbstractCommand {
	String networkName = null;

	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String commandName() { return "destroy"; }

	/**
	 * parse is the main parse routine.  It is handed the entire command
	 * along with all of its arguments.  If the command is successfully
	 * parsed, the number of arguments actually read is returned.
	 *
	 * @param args the arguments to the command. 
	 */
	public int parse(List<String> args, HashMap<String,String>optMap) throws ParseException {
		networkName = args.get(1);

		return args.size();
	}

	/**
	 * read the session
	 *
	 * @param substitutions reserved for future use
	 */
	public int execute(String[] substitutions) throws Exception {
		// Do the appropriate substitutions (if any)
		CyLogger.getLogger(DestroyCommand.class).debug("DestroyCommand: executing");

		try {
			CyNetwork network = Cytoscape.getNetwork(networkName);
			Cytoscape.destroyNetworkView(network);
			Cytoscape.destroyNetwork(network);
		} catch (Exception e) {
			throw new ParseException("Unable to destroy network "+networkName+": "+
			                         e.getMessage());
		}
		return -1;
	}

}


