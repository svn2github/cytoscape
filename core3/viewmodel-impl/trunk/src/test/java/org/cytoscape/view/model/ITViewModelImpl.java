package org.cytoscape.view.model;

import org.cytoscape.integration.AbstractIntegrationTester;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.RootVisualLexicon;

/**
 * Integration test for viewmodel-impl bundle.
 * 
 * @author kono
 * 
 */
public class ITViewModelImpl extends AbstractIntegrationTester {

	public ITViewModelImpl() {
		super( "org.cytoscape.viewmodel-impl",
		       new String[] { "org.cytoscape, event-api, 1.0-SNAPSHOT",
		                      "org.cytoscape, event-impl, 1.0-SNAPSHOT",
		                      "org.cytoscape, model-api, 1.0-SNAPSHOT",
		                      "org.cytoscape, integration-test-support, 1.0-SNAPSHOT",
		                      "org.cytoscape, service-util, 1.0-SNAPSHOT",
		                      "org.cytoscape, viewmodel-api, 1.0-SNAPSHOT",
		                      "org.cytoscape, viewmodel-impl, 1.0-SNAPSHOT", },
		       new String[] { "rootVisualLexicon", "cyNetworkViewFactory" },
		       new Class[] { RootVisualLexicon.class, CyNetworkViewFactory.class } 
			   );
	}
}
