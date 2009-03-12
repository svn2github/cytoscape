package org.cytoscape.webservice.psicquic;

import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;

public class PSICQUICUniversalClientPlugin extends CytoscapePlugin {

	public PSICQUICUniversalClientPlugin() {
		
		try {
			WebServiceClientManager.registerClient(PSICQUICUniversalClient.getClient());
		} catch (Exception e) {
			CyLogger.getLogger().error("Failed to register PSICQUIC client.", e);
		}
		
	}
}
