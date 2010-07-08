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

import structureViz.actions.Chimera;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;

enum Command {
  ALIGNSTRUCTURES("alignstructures", 
	                "Perform sequence-driven structural superposition on a group of structures", 
	                "reference|structureList|referencechain|chainlist"),
	CLEARCLASHES("clear clashes", "Clear clashes", ""),
	CLEARHBONDS("clear hbonds", "Clear hydrogen bonds", ""),
	CLOSE("close", "Close some or all of the currently opened structures","structurelist=selected"),
	COLOR("color", "Color part of all of a structure",
	               "preset|residues|labels|ribbons|surfaces|structurelist|atomspec"),
	DEPICT("depict", "Change the depiction of a structure",
	                 "preset|style|ribbonstyle|surfacestyle|transparency|structurelist|atomspec=selected"),
	EXIT("exit", "Exit Chimera",""),
	FINDCLASHES("find clashes", "Find clashes between two models or parts of models","structurelist|atomspec=selected|continuous"),
	FINDHBONDS("find hbonds", "Find hydrogen bonds between two models or parts of models","structurelist|atomspec=selected"),
	FOCUS("focus", "Focus on a structure or part of a structure","structurelist|atomspec"),
	HIDE("hide", "Hide parts of a structure", "structurelist|atomspec=selected"),
	LISTCHAINS("list chains", "List the chains in a structure", "structurelist=all"), 
	LISTRES("list residues", "List the residues in a structure", "structurelist=all|chain"),
	LISTSTRUCTURES("list structures", "List all of the open structures",""),
	MOVE("move", "Move (translate) a model","x|y|z|structurelist=selected"),
	OPENSTRUCTURE("open structure", "Open a new structure in Chimera","pdbid|modbaseid|nodeList"),
	RAINBOW("rainbow", "Color part of all of a structure in a rainbow scheme",
	                   "structurelist|atomspec"),
	ROTATE("rotate", "Rotate a model","x|y|z|center|structurelist=selected"),
	SELECT("select", "Select a structure or parts of a structure", "structurelist|atomspec"),
	SEND("send", "Send a command to chimera", "command"),
	SHOW("show", "Show parts of a structure", "structurelist|atomspec");

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

	public static final String ATOMSPEC = "atomspec";
	public static final String CHAIN = "chain";
	public static final String CONTINUOUS = "continuous";
	public static final String LABELS = "labels";
	public static final String MODELLIST = "modelList";
	public static final String NODELIST = "nodeList";
	public static final String PRESET = "preset";
	public static final String RESIDUES = "residues";
	public static final String RIBBONS = "ribbons";
	public static final String RIBBONSTYLE = "ribbonstyle";
	public static final String SELECTED = "selected";
	public static final String STRUCTURELIST = "structureList";
	public static final String STYLE = "style";
	public static final String SURFACES = "surfaces";
	public static final String SURFACESTYLE = "surfacestyle";
	public static final String TRANSPARENCY = "transparency";

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
		this.chimera = Chimera.GetChimeraInstance(Cytoscape.getCurrentNetworkView(), logger);
		if (!chimera.isLaunched() && !launchChimera(result, chimera))
			return result; // Oops!  Didn't launch

		String structureSpec = getArg(command, STRUCTURELIST, args);
		List<Structure> structureList = CommandUtils.getStructureList(structureSpec, chimera);
		// System.out.println("Structurelist = "+structureList);

