package org.cytoscape.attributes;

import java.util.Map;
import java.util.List;

/** 
 * This interface controls the creation of attributes for a given CyAttributes
 * representation. This is necessary because one CyAttributesManager object could be
 * shared by many CyAttributes objects.
 */
public interface CyAttributesManager {

	/**
	 * This method provides a simple map of names to attribute types. 
	 * This map can then be queried to identify all attribute names
	 * and their types.
	 * @return A map of String attribute names to their types.
	 */
	public Map<String,Class<?>> getTypeMap();

	/**
	 * Removes the attribute of the specified name from the CyAttributes objects.
	 * @param attributeName The name identifying the attribute.
	 */
	public void deleteAttribute(String attributeName);

	/**
	 * Creates an attribute for the specified name and of the specified type.
	 * @param attributeName The name identifying the attribute.
	 * @param type The type associated with the attribute. 
	 */
	public <T> void createAttribute(String attributeName, Class<? extends T> type); 

	/**
	 * Returns a {@link CyAttributes} object for the specified SUID. 
	 * @param suid A Session Unique Identifier.
	 * @return A CyAttributes object identified by the SUID or null if 
	 */
	public CyAttributes getCyAttributes(final long suid);

	/**
	 * Returns a {@link List} of all attribute values for a given attribute
	 * identifier.
	 * @param attributeName The name identifying the attribute.
	 * @param type The type associated with the attribute. 
	 * @return A {@link List} of all attribute values for the specified attribute 
	 * name and type.
	 */
	public <T> List<T> getAll(String attributeName, Class<? extends T> type);
}
