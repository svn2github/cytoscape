
package integration; 

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.integration.AbstractIntegrationTester;

/**
 * Integration test for model-impl bundle.
 * 
 * @author kono
 * @author mes
 */
public class ITSpringIntegration extends AbstractIntegrationTester {

	public ITSpringIntegration() {
		super( "org.cytoscape.event-impl",
		       new String[] { "org.cytoscape, event-api, 1.0-SNAPSHOT",
		                      "org.cytoscape, event-impl, 1.0-SNAPSHOT" ,
		                      "org.cytoscape, integration-test-support, 1.0-SNAPSHOT" ,
							  },
		       new String[] { "cyEventHelper" },
		       new Class[] { CyEventHelper.class },
		       new String[] { "org.cytoscape.event" }
			   );
	}
}