		// Main command cascade
		if (Command.ALIGNSTRUCTURES.equals(command)) {

		//
		// CLEARCLASHES("clear clashes", "Clear clashes"),
		//
		} else if (Command.CLEARCLASHES.equals(command)) {
				result = AnalysisCommands.clearClashes(chimera, result);

		//
		// CLEARHBONDS("clear hbonds", "Clear hydrogen bonds"),
		//
		} else if (Command.CLEARHBONDS.equals(command)) {
				result = AnalysisCommands.clearHBonds(chimera, result);

		//
		// CLOSE("close", "Close some or all of the currently opened structures","structurelist=selected"),
		//
		} else if (Command.CLOSE.equals(command)) {
			return StructureCommands.closeCommand(chimera, result, structureList);

		//
		// COLOR("color", "Color part of all of a structure",
	  //                "residues|labels|ribbons|surfaces|structurelist|atomspec"),
		//
		} else if (Command.COLOR.equals(command)) {

			// See if we have a preset given.  If so, it overrides everything else
			String preset = getArg(command,PRESET,args);
			if (preset != null)
				return DisplayCommands.preset(chimera, result, preset);

			String residues = getArg(command, RESIDUES, args);
			String labels = getArg(command, LABELS, args);
			String ribbons = getArg(command, RIBBONS, args);
			String surfaces = getArg(command, SURFACES, args);
			if (structureList != null) {
				result = DisplayCommands.colorStructure(chimera, result, structureList, residues, labels, ribbons, surfaces);
			} else {
				String atomSpec = getArg(command,ATOMSPEC, args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec,chimera);
				result = DisplayCommands.colorSpecList(chimera, result, specList, residues, labels, ribbons, surfaces);
			}

		//
		// DEPICT("depict", "Change the depiction of a structure",
	  //        "preset|style|ribbonstyle|surfacestyle|transparency|structurelist|atomspec=selected"),
		//
		} else if (Command.DEPICT.equals(command)) {
			String preset = getArg(command,PRESET,args);
			if (preset != null)
				return DisplayCommands.preset(chimera, result, preset);

			String style = getArg(command,STYLE,args);
			String surfacestyle = getArg(command,SURFACESTYLE,args);
			String ribbonstyle = getArg(command,RIBBONSTYLE,args);
			String trans = getArg(command,TRANSPARENCY,args);
			if (structureList != null) {
				result = DisplayCommands.depictStructure(chimera, result, structureList, style, 
				                                         ribbonstyle, surfacestyle, trans);
			} else {
				String atomSpec = getArg(command,ATOMSPEC,args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec,chimera);
				result = DisplayCommands.depictSpecList(chimera, result, specList, style,
				                                        ribbonstyle, surfacestyle, trans);
			}

		//
		// EXIT("exit", "Exit Chimera",""),
		//
		} else if (Command.EXIT.equals(command)) {
			chimera.exit();

		//
		// FINDCLASHES("find clashes", "Find clashes between two models or parts of models","structurelist|atomspec=selected|continuous"),
		//
		} else if (Command.FINDCLASHES.equals(command)) {
			String continuous = getArg(command, CONTINUOUS, args);
			if (structureList != null) {
				result = AnalysisCommands.findClashesStructure(chimera, result, structureList, continuous);
			} else  {
				String atomSpec = getArg(command,ATOMSPEC, args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec,chimera);
				result = AnalysisCommands.findClashesSpecList(chimera, result, specList, continuous);
			}
		//
		// FINDHBONDS("find hbonds", "Find hydrogen bonds between two models or parts of models","structurelist|atomspec=selected"),
		//
		} else if (Command.FINDHBONDS.equals(command)) {
			String continuous = getArg(command, CONTINUOUS, args);
			if (structureList != null) {
				result = AnalysisCommands.findHBondsStructure(chimera, result, structureList);
			} else  {
				String atomSpec = getArg(command,ATOMSPEC, args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec,chimera);
				result = AnalysisCommands.findHBondsSpecList(chimera, result, specList);
			}

		//
		// FOCUS("focus", "Focus on a structure or part of a structure","structurelist|atomspec=selected"),
		//
		} else if (Command.FOCUS.equals(command)) {
			if (structureList != null) {
				result = DisplayCommands.focusStructure(chimera, result, structureList);
			} else {
				String atomSpec = getArg(command,ATOMSPEC, args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec, chimera);
				result = DisplayCommands.focusSpecList(chimera, result, specList);
			}

		//
		// HIDE("hide", "Hide parts of a structure", "structurelist|atomspec=selected"),
		//
		} else if (Command.HIDE.equals(command)) {
			if (structureList != null) {
				result = DisplayCommands.displayStructure(chimera, result, structureList, true);
			} else {
				String atomSpec = getArg(command,ATOMSPEC, args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec,chimera);
				result = DisplayCommands.displaySpecList(chimera, result, specList, true);
			}

		//
		// LISTCHAINS("list chains", "List the chains in a structure", "structurelist=all"), 
		//
		} else if (Command.LISTCHAINS.equals(command)) {
			return StructureCommands.listChains(chimera, result, structureList);

		//
		// LISTRES("list residues", "List the residues in a structure", "structurelist=all|chain=all"),
		//
		} else if (Command.LISTRES.equals(command)) {
			String chains = getArg(command, "chain", args);
			return StructureCommands.listResidues(chimera, result, structureList, chains);

		//
		// LISTSTRUCTURES("list structures", "List all of the open structures",""),
		//
		} else if (Command.LISTSTRUCTURES.equals(command)) {
			List<ChimeraModel> models = chimera.getChimeraModels();
			result.addResult("modelList", models);
			for (ChimeraModel model: models) {
				result.addMessage(model.toString());
			}

		//
		// OPENSTRUCTURE("open structure", "Open a new structure in Chimera","pdbid|modbaseid|nodeList"),
		//
		} else if (Command.OPENSTRUCTURE.equals(command)) {
			String pdb = getArg(command, "pdbid", args);
			String modbaseid = getArg(command, "modbaseid", args);
			String smiles = getArg(command, "smiles", args);
			String nodelist = getArg(command, "nodeList", args);
			if (pdb == null && modbaseid == null && smiles == null && nodelist == null)
				throw new CyCommandException("One of nodeList, pdbid, modbaseid, or smiles must be specified");

			if (nodelist != null) {
				return StructureCommands.openCommand(chimera, result, nodelist);
			} else
				return StructureCommands.openCommand(chimera, result, pdb, modbaseid, smiles);

		//
		// MOVE("move", "Move (translate) a model","x|y|z|structurelist=selected"),
		//
		} else if (Command.MOVE.equals(command)) {
			String x = getArg(command, "x", args);
			String y = getArg(command, "y", args);
			String z = getArg(command, "z", args);

			if (x == null && y == null && z == null)
				throw new CyCommandException("One of x, y, or z must be specified");

			result = DisplayCommands.moveStructure(chimera, result, x, y, z, structureList);
		//
		// RAINBOW("rainbow", "Color part of all of a structure in a rainbow scheme",
    //                    "structurelist|atomspec"),
		//
		} else if (Command.RAINBOW.equals(command)) {
			if (structureList != null) {
				result = DisplayCommands.rainbowStructure(chimera, result, structureList);
			} else {
				String atomSpec = getArg(command,ATOMSPEC, args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec,chimera);
				result = DisplayCommands.rainbowSpecList(chimera, result, specList);
			}

		//
		// ROTATE("rotate", "Rotate a model","x|y|z|center|structurelist=selected"),
		//
		} else if (Command.ROTATE.equals(command)) {
			String x = getArg(command, "x", args);
			String y = getArg(command, "y", args);
			String z = getArg(command, "z", args);
			String center = getArg(command, "center", args);

			if (x == null && y == null && z == null)
				throw new CyCommandException("One of x, y, or z must be specified");
			
			result = DisplayCommands.rotateStructure(chimera, result, x, y, z, center, structureList);

		//
		// SELECT("select", "Select a structure or parts of a structure", "structurelist|atomspec"),
		//
		} else if (Command.SELECT.equals(command)) {
			if (structureList != null) {
				result = DisplayCommands.selectStructure(chimera, result, structureList);
			} else {
				String atomSpec = getArg(command,ATOMSPEC, args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec,chimera);
				result = DisplayCommands.selectSpecList(chimera, result, specList);
			}

		//
		// SEND("send", "Send a command to chimera", "command"),
		//
		} else if (Command.SEND.equals(command)) {
			String com = getArg(command, "command", args);
			if (com == null)
				throw new CyCommandException("send command requires a command argument");
			for (String reply: chimera.commandReply(com)) {
				result.addMessage(reply);
			}

		//
		// SHOW("show", "Show parts of a structure", "structurelist|atomspec");
		//
		} else if (Command.SHOW.equals(command)) {
			if (structureList != null) {
				result = DisplayCommands.displayStructure(chimera, result, structureList, false);
			} else {
				String atomSpec = getArg(command,ATOMSPEC, args);
				List<ChimeraStructuralObject> specList = CommandUtils.getSpecList(atomSpec,chimera);
				result = DisplayCommands.displaySpecList(chimera, result, specList, false);
			}
		}

		return result;
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
