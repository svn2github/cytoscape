package org.cytoscape.ontology;

import java.net.URI;

/**
 * Create Ontology object from URI.
 * 
 * TODO: integration to IO framework
 * 
 * @author k
 *
 */
public interface OntologyFactory {
	
	public Ontology getOntology(URI rdf);
}
