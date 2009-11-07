package org.cytoscape.webservice.psicquic;

import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;

/**
 * PSICQUIC Universal Client class.
 * 
 * <p>
 * This class simply register itself to the Web Service Client framework.
 * 
 * @author kono
 * @since Cytoscape 2.7
 *
 */
public class PSICQUICUniversalClientPlugin extends CytoscapePlugin {

	public PSICQUICUniversalClientPlugin() {

		try {
			WebServiceClientManager.registerClient(PSICQUICUniversalClient
					.getClient());
		} catch (Exception e) {
			CyLogger.getLogger().error(
					"Failed to register PSICQUIC Universal client.", e);
		}

	}
}
