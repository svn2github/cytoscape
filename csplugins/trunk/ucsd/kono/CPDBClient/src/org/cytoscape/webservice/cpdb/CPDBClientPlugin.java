package org.cytoscape.webservice.cpdb;

import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.plugin.CytoscapePlugin;


/**
 * Intact Web Service Client Plugin.<br>
 *
 * @author kono
 * @version 0.5
 * @since Cytoscape 2.6
 */
public class CPDBClientPlugin extends CytoscapePlugin {
	/**
	 * Register IntAct client to the web service client manager
	 *
	 * @throws Exception  DOCUMENT ME!
	 */
	public CPDBClientPlugin() throws Exception {
		// Register this client to the manager.
		WebServiceClientManager.registerClient(CPDBClient.getClient());
	}
}
