package org.cytoscape.ontology;

import org.cytoscape.model.CyNode;

public interface Term {
	
	public String getID();

	public String getName();
	
	public <T> T getTermAnnotation(String annotationName, Class<T> type);
	
	public Alias getAlias();
	
	public CyNode getNode();
}
