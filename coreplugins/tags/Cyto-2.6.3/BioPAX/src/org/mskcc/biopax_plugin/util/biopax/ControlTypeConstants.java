// $Id: ControlTypeConstants.java,v 1.3 2006/06/15 22:06:02 grossb Exp $
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

import java.util.HashSet;
import java.util.Set;


/**
 * Controlled Vocabulary Terms associated with the BioPAX CONTROL-TYPE Element.
 *
 * @author Ethan Cerami.
 */
public class ControlTypeConstants {
	private static HashSet inhibitionSet;
	private static HashSet activationSet;

	/**
	 * Control Type:  INHIBITION
	 */
	public static final String CONTROL_TYPE_INHIBITION = "INHIBITION";

	/**
	 * Control Type:  INHIBITION-COMPETITIVE
	 */
	public static final String CONTROL_TYPE_INHIBITION_COMPETITIVE = "INHIBITION-COMPETITIVE";

	/**
	 * Control Type:  INHIBITION-IRREVERSIBLE
	 */
	public static final String CONTROL_TYPE_INHIBITION_IRREVERSIBLE = "INHIBITION-IRREVERSIBLE";

	/**
	 * Control Type:  INHIBITION-OTHER
	 */
	public static final String CONTROL_TYPE_INHIBITION_OTHER = "INHIBITION-OTHER";

	/**
	 * Control Type:  INHIBITION-UNKMECH
	 */
	public static final String CONTROL_TYPE_INHIBITION_UNKMECH = "INHIBITION-UNKMECH";

	/**
	 * Control Type:  INHIBITION-UNCOMPETITIVE
	 */
	public static final String CONTROL_TYPE_INHIBITION_UNCOMPETITIVE = "INHIBITION-UNCOMPETITIVE";

	/**
	 * Conrol Type:  INHIBITION-NONCOMPETITIVE
	 */
	public static final String CONTROL_TYPE_INHIBITION_NONCOMPETITIVE = "INHIBITION-NONCOMPETITIVE";

	/**
	 * Control Type:  INHIBITION-ALLOSTERIC
	 */
	public static final String CONTROL_TYPE_INHIBITION_ALLOSTERIC = "INHIBITION-ALLOSTERIC";

	/**
	 * Control Type:  ACTIVATION
	 */
	public static final String CONTROL_TYPE_ACTIVATION = "ACTIVATION";

	/**
	 * Control Type:  ACTIVATION-ALLOSTERIC
	 */
	public static final String CONTROL_TYPE_ACTIVATION_ALLOSTERIC = "ACTIVATION-ALLOSTERIC";

	/**
	 * Control Type: ACTIVATION-NONALLOSTERIC
	 */
	public static final String CONTROL_TYPE_ACTIVATION_NONALLOSTERIC = "ACTIVATION-NONALLOSTERIC";

	/**
	 * Control Type:  ACTIVATION-UNKMECH
	 */
	public static final String CONTROL_TYPE_ACTIVATION_UNKMECH = "ACTIVATION-UNKMECH";

	/**
	 * Gets a Set of Inhibition Control Types.
	 *
	 * @return Set of Strings.
	 */
	public static Set getInhibitionSet() {
		if (inhibitionSet == null) {
			inhibitionSet = new HashSet();
			inhibitionSet.add(CONTROL_TYPE_INHIBITION);
			inhibitionSet.add(CONTROL_TYPE_INHIBITION_COMPETITIVE);
			inhibitionSet.add(CONTROL_TYPE_INHIBITION_IRREVERSIBLE);
			inhibitionSet.add(CONTROL_TYPE_INHIBITION_OTHER);
			inhibitionSet.add(CONTROL_TYPE_INHIBITION_UNKMECH);
			inhibitionSet.add(CONTROL_TYPE_INHIBITION_UNCOMPETITIVE);
			inhibitionSet.add(CONTROL_TYPE_INHIBITION_NONCOMPETITIVE);
			inhibitionSet.add(CONTROL_TYPE_INHIBITION_ALLOSTERIC);
		}

		return inhibitionSet;
	}

	/**
	 * Gets a Set of Activation Control Types.
	 *
	 * @return Set of Strings.
	 */
	public static Set getActivationSet() {
		if (activationSet == null) {
			activationSet = new HashSet();
			activationSet.add(CONTROL_TYPE_ACTIVATION);
			activationSet.add(CONTROL_TYPE_ACTIVATION_ALLOSTERIC);
			activationSet.add(CONTROL_TYPE_ACTIVATION_NONALLOSTERIC);
			activationSet.add(CONTROL_TYPE_ACTIVATION_UNKMECH);
		}

		return activationSet;
	}
}
