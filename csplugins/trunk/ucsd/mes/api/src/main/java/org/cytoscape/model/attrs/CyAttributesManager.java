package org.cytoscape.model.attrs;

import java.util.Map;

public interface CyAttributesManager {

	public Map<String,Class<?>> getTypeMap();
	public void deleteAttribute(String attributeName);
	public <T> void createAttribute(String attributeName, Class<? extends T> type); 
}
