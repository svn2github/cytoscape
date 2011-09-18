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
package structureViz.ui;

// System imports
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;

import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.calculators.GenericNodeCustomGraphicCalculator;
import cytoscape.visual.customgraphic.CustomGraphicsManager;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.ImageUtil;
import cytoscape.visual.customgraphic.impl.bitmap.URLImageCustomGraphics;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;

import giny.model.GraphObject;

// structureViz imports
import structureViz.StructureViz;
import structureViz.actions.Chimera;

/**
 * The StructureViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class CustomGraphicsUtil { 

	static void applyImage(CyLogger logger, Chimera chimera, 
	                       GraphObject context) {
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();
		String path = null;
		CyCustomGraphics cg = null;
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("structureViz", ".png");
			path = tmpFile.getAbsolutePath();
			// Not really a select command, but it does cause us to wait....
			chimera.select("copy file "+path+" png ");
			// Create the custom graphics
			URL imageURL = tmpFile.toURI().toURL();
			CustomGraphicsManager cgm = vmm.getCustomGraphicsManager();
			cg = new URLImageCustomGraphics(imageURL.toString());
			cgm.addGraphics(cg, imageURL);
		} catch (MalformedURLException mue) {
			logger.warning("Unable to get a URL from "+path+": "+mue.getMessage());
			return;
		} catch (IOException ioe) {
			logger.warning("IO exception reading "+path+": "+ioe.getMessage());
			return;
		}

		// We need to create an attribute we can map to...
		String value = context.getIdentifier()+": "+path;
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		nodeAttributes.setAttribute(context.getIdentifier(), "ChimeraImage", value);

		// Now, add it to the visual style
		VisualStyle style = vmm.getVisualStyle(); // Get the visual style
		// We need to create a calculator
		NodeAppearanceCalculator nac = style.getNodeAppearanceCalculator();
		Calculator cgcalc = 
			nac.getCalculator(VisualPropertyType.NODE_CUSTOM_GRAPHICS_1);

		// Do we already have a discrete map?
		if (cgcalc != null && (cgcalc.getMapping(0) instanceof DiscreteMapping)) {
			// Yes, add to it
			ObjectMapping mapping = cgcalc.getMapping(0);
			DiscreteMapping disMapping = (DiscreteMapping)mapping;
			disMapping.putMapValue(value, cg);
		} else {
			// No, create a new one
			DiscreteMapping dm = 
				new DiscreteMapping(CyCustomGraphics.class, "ChimeraImage");
			dm.putMapValue(value, cg);

			cgcalc = 
				new GenericNodeCustomGraphicCalculator("ChimeraImage", dm,
			                                         VisualPropertyType.NODE_CUSTOM_GRAPHICS_1);
			catalog.addCalculator(cgcalc);
		}
		nac.setCalculator(cgcalc);
		vmm.applyAppearances();

		// Now, delete the temp file we created
		tmpFile.delete();
		Cytoscape.getCurrentNetworkView().updateView();

	}
}
