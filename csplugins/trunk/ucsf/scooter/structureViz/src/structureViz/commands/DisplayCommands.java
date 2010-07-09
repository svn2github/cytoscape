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
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;

/**
 * 
 */
public class DisplayCommands extends AbstractCommands {

	static public CyCommandResult colorStructure(Chimera chimera, CyCommandResult result, 
	                                             List<Structure>structureList, String residueColor, 
	                                             String labelColor, String ribbonColor, String surfaceColor) {
		return colorSpecList(chimera, result, specListFromStructureList(chimera, structureList), 
		                     residueColor, labelColor, ribbonColor, surfaceColor);
	}

	static public CyCommandResult colorSpecList(Chimera chimera, CyCommandResult result, 
	                                            List<ChimeraStructuralObject>specList, String residueColor, 
	                                            String labelColor, String ribbonColor, String surfaceColor) {
		for (ChimeraStructuralObject cso: specList) {
			String atomSpec = cso.toSpec();
			if (residueColor != null && residueColor.length() > 0) {
				List<String> c = DisplayActions.colorAction(chimera, atomSpec, residueColor, ",a");
				result = addReplies(result, c, "Colored "+cso+" residues "+residueColor);
			}
			if (labelColor != null && labelColor.length() > 0) {
				List<String> c = DisplayActions.colorAction(chimera, atomSpec, labelColor, ",l");
				result = addReplies(result, c, "Colored "+cso+" labels "+labelColor);
			}
			if (ribbonColor != null && ribbonColor.length() > 0) {
				List<String> c = DisplayActions.colorAction(chimera, atomSpec, ribbonColor, ",r");
				result = addReplies(result, c, "Colored "+cso+" ribbons "+ribbonColor);
			}
			if (surfaceColor != null && surfaceColor.length() > 0) {
				List<String> c = DisplayActions.colorAction(chimera, atomSpec, surfaceColor, ",s");
				result = addReplies(result, c, "Colored "+cso+" surfaces "+surfaceColor);
			}
		}
		return result;
	}

	static public CyCommandResult rainbowStructure(Chimera chimera, CyCommandResult result, 
	                                               List<Structure>structureList) {
		return rainbowSpecList(chimera, result, specListFromStructureList(chimera, structureList));
	}

	static public CyCommandResult rainbowSpecList(Chimera chimera, CyCommandResult result, 
	                                              List<ChimeraStructuralObject>specList) {
		for (ChimeraStructuralObject cso: specList) {
			String atomSpec = cso.toSpec();
			List<String> c = DisplayActions.rainbowAction(chimera, atomSpec);
			result = addReplies(result, c, "Colored "+cso+" rainbow");
		}

		return result;
	}

	static public CyCommandResult preset(Chimera chimera, CyCommandResult result, String preset) throws CyCommandException {
		List<String>presetList = chimera.getPresets();
		for (String fullPreset: presetList) {
			// Strip off the description
			String [] com = fullPreset.toLowerCase().split("[(]");
			if (com[0].trim().equals(preset.toLowerCase()))
				return addReplies(result, DisplayActions.presetAction(chimera, com[0]), "Applied preset "+com[0].trim());
		}

		// Make a printable list of presets to tell the user
		String presets = "";
		for (String p: presetList) 
			presets+=", "+p;
		throw new CyCommandException("No preset: "+preset+" available.  Current presets are: "+presets.substring(2));
	}

	static public CyCommandResult displayStructure(Chimera chimera, CyCommandResult result, 
	                                               List<Structure>structureList, String structSpec, boolean hide) 
	                                                                                         throws CyCommandException { 
		return displaySpecList(chimera, result, specListFromStructureList(chimera, structureList), structSpec, hide);
	}

	static public CyCommandResult displaySpecList(Chimera chimera, CyCommandResult result, 
	                                              List<ChimeraStructuralObject>specList, String structSpec, boolean hide) 
	                                                                                         throws CyCommandException {
		checkStructSpec(structSpec);
		for (ChimeraStructuralObject cso: specList) {
			String atomSpec = cso.toSpec();
			List<String> c = DisplayActions.displayAction(chimera, atomSpec, structSpec, hide);
			if (hide)
				result = addReplies(result, c, "Hid "+cso);
			else
				result = addReplies(result, c, "Showing "+cso);
		}

		return result;
	}
			
