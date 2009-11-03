package org.cytoscape.ontology;

/**
 * Managing map of ontology DAGs.
 * <p>
 * This is a replacement for Ontology Server in older versions.
 * 
 * @author k
 *
 */
public interface OntologyManager {
	
	public void addOntology(Ontology ontology);
	public void deleteOntology(Ontology ontology);
	
	public Ontology getOntology(String ontologyID);

}
