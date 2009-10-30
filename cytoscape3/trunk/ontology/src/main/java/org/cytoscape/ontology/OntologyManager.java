package org.cytoscape.ontology;

public interface OntologyManager {
	
	public void addOntology(Ontology ontology);
	public void deleteOntology(Ontology ontology);
	
	public Ontology getOntology(String ontologyID);

}