	static public CyCommandResult focusStructure(Chimera chimera, CyCommandResult result, 
	                                             List<Structure>structureList) { 
		return focusSpecList(chimera, result, specListFromStructureList(chimera, structureList));
	}

	static public CyCommandResult focusSpecList(Chimera chimera, CyCommandResult result, 
	                                            List<ChimeraStructuralObject>specList) {
		if (specList == null || specList.size() == 0) {
			List<String> c = DisplayActions.focusAction(chimera, null);
			result = addReplies(result, c, "Focused on entire scene");
			return result;
		}

		for (ChimeraStructuralObject cso: specList) {
			String atomSpec = cso.toSpec();
			List<String> c = DisplayActions.focusAction(chimera, atomSpec);
			result = addReplies(result, c, "Focused on "+cso);
		}

		return result;
	}

	static public CyCommandResult selectStructure(Chimera chimera, CyCommandResult result, 
	                                               List<Structure>structureList, String structSpec, boolean clear) 
	                                                                                         throws CyCommandException {
		return selectSpecList(chimera, result, specListFromStructureList(chimera, structureList), structSpec, clear);
	}

	static public CyCommandResult selectSpecList(Chimera chimera, CyCommandResult result, 
	                                             List<ChimeraStructuralObject>specList, String structSpec, boolean clear) 
	                                                                                         throws CyCommandException {
		checkStructSpec(structSpec);
		if (specList == null || specList.size() == 0) {
			if (structSpec == null) {
				List<String> c = DisplayActions.selectAction(chimera, null, null, clear);
				result = addReplies(result, c, "Selected entire scene");
			} else {
				List<String> c = DisplayActions.selectAction(chimera, null, structSpec, clear);
				result = addReplies(result, c, "Selected "+structSpec+" in entire scene");
			}
			return result;
		}

		for (ChimeraStructuralObject cso: specList) {
			String atomSpec = cso.toSpec();
			List<String> c = DisplayActions.selectAction(chimera, atomSpec, structSpec, clear);
			result = addReplies(result, c, "Selected "+cso);
		}

		return result;
	}

	static public CyCommandResult depictStructure(Chimera chimera, CyCommandResult result, 
	                                              List<Structure>structureList, String style,
	                                              String ribbonstyle, String surfacestyle, 
	                                              String transparency) throws CyCommandException {

		return depictSpecList(chimera, result, specListFromStructureList(chimera, structureList),
		                      style, ribbonstyle, surfacestyle, transparency);
	}

	static public CyCommandResult depictSpecList(Chimera chimera, CyCommandResult result, 
	                                             List<ChimeraStructuralObject>specList,
	                                             String style, String ribbonstyle, String surfacestyle,
	                                             String transparency) throws CyCommandException {
		// This is a pretty complicated command.  We need to handle the representations of atoms, 
		// ribbons, and surfaces.  Here is what's legal:
		// 		style = none|wire|stick|bs|sphere|cpk
		// 		ribbonstyle = none|flat|edged|round
		// 		surfacestyle = none|solid|mesh|dot
		// 		transparency = xx% where xx is an integer between 0 and 100

		// Define our legal commands
		String[] styleCommands = {"none","wire","stick","bs","sphere","cpk"};
		String[] ribbonStyles = {"none","flat","edged","round"};
		String[] surfaceStyles = {"none","solid","mesh","dot"};

		if (!legalArgument(style, styleCommands))
			throw new CyCommandException("style argument must be one of "+styleCommands);
		if (!legalArgument(ribbonstyle, ribbonStyles))
			throw new CyCommandException("ribbon argument must be one of "+ribbonStyles);
		if (!legalArgument(surfacestyle, surfaceStyles))
			throw new CyCommandException("surface argument must be one of "+surfaceStyles);

		for (ChimeraStructuralObject cso: specList) {
			String atomSpec = cso.toSpec();
			if (style != null) {
				List<String> c = DisplayActions.depictAtomsAction(chimera, atomSpec, style);
				result = addReplies(result, c, "Changed style for "+cso+" to "+style);
			}

			if (ribbonstyle != null) {
				List<String> c = DisplayActions.depictRibbonsAction(chimera, atomSpec, ribbonstyle);
				result = addReplies(result, c, "Changed ribbon style for "+cso+" to "+ribbonstyle);
			}

			if (cso instanceof ChimeraModel && surfacestyle != null) {
				// Handle transparency
				int transparencyPercent = -1;
				if (transparency != null) {
					String trans[] = transparency.split("%");
					transparencyPercent = Integer.parseInt(trans[0]);
				}
				List<String> c = DisplayActions.depictSurfacesAction(chimera, atomSpec, surfacestyle, transparencyPercent);
				result = addReplies(result, c, "Changed surface style for "+cso+" to "+surfacestyle);
			}
		}

		return result;
	}

