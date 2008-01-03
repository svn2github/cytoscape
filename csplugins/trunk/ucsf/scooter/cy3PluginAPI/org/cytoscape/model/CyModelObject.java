package org.cytoscape.model;

import java.util.Set;

/**
 * CyModelObject is the base interface for CyNetwork, 
 * CyNode, and CyEdge.  It provides a small set of routines
 * that are common for all of the network objects
 */
public interface CyModelObject {

	/**
	 * Make a deep copy of this object
	 *
	 * @return the object copy
	 */
	public CyModelObject clone();

	/**
	 * Get the identifier for this object
	 *
	 * @return identifier
	 */
	public int getIdentifier();

	/**
	 * Get the CyAttributes for this object
	 *
	 * @return list of CyAttributes
	 */
	public CyAttributes getAttributes();

	/**
	 * Return a typed attribute for this object
	 *
	 * @param attributeName the name of the attribute to get
	 * @param type the type of the attribute to get
	 * @return the value for the attribute, or "null" if either
	 * the attribute doesn't exist or has a null value.
	 */
	public Object getAttribute(String attributeName, CyAttributes.AttributeType type);

	/**
	 * Test to see if an attribute is defined for this object in this network
	 *
	 * @param attributeName the name of the attribute to check
	 * @return true if the attribute exists
	 */
	public boolean hasAttribute(String attributeName);

	/**
	 * Update or create an attribute for this object.
	 *
	 * @param attributeName the name of the attribute to be set
	 * @param attributeValue the value of the attribute to be set
	 * @param type the type of the attribute to be set
	 */
	public void setAttribute(String attributeName, Object attributeValue, CyAttributes.AttributeType type);

	/**
	 * Return a string representation of this object.  Usually,
	 * this is either the identifier or name of the object.
	 */
	public String toString();

	/**
	 * Add a change listener to this object
	 *
	 * @param listener the change listener to add
	 */
	public void addChangeListener(CyModelObjectChangeListener listener);

	/**
	 * Remove a change listener from this object
	 *
	 * @param listener the listener to remove
	 */
	public void removeChangeListener(CyModelObjectChangeListener listener);
}
