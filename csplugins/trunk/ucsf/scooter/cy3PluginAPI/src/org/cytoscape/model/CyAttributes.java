package org.cytoscape.model;

import java.util.List;

/**
 * CyAttributes contain all of the annotation information that goes beyond
 * the basic graph model.  What is presented here is merely a skeleton until
 * ongoing discussions about the CyAttributes interface are completed
 */

public interface CyAttributes {
	public enum AttributeType {TYPE_BOOLEAN, TYPE_FLOATING, TYPE_INTEGER, TYPE_STRING, TYPE_LIST, TYPE_MAP};

	/**
 	 * Determines if the specified id/attributeName pair exists
 	 *
 	 * @param id unique identifier
 	 * @param attributeName attribute name
 	 * @return true if there is an attribute named "attributeName" for object "id"
 	 */
	public boolean hasAttribute(int id, String attributeName);

	/**
 	 * Determines if the specified id/attributeName/type tuple exists
 	 *
 	 * @param id unique identifier
 	 * @param attributeName attribute name
 	 * @param type if the attribute exists, does it have the requisite type?
 	 * @return true if there is an attribute named "attributeName" for object "id" with type "type"
 	 */
	public boolean hasAttribute(int id, String attributeName, AttributeType type);

	/**
 	 * Sets an attribute
 	 *
 	 * @param id unique identifier
 	 * @param attributeName the attribute name
 	 * @param type the type of the attribute
 	 * @param value the value to set
 	 */
	public void setAttribute(int id, String attributeName, AttributeType type, Object value)
		throws IllegalArgumentException;

	/**
 	 * Gets an attribute
 	 *
 	 * @param id unique identifier
 	 * @param attributeName the attribute name
 	 * @param type the type of the attribute
 	 */
	public Object getAttribute(int id, String attributeName, AttributeType type)
		throws IllegalArgumentException;

	/**
 	 * Gets the type of an attribute
 	 *
 	 * @param attributeName the name of the attribute
 	 * @return the type
 	 */
	public AttributeType getType(String attributeName);

	/**
 	 * Remove an attribute
 	 *
 	 * @param id unique identifier
 	 * @param attributeName the attribute name
 	 */
	public void deleteAttribute(int id, String attributeName);
}
