
package org.cytoscape.model.attrs;


public interface CyAttributes {


	/**
	 * Set an attribute value of type T.
	 * Will only allow Object to be Integer, String, Float, Boolean, 
	 * or a List or Map of those types.
	 * Should fire an event indicating that an attribute has been set.
	 */
	public void set(String attributeName, Object value);

	/**
	 * Get and attribute value of type T.
	 */
	public <T> T get(String attributeName, Class<? extends T> c);

	/**
	 * Does an attribute of type T exist for this SUID?
	 */
	public <T> boolean contains(String attributeName, Class<? extends T> c);

	/**
	 * Deletes this value of the attribute, but not the attribute itself. 
	 */
	public void remove(String AttributeName);
}
