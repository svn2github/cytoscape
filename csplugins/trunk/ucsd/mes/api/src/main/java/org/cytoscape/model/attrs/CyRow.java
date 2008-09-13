package org.cytoscape.model.attrs;

import java.util.Map;

public interface CyRow {
	public <T> T get(String columnName, Class<? extends T> type);
	public <T> void set(String columnName, T value);
	public Class<?> contains(String columnName);
	public <T> boolean contains(String columnName,Class<? extends T> type);
	public Map<String,Object> getAllValues();
}

