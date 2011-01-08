package org.cytoscape.webservice;

import java.util.Set;

import org.cytoscape.model.CyNetwork;


/**
 * If a client can import networks from service, it should implement this.
 *
 */
public interface NetworkImportClient {
	
	/**
	 * Send a query to the service and import network(s).
	 * 
	 * @param query
	 * @return
	 */
	Set<CyNetwork> importNetwork(Object query);
}
