package org.cytoscape.view.model;

public interface Range<T> {

	Class<T> getType();
	
	/**
	 * If this range is a set of discrete values, return true.
	 * 
	 * @return If discrete, return true.
	 * 
	 */
	boolean isDiscrete();
}
