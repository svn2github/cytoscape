// $Id: BioPaxEntityParser.java,v 1.7 2006/06/21 20:12:54 grossb Exp $
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

import org.jdom.Attribute;
import org.jdom.Element;
import org.mskcc.biopax_plugin.util.links.ExternalLink;
import org.mskcc.biopax_plugin.util.rdf.RdfQuery;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Utility for easily extract BioPAX Fields.
 *
 * @author Ethan Cerami.
 */
public class BioPaxEntityParser {
	private Element resource;
	private RdfQuery rdfQuery;

	/**
	 * Constructor.
	 *
	 * @param resource Resource Element.
	 * @param rdfMap   HashMap of RDF Elements.
	 */
	public BioPaxEntityParser(Element resource, HashMap rdfMap) {
		this.resource = resource;
		this.rdfQuery = new RdfQuery(rdfMap);
	}

	/**
	 * Gets the BioPAX Type.
	 *
	 * @return BioPAX Type String.
	 */
	public String getType() {
		return resource.getName();
	}

	/**
	 * Gets the Short Name field.
	 *
	 * @return short name field, or null if not available.
	 */
	public String getShortName() {
		String shortName = null;
		Element shortNameElement = rdfQuery.getNode(resource, "SHORT-NAME");

		if (shortNameElement != null) {
			shortName = shortNameElement.getTextNormalize();
		}

		return shortName;
	}

	/**
	 * Gets the Name Field.
	 *
	 * @return name field, or null if not available.
	 */
	public String getName() {
		String name = null;
		Element nameElement = rdfQuery.getNode(resource, "NAME");

		if (nameElement != null) {
			name = nameElement.getTextNormalize();
		}

		return name;
	}

	/**
	 * Gets the RDF ID.
	 *
	 * @return RDF ID, or null if not available.
	 */
	public String getRdfId() {
		Attribute idAttribute = BioPaxUtil.extractRdfIdAttribute(resource);

		if (idAttribute != null) {
			return idAttribute.getValue();
		} else {
			return null;
		}
	}

	/**
	 * Gets ArrayList of Synonyms.
	 *
	 * @return ArrayList of Synonym String Objects.
	 */
	public ArrayList getSynonymList() {
		ArrayList synList = new ArrayList();
		ArrayList synListElements = rdfQuery.getNodes(resource, "SYNONYMS");

		if (synListElements.size() > 0) {
			for (int i = 0; i < synListElements.size(); i++) {
				Element synElement = (Element) synListElements.get(i);
				synList.add(synElement.getTextNormalize());
			}
		}

		return synList;
	}

	/**
	 * Gets the Organism Name.
	 *
	 * @return organism field, or null if not available.
	 */
	public String getOrganismName() {
		String organism = null;
		Element orgElement = rdfQuery.getNode(resource, "ORGANISM/*/NAME");

		if (orgElement != null) {
			organism = orgElement.getTextNormalize();
		}

		return organism;
	}

	/**
	 * Gets the NCBI Taxonomy ID.
	 *
	 * @return taxonomyId, or -1, if not available.
	 */
	public int getOrganismTaxonomyId() {
		int taxonomyId = -1;
		Element taxonomyElement = rdfQuery.getNode(resource, "ORGANISM/*/TAXON-XREF/*/ID");

		if (taxonomyElement != null) {
			String taxId = taxonomyElement.getTextNormalize();

			try {
				taxonomyId = Integer.parseInt(taxId);
			} catch (NumberFormatException e) {
				taxonomyId = -1;
			}
		}

		return taxonomyId;
	}

	/**
	 * Gets the Comment field.
	 *
	 * @return comment field or null, if not available.
	 */
	public String getComment() {
		String comment = null;
		Element commentElement = rdfQuery.getNode(resource, "COMMENT");

		if (commentElement != null) {
			comment = commentElement.getTextNormalize();
		}

		return comment;
	}

	/**
	 * Gets the Availability Field.
	 *
	 * @return availability field or null, if not available.
	 */
	public String getAvailability() {
		String availability = null;
		Element availElement = rdfQuery.getNode(resource, "AVAILABILITY");

		if (availElement != null) {
			availability = availElement.getTextNormalize();
		}

		return availability;
	}

	/**
	 * Gets an ArrayList of all Unification XRefs.
	 *
	 * @return ArrayList of ExternalLink Objects.
	 */
	public ArrayList getUnificationXRefs() {
		ArrayList xrefListElements = rdfQuery.getNodes(resource, "XREF/unificationXref");

		return extractXrefs(xrefListElements);
	}

	/**
	 * Gets an ArrayList of all Relationship XRefs.
	 *
	 * @return ArrayList of ExternalLink Objects.
	 */
	public ArrayList getRelationshipXRefs() {
		ArrayList xrefListElements = rdfQuery.getNodes(resource, "XREF/relationshipXref");

		return extractXrefs(xrefListElements);
	}

	/**
	 * Gets an ArrayList of all XRefs.
	 *
	 * @return ArrayList of ExternalLink Objects.
	 */
	public ArrayList getAllXRefs() {
		ArrayList xrefListElements = rdfQuery.getNodes(resource, "XREF/*");

		return extractXrefs(xrefListElements);
	}

	private ArrayList extractXrefs(ArrayList xrefListElements) {
		ArrayList dbList = new ArrayList();

		if (xrefListElements.size() > 0) {
			for (int i = 0; i < xrefListElements.size(); i++) {
				Element ref = (Element) xrefListElements.get(i);
				Element dbElement = rdfQuery.getNode(ref, "DB");
				Element idElement = rdfQuery.getNode(ref, "ID");

				if ((dbElement != null) && (idElement != null)) {
					String dbName = dbElement.getTextNormalize();
					String id = idElement.getTextNormalize();
					ExternalLink link = new ExternalLink(dbName, id);
					dbList.add(link);
				}
			}
		}

		return dbList;
	}
}
