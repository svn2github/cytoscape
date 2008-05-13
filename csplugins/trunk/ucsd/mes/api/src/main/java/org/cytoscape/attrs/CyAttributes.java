
package org.cytoscape.attrs;


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
	 * Deletes on the mapping between this SUID and this attribute.
	 */
	public void deleteAttribute(int suid, String attributeName);

	/**
	 * Set an attribute value of type T.
	 * Will only allow Object to be Integer, String, Float, Boolean, 
	 * or a List or Map of those types.
	 * Should fire an event indicating that an attribute has been set.
	 */
	public void set(int suid, String attributeName, Object value);

	/**
	 * Get and attribute value of type T.
	 */
	public <T> T get(int suid, String attributeName, Class<? extends T> c);

	/**
	 * Does an attribute of type T exist for this SUID?
	 */
	public <T> boolean contains(int suid, String attributeName, Class<? extends T> c);
}
