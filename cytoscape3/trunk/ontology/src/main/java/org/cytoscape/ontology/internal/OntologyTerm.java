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
package org.cytoscape.ontology.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.ontology.Alias;
import org.cytoscape.ontology.Ontology;
import org.cytoscape.ontology.Term;
import org.cytoscape.ontology.internal.readers.OBOTags;

/**
 * Simple in-memory implementation of an ontology term based on BioJava's
 * interface.<br>
 * 
 * <p>
 * This implementation uses CyNetwork and CyAttributes as its actual data
 * storage. A term is equal to a CyNode and the node can have attributes like
 * other regular nodes in CyNetwork. Synonyms, description, etc are stored in
 * CyAttributes.
 * </p>
 * 
 * @since Cytoscape 2.4
 * @version 0.8
 * @author kono
 * 
 */
public class OntologyTerm implements Term {
	/*
	 * These constants will be used as the attribute names in CyAttributes.
	 */

	// protected static final String DESCRIPTION = "description";
	protected static final String SYNONYM = "synonym";

	private static final String ID = "Term ID";
	private static final String NAME = "Term Name";

	/**
	 * Node represents an ontology Term
	 */
	private final CyNode node;
	
	private final Alias alias;

	/**
	 * Constructor.<br>
	 * 
	 * @param name
	 *            ID of this term. SHOULD NOT BE NULL.
	 * @param ontologyName
	 * @param description
	 */
	public OntologyTerm(Ontology parent, String id, String ontologyName,
			String description) {
		if (id == null)
			throw new IllegalArgumentException("id cannot be null.");
		if (ontologyName == null)
			throw new IllegalArgumentException("ontologyName cannot be null.");
		node = parent.getDAG().addNode();

		node.attrs().set(ID, id);
		node.attrs().set(NAME, ontologyName);

		if (description != null) {
			node.attrs().set(
					OBOTags.getPrefix() + "." + OBOTags.DEF.toString(),
					description);
		}
		
		alias = new AliasImpl(node);
	}

	/**
	 * Return name (ID) of this term.<br>
	 */
	public String getName() {
		return node.attrs().get(ID, String.class);
	}

	/**
	 * Return a human-readable description of this term, or the empty string if
	 * none is available.
	 * 
	 */
	public String getDescription() {
		return node.attrs().get(
				OBOTags.getPrefix() + "." + OBOTags.DEF.toString(), String.class);
	}

	@Override
	public String getID() {
		return node.attrs().get(ID, String.class);
	}

	@Override
	public <T> T getTermAnnotation(String annotationName, Class<T> type) {
		return node.attrs().get(annotationName, type);
	}

	@Override
	public CyNode getNode() {
		return node;
	}

	@Override
	public Alias getAlias() {
		return alias;
	}
}
