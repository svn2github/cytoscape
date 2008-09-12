
package org.cytoscape.attributes;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * This class provides a basic implementation of a {@link CyAttributesManager}
 * that can be used to seed the construction of new {@link CyAttributesManager}
 * objects. This class can be used to define attributes that should exist
 * for all instances of {@link CyAttributes}.  Currently only the "Name" attribute
 * with type String has been defined. 
 */
public class InitialCyAttributesManager implements CyAttributesManager {

	private static CyAttributesManager initial = new InitialCyAttributesManager();

	public static CyAttributesManager getInstance() {
		return initial; 
	}

	private final Map<String,Class<?>> typeMap;

	private InitialCyAttributesManager() {
		typeMap = new HashMap<String,Class<?>>();
		typeMap.put("Name",String.class);
	}
	
	public Map<String,Class<?>> getTypeMap() {
		return new HashMap<String,Class<?>>(typeMap);
	}

	public void deleteAttribute(String attributeName) {
		if ( attributeName == null )
			throw new NullPointerException("attribute name is null");

		if ( typeMap.containsKey(attributeName) )
			typeMap.remove(attributeName);
	}

	public <T> void createAttribute(String attributeName, Class<? extends T> type) {
		if ( attributeName == null )
			throw new NullPointerException("attribute name is null");
		if ( type == null )
			throw new NullPointerException("type is null");
	
		if ( typeMap.containsKey(attributeName) )
			throw new IllegalArgumentException("attribute already exists for name: " + attributeName);

		typeMap.put(attributeName,type);
	}

	public CyAttributes getCyAttributes(final long suid) {
		throw new UnsupportedOperationException("Not currently implemented");
	}

	public <T> List<T> getAll(String attributeName, Class<? extends T> type) {
		throw new UnsupportedOperationException("Not currently implemented");
	}
}
