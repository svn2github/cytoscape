package org.cytoscape.model.builder;


import java.util.Map;
import java.util.Collection;


public interface CyTableBuilder {

	void createColumn(String name, Class<?> type, boolean mutability);

	void setPrimaryKeyColumnName( String name );

	String getPrimaryKeyColumnName();

	Collection<String> getColumnNames();

	Class<?> getColumnType(String columnName);

	boolean getColumnMutability(String columnName);

	Collection<Object> getPrimaryKeys();

    Map<Object,Object> getColumnData(String columnName);

	void setColumnValue(String columnName, Object primaryKey, Object value);
}
