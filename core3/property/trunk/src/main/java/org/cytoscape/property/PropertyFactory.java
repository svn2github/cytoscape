package org.cytoscape.property;

public interface PropertyFactory<T> {
	
	public T createProperty();

	public void setResourceLocation(String location);
	
}
