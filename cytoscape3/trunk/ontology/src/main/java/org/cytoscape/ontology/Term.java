package org.cytoscape.ontology;

public interface Term {
	
	public String getID();

	public String getName();
	
	public <T> T getTermAnnotation(Class<T> type, String annotationName);
}
