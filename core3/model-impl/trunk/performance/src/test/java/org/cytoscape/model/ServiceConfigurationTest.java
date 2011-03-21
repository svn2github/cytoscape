package org.cytoscape.model; 


import org.easymock.EasyMock;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.MavenConfiguredJUnit4TestRunner;
import org.osgi.util.tracker.ServiceTracker;

import org.cytoscape.event.CyEvent;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.equations.Interpreter;
import org.cytoscape.integration.ServiceTestSupport;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.RowCreatedMicroListener;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.service.util.CyServiceRegistrar;


@RunWith(MavenConfiguredJUnit4TestRunner.class)
public class ServiceConfigurationTest extends ServiceTestSupport {
	@Before 
	public void setup() {
		registerMockService(Interpreter.class);
		registerMockService(CyServiceRegistrar.class);
	}

	@Test
	public void testAddNode() {
		final ServiceTracker tracker =
			new ServiceTracker(bundleContext, CyNetworkFactory.class.getName(), null);
		tracker.open();

		// Obtain a CyNetworkFactory service:
		CyNetworkFactory factory = null;
		try {
			final int WAIT_TIME = 2000; // seconds
			factory = (CyNetworkFactory)tracker.waitForService(WAIT_TIME);
		} catch (final InterruptedException ie) {
			fail("Did not get an instance of a CyNetworkFactory service within the specified amount of time!");
		}
		assertNotNull(factory);

		final CyNetwork network = factory.getInstance();
		assertNotNull(network);
	}
}
