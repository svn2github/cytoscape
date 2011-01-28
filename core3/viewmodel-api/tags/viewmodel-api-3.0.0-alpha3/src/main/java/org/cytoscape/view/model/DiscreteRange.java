package org.cytoscape.view.model;

import java.util.Set;

public interface DiscreteRange<T> extends Range<T> {
	
	/**
	 * Returns all available values in this type of data.
	 * For example, if T is NodeShape, this method returns all types of node shape, 
	 * such as oval, triangle, rectangle, etc.
	 * 
	 * @return All available values of the type T.
	 * 
	 */
	Set<T> values();
}
