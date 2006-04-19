/*
 File: Semantics.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
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

import giny.model.Edge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.servers.BioDataServer;

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
	 * @deprecated Use {@link CyNode.getIdentifier()} instead.
	 *
	 * This attribute will be removed in April, 2007.
	 */
	public static final String IDENTIFIER = "identifier";

	/**
	 * @deprecated Use {@link CyNode.getIdentifier()} instead.
	 *
	 * This attribute will be removed in April, 2007.
	 */
	public static final String CANONICAL_NAME = "canonicalName";

	/**
	 * @deprecated Use {@link CyNode.getIdentifier()} instead.
	 *
	 * This attribute will be removed in April, 2007.
	 */
	public static final String COMMON_NAME = "commonName";

	/**
	 * @deprecated Use {@link CyNode.getIdentifier()} instead.
	 *
	 * This attribute will be removed in April, 2007.
	 */
	public static final String ALIASES = "aliases";

	public static final String SPECIES = "species";
	public static final String INTERACTION = "interaction";
	public static final String MOLECULE_TYPE = "molecule_type";
	public static final String PROTEIN = "protein";
	public static final String DNA = "DNA";
	public static final String RNA = "RNA";
	public static final String MOLECULAR_FUNCTION = "molecular_function";
	public static final String BIOLOGICAL_PROCESS = "biological_process";
	public static final String CELLULAR_COMPONENT = "cellular_component";

	public static final String LABEL = "label";

	/**
	 * This method should be called in the process of creating a new network, or
	 * upon loading a new bioDataServer. This method will use the bioDataServer
	 * to assign common names to the network using the synonym utilities of the
	 * bioDataServer. In the process, it will assign a species attribute for any
	 * name that does not currently have one.
	 * 
	 * Currently, this method calls assignSpecies and assignCommonNames. At some
	 * point it may be desirable to check the configuration to see what to do,
	 * or put up a UI to prompt the user for what services they would like.
	 */
	public static void applyNamingServices(cytoscape.CyNetwork network) {
		assignSpecies(network);
		assignCommonNames(network, Cytoscape.getBioDataServer());
	}

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
	public static void assignSpecies(cytoscape.CyNetwork network) {
		if (network == null) {
			return;
		}

		String defaultSpecies = CytoscapeInit.getProperty("defaultSpeciesName");
		if (defaultSpecies == null) {
			return;
		} // we have no value to set

		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CountedIterator keys = nodeAttributes.getMultiHashMap().getObjectKeys(
				CANONICAL_NAME);
		String[] canonicalNames = new String[keys.numRemaining()];
		int inx = 0;
		while (keys.hasNext()) {
			canonicalNames[inx++] = (String) keys.next();
		}
		for (int i = 0; i < canonicalNames.length; i++) {
			String canonicalName = canonicalNames[i];
			String species = nodeAttributes.getStringAttribute(canonicalName,
					SPECIES);
			if (species == null) { // only do something if no value exists
				nodeAttributes.setAttribute(canonicalName, SPECIES,
						defaultSpecies);
			}
		}
	}

	/**
	 * Returns every unique species defined in the supplied network. Searches
	 * the species attribute in the node attributes of the supplied network and
	 * returns a Set containing every unique value found.
	 */
	public static Set getSpeciesInNetwork(cytoscape.CyNetwork network) {
		Set returnSet = new HashSet();
		if (network == null) {
			return returnSet;
		}
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		// in the following map, keys are objects names and values are the
		// species
		CountedIterator keys = nodeAttributes.getMultiHashMap().getObjectKeys(
				SPECIES);
		while (keys.hasNext()) {
			returnSet.add(nodeAttributes.getStringAttribute((String) keys
					.next(), SPECIES));
		}
		return returnSet;
	}

	/**
	 * Use the given BioDataServer to set all of the aliases for a node, given
	 * its species.
	 * 
	 * Note: This needs optiomization
	 * 
	 * 
	 * @param node
	 *            the Node that will be assigned names
	 * @param species
	 *            the species of the Node ( NOTE: if null, there will be a check
	 *            to see if the node has a species set for attribute
	 *            Semantics.SPECIES, if not, then the general Cytoscape
	 *            defaultSpecies ( settable with -s ) will be used. )
	 * @param bds
	 *            the given BioDataServer ( NOTE: if null, then the general
	 *            Cytoscape BioDataServer will be used ( settable with -b ) ).
	 */
	public static void assignNodeAliases(CyNode node, String species,
			BioDataServer bds) {

		String id = node.getIdentifier();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// can't have a null node
		if (node == null)
			return;

		// If species are null, use default species name
		if (species == null) {

			species = nodeAttributes.getStringAttribute(id, SPECIES);
			if (species == null) {
				species = CytoscapeInit.getProperty("defaultSpeciesName");
			}
		}
		// Check for the case when we don't have a default species
		if (species != null) {
			nodeAttributes.setAttribute(id, SPECIES, species);
		}

		// Get Gene Ontology Server
		if (bds == null)
			bds = Cytoscape.getBioDataServer();

		// return if no deafult BioDataServer
		if (bds == null)
			return;

		// now do the name assignment
		String cname = bds.getCanonicalName(species, id);

		if (id != nodeAttributes.getStringAttribute(id, CANONICAL_NAME)) {
			nodeAttributes.setAttribute(id, CANONICAL_NAME, id);
		}

		if (cname != null) {
			String[] synonyms = bds.getAllCommonNames(species, cname);
			StringBuffer concat = new StringBuffer();
			String common_name = null;
			
			for (int j = 0; j < synonyms.length; ++j) {
				if (synonyms[j].equals(id)) {
					
				} else {
					concat.append(synonyms[j] + " ");
					if (common_name == null)
						common_name = synonyms[j];
				}
			}
			if (common_name == null) {
				common_name = cname;
				concat.append(cname);
			}
			// Fill both aliases and common names
			nodeAttributes.setAttribute(id, ALIASES, concat.toString());
			nodeAttributes.setAttribute(id, COMMON_NAME, common_name);
		}
	}

	/**
	 * This method takes every canonical name defines in the node attributes of
	 * the given network, and attempts to assign a common name by getting a list
	 * of synonyms for the canonical name from the bioDataServer and using the
	 * first synonym as the common name.
	 * 
	 * This operation requires the SPECIES attribute to be defined for the
	 * canonical name, as input to the bioDataServer. Any canonicalName that
	 * does not have this attribute defined is skipped.
	 * 
	 * This method does nothing if either argument is null. Also, for any
	 * canonical name, this method does nothing if no synonyms for that name can
	 * be provided by the bioDataServer.
	 */
	public static void assignCommonNames(cytoscape.CyNetwork network,
			BioDataServer bioDataServer) {
		if (network == null || bioDataServer == null) {
			return;
		}

		for (Iterator it = network.nodesIterator(); it.hasNext();) {
			CyNode node = (CyNode) it.next();
			assignNodeAliases(node, null, bioDataServer);
		}
	}

	/**
	 * Returns an array containing all of the unique interaction types present
	 * in the network. Formally, gets from the edge attributes all of the unique
	 * values for the "interaction" attribute.
	 * 
	 * If the argument is null, returns an array of length 0.
	 */
	public static String[] getInteractionTypes(cytoscape.CyNetwork network) {
		if (network == null) {
			return new String[0];
		}
		final HashMap dupsFilter = new HashMap();
		final CyAttributes attrs = Cytoscape.getEdgeAttributes();
		final MultiHashMap mmap = attrs.getMultiHashMap();
		final CountedIterator objs = mmap.getObjectKeys(Semantics.INTERACTION);

		while (objs.hasNext()) {
			final String obj = (String) objs.next();
			;
			final Object val = mmap.getAttributeValue(obj,
					Semantics.INTERACTION, null);
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
	public static String getInteractionType(cytoscape.CyNetwork network, Edge e) {
		if (network == null || e == null) {
			return null;
		}
		String canonicalName = e.getIdentifier();
		return Cytoscape.getEdgeAttributes().getStringAttribute(canonicalName,
				Semantics.INTERACTION);
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
	public static boolean areSynonyms(String firstName, String secondName,
			cytoscape.CyNetwork network) {
		if (firstName == null || secondName == null) {
			return (firstName == null && secondName == null);
		}
		if (firstName.equalsIgnoreCase(secondName)) {
			return true;
		}
		List firstSynonyms = getAllSynonyms(firstName, network);
		List secondSynonyms = getAllSynonyms(secondName, network);
		for (Iterator firstI = firstSynonyms.iterator(); firstI.hasNext();) {
			String firstSyn = (String) firstI.next();
			for (Iterator secondI = secondSynonyms.iterator(); secondI
					.hasNext();) {
				String secondSyn = (String) secondI.next();
				if (firstSyn.equalsIgnoreCase(secondSyn)) {
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
	public static List getAllSynonyms(String name, cytoscape.CyNetwork network) {
		List returnList = new ArrayList();
		if (name == null) {
			return returnList;
		}
		returnList.add(name);
		String species = null;
		if (network != null) {
			String commonName = Cytoscape.getNodeAttributes()
					.getStringAttribute(name, COMMON_NAME);
			if (commonName != null) {
				returnList.add(commonName);
			}
			species = Cytoscape.getNodeAttributes().getStringAttribute(name,
					SPECIES);
		}
		BioDataServer bioDataServer = Cytoscape.getBioDataServer();
		species = CytoscapeInit.getProperty("defaultSpeciesName");
		if (species != null) {
			String[] synonyms = bioDataServer.getAllCommonNames(species, name);
			returnList.addAll(Arrays.asList(synonyms));
			// we assume that this list of synonyms from the bioDataServer
			// includes
			// any canonical and common names registered with the node
			// attributes,
			// so we don't have to get a canonical name from the bioDataServer
			// and go back to the node attributes to check those attributes
		}
		return returnList;
	}

}