	static public CyCommandResult rotateStructure(Chimera chimera, CyCommandResult result, 
	                                            String x, String y, String z, String center,
		                                          List<Structure>structureList) throws CyCommandException {
		for (Structure st: structureList) {
			ChimeraModel mod = chimera.getChimeraModel(st.modelNumber());
			if (x != null) rotateAxis(chimera, result, "x", x, mod.toSpec(), center);
			if (y != null) rotateAxis(chimera, result, "y", y, mod.toSpec(), center);
			if (z != null) rotateAxis(chimera, result, "z", z, mod.toSpec(), center);
		}
		return result;
	}


	static public CyCommandResult moveStructure(Chimera chimera, CyCommandResult result, 
	                                            String x, String y, String z, 
		                                          List<Structure>structureList) throws CyCommandException {
		for (Structure st: structureList) {
			ChimeraModel mod = chimera.getChimeraModel(st.modelNumber());
			if (x != null) moveAxis(chimera, result, "x", x, mod.toSpec());
			if (y != null) moveAxis(chimera, result, "y", y, mod.toSpec());
			if (z != null) moveAxis(chimera, result, "z", z, mod.toSpec());
		}
		return result;
	}

	static private void moveAxis(Chimera chimera, CyCommandResult result, String axis, 
	                                  String distance, String modelSpec) throws CyCommandException {
		Double d;
		try { d = Double.parseDouble(distance); } catch (NumberFormatException e) { d = null; }

		if (d == null) throw new CyCommandException("Value for "+axis+" must be a floating point value");
		List<String> c = DisplayActions.moveAxisAction(chimera, modelSpec, axis, d);
		addReplies(result, c, "Moved "+modelSpec+" "+d+" units in the "+axis+" direction");
	}

	static private void rotateAxis(Chimera chimera, CyCommandResult result, String axis, 
	                               String distance, String modelSpec, String center) throws CyCommandException {
		Double d;
		try { d = Double.parseDouble(distance); } catch (NumberFormatException e) { d = null; }

		if (d == null) throw new CyCommandException("Value for "+axis+" must be a floating point value");
		List<String> c = DisplayActions.rotateAxisAction(chimera, modelSpec, axis, d, center);
		addReplies(result, c, "Rotated "+modelSpec+" "+d+" degrees in the "+axis+" direction");
	}

	static private void checkStructSpec(String structSpec) throws CyCommandException {
		// Legal structure designations
		String[] legalStructSpec = {"full","minimal", "ions","ligand","main","nucleic acid","protein","helix","strand","turn",
		                            "with CA/C1'", "without CA/C1'","solvent"};

		if (structSpec == null) return;

		String legalList = "";
		for (String s: legalStructSpec) legalList += ","+s;

		if (!legalArgument(structSpec, legalStructSpec))
			throw new CyCommandException("structuretype specification "+structSpec+
			                             " is unknown.  Legal values are: ["+legalList.substring(1)+"]");
		return;
	}

}
