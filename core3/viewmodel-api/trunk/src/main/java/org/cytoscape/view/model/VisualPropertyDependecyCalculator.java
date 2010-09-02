package org.cytoscape.view.model;

public interface VisualPropertyDependecyCalculator<T> {

	/**
	 * "Piping" given value to the child visual property.
	 * 
	 * @param value
	 * @return
	 */
	T convert(T value);

}
