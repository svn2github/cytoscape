package org.cytoscape.io.webservice.client;

import java.util.Set;

import org.cytoscape.model.CyTable;
import org.cytoscape.work.Task;

/**
 * If a client can import key-value pair (attributes), it should implement this.
 *
 */
public interface CyTableImportClient extends Task {
	
	/**
	 * Send a query to the service and get a set of key-value pairs.
	 * 
	 * @return Set of CyTables created from service call result.
	 */
	Set<CyTable> importCyTable();

}
