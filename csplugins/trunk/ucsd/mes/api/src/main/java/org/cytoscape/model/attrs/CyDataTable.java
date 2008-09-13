package org.cytoscape.model.attrs;

import java.util.Map;
import java.util.List;

/** 
 *
 */
public interface CyDataTable {

	public boolean isPublic();

	public String getName();

	/**
	 */
	public Map<String,Class<?>> getColumnTypeMap();


	/**
	 * @param attributeName The name identifying the attribute.
	 */
	public void deleteColumn(String attributeName);

	/**
	 * @param attributeName The name identifying the column.
	 * @param type The type associated with the column. 
	 * @param unique Whether the entries in the column are unique. 
	 */
	public <T> void createColumn(String attributeName, Class<? extends T> type, boolean unique); 

	public List<String> getUniqueColumns();

	public <T> List<? extends T> getColumnValues(String columnName, Class<? extends T> type);

	public CyRow getRow(long index);

	public CyRow addRow();

	public static final String PRIMARY_KEY = "AID";
}
