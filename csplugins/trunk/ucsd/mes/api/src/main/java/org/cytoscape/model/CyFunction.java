package org.cytoscape.model;

/**
 * A {@link CyRow} in a {@link CyDataTable} may contain either
 * values of the base types (see {@link CyDataTable}) or a
 * CyFunction. CyFunctions are evaluated when the value for the
 * row is accessed and return the newly created value.  These
 * can be used as references to rows in other columns or CyDataTables.
 */
public interface CyFunction<T> {

	/**
	 * @return The type of the value returned by the function.
	 */
	Class<T> getBaseType();	

	/**
	 * This method will evaluate the function when it is called.
	 * @return The value returned by this function.
	 */
	T getValue();

	/**
	 * @return The string that defines the function.
	 */
	String getFunction();

	/**
	 * @param functionDesc A string describing the function. 
	 */
	void setFunction(String functionDesc);
}
