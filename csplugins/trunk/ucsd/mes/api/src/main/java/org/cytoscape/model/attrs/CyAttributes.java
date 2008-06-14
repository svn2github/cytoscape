
package org.cytoscape.model.attrs;


public interface CyAttributes {

	/**
	 *
	 */
	public String[] getAttributeNames();

	/**
	 * It's almost like we need attributes of Attributes...
	 */
	public void setAttributeDescription(String attributeName, String description);

	/**
	 *
	 */
	public String getAttributeDescription(String attributeName);

	/**
	 *
	 */
	public Class getAttributeType(String attributeName);

	/**
	 *
	 */
	public void setUserVisible(String attributeName, boolean value);

	/**
	 *
	 */
	public boolean getUserVisible(String attributeName);

	/**
	 *
	 */
	public void setUserEditable(String attributeName, boolean value);

	/**
	 *
	 */
	public boolean getUserEditable(String attributeName);

	/**
	 * Deletes every mapping made to this attribute. 
	 */
	public void deleteAttribute(String attributeName);


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
}
