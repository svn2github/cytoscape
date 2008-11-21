
package org.cytoscape.attributes;


/** 
 * The interface used for accessing the attributes associated with a 
 * a given object.
 */
public interface CyAttributes {

	/**
	 * Set an attribute value of type T.
	 * Will only allow {@link Object} to be {@link Integer}, {@link String}, 
	 * {@link Float}, {@link Boolean}, 
	 * or a {@link java.util.List} or {@link java.util.Map} of those types.
	 * Should fire an event indicating that an attribute has been set.
	 * @param attributeName The name identifying the attribute.
	 * @param value The value assigned to this attribute. This Object must
	 * be of type {@link Integer}, {@link String}, {@link Float}, {@link Boolean}, 
	 * or a {@link java.util.List} or {@link java.util.Map} of those types. 
	 * An {@link IllegalArgumentException}
	 * will be thrown if the value is not of the correct type.
	 * @throws IllegalArgumentException When the value parameter is incorrectly typed.
	 */
	public void set(String attributeName, Object value);

	/**
	 * Get and attribute value of type T.
	 * @param attributeName The name identifying the attribute.
	 * @param c The type of the attribute.  This is crucial because it tells the
	 * method the type of the object that it should be returning.
	 * @throws IllegalArgumentException When the c parameter doesn't match the type bound 
	 * to the attributeName.
	 */
	public <T> T get(String attributeName, Class<? extends T> c);

	/**
	 * Does an attribute of type T exist for this SUID?
	 * @param attributeName The name identifying the attribute.
	 * @param c The type of the attribute.  This is included here to provide
	 * a consistent attribute interface.  If we make the assumption that
	 * an attribute with a particular name can only have one type, then 
	 * this type parameter doesn't need to be here.
	 */
	public Class<?> contains(String attributeName);

	/**
	 * Deletes this value of the attribute, but not the attribute itself. 
	 * @param attributeName The name identifying the attribute.
	 */
	public void remove(String attributeName);

	/**
	 * DO NOT USE THIS!!!!.  This is a temporary method used to accomodate
	 * the VizMapper.  It will go away once the VizMapper is refactored.
	 */
	public Object getRaw(String attributeName);

	/**
	 * Returns the CyAttributesManager that manages this CyAttributes object. 
	 * @return The CyAttributesManager that manages this CyAttributes object. 
	 */
	public CyAttributesManager getAttrMgr();
}
