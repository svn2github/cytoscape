/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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
package bioCycPlugin.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;

enum Command {
  LISTDATABASES("list databases", 
	                "List all of the available databases", ""),
  LISTPATHWAYS("list pathways", 
	            "List all of the pathways that meet the criteria",
	            "database=ecoli|protein|gene"),
  LISTREACTIONS("list reactions", 
	            "List all of the reactions that meet the criteria",
	            "database=ecoli|protein|gene"),
	LOADPATHWAY("load pathway",
	            "Load a pathway in biopax format",
	            "database=ecoli|pathway");

  private String command = null;
  private String argList = null;
  private String desc = null;

  Command(String command, String description, String argList) {
    this.command = command;
    this.argList = argList;
    this.desc = description;
  }

  public String getCommand() { return command; }
  public String getArgString() { return argList; }
  public String getDescription() { return desc; }
  public boolean equals(String com) { return command.equals(com); }
}


/**
 * 
 */
public class BioCycCommandHandler extends AbstractCommandHandler {
	CyLogger logger;

	public BioCycCommandHandler(String namespace, CyLogger logger) {
		super(CyCommandManager.reserveNamespace(namespace));

		this.logger = logger;

		for (Command command: Command.values()) {
			addCommand(command.getCommand(), command.getDescription(), command.getArgString());
		}
	}

  public CyCommandResult execute(String command, Collection<Tunable>args)
                                                      throws CyCommandException, RuntimeException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args)
                                                      throws CyCommandException, RuntimeException {
		CyCommandResult result = new CyCommandResult();

  	// LISTDATABASES("list databases", 
	 	//               "List all of the available databases", ""),
	 	if (Command.LISTDATABSES.equals(command)) {
			

  	// LISTPATHWAYS("list pathways", 
	 	//            "List all of the pathways that meet the criteria",
	 	//            "database=ecoli|protein|gene"),
	 	} else if (Command.LISTPATHWAYS.equals(command)) {


  	// LISTREACTIONS("list reactions", 
	 	//            "List all of the reactions that meet the criteria",
	 	//            "database=ecoli|protein|gene"),
	 	} else if (Command.LISTREACTIONS.equals(command)) {


		// LOADPATHWAY("load pathway",
	 	//            "Load a pathway in biopax format",
	 	//            "database=ecoli|pathway");
	 	} else if (Command.LOADPATHWAY.equals(command)) {
		}

		return result;
	}

	private boolean getBooleanArg(String command, String arg, Map<String, Object>args) {
		String com = getArg(command, arg, args);
		if (com == null || com.length() == 0) return false;
		boolean b = false;
		b = Boolean.parseBoolean(com);
		// throw new CyCommandException(arg+" must be 'true' or 'false'");
		return b;
	}

	private void addCommand(String command, String description, String argString) {
		// Add the description first
		addDescription(command, description);

		if (argString == null) {
			addArgument(command);
			return;
		}

		// Split up the options
		String[] options = argString.split("\\|");
		for (int opt = 0; opt < options.length; opt++) {
			String[] args = options[opt].split("=");
			if (args.length == 1)
				addArgument(command, args[0]);
			else
				addArgument(command, args[0], args[1]);
		}
	}

}
