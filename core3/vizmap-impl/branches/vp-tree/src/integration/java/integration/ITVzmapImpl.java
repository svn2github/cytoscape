package integration;

import org.cytoscape.integration.AbstractIntegrationTester;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;

public class ITVzmapImpl extends AbstractIntegrationTester {

	public ITVzmapImpl() {
		super("org.cytoscape.view.vizmap-impl", 
				
				new String[] {
				"org.cytoscape, event-api, 1.0-SNAPSHOT",
				"org.cytoscape, event-impl, 1.0-SNAPSHOT",
				"org.cytoscape, model-api, 1.0-SNAPSHOT",
				"org.cytoscape, integration-test-support, 1.0-SNAPSHOT",
				"org.cytoscape, service-util, 1.0-SNAPSHOT",
				"org.cytoscape, viewmodel-api, 1.0-SNAPSHOT",
				"org.cytoscape, viewmodel-impl, 1.0-SNAPSHOT",
				"org.cytoscape, vizmap-api, 1.0-SNAPSHOT",
				"org.cytoscape, vizmap-impl, 1.0-SNAPSHOT",
				"org.cytoscape, presentation-api, 1.0-SNAPSHOT"
				
				},
				new String[] { "visualStyleFactory", "visualMappingManager" },
				new Class[] { VisualStyleFactory.class, VisualMappingManager.class },
				new String[] { });
	}

}
