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
import java.util.Arrays;
import java.util.List;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;

import structureViz.actions.Chimera;
import structureViz.actions.CyChimera;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.Structure;
import structureViz.ui.ModelNavigatorDialog;

/**
 * 
 */
public class StructureCommands {

	static public CyCommandResult openCommand(Chimera chimera, CyCommandResult result, 
	                                          String nodelist, boolean showDialog) throws CyCommandException {
		String [] nodes = nodelist.split(",");
		for (String node: nodes) {
			System.out.println("Node: "+node);
			String structureNames = CyChimera.getStructureName(node.trim());
			System.out.println("StructureNames: "+structureNames);
			if (structureNames != null) {
				for (String struct: structureNames.split(",")) {
					result = openCommand(chimera, result, struct, null, null, showDialog);
				}
			}
		}
		return result;
	}


	static public CyCommandResult openCommand(Chimera chimera, CyCommandResult result, 
	                                          String pdb, String modbase, String smiles, boolean showDialog) 
	                                                 throws CyCommandException {

		if (pdb != null && (modbase != null || smiles != null))
			throw new CyCommandException("Only one of pdbid or modbaseid can be specified");

		String name = pdb;
		Structure.StructureType structureType = Structure.StructureType.PDB_MODEL;
			
		if (modbase != null)  {
			name = modbase;
			structureType = Structure.StructureType.MODBASE_MODEL;
		} else if (smiles != null) {
			name = smiles;
			structureType = Structure.StructureType.SMILES;
		}

		Structure st = new Structure(name, null, structureType);
		chimera.open(st);

		// To make sure, see if we can get the Chimera model
		ChimeraModel model = chimera.getModel(name);

		if (showDialog) {
			chimera.launchDialog();
		}

		result.addMessage("Opened "+model);
		return result;
	}

	static public CyCommandResult closeCommand(Chimera chimera, CyCommandResult result, 
	                                           List<Structure> structureList) throws CyCommandException {
		if(structureList == null)
			throw new CyCommandException("close: structurelist must be specified");

		if(structureList.size() == 0)
			result.addMessage("Nothing to close");
		else {
			for (Structure s: structureList) {
				result.addMessage("Closed "+s);
				chimera.close(s);
			}
		}
		return result;
	}

	static public CyCommandResult listChains(Chimera chimera, CyCommandResult result, 
	                                         List<Structure> structureList) throws CyCommandException {

		List<ChimeraChain> chainList = getChainList(chimera, structureList);
		List<String> chainStrings = new ArrayList<String>();

		for (ChimeraChain chain: chainList) {
				result.addMessage(chain.toString());
				chainStrings.add(chain.toString());
		}
		result.addResult("ChainList",chainStrings);
		return result;
	}

	static public CyCommandResult listResidues(Chimera chimera, CyCommandResult result, 
	                                           List<Structure> structureList, String chains) throws CyCommandException {
		List<String> chainSpecs = new ArrayList<String>();
		List<String> residueList = new ArrayList<String>();

		if (chains != null)
			chainSpecs = Arrays.asList(chains.split(","));

		List<ChimeraChain> chainList = getChainList(chimera, structureList);
		for (ChimeraChain chain: chainList) {
			String chainName = "Model: "+chain.getChimeraModel().getModelName();
			if (chain.getChainId().equals("_"))
				chainName += " Chain: (no ID)";
			else
				chainName += " Chain: "+chain.getChainId();

			if (chainSpecs.size() > 0) {
				boolean found = false;
				for (String cs: chainSpecs) {
					if (chain.getChainId().equals(cs)) {
						found = true;
						break;
					}
				}
				if (!found) continue;
			}
			// OK, we've got a chain, now get the residues
			for (ChimeraResidue residue: chain.getResidues()) {
				String residueName = chainName+" Residue: "+residue.toString();
				result.addMessage(residueName);
				residueList.add(residueName);
			}

		}
		result.addResult("ResidueList",residueList);
		return result;
	}

	static private List<ChimeraChain> getChainList(Chimera chimera, List<Structure>structureList) {
		List<ChimeraModel> modelList = new ArrayList<ChimeraModel>();

		if (structureList == null)
			modelList = chimera.getChimeraModels();
		else {
			for (ChimeraModel model: chimera.getChimeraModels()) {
				if (structureList.contains(model.getStructure()))
					modelList.add(model);
			}
		}

		List<ChimeraChain> chainList = new ArrayList<ChimeraChain>();
		for (ChimeraModel model: modelList) {
			for (ChimeraChain chain: model.getChains()) {
				chainList.add(chain);
			}
		}
		return chainList;
	}
}
