package org.cytoscape.model;

/**
 * An interface like this would allow for other interfaces that are specific to an attribute to be overlaid,
 * such as edge direction or edge weight
 */
public interface AttributeHolder {

	public Object findAttribute(Object obj);
	
	public void addAttribute(Object obj);
	
	public Object[] getAttributes();
	
	
}
