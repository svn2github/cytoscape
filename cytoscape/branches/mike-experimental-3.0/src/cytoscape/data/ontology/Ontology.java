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

import cytoscape.GraphPerspective;
import cytoscape.Node;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import cytoscape.data.ontology.readers.OBOTags;
import cytoscape.data.readers.MetadataEntries;
import cytoscape.data.readers.MetadataParser;

import cytoscape.Node;

import org.biojava.ontology.AlreadyExistsException;
import org.biojava.ontology.OntologyOps;
import org.biojava.ontology.Term;
import org.biojava.ontology.Triple;
import org.biojava.ontology.Variable;

import org.biojava.utils.AbstractChangeable;
import org.biojava.utils.ChangeVetoException;

import java.net.URISyntaxException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * General Ontology class which implements
 * <ahref="http://www.biojava.org/docs/api14/org/biojava/ontology/Ontology.html">Ontology
 * interface from BioJava project</a>.<br>
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
public class Ontology extends AbstractChangeable implements org.biojava.ontology.Ontology {
	/*
	 * For metadata
	 */
	private static final String CURATOR = "curator";
	private static final String OBO_FORMAT = "OBO Flat File";
	private static final String DATA_TYPE = "Ontology DAG";

	// This network attribute indicates this is an ontology or not.
	/**
	 *
	 */
	public static final String IS_ONTOLOGY = "Is_Ontology";

	/**
	 * Name of this ontorogy. This will be used as the ID of this ontology.
	 */
	protected String name;
	protected MetadataParser metaParser;

	/*
	 * Currently not used.
	 */
	private final OntologyOps ops;

	/*
	 * Actual DAG of the Ontology
	 */
	private GraphPerspective ontologyGraph;
	private CyAttributes ontologyAttr;
	private CyAttributes termAttr;

	/**
	 * Constructor.<br>
	 *
	 * <p>
	 * Creates null network for this ontology.
	 * </p>
	 *
	 * @param name
	 *            Name of this ontology. Will be used as ID.
	 * @throws URISyntaxException
	 */
	public Ontology(final String name) {
		this(name, null, null, null);
	}

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
	public Ontology(final String name, final String curator, final String description,
	                final GraphPerspective dag) {
		ontologyAttr = Cytoscape.getNetworkAttributes();
		termAttr = Cytoscape.getNodeAttributes();

		this.name = name;

		// Not yet implemented.
		ops = new OntologyOps() {
				public Set getRemoteTerms() {
					// TODO Auto-generated method stub
					return null;
				}
			};

		if (dag == null) {
			this.ontologyGraph = Cytoscape.createNetwork(this.name);
		} else {
			this.ontologyGraph = dag;
		}

		ontologyAttr.setAttribute(ontologyGraph.getIdentifier(), IS_ONTOLOGY, true);
		ontologyAttr.setUserEditable(IS_ONTOLOGY, false);
		ontologyAttr.setUserVisible(IS_ONTOLOGY, false);

		/*
		 * Setup metadata & graph (network) attributes
		 */
		metaParser = new MetadataParser(ontologyGraph);
		metaParser.setMetadata(MetadataEntries.TITLE, name);
		metaParser.setMetadata(MetadataEntries.FORMAT, OBO_FORMAT);
		metaParser.setMetadata(MetadataEntries.TYPE, DATA_TYPE);

		if (curator != null) {
			ontologyAttr.setAttribute(ontologyGraph.getIdentifier(), CURATOR, curator);
		}

		if (description != null) {
			metaParser.setMetadata(MetadataEntries.DESCRIPTION, description);
		}
	}

	/**
	 * Returns name (actually, an ID).
	 *
	 * @return Name of the ontology as string
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return Curator as string
	 */
	public String getCurator() {
		return ontologyAttr.getStringAttribute(ontologyGraph.getIdentifier(), CURATOR);
	}

	/**
	 * Add a new ontology term to the DAG.<br>
	 *
	 * @param newTerm
	 */
	public void add(OntologyTerm newTerm) {
		Node newOntologyTerm = Cytoscape.getCyNode(newTerm.getName(), true);
		ontologyGraph.addNode(newOntologyTerm);
		termAttr.setAttribute(newOntologyTerm.getIdentifier(), OBOTags.DEF.toString(),
		                      newTerm.getDescription());
	}

	/**
	 *
	 * @return Number of ontology terms in this ontology.
	 */
	public int size() {
		return ontologyGraph.getNodeCount();
	}

