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
package structureViz.commands;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;

import structureViz.actions.Chimera;
import structureViz.model.ChimeraModel;
import structureViz.model.Structure;

enum Command {
  ALIGNSTRUCTURES("alignstructures", 
	                "Perform sequence-driven structural superposition on a group of structures", 
	                "reference|structureList=selected"),
	CLOSE("close", "Close some or all of the currently opened structures","structurelist=selected"),
	COLOR("color", "Color part of all of a structure",
	               "chain|residues|labels|ribbons|surfaces|structurelist=selected"),
	DEPICT("depict", "Change the depiction of a structure",
	                 "preset=Interactive1|style=stick|ribbonstyle=round|surfacestyle=solid|transparency=0|structurelist=selected"),
	EXIT("exit", "Exit Chimera",""),
	FINDCLASHES("find clashes", "Find clashes between two models or parts of models","structurelist=selected"),
	FINDHBONDS("find hbonds", "Find hydrogen bonds between two models or parts of models","structurelist=selected"),
	FOCUS("focus", "Focus on a structure or part of a structure","structurelist=selected"),
	HIDE("hide", "Hide parts of a structure", "structurelist=selected"),
	LISTCHAINS("list chains", "List the chains in a structure", "structurelist=selected"), 
	LISTRES("list residues", "List the residues in a structure", "structurelist=selected|chain=all"),
	LISTSTRUCTURES("list structures", "List all of the open structures",""),
	OPENSTRUCTURE("open structure", "Open a new structure in Chimera","pdbid|modbaseid|nodeList"),
	SELECT("select", "Select a structure or parts of a structure", "structurelist"),
	SEND("send", "Send a command to chimera", "command"),
	SHOW("show", "Show parts of a structure", "structurelist");

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
public class StructureVizCommandHandler extends AbstractCommandHandler {
	CyLogger logger;
	Chimera chimera = null;

	public StructureVizCommandHandler(String namespace, CyLogger logger) {
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

		// Launch Chimera
		Chimera chimera = Chimera.GetChimeraInstance(Cytoscape.getCurrentNetworkView(), logger);
		if (!chimera.isLaunched() && !launchChimera(result, chimera))
			return result; // Oops!  Didn't launch


		// Main command cascade
		if (Command.ALIGNSTRUCTURES.equals(command)) {
		} else if (Command.CLOSE.equals(command)) {
			//
			// CLOSE("close", "Close some or all of the currently opened structures","structurelist=selected"),
			//
		} else if (Command.COLOR.equals(command)) {
		} else if (Command.DEPICT.equals(command)) {
		} else if (Command.EXIT.equals(command)) {
			//
			// EXIT("exit", "Exit Chimera",""),
			//
			chimera.exit();
		} else if (Command.FINDCLASHES.equals(command)) {
		} else if (Command.FINDHBONDS.equals(command)) {
		} else if (Command.FOCUS.equals(command)) {
		} else if (Command.HIDE.equals(command)) {
		} else if (Command.LISTCHAINS.equals(command)) {
		} else if (Command.LISTRES.equals(command)) {
		} else if (Command.LISTSTRUCTURES.equals(command)) {
			//
			// LISTSTRUCTURES("list structures", "List all of the open structures",""),
			//
			List<ChimeraModel> models = chimera.getChimeraModels();
			System.out.println("Models = "+models);
			result.addResult("modelList", models);
			for (ChimeraModel model: models) {
				result.addMessage(model.toString());
			}
		} else if (Command.OPENSTRUCTURE.equals(command)) {
			//
			// OPENSTRUCTURE("open structure", "Open a new structure in Chimera","pdbid|modbaseid|nodeList"),
			//
			String pdb = getArg(command, "pdbid", args);
			String modbaseid = getArg(command, "modbaseid", args);
			String smiles = getArg(command, "smiles", args);
			String nodelist = getArg(command, "nodeList", args);

			if (nodelist != null) {
			} else
				return StructureCommands.openCommand(chimera, result, pdb, modbaseid, smiles);

		} else if (Command.SELECT.equals(command)) {
		} else if (Command.SEND.equals(command)) {
			//
			// SEND("send", "Send a command to chimera", "command"),
			//
			String com = getArg(command, "command", args);
			if (com == null)
				throw new CyCommandException("send command requires a command argument");
			for (String reply: chimera.commandReply(com)) {
				result.addMessage(reply);
			}
		} else if (Command.SHOW.equals(command)) {
		}

		return result;
	}

	/**
 	 * Return the list of structures corresponding to the passed arguments
 	 */
	private List<Structure> getStructureList(String command, Map<String,Object>args) {
		return null;
	}

	private void addCommand(String command, String description, String argString) {
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

	private boolean launchChimera(CyCommandResult result, Chimera chimera) {
		boolean launched = false;
		String message = "Unable to launch UCSF Chimera";
		try {
			launched = chimera.launch();
		} catch (IOException e) {
			message += ": "+e.getMessage();
		}
		if (!launched) {
			result.addError(message);
		}
		return launched;
	}
}
