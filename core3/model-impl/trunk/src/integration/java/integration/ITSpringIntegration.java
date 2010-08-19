
package integration; 

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;

import org.cytoscape.integration.AbstractIntegrationTester;

/**
 * Integration test for model-impl bundle.
 * 
 * @author kono
 * @author mes
 */
public class ITSpringIntegration extends AbstractIntegrationTester {

	public ITSpringIntegration() {
		super( "org.cytoscape.model-impl",
		       new String[] { "org.cytoscape, event-api, 1.0-SNAPSHOT",
		                      "org.cytoscape, event-impl, 1.0-SNAPSHOT",
		                      "org.cytoscape, model-api, 1.0-SNAPSHOT", 
		                      "org.cytoscape, model-impl, 1.0-SNAPSHOT",
		                      "org.cytoscape, integration-test-support, 1.0-SNAPSHOT" ,
							  },
		       new String[] { "cyNetworkFactory", "cyDataTableFactory", 
			                  "cyRootNetworkFactory", "cyTableManager" },
		       new Class[] { CyNetworkFactory.class, CyDataTableFactory.class, 
			                 CyRootNetworkFactory.class, CyTableManager.class },
		       new String[] { "org.cytoscape.model", "org.cytoscape.model.subnetwork" }
			   );
	}
}
