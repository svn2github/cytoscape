package org.cytoscape.model;

import java.util.Map;
import java.util.List;

/** 
 * 
 */
public interface CyDataTable {

	/**
	 * A public CyDataTable is a table that is accessible to the user
	 * through the user interface.  Private or non-public CyDataTables will
	 * not be visible to the user from the normal user interface, although
	 * they will be accessible to plugin writers through the API.
	 * @return Whether or not this CyDataTable should be publicly accessible.
	 */
	public boolean isPublic();

	/**
	 * @return The session unique identifier.
	 */
	public long getSUID();

	/**
	 * @return A human readable name for the CyDataTable.
	 */
	public String getTitle();

	/**
	 * @param title The human readable title for the CyDataTable
	 * suitable for use in a user interface.
	 */
	public void setTitle(String title);

	/**
	 * The keySet of this map defines all columns in the CyDataTable and the
	 * the values of this map define the types of the columns.
	 * @return A map of column names to the {@link Class} objects that defines
	 * the column type.
	 */
	public Map<String,Class<?>> getColumnTypeMap();


	/**
	 * @param columnName The name identifying the attribute.
	 */
	public void deleteColumn(String columnName);

	/**
	 * @param columnName The name identifying the column.
	 * @param type The type associated with the column. 
	 * @param unique Whether the entries in the column are unique. 
	 */
	public <T> void createColumn(String columnName, Class<? extends T> type, boolean unique); 

	/**
	 * Unique columns can be used to map the values from one CyDataTable to another.
	 * @return A list of column names where the values within the column are
	 * guaranteed to be unique. 
	 */
	public List<String> getUniqueColumns();

	/**
	 * @param columnName The name identifying the column to return.
	 * @param type The type of the column to return.
	 * @return The list of values of type T that exist in the column specified by
	 * the columnName.
	 */
	public <T> List<? extends T> getColumnValues(String columnName, Class<? extends T> type);

	/**
	 * @param primaryKey The primary key index of the row to return.
	 * @return The {@link CyRow} identified by the specified index.
	 */
	public CyRow getRow(long primaryKey);

	/**
	 * @return A new {@link CyRow} object for this CyDataTable. 
	 */
	public CyRow addRow();

	/**
	 * By default all {@link CyRow}s created have a primary key column of
	 * type {@link Integer} that gets created at initialization which is
	 * identified by this string.
	 * If the CyDataTable is created and immediately bound to a {@link CyNetwork}
	 * then the primary key attribute is populated with the SUID of the 
	 * {@link GraphObject}.
	 */
	public static final String PRIMARY_KEY = "AID";
}
