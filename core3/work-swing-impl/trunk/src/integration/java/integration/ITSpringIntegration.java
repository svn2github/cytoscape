package integration; 


import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableHandlerFactory;
import org.cytoscape.work.swing.GUITaskManager;
import org.cytoscape.work.undo.UndoSupport;

import org.cytoscape.integration.AbstractIntegrationTester;


/**
 * Integration test for work-swing-impl bundle.
 * 
 * @author ruschein
 */
public class ITSpringIntegration extends AbstractIntegrationTester {

	public ITSpringIntegration() {
		super( "org.cytoscape.work-swing-impl",
		       new String[] { "org.cytoscape, work-api, 1.0-SNAPSHOT",
		                      "org.cytoscape, work-swing-api, 1.0-SNAPSHOT",
		                      "org.cytoscape, work-spring-hack, 1.0-SNAPSHOT",
		                      "org.cytoscape, property, 1.0-SNAPSHOT",
		                      "org.ops4j.pax.logging, pax-logging-api, 1.5.2",
		                      "org.cytoscape, work-swing-impl, 1.0-SNAPSHOT",
		                      "org.cytoscape, integration-test-support, 1.0-SNAPSHOT" ,
							  },
		       new String[] { "undoSupport", "swingTaskManager", "swingTaskManager", "guiHandlerFactory" },
		       new Class[] { UndoSupport.class, GUITaskManager.class, TaskManager.class, TunableHandlerFactory.class },
		       new String[] { }
		       );
	}
}
