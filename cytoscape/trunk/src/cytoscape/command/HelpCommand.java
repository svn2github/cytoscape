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
package cytoscape.command;

import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Command provides an interface to list commands, subcommands and
 * their settings
 */
class HelpCommand implements CyCommandHandler {
  public final static String HELP = "help";

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		if (command == null || command.length() == 0) {
			result.addMessage("Available commands: ");
			for ( String ns : CyCommandManager.getNamespaceList() )
				result.addMessage("  "+ns);
			
			result.addMessage("For detailed information type: ");
			result.addMessage(" help command");
		} else {
			// See if we have a command arg...
			if (args != null && args.size() > 0) {
				// We do, get the command we want details on
				for (String subc: args.keySet()) {
					CyCommandHandler ch = CyCommandManager.getCommand(command, subc);
					String desc = ch.getDescription(subc);
					if (desc == null)
						result.addMessage("Detailed information on '" + command + " " + subc+"' is not available  ");
					else
						result.addMessage(desc);
					break;
				}
			} else {
				result.addMessage("Available " + command + " commands: ");
				for ( String c : CyCommandManager.getCommandList(command) )
					result.addMessage("  "+ command + " " + c);
			}
		}

		return result;
	}

	public CyCommandResult execute(String command, Collection<Tunable> arguments) throws CyCommandException {
		return execute( command, new HashMap<String,Object>() );
	}

	public List<String> getCommands() {
		return CyCommandManager.getNamespaceList();
	}

	public List<String> getArguments(String command) { return new ArrayList<String>(); }
	public Map<String, Object> getSettings(String command) { return new HashMap<String,Object>(); }
	public Map<String, Tunable> getTunables(String command) { return new HashMap<String,Tunable>(); }

	public String getDescription(String command) {
		return "The 'help' command returns information about available commands.  For detailed information, type: 'help command'";
	}
}
