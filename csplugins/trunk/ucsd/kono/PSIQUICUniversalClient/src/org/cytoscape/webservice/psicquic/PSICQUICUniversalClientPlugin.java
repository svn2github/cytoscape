package org.cytoscape.webservice.psicquic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
			
			final ExecutorService ex = Executors.newSingleThreadExecutor();
	        System.out.println("Initializatin process start in separate thread for PSICQUIC");
	        ex.execute(new InitTask());
			
		} catch (Exception e) {
			CyLogger.getLogger().error(
					"Failed to register PSICQUIC Universal client.", e);
		}
		
		

	}
	
	class InitTask implements Runnable {

		public void run() {
			WebServiceClientManager.registerClient(PSICQUICUniversalClient
					.getClient());
		}
		
	}
}
