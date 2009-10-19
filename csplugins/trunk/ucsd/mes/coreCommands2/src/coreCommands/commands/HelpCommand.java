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

import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This Command provides an interface to list commands, subcommands and
 * their settings
 */
public class HelpCommand extends AbstractCommand {

	public HelpCommand() {
		// Define our subcommands
		settingsMap = new HashMap();
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getHandlerName() { return "help"; }

	public List<String> getCommands() {
		List<String> subList = getHandlerNameList();

		// Add the empty command
		subList.add("");

		return subList;
	}

	public CyCommandResult execute(String command, Map<String, String>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();

		if (command == null || command.length() == 0) {
			result.addMessage("Available commands: ");
			for (String comm: getHandlerNameList()) {
				result.addMessage("  "+comm);
			}
			result.addMessage("For more information about a command, type 'help command'");
		} else {
			CyCommandHandler handler = CyCommandManager.getHandler(command);
			if (handler == null)
				throw new CyCommandException("help: No such command: "+handler);

			result.addMessage("Commands for "+handler+":");
			for (String sub: handler.getCommands()) {
				List<String> argList = handler.getArguments(sub);
				if (argList == null || argList.size() == 0) {
					result.addMessage("  "+sub);
				} else {
					sub += " [";
					for (String subArg: argList) {
						sub += subArg+", ";
					}

					// Remove the training ','
					result.addMessage("  "+sub.substring(0, sub.length()-2)+"]");
				}
			}
		}
		return result;
	}

	private List<String> getHandlerNameList() {
		List<String> subList = new ArrayList();

		// Get the list of commands
		List<CyCommandHandler> comList = CyCommandManager.getHandlerList();
		for (CyCommandHandler c: comList)
			subList.add(c.getHandlerName());
		return subList;
	}
}
