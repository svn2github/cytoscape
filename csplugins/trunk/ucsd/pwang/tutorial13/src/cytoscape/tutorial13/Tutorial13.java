package cytoscape.tutorial13;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.webservice.WebServiceClientManager;

/**
 * 
 */

public class Tutorial13 extends CytoscapePlugin {
	/**
	 * Register to the Web Service Client.
	 */
	public Tutorial13() throws Exception {
		// Register web service client to the manager.
		WebServiceClientManager.registerClient(My_NCBIClient.getClient());
	}
}
