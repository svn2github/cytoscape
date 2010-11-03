
package integration; 

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyTableFactory;
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
		       new String[] { "org.cytoscape, event-api, 3.0.0-alpha1",
		                      "org.cytoscape, model-api, 3.0.0-alpha1", 
		                      "org.cytoscape, model-impl, 3.0.0-alpha2-SNAPSHOT",
		                      "org.cytoscape, integration-test-support, 3.0.0-alpha2" ,
							  },
		       new String[] { "cyNetworkFactory", "cyTableFactory", 
			                  "cyRootNetworkFactory", "cyTableManager" },
		       new Class[] { CyNetworkFactory.class, CyTableFactory.class, 
			                 CyRootNetworkFactory.class, CyTableManager.class },
		       new String[] { "org.cytoscape.model", "org.cytoscape.model.subnetwork" }
			   );
	}
}
