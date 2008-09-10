package org.cytoscape.view.model2;

import javax.swing.Icon;

/**
 * Defines all of the possible visual properties of a network in Cytoscape as
 * well as the type of that property. The benefit of making this an enum is that
 * it allows us to quickly see all properties that have been set. We'll probably
 * want some way to distinguish node properties from edge and network
 * properties.
 */
public interface VisualProperty<T> {
	
	/**
	 * The type of object represented by this property.
	 */
	public Class<T> getType();

	/**
	 * The default value of this property.
	 */
	public T getDefault(); 

	/**
	 * Used for hashes identifying this property.
	 */
	public String getID();

	/**
	 * For presentation to humans.
	 */
	public String getName();

	/**
	 * Returns an Icon for this VisualProperty based on the input value.
	 */
	public Icon getIcon(T value);

}

