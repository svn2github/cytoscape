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

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;

import structureViz.actions.Chimera;
import structureViz.actions.CyChimera;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;
import structureViz.ui.ModelNavigatorDialog;

/**
 * 
 */
public class StructureCommands {

	static public CyCommandResult openCommand(Chimera chimera, CyCommandResult result, 
	                                          String nodelist, boolean showDialog) throws CyCommandException {
		String [] nodes = nodelist.split(",");
		for (String nodeName: nodes) {
			CyNode node = Cytoscape.getCyNode(nodeName);
			String structureNames = CyChimera.getStructureName(nodeName.trim());
			if (structureNames != null) {
				for (String struct: structureNames.split(",")) {
					result = openCommand(chimera, result, struct, null, null, showDialog);
					// Get the model we created
					ChimeraModel model = chimera.getModel(struct);
					Structure modelStruct = model.getStructure();
					// Now, associate the node with the structure
					if (node != null)
						modelStruct.setNode(node);
				}
			}
		}
		return result;
	}

	static public CyCommandResult openCommand(Chimera chimera, CyCommandResult result, String nodelist,
	                                          String pdb, boolean showDialog) throws CyCommandException {
		String [] nodes = nodelist.split(",");
		for (String nodeName: nodes) {
			CyNode node = Cytoscape.getCyNode(nodeName);
			String structureNames = CyChimera.getStructureName(nodeName.trim());
			if (structureNames != null) {
				for (String struct: structureNames.split(",")) {
					if (struct.trim().equals(pdb)) {
						result = openCommand(chimera, result, struct, null, null, showDialog);
						// Get the model we created
						ChimeraModel model = chimera.getModel(struct);
						Structure modelStruct = model.getStructure();
						// Now, associate the node with the structure
						if (node != null)
							modelStruct.setNode(node);
						return result;
					}
				}
			}
		}
		result.addMessage("No structures named "+pdb+" in node "+nodelist);
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

		List<ChimeraChain> chainList = getChainList(chimera, structureList, null);
		List<String> chainStrings = new ArrayList<String>();

		for (ChimeraChain chain: chainList) {
				result.addMessage(chain.toString());
				chainStrings.add(chain.toString());
		}
		result.addResult("ChainList",chainStrings);
		return result;
	}

	static public CyCommandResult listResidues(Chimera chimera, CyCommandResult result, 
	                                           List<Structure> structureList, 
	                                           List<ChimeraStructuralObject>chains) throws CyCommandException {
		List<String> residueList = new ArrayList<String>();

		List<ChimeraChain> chainList = getChainList(chimera, structureList, chains);
		for (ChimeraChain chain: chainList) {
			String chainName = "Model: "+chain.getChimeraModel().getModelName();
			if (chain.getChainId().equals("_"))
				chainName += " Chain: (no ID)";
			else
				chainName += " Chain: "+chain.getChainId();

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


	static public CyCommandResult listSelectedModels(Chimera chimera, CyCommandResult result) {
		List<ChimeraStructuralObject> selList = chimera.getSelectionList();
		if (selList == null || selList.size() == 0) {
			result.addMessage("Nothing is selected");
			return result;
		}
		List<String> selectedModels = new ArrayList<String>();
		result.addMessage("Currently selected models: ");
		for (ChimeraStructuralObject obj: selList) {
			if (obj instanceof ChimeraModel) {
				selectedModels.add(((ChimeraModel)obj).toSpec());
				result.addMessage("    "+obj.toString());
			}
		}
		result.addResult("Models",selectedModels);
		return result;
	}

	static public CyCommandResult listSelectedChains(Chimera chimera, CyCommandResult result, 
	                                                 List<Structure>structureList) {
		List<ChimeraStructuralObject> selList = chimera.getSelectionList();
		if (selList == null || selList.size() == 0) {
			result.addMessage("Nothing is selected");
			return result;
		}
		List<ChimeraChain> chainList = getChainList(chimera, structureList, null);
		List<String> selectedChains = new ArrayList<String>();
		result.addMessage("Currently selected chains: ");
		for (ChimeraStructuralObject obj: selList) {
			if (obj instanceof ChimeraChain && chainList.contains((ChimeraChain)obj)) {
				selectedChains.add(((ChimeraChain)obj).toSpec());
				result.addMessage("    "+obj.toString());
			}
		}
		result.addResult("Chains",selectedChains);
		return result;
	}

	static public CyCommandResult listSelectedResidues(Chimera chimera, CyCommandResult result,
	                                                   List<Structure> structureList, 
	                                                   List<ChimeraStructuralObject> chains) throws CyCommandException {
		List<ChimeraStructuralObject> selList = chimera.getSelectionList();
		if (selList == null || selList.size() == 0) {
			result.addMessage("Nothing is selected");
			return result;
		}

		List<ChimeraChain> chainList = getChainList(chimera, structureList, chains);

		List<String> selectedResidues = new ArrayList<String>();
		result.addMessage("Currently selected residues: ");
		for (ChimeraStructuralObject obj: selList) {
			if (obj instanceof ChimeraResidue) {
				ChimeraResidue res = (ChimeraResidue)obj;
				String chainId = res.getChainId();
				ChimeraModel model = res.getChimeraModel();
				ChimeraChain chain = model.getChain(chainId);
				if (chain != null && chainList.contains(chain)) {
					selectedResidues.add(res.toSpec());
					// Get a nicer residue display
			    String nodeName = "{none}";
			    Structure structure = model.getStructure();
			    if (structure != null && structure.getIdentifier() != null)
			      nodeName = structure.getIdentifier();
			    String displayName = model.getModelName();
			    if (displayName.length() > 14)
			      displayName = displayName.substring(0,13)+"...";
			    if (chainId.equals("_")) {
						result.addMessage("    "+nodeName+"; "+displayName+" Chain (no ID): "+res.toString()+" ["+res.toSpec()+"]");
					} else {
						result.addMessage("    "+nodeName+"; "+displayName+" Chain "+chainId+": "+res.toString()+" ["+res.toSpec()+"]");
					}
				}
			}
		}
		result.addResult("Residues",selectedResidues);
		return result;
	}


	static private List<ChimeraChain> getChainList(Chimera chimera, List<Structure>structureList, 
	                                               List<ChimeraStructuralObject>chainSpecs) {
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
				// We only want to add it to our list if this chain is in chainSpecs
				if (chainSpecs == null || chainSpecs.size() == 0 || chainSpecs.contains(chain))
					chainList.add(chain);
			}
		}
		return chainList;
	}
}
