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

import structureViz.actions.Align;
import structureViz.actions.AnalysisActions;
import structureViz.actions.Chimera;
import structureViz.actions.DisplayActions;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;

/**
 * The analysis commands for structureViz
 */
public class AnalysisCommands extends AbstractCommands {

	/**
	 * This is called to handle the "find clashes" command.
	 *
	 * @param chimera the Chimera object
	 * @param result the CyCommandResult
	 * @param structureList the structureList we're looking to find clashes between
	 * @param continuous the continuous flag
	 * @return the updated CyCommandResult
	 */
	static public CyCommandResult findClashesStructure(Chimera chimera, CyCommandResult result, 
	                                                 List<Structure>structureList, String continuous) {
		return findClashesSpecList(chimera, result, 
		                           specListFromStructureList(chimera, structureList), continuous);
	}

	/**
	 * This is called to handle the "find clashes" command.
	 *
	 * @param chimera the Chimera object
	 * @param result the CyCommandResult
	 * @param specList the specList we're looking to find clashes between
	 * @param continuous the continuous flag
	 * @return the updated CyCommandResult
	 */
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

	/**
	 * This is called to handle the "clear clashes" command.
	 *
	 * @param chimera the Chimera object
	 * @param result the CyCommandResult
	 * @return the updated CyCommandResult
	 */
	static public CyCommandResult clearClashes(Chimera chimera, CyCommandResult result) {
		List<String> c = AnalysisActions.clearClashAction(chimera);
		result = addReplies(result, c, "Cleared clashes");
		return result;
	}

	/**
	 * This is called to handle the "find hbonds" command.
	 *
	 * @param chimera the Chimera object
	 * @param result the CyCommandResult
	 * @param structureList the structureList we're looking to find clashes between
	 * @param limit the limits on the detection
	 * @param intermodel whether to find H-bonds between models
	 * @param intramodel whether to find H-bonds within models
	 * @return the updated CyCommandResult
	 */
	static public CyCommandResult findHBondsStructure(Chimera chimera, CyCommandResult result, 
	                                                  List<Structure>structureList, String limit,
	                                                  boolean intermodel, boolean intramodel)
	                                                  throws CyCommandException {
		return findHBondsSpecList(chimera, result, specListFromStructureList(chimera, structureList),
		                          limit, intermodel, intramodel);
	}

	/**
	 * This is called to handle the "find hbonds" command.
	 *
	 * @param chimera the Chimera object
	 * @param result the CyCommandResult
	 * @param specList the specList we're looking to find clashes between
	 * @param limit the limits on the detection
	 * @param intermodel whether to find H-bonds between models
	 * @param intramodel whether to find H-bonds within models
	 * @return the updated CyCommandResult
	 */
	static public CyCommandResult findHBondsSpecList(Chimera chimera, CyCommandResult result, 
	                                                 List<ChimeraStructuralObject>specList, String limit,
	                                                 boolean intermodel, boolean intramodel)
	                                                 throws CyCommandException {
		String atomSpec = "";
		for (ChimeraStructuralObject cso: specList) {
			if (atomSpec.length() == 0)
				atomSpec = cso.toSpec();
			else
				atomSpec += ","+cso.toSpec();
		}
		// Make sure we have a "legal" limit
		if (limit != null) {
			if (!limit.equals("cross") && !limit.equals("both") && !limit.equals("any"))
				throw new CyCommandException("limit argument must be one of 'cross' or 'both' or 'any'");
		}
		List<String> c = AnalysisActions.findHBondAction(chimera, atomSpec, limit, intermodel, intramodel);
		return addReplies(result, c, "Finding HBonds for "+atomSpec);
	}

	/**
	 * This is called to handle the "clear hbonds" command.
	 *
	 * @param chimera the Chimera object
	 * @param result the CyCommandResult
	 * @return the updated CyCommandResult
	 */
	static public CyCommandResult clearHBonds(Chimera chimera, CyCommandResult result) {
		List<String> c = AnalysisActions.clearHBondAction(chimera);
		result = addReplies(result, c, "Cleared hydrogen bonds");
		return result;
	}

	/**
	 * This is called to handle the "align structures" command.
	 *
	 * @param chimera the Chimera object
	 * @param result the CyCommandResult
	 * @param referenceStruct the reference structure
	 * @param structureList the list of structures to align to the reference
	 * @param showSequences show the resulting pairwise alignments
	 * @param createEdges create edges in Cytoscape corresponding to the alignments
	 * @param assignAttributes assign edge attributes with the alignment results
	 * @return the updated CyCommandResult
	 */
	static public CyCommandResult alignStructures(Chimera chimera, CyCommandResult result, 
	                                              Structure referenceStruct, 
	                                              List<Structure> structures,
			                                          boolean showSequences, boolean createEdges,
	                                              boolean assignAttributes) {
		try {
		// Do the alignment
		Align align = getAlign(chimera, showSequences, createEdges, assignAttributes);
		align.align(referenceStruct, structures);
		List<ChimeraStructuralObject>specList = specListFromStructureList(chimera, structures);
		result =  getAlignResults(align, result, specList);
		} catch (Exception e) { e.printStackTrace(); }
		return result;
	}

	/**
	 * This is called to handle the "align chains" command.
	 *
	 * @param chimera the Chimera object
	 * @param result the CyCommandResult
	 * @param referenceChain the reference chain
	 * @param structureList the list of chains to align to the reference
	 * @param showSequences show the resulting pairwise alignments
	 * @param createEdges create edges in Cytoscape corresponding to the alignments
	 * @param assignAttributes assign edge attributes with the alignment results
	 * @return the updated CyCommandResult
	 */
	static public CyCommandResult alignChains(Chimera chimera, CyCommandResult result, 
	                                          ChimeraStructuralObject referenceChain, 
	                                          List<ChimeraStructuralObject>chainList,
			                                      boolean showSequences, boolean createEdges,
	                                          boolean assignAttributes)
	                                          throws CyCommandException {
		// Make sure everything is a chain
		if (!(referenceChain instanceof ChimeraChain))
			throw new CyCommandException("Reference chain must be a chain specification");

		for (ChimeraStructuralObject obj: chainList) {
			if (!(obj instanceof ChimeraChain))
				throw new CyCommandException("Chains to align must be chain specifications");
		}
		// Do the alignment
		Align align = getAlign(chimera, showSequences, createEdges, assignAttributes);
		align.align(referenceChain, chainList);
		return getAlignResults(align, result, chainList);
	}

	private static Align getAlign(Chimera chimera, boolean showSequences, boolean createEdges,
	                              boolean assignAttributes) {
		Align align = new Align(chimera);
		align.setCreateEdges(assignAttributes);
		align.setCreateNewEdges(createEdges);
		align.setShowSequence(showSequences);
		return align;
	}

	private static CyCommandResult getAlignResults(Align align, CyCommandResult result,
	                                               List<ChimeraStructuralObject> matchList) {
		for (ChimeraStructuralObject obj: matchList) {
			float[] matchResults;
			if (obj instanceof ChimeraModel)
				matchResults = align.getResults(((ChimeraModel)obj).getModelName());
			else
				matchResults = align.getResults(obj.toString());
			result.addMessage("Alignment results for "+obj.toString()+": ");
			for (int i = 0; i < 3; i++) {
				result.addMessage("     "+Align.attributeKeys[i]+"="+matchResults[i]);
			}
			result.addResult(obj.toString(), ""+matchResults[0]+","+matchResults[1]+","+matchResults[2]);
		}
		return result;
	}
}
