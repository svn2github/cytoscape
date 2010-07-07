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

import structureViz.actions.Chimera;
import structureViz.actions.DisplayActions;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;

/**
 * 
 */
public class DisplayCommands {

	static public CyCommandResult colorStructure(Chimera chimera, CyCommandResult result, 
	                                             List<Structure>structureList, String residueColor, 
	                                             String labelColor, String ribbonColor, String surfaceColor) {
		List<ChimeraStructuralObject> objList = new ArrayList<ChimeraStructuralObject>();
		for (Structure st: structureList) {
			objList.add(chimera.getChimeraModel(st.modelNumber()));
		}
		return colorSpecList(chimera, result, objList, residueColor, labelColor, ribbonColor, surfaceColor);
	}

	static public CyCommandResult colorSpecList(Chimera chimera, CyCommandResult result, 
	                                            List<ChimeraStructuralObject>specList, String residueColor, 
	                                            String labelColor, String ribbonColor, String surfaceColor) {
		for (ChimeraStructuralObject cso: specList) {
			List<String> colorResult = new ArrayList<String>();
			String atomSpec = cso.toSpec();
			if (residueColor != null && residueColor.length() > 0) {
				List<String> c = DisplayActions.colorAction(chimera, atomSpec, residueColor, ",a");
				if (c != null && c.size() > 0) 
					colorResult.addAll(c);
				else
					colorResult.add("Colored "+cso+" residues "+residueColor);
			}
			if (labelColor != null && labelColor.length() > 0) {
				List<String> c = DisplayActions.colorAction(chimera, atomSpec, labelColor, ",l");
				if (c != null && c.size() > 0) 
					colorResult.addAll(c);
				else
					colorResult.add("Colored "+cso+" labels "+labelColor);
			}
			if (ribbonColor != null && ribbonColor.length() > 0) {
				List<String> c = DisplayActions.colorAction(chimera, atomSpec, ribbonColor, ",r");
				if (c != null && c.size() > 0) 
					colorResult.addAll(c);
				else
					colorResult.add("Colored "+cso+" ribbons "+ribbonColor);
			}
			if (surfaceColor != null && surfaceColor.length() > 0) {
				List<String> c = DisplayActions.colorAction(chimera, atomSpec, surfaceColor, ",s");
				if (c != null && c.size() > 0) 
					colorResult.addAll(c);
				else
					colorResult.add("Colored "+cso+" surfaces "+surfaceColor);
			}
			for (String s: colorResult)
				result.addMessage(s);
		}
		return result;
	}

	static public CyCommandResult rainbowStructure(Chimera chimera, CyCommandResult result, 
	                                               List<Structure>structureList) {
		List<ChimeraStructuralObject> objList = new ArrayList<ChimeraStructuralObject>();
		for (Structure st: structureList) {
			objList.add(chimera.getChimeraModel(st.modelNumber()));
		}
		return rainbowSpecList(chimera, result, objList);
	}

	static public CyCommandResult rainbowSpecList(Chimera chimera, CyCommandResult result, 
	                                              List<ChimeraStructuralObject>specList) {
		for (ChimeraStructuralObject cso: specList) {
			List<String> colorResult = new ArrayList<String>();
			String atomSpec = cso.toSpec();
			List<String> c = DisplayActions.rainbowAction(chimera, atomSpec);
			if (c != null && c.size() > 0) 
				colorResult.addAll(c);
			else
				colorResult.add("Colored "+cso+" rainbow");

			for (String s: colorResult)
				result.addMessage(s);
		}

		return result;
	}

	static public String getAtomSpec(List<ChimeraStructuralObject> specList) {
		String atomSpec = "";
		for (ChimeraStructuralObject cso: specList)
			atomSpec += ","+cso.toSpec();
		return atomSpec.substring(1);
	}
}