	/**
	 * Return all the terms in this ontology.<br>
	 *
	 * @return All ontology terms as Set object.
	 */
	public Set getTerms() {
		Set<Term> terms = new HashSet<Term>();

		Iterator nodeIt = ontologyGraph.nodesIterator();

		while (nodeIt.hasNext()) {
			final Node node = (Node) nodeIt.next();
			final String id = node.getIdentifier();
			final Term term = new OntologyTerm(node.getIdentifier(), this.name,
			                                   termAttr.getStringAttribute(id,
			                                                               OBOTags.DEF.toString()));
			terms.add(term);
		}

		return terms;
	}

	/**
	 * @return true if the term is in the DAG.
	 */
	public boolean containsTerm(String id) {
		Node testNode = Cytoscape.getCyNode(id, false);

		if (testNode == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Fetch the term with the specified name.
	 *
	 * @return The term named <code>name</code>
	 * @throws NoSuchElementException
	 *             if no term exists with that name
	 */
	public OntologyTerm getTerm(String id) {
		final OntologyTerm term = new OntologyTerm(id, name,
		                                           termAttr.getStringAttribute(id,
		                                                                       OBOTags.DEF.toString()));

		return term;
	}

	/**
	 *
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Ontology Name: " + name + ", ");
		sb.append("Curator: "
		          + ontologyAttr.getStringAttribute(ontologyGraph.getIdentifier(), CURATOR) + ", ");
		sb.append("Description: " + this.getDescription());

		return sb.toString();
	}

	/**
	 * See if a triple exists in this ontology.<br>
	 *
	 */
	public boolean containsTriple(Term subject, Term object, Term predicate) {
		Node source = Cytoscape.getCyNode(subject.getName());
		Node target = Cytoscape.getCyNode(object.getName());

		String interaction = predicate.getName();

		if ((source == null) || (target == null)) {
			return false;
		}

		if (Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, interaction, false) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create a new term in this ontology.
	 */
	public Term createTerm(String name, String description)
	    throws AlreadyExistsException, ChangeVetoException, IllegalArgumentException {
		Node newNode = Cytoscape.getCyNode(name, true);
		ontologyGraph.addNode(newNode);
		termAttr.setAttribute(name, MetadataEntries.DESCRIPTION.toString(), description);

		Term newTerm = new OntologyTerm(name, this.name, description);

		return newTerm;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param arg0 DOCUMENT ME!
	 * @param arg1 DOCUMENT ME!
	 * @param arg2 DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws AlreadyExistsException DOCUMENT ME!
	 * @throws ChangeVetoException DOCUMENT ME!
	 * @throws IllegalArgumentException DOCUMENT ME!
	 */
	public Term createTerm(String arg0, String arg1, Object[] arg2)
	    throws AlreadyExistsException, ChangeVetoException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param subject DOCUMENT ME!
	 * @param object DOCUMENT ME!
	 * @param predicate DOCUMENT ME!
	 * @param name DOCUMENT ME!
	 * @param description DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws AlreadyExistsException DOCUMENT ME!
	 * @throws ChangeVetoException DOCUMENT ME!
	 */
	public Triple createTriple(Term subject, Term object, Term predicate, String name,
	                           String description)
	    throws AlreadyExistsException, ChangeVetoException {
		Triple newTriple = new cytoscape.data.ontology.Triple(subject, object, predicate, name,
		                                                      description);

		//Cytoscape.getCyEdge(subject.getName(), object.getName(), null, predicate.getName());
		return newTriple;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param arg0 DOCUMENT ME!
	 * @param arg1 DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws AlreadyExistsException DOCUMENT ME!
	 * @throws ChangeVetoException DOCUMENT ME!
	 * @throws IllegalArgumentException DOCUMENT ME!
	 */
	public Variable createVariable(String arg0, String arg1)
	    throws AlreadyExistsException, ChangeVetoException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Delete a term from DAG (CyNetwork)
	 */
	public void deleteTerm(Term arg0) throws ChangeVetoException {
		ontologyGraph.removeNode(Cytoscape.getRootGraph().getNode(arg0.getName()).getRootGraphIndex(),
		                         true);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return metaParser.getMetadataMap().get(MetadataEntries.DESCRIPTION.toString()).toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public OntologyOps getOps() {
		return ops;
	}

	/**
	 * returns set of Triples.<br>
	 *
	 * This is an expensive operation!
	 */
	public Set getTriples(Term arg0, Term arg1, Term arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param arg0 DOCUMENT ME!
	 * @param arg1 DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws ChangeVetoException DOCUMENT ME!
	 * @throws IllegalArgumentException DOCUMENT ME!
	 */
	public Term importTerm(Term arg0, String arg1)
	    throws ChangeVetoException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
}
