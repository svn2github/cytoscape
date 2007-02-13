
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.data.ontology;

import cytoscape.Cytoscape;

import cytoscape.data.ontology.readers.OBOTags;

import java.util.Map;


/**
 * A Gene Ontology term. This class is an extended version of normal ontology.
 *
 * @author kono
 *
 */
public class GOTerm extends OntologyTerm {
	/**
	 * Creates a new GOTerm object.
	 *
	 * @param id  DOCUMENT ME!
	 * @param termName  DOCUMENT ME!
	 * @param ontologyName  DOCUMENT ME!
	 * @param description  DOCUMENT ME!
	 */
	public GOTerm(final String id, final String termName, final String ontologyName,
	              final String description) {
		super(id, ontologyName, description);

		if (termName != null) {
			Cytoscape.getNodeAttributes()
			         .setAttribute(id, OBOTags.getPrefix() + "." + OBOTags.NAME.toString(), termName);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getNameSpace() {
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map getCrossReferences() {
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFullName() {
		return Cytoscape.getNodeAttributes()
		                .getStringAttribute(super.getName(),
		                                    OBOTags.getPrefix() + "." + OBOTags.NAME.toString());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return Cytoscape.getNodeAttributes()
		                .getStringAttribute(super.getName(),
		                                    OBOTags.getPrefix() + "." + OBOTags.DEF.toString());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getType() {
		return Cytoscape.getNodeAttributes()
		                .getStringAttribute(super.getName(),
		                                    OBOTags.getPrefix() + "."
		                                    + OBOTags.NAMESPACE.toString());
	}
}
