package org.cytoscape.webservice;

import java.util.Map;

/**
 * If a client can import key-value pair (attributes), it should implement this.
 *
 */
public interface AttributeImportClient {
	
	/**
	 * Send a query to the service and get a set of key-value pairs.
	 * 
	 * @param query
	 * @return
	 */
	Map<String, Object> importAttributes(Object query);

}
