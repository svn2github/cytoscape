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

import java.net.URISyntaxException;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.ontology.Ontology;
import org.cytoscape.ontology.Term;

/**
 * General Ontology class which implements <ahref=
 * "http://www.biojava.org/docs/api14/org/biojava/ontology/Ontology.html"
 * >Ontology interface from BioJava project</a>.<br>
 * 
 * This class representes a general and simple ontology class.&nbsp;This
 * implementation uses <br>
 * CyNetwork and CyAttributes for its actual data storage.
 * 
 * @version 0.8
 * @since Cytoscape 2.4
 * @see org.biojava.ontology
 * 
 * @author kono
 * 
 */
public class OntologyImpl implements Ontology {

	/**
	 * Name of this ontorogy. This will be used as the ID of this ontology.
	 */
	protected String id;

	/*
	 * Actual DAG of the Ontology
	 */
	private CyNetwork ontologDAG;

	/**
	 * Constructor.<br>
	 * 
	 * <p>
	 * Takes CyNetwork as its DAG.
	 * </p>
	 * 
	 * @param name
	 * @param curator
	 * @param description
	 * @param dag
	 * @throws URISyntaxException
	 * @throws URISyntaxException
	 */
	public OntologyImpl(final String id, final CyNetwork dag) {
		if (id == null)
			throw new IllegalArgumentException("id cannot be null.");
		if (dag == null)
			throw new IllegalArgumentException("dag cannot be null.");

		this.id = id;
		this.ontologDAG = dag;

		this.ontologDAG.attrs().set("name", id);

		// TODO attributes are implicitly visible/editable based on their
		// namespace
		// ontologyAttr.setUserEditable(IS_ONTOLOGY, false);
		// ontologyAttr.setUserVisible(IS_ONTOLOGY, false);

	}

	/**
	 * Returns name (actually, an ID).
	 * 
	 * @return Name of the ontology as string
	 */
	public String getName() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Ontology Name: " + id;
	}

	@Override
	public CyNetwork getDAG() {
		return this.ontologDAG;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public Ontology getPathToRoot(Term target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Term getRootTerm() {
		// TODO Auto-generated method stub
		return null;
	}
}
