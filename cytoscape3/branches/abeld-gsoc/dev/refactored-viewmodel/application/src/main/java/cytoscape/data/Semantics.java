/*
 File: Semantics.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.data;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.GraphObject;

import java.util.*;


/**
 * This class defines names for certain data attributes that are commonly used
 * within Cytoscape. The constants defined here are provided to enable different
 * modules to use the same name when referring to the same conceptual attribute.
 *
 * This class also defines some static methods for assigning these attributes to
 * a network, given the objects that serve as the source for this information.
 */
public class Semantics {

	/**
	 * The name "CANONICAL_NAME" is an historical artifact.  The <b>ONLY</b>
	 * purpose for this variable is to provide the name for an automatically
	 * created node attribute that is guaranteed to be created when the
	 * node is created.  Any other purpose or function this value has supported
	 * in the past is officially no longer supported!
	 */
	public static final String CANONICAL_NAME = "canonicalName";

	
	/**
	 * From v2.3, the following two terms will be used only by
	 * Gene Ontology Server.
	 *  - The basic meaning is same as above, but canonical name will be
	 * replaced by the node id. - Aliases are no longer String object. It's a
	 * list now.
	 */
	public static final String GO_COMMON_NAME = "GO Common Name";


	/**
	 *
	 */
	public static final String SPECIES = "species";

	/**
	 *
	 */
	public static final String INTERACTION = "interaction";
	
	/**
	 * Edge attribute, if true, edge is directed, if false it is undirected
	 */
	public static final String IS_DIRECTED= "is directed";
	/**
	 *
	 */
	public static final String MOLECULE_TYPE = "molecule_type";

	/**
	 *
	 */
	public static final String PROTEIN = "protein";

	/**
	 *
	 */
	public static final String DNA = "DNA";

	/**
	 *
	 */
	public static final String RNA = "RNA";

	/**
	 *
	 */
	public static final String MOLECULAR_FUNCTION = "molecular_function";

	/**
	 *
	 */
	public static final String BIOLOGICAL_PROCESS = "biological_process";

	/**
	 *
	 */
	public static final String CELLULAR_COMPONENT = "cellular_component";



	/**
	 * This method returns a list of all names that are synonyms of the given
	 * name. The returned list will include the name argument itself, and thus
	 * will always be non-null and contain at least one member (unless the
	 * argument itself is null, in which case a list of size 0 is returned). The
	 * search for other names follows the following steps:
	 *
	 * First, if the network argument is non-null and the node attributes
	 * include the name argument as a canonical name, then add any entry for the
	 * COMMON_NAME attribute associated with the canonical name. Next, if a
	 * BioDataServer is available, try to get a species for the given name
	 * either from the SPECIES attribute associated with the canonicalName, or
	 * using the return value of getDefaultSpecies if needed. If a species can
	 * be determined, then use the BioDataServer to add all the synonyms that
	 * are registered for the name argument.
	 */
	public static List<String> getAllSynonyms(final GraphObject go, final CyNetwork network) {
		final List<String> returnList = new ArrayList<String>();

		if (go == null) {
			return returnList;
		}

		returnList.add(go.attrs().get("name",String.class));

		String species = null;

		if (network != null) {
			final String commonName = go.attrs().get(GO_COMMON_NAME,String.class);

			if (commonName != null) {
				returnList.add(commonName);
			}

		}

		return returnList;
	}
}
