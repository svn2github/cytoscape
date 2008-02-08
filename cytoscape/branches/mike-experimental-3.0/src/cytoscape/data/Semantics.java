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

import cytoscape.GraphPerspective;
import cytoscape.Node;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;

import cytoscape.Edge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


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

	
	// KONO:04/19/2006 From v2.3, the following two terms will be used only by
	// Gene Ontology Server.
	//  - The basic meaning is same as above, but canonical name will be
	// replaced by the node id. - Aliases are no longer String object. It's a
	// list now.

	/**
	 *
	 */
	public static final String GO_COMMON_NAME = "GO Common Name";

	/**
	 *
	 */
	public static final String GO_ALIASES = "GO Aliases";

	/**
	 *
	 */
	public static final String SPECIES = "species";

	/**
	 *
	 */
	public static final String INTERACTION = "interaction";

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
	 * This method attempts to set a species attribute for every canonical name
	 * defined in the node attributes member of the supplied network. The value
	 * returned by getDefaultSpecies is used; if this return value is null, then
	 * this method exits without doing anything, as there is no species to set.
	 *
	 * If a canonical name already has an entry for the SPECIES attribute, then
	 * this method does not change that value. Otherwise, this method sets the
	 * value of that attribute to that returned by getDefaultSpecies.
	 *
	 * This method does nothing at all if either argument is null.
	 */
	public static void assignSpecies(final GraphPerspective network) {
		if (network == null) {
			return;
		}

		final String defaultSpecies = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");

		if (defaultSpecies == null) {
			return;
		} // we have no value to set

		final CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		final Iterator nodeIt = network.nodesIterator();

		while (nodeIt.hasNext()) {
			final String nodeLabel = ((Node) nodeIt.next()).getIdentifier();
			final String species = nodeAttributes.getStringAttribute(nodeLabel, SPECIES);

			if (species == null) { // only do something if no value exists
				nodeAttributes.setAttribute(nodeLabel, SPECIES, defaultSpecies);
			}
		}
	}

	/**
	 * Returns every unique species defined in the supplied network. Searches
	 * the species attribute in the node attributes of the supplied network and
	 * returns a Set containing every unique value found.
	 */
	public static Set getSpeciesInNetwork(final GraphPerspective network) {
		final Set returnSet = new HashSet();

		if (network == null) {
			return returnSet;
		}

		final CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// in the following map, keys are objects names and values are the
		// species
		final CountedIterator keys = nodeAttributes.getMultiHashMap().getObjectKeys(SPECIES);

		while (keys.hasNext()) {
			returnSet.add(nodeAttributes.getStringAttribute((String) keys.next(), SPECIES));
		}

		return returnSet;
	}


	/**
	 * Returns an array containing all of the unique interaction types present
	 * in the network. Formally, gets from the edge attributes all of the unique
	 * values for the "interaction" attribute.
	 *
	 * If the argument is null, returns an array of length 0.
	 */
	public static String[] getInteractionTypes(final GraphPerspective network) {
		if (network == null) {
			return new String[0];
		}

		final HashMap dupsFilter = new HashMap();
		final CyAttributes attrs = Cytoscape.getEdgeAttributes();
		final MultiHashMap mmap = attrs.getMultiHashMap();
		final CountedIterator objs = mmap.getObjectKeys(Semantics.INTERACTION);

		while (objs.hasNext()) {
			final String obj = (String) objs.next();
			final Object val = mmap.getAttributeValue(obj, Semantics.INTERACTION, null);
			dupsFilter.put(val, val);
		}

		final String[] returnThis = new String[dupsFilter.size()];
		final Iterator uniqueIter = dupsFilter.keySet().iterator();
		int inx = 0;

		while (uniqueIter.hasNext()) {
			returnThis[inx++] = (String) uniqueIter.next();
		}

		return returnThis;
	}

	// -------------------------------------------------------------------------
	/**
	 * Returns the interaction type of the given edge. Formally, gets from the
	 * edge attributes the value for the "interaction" attribute".
	 *
	 * If either argument is null, returns null.
	 */
	public static String getInteractionType(final GraphPerspective network, final Edge edge) {
		if ((network == null) || (edge == null)) {
			return null;
		}

		return Cytoscape.getEdgeAttributes()
		                .getStringAttribute(edge.getIdentifier(), Semantics.INTERACTION);
	}

	// -------------------------------------------------------------------------
	/**
	 * This method is used to determine if two, potentially different names
	 * really refer to the same thing; that is, the two names are synonyms. The
	 * rules applied are as follows:
	 *
	 * 1) If either name is null, this method returns true if both are null,
	 * false otherwise. 2) If the names themselves match, this method returns
	 * true 3) The getAllSynonyms method is called for both names, to get all
	 * known synonyms. each possible pair of synonyms is compared, and this
	 * method returns true if any match is found, false otherwise.
	 *
	 * In all cases, comparisons are done with name1.equalsIgnoreCase(name2).
	 *
	 * The network and cytoscapeObj arguments may be null, which simply limits
	 * the tests that can be done to find synonyms.
	 */
	public static boolean areSynonyms(final String firstName, final String secondName,
	                                  final GraphPerspective network) {
		if ((firstName == null) || (secondName == null)) {
			return ((firstName == null) && (secondName == null));
		}

		if (firstName.equalsIgnoreCase(secondName)) {
			return true;
		}

		final List firstSynonyms = getAllSynonyms(firstName, network);
		final List secondSynonyms = getAllSynonyms(secondName, network);

		for (Iterator firstI = firstSynonyms.iterator(); firstI.hasNext();) {
			for (Iterator secondI = secondSynonyms.iterator(); secondI.hasNext();) {
				if (((String) firstI.next()).equalsIgnoreCase((String) secondI.next())) {
					return true;
				}
			}
		}

		return false;
	}

	// -------------------------------------------------------------------------
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
	public static List getAllSynonyms(final String name, final GraphPerspective network) {
		final List returnList = new ArrayList();

		if (name == null) {
			return returnList;
		}

		returnList.add(name);

		String species = null;

		if (network != null) {
			final String commonName = Cytoscape.getNodeAttributes()
			                                   .getStringAttribute(name, GO_COMMON_NAME);

			if (commonName != null) {
				returnList.add(commonName);
			}

		}

		return returnList;
	}
}
