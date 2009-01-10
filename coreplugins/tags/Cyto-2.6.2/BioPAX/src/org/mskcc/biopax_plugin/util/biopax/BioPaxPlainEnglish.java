// $Id: BioPaxPlainEnglish.java,v 1.6 2006/08/23 15:20:33 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.util.biopax;

import java.util.HashMap;


/**
 * A simple Utility for converting BioPAX Types into "Plain English".
 * For example, the type "biochemicalReaction" is converted to
 * "Biochemical Reaction".
 *
 * @author Ethan Cerami.
 */
public class BioPaxPlainEnglish {
	private static HashMap map;

	/**
	 * Converts the specified type into "Plain English".
	 * For example, the type "biochemicalReaction" is converted to
	 * "Biochemical Reaction".
	 * <p/>
	 * If the type is not know, the origianl argument type is simply returned.
	 *
	 * @param type BioPAX Type String.
	 * @return BioPAX Type String, in "Plain English".
	 */
	public static String getTypeInPlainEnglish(String type) {
		if (map == null) {
			initMap();
		}

		String plainEnglish = (String) map.get(type);

		if (plainEnglish == null) {
			return type;
		} else {
			return plainEnglish;
		}
	}

	private static void initMap() {
		map = new HashMap();
		map.put("protein", "Protein");
		map.put("smallMolecule", "Small Molecule");
		map.put("physicalEntity", "Physical Entity");
		map.put("complex", "Complex");
		map.put("dna", "DNA");
		map.put("rna", "RNA");
		map.put("interaction", "Interaction");
		map.put("physicalInteraction", "Physical Interaction");
		map.put("control", "Control");
		map.put("catalysis", "Catalysis");
		map.put("modulation", "Modulation");
		map.put("conversion", "Conversion");
		map.put("biochemicalReaction", "Biochemical Reaction");
		map.put("complexAssembly", "Complex Assembly");
		map.put("transportWithBiochemicalReaction", "Transport with Biochemical Reaction");
		map.put("transport", "Transport");
		map.put("transportWithBiochemicalReaction", "Transport with Biochemical Reaction");
		// chemical modifications
		map.put("acetylation site", "Acetylation Site");
		map.put("glycosylation site", "Glycosylation Site");
		map.put("phosphorylation site", "Phosphorylation Site");
		map.put("sumoylation site", "Sumoylation Site");
		map.put("ubiquitination site", "Ubiquitination Site");
		// cellular locations
		map.put("cellular component unknown", "Cellular Component Unknown");
		map.put("centrosome", "Centrosome");
		map.put("cytoplasm", "Cytoplasm");
		map.put("endoplasmic reticulum", "Endoplasmic Reticulum");
		map.put("endosome", "Endosome");
		map.put("extracellular", "Extracellular");
		map.put("golgi apparatus", "Golgi Apparatus");
		map.put("mitochondrion", "Mitochondrion");
		map.put("nucleoplasm", "NP");
		map.put("nucleus", "Nucleus");
		map.put("plasma membrane", "Plasma Membrane");
		map.put("ribosome", "Ribosome");
		map.put("transmembrane", "TM");
	}
}
