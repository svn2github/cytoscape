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
import cytoscape.command.CyCommandException;

import structureViz.actions.Chimera;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraSelectedObject;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;

/**
 * 
 */
public class CommandUtils {

	/**
 	 * Return the list of structures corresponding to the passed arguments.  A
 	 * structure can be specified by it's model number (#N), it's name, or the
 	 * name of the node it's associated with.  It can also refer to the currently
 	 * selected model or list of models
 	 */
	public static List<Structure> getStructureList(String structureSpec, Chimera chimera) throws CyCommandException {
		if (structureSpec == null)
			return null;

		List<Structure> structureList = new ArrayList<Structure>();

		// Special case: structurelist="selected"
		if (StructureVizCommandHandler.SELECTED.equals(structureSpec)) {
			List<ChimeraStructuralObject> selectionList = chimera.getSelectionList();
			for (ChimeraStructuralObject model: selectionList) {
				if (model instanceof ChimeraModel)
					structureList.add(((ChimeraModel)model).getStructure());
			}
			return structureList;
		} else if ("all".equals(structureSpec)) {
			return chimera.getOpenStructs();
		}

		String[] structureArray = structureSpec.split(",");
		for (String structure: structureArray) {
			Structure st = getStructureFromSpec(chimera, structure);
			if (st != null)
				structureList.add(st);
		}
		return structureList;
	}

	/**
 	 * Return a Structure when given a structureSpec, which can either be a model
 	 * number, a node name, or a model name.
 	 */
	private static Structure getStructureFromSpec(Chimera chimera, String spec) throws CyCommandException {
		ChimeraModel m = null;
		try {
			if (spec.startsWith("#")) {
				try {
					int decimalOffset = spec.substring(1).indexOf('.');	// Do we have a sub-model?
					if (decimalOffset > 0) {
						int subModelNumber = Integer.parseInt(spec.substring(decimalOffset+1));
						int modelNumber = Integer.parseInt(spec.substring(1, decimalOffset));
						m = chimera.getModel(modelNumber, subModelNumber);
						if (m == null) {
							throw new CyCommandException("No open model: "+modelNumber+"."+subModelNumber);
						}
					} else {
						int modelNumber = Integer.parseInt(spec.substring(1));
						m = chimera.getModel(modelNumber, 0);
						if (m == null) {
							throw new CyCommandException("No open model: "+modelNumber);
						}
					}
				} catch (NumberFormatException e) {
					throw new CyCommandException("Model numbers must be numeric");
				}
			} else {
				m = chimera.getModel(spec);
				if (m == null) {
					// See if we have a node by that name
					CyNode n = Cytoscape.getCyNode(spec, false);
					if (n != null) {
						for (Structure struct: chimera.getOpenStructs()) {
							if (struct.node() == n)
								return struct;
						}
					}
					throw new CyCommandException("No open model: "+spec);
				}
			}
		} catch (Exception e) {
			throw new CyCommandException("Exception: "+e);
		}
		return m.getStructure();
	}

	/**
 	 * A speclist is a more complicated version of a structurelist that allows the user
 	 * to specify not only the model, but the chains and residues.  A speclist is a comma-
 	 * separated list of specifications, where each specification is of the form: 
 	 * 		model:residues.chains@atoms
 	 * where model is the same as the structure spec described above, residues are a list of residue
 	 * numbers or type+number, and the chain is the chain spec.  To refer to an entire chain, simply
 	 * leave the residues out: model.chains
 	 * 
 	 * You can also use "-" to indicate ranges of residues
 	 */
	public static List<ChimeraStructuralObject> getSpecList(String specString, Chimera chimera) throws CyCommandException {
		List <ChimeraStructuralObject> specList = new ArrayList<ChimeraStructuralObject>();
		if (specString == null || specString.length() == 0) 
			return specList;

		// Special case: "selected"
		if (specString.equals(StructureVizCommandHandler.SELECTED)) {
			specList.add(new ChimeraSelectedObject());
			return specList;
		}

		String[] specArray = specString.split(",");
		for (String spec: specArray) {
			ChimeraModel model = null;
			ChimeraChain chain = null;
			if (spec.indexOf(':') > 0) {
				String[] modelSpec = spec.split(":",2);
				// Get the structure
				Structure st = getStructureFromSpec(chimera, modelSpec[0]);
				model = chimera.getChimeraModel(st.modelNumber());
				spec = modelSpec[1];
			}
			if (spec.indexOf('.') >= 0) {
				String[] chainSpec = spec.split("\\.",2);
				// If we don't have a model at this point, then
				// chainSpec[0] must be the model
				if (model == null) {
					Structure st = getStructureFromSpec(chimera, chainSpec[0]);
					model = chimera.getChimeraModel(st.modelNumber());
					chain = model.getChain(chainSpec[1]);
					specList.add(chain);
				} else {
					// We already have a model, so chainSpec[0] is a residue-range
					chain = model.getChain(chainSpec[1]);
					if (chainSpec[0].length() > 0) {
						// Get the residues
						specList.addAll(chain.getResidueRange(chainSpec[0]));
					} else
						specList.add(chain);
				}
			} else {
				// No chain spec
				// If we don't have a model at this point, then we've got a bare
				// model spec
				if (model == null) {
					Structure st = getStructureFromSpec(chimera, spec);
					specList.add(chimera.getChimeraModel(st.modelNumber()));
				} else {
					// Use the default chain
					chain = model.getChain("_");
					// Get the residues
					specList.addAll(chain.getResidueRange(spec));
				}
			}
		}
		return specList;
	}

	public static String SpecListToString(Chimera chimera, List<ChimeraStructuralObject>specList) {
		return null;
	}
}
