package edu.ucsd.bioeng.idekerlab.keggwsc;

import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.plugin.CytoscapePlugin;

public class KEGGClientPlugin extends CytoscapePlugin {

	public KEGGClientPlugin() throws Exception {
		// Register this client to the manager.
		WebServiceClientManager.registerClient(KEGGClient.getClient());
	}
	
}
