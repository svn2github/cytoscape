package org.cytoscape.io.webservice.client;

import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Task;


/**
 * If a client can import networks from service, it should implement this.
 *
 */
public interface NetworkImportTask extends Task {
	
	/**
	 * Send a query to the service and import network(s).
	 * Usually, this method creates attributes, too.  They are local to network.
	 * 
	 * @return Set of networks imported.
	 * 
	 */
	Set<CyNetwork> importNetwork();
}
