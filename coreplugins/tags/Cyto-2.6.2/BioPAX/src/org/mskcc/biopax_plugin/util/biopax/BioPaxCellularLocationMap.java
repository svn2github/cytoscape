// $Id: BioPaxCellularLocationMap.java,v 1.4 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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
 * Hashmap which maps BioPax Chemical Modifications to Abbreviations.
 * <p/>
 * - IF YOU ADD TO THIS, ADD TO BioPaxPlainEnglish -
 *
 * @author Benjamin Gross
 */
public class BioPaxCellularLocationMap extends HashMap {
	/**
	 * Constructor.
	 */
	public BioPaxCellularLocationMap() {
		put("cellular component unknown", "");
		put("centrosome", "CE");
		put("cytoplasm", "CY");
		put("endoplasmic reticulum", "ER");
		put("endosome", "EN");
		put("extracellular", "EM");
		put("golgi apparatus", "GA");
		put("mitochondrion", "MI");
		put("nucleoplasm", "NP");
		put("nucleus", "NU");
		put("plasma membrane", "PM");
		put("ribosome", "RI");
		put("transmembrane", "TM");
	}
}
