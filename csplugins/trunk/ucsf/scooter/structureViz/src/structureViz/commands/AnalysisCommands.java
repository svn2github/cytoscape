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

import java.util.ArrayList;
import java.util.List;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandResult;

import structureViz.actions.AnalysisActions;
import structureViz.actions.Chimera;
import structureViz.actions.DisplayActions;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;

/**
 * 
 */
public class AnalysisCommands extends AbstractCommands {

	static public CyCommandResult findClashesStructure(Chimera chimera, CyCommandResult result, 
	                                                 List<Structure>structureList, String continuous) {
		return findClashesSpecList(chimera, result, specListFromStructureList(chimera, structureList), continuous);
	}

	static public CyCommandResult findClashesSpecList(Chimera chimera, CyCommandResult result, 
	                                            List<ChimeraStructuralObject>specList, String continuous) { 
		boolean cont = false;

		if (continuous != null)
			cont = Boolean.parseBoolean(continuous);
			
		String atomSpec = "";
		for (ChimeraStructuralObject cso: specList) {
			if (atomSpec.length() == 0)
				atomSpec = cso.toSpec();
			else
				atomSpec += ","+cso.toSpec();
		}
		List<String> c = AnalysisActions.findClashAction(chimera, atomSpec, cont);
		return addReplies(result, c, "Finding clashes for "+atomSpec);
	}

	static public CyCommandResult clearClashes(Chimera chimera, CyCommandResult result) {
		List<String> c = AnalysisActions.clearClashAction(chimera);
		result = addReplies(result, c, "Cleared clashes");
		return result;
	}

	static public CyCommandResult findHBondsStructure(Chimera chimera, CyCommandResult result, 
	                                                 List<Structure>structureList) {
		return findHBondsSpecList(chimera, result, specListFromStructureList(chimera, structureList));
	}

	static public CyCommandResult findHBondsSpecList(Chimera chimera, CyCommandResult result, 
	                                            List<ChimeraStructuralObject>specList) { 
		String atomSpec = "";
		for (ChimeraStructuralObject cso: specList) {
			if (atomSpec.length() == 0)
				atomSpec = cso.toSpec();
			else
				atomSpec += ","+cso.toSpec();
		}
		List<String> c = AnalysisActions.findHBondAction(chimera, atomSpec);
		return addReplies(result, c, "Finding HBonds for "+atomSpec);
	}

	static public CyCommandResult clearHBonds(Chimera chimera, CyCommandResult result) {
		List<String> c = AnalysisActions.clearHBondAction(chimera);
		result = addReplies(result, c, "Cleared hydrogen bonds");
		return result;
	}

}
