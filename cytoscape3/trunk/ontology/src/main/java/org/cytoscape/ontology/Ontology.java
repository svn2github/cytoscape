package org.cytoscape.ontology;

import org.cytoscape.model.CyNetwork;

public interface Ontology {
	
	public String getID();
	
	public String getName();
	
	public Term getRootTerm();
	
	public Ontology getPathToRoot(Term target);
	
	public CyNetwork getDAG();

}
