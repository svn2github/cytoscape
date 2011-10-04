/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
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
package structureViz.model;

import structureViz.actions.Chimera;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraStructuralObject;

/**
 */
public class StructureUtils {
	/**
	 * This method takes a Chimera atomSpec ([#model]:[residue][.chainID]) and returns
	 * the lowest-level object referenced by the spec.  For example, if the spec is
	 * "#0", this method will return a ChimeraModel.  If the spec is ":.A", it will return
	 * a ChimeraChain, etc.
	 *
	 * @param atomSpec the specification string
	 * @param chimeraObject the Chimera object we're currently using
	 * @return a ChimeraStructuralObject of the lowest type
	 */
	public static ChimeraStructuralObject fromSpec(String atomSpec, Chimera chimeraObject) {
		ChimeraModel model = null;
		ChimeraChain chain = null;
		ChimeraResidue residue = null;

		String[] split = atomSpec.split(":|@");
		// 0 = model; 1 = Residue and Chain; 2 = Atom
		if (split[0].length() == 0)  {
			// No model specified...
			model = chimeraObject.getChimeraModels().get(0);
		} else {
			int modelNumber = 0;
			int submodelNumber = 0;
			String[] subSplit = split[0].substring(1).split(".");
			modelNumber = Integer.parseInt(subSplit[0]);
			if (subSplit.length > 1)
				submodelNumber = Integer.parseInt(subSplit[1]);

			model = chimeraObject.getChimeraModel(modelNumber, submodelNumber);
		}

		// Split into residue and chain
		String[] residueChain = split[1].split("\\.");
		// 0 = Residue; 1 = Chain
		if (residueChain.length == 2 && residueChain[1].length() > 0)
			chain = model.getChain(residueChain[1]);

		if (residueChain[0].length() > 0) {
			if (chain == null)
				residue = model.getResidue(residueChain[0]);
			else
				residue = chain.getResidue(residueChain[0]);
		}

		if (residue != null) 
			return residue;
		else if (chain != null)
			return chain;

		return model;
	}
			

	/**
	 * This method takes a Cytoscape attribute specification ([structure#][residue][.chainID]) 
	 * and returns the lowest-level object referenced by the spec.  For example, if the spec is
	 * "1tkk", this method will return a ChimeraModel.  If the spec is ".A", it will return
	 * a ChimeraChain, etc.
	 *
	 * @param attrSpec the specification string
	 * @param chimeraObject the Chimera object we're currently using
	 * @return a ChimeraStructuralObject of the lowest type
	 */
	public static ChimeraStructuralObject fromAttribute(String attrSpec, Chimera chimeraObject) { 
		if (attrSpec.indexOf(',') > 0 || attrSpec.indexOf('-') > 0) {
			// No support for either lists or ranges
			return null;
		}

		String residue = null;
		String model = null;
		String chain = null;

		ChimeraModel chimeraModel = null;
		ChimeraChain chimeraChain = null;
		ChimeraResidue chimeraResidue = null;

		// System.out.println("Getting object from attribute: "+attrSpec);

		String[] split = attrSpec.split("#|\\.");
		if (split.length == 1) {
			// Residue only
			residue = split[0];
		} else if (split.length == 3) {
			// We have all three
			model = split[0];
			residue = split[1];
			chain = split[2];
		} else if (split.length == 2 && attrSpec.indexOf('#') > 0) {
			// Model and Residue
			model = split[0];
			residue = split[1];
		} else {
			// Residue and Chain
			residue = split[0];
			chain = split[1];
		}

		// System.out.println("model = "+model+" chain = "+chain+" residue = "+residue);

		if (model != null) {
			Structure st = Structure.getStructure(model);
			if (st != null) {
				chimeraModel = chimeraObject.getChimeraModel(st.modelNumber(), st.subModelNumber());
			}
		} else
			chimeraModel = chimeraObject.getChimeraModels().get(0);

		// System.out.println("ChimeraModel = "+chimeraModel);

		if (chain != null) {
			chimeraChain = chimeraModel.getChain(chain);
			// System.out.println("ChimeraChain = "+chimeraChain);
		}

		if (residue != null) {
			chimeraResidue = null;
			if (chimeraChain != null) 
				chimeraResidue = chimeraChain.getResidue(residue);
			chimeraResidue = chimeraModel.getResidue(residue);
			// System.out.println("ChimeraResidue = "+chimeraResidue);
			return chimeraResidue;
		}

		if (chimeraChain != null)
			return chimeraChain;

		if (chimeraModel != null)
			return chimeraModel;

		return null;
	}

	public static ChimeraModel getModel(String atomSpec, Chimera chimeraObject) {
		// System.out.println("getting model for "+atomSpec);
		String[] split = atomSpec.split(":");
		// No model specified....
		if (split[0].length() == 0) return null;

		// System.out.println("model = "+split[0].substring(1));
		int model = 0;
		int submodel = 0;
		String[] subSplit = split[0].substring(1).split("\\.");
		if (subSplit.length > 0)
			model = Integer.parseInt(subSplit[0]);
		else
			model = Integer.parseInt(split[0].substring(1));

		if (subSplit.length > 1)
			submodel = Integer.parseInt(subSplit[1]);

		return chimeraObject.getChimeraModel(model, submodel);
	}

	public static ChimeraResidue getResidue(String atomSpec, Chimera chimeraObject) {
		// System.out.println("Getting residue from: "+atomSpec);
		ChimeraModel model = getModel(atomSpec, chimeraObject); // Get the model
		if (model == null) {
			model = chimeraObject.getChimeraModels().get(0);
		}
		return getResidue(atomSpec, model, chimeraObject);
	}

	public static ChimeraResidue getResidue(String atomSpec, ChimeraModel model, Chimera chimeraObject) {
		// System.out.println("Getting residue from: "+atomSpec);
		ChimeraResidue residue = null;
		String[] split = atomSpec.split(":|@");

		// Split into residue and chain
		String[] residueChain = split[1].split("\\.");

		if (residueChain[0].length() == 0) return null;

		if (residueChain.length == 2 && residueChain[1].length() > 0) {
			ChimeraChain chain = model.getChain(residueChain[1]);
			return chain.getResidue(residueChain[0]);
		}
		return model.getResidue(residueChain[0]);
	}

	public static ChimeraChain getChain(String atomSpec, ChimeraModel model, Chimera chimeraObject) {
		String[] split = atomSpec.split(":|@");

		// Split into residue and chain
		String[] residueChain = split[1].split("\\.");
		if (residueChain.length == 1)
			return null;

		return model.getChain(residueChain[1]);
	}

	public static boolean isBackbone(String atomSpec, Chimera chimeraObject) {
		String[] split = atomSpec.split("@");
		String atom = split[1];
		if (atom.equals("C") || atom.equals("CA") || atom.equals("N") || atom.equals("H") ||
		    atom.equals("O"))
			return true;
		return false;
	}
}
