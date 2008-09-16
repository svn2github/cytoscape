package org.cytoscape.model;

import java.util.Map;

/**
 * This interface represents one row in a CyDataTable.
 */
public interface CyRow {
	/**
	 * @param columnName The name of the column to return.
	 * @param type The type of the column to return. The type
	 * <b>must</b> be type the column is defined for otherwise
	 * an IllegalArgument exception will be thrown.
	 * @return The a value of type T that is identified by the
	 * specified columnName and type.
	 */
	public <T> T get(String columnName, Class<? extends T> type);

	/**
	 * @param columnName The string identifying the column whose value
	 * should be set.
	 * @param value The value to set the column to.
	 */
	public <T> void set(String columnName, T value);

	/**
	 * @param columnName The name of the column to check.
	 * @return The Class<?> object that is defined for this column. Will
	 * return null if the column has not been defined. Will always return
	 * a base type. If the value is actually a {@link CyFunction} the
	 * function will be evaluated and the function must evaluate to the
	 * type of the column.
	 */
	public Class<?> contains(String columnName);

	/**
	 * @param columnName The name of the column to check.
	 * @param type The type of the column to check.  The type can be
	 * one of the base types OR {@link CyFunction} so that we
	 * can determine whether a column is a function or raw data.
	 */
	public <T> boolean contains(String columnName,Class<? extends T> type);

	/**
	 * @return A map of column names to Objects that contain the values
	 * contained in this Row.
	 */
	public Map<String,Object> getAllValues();
}

