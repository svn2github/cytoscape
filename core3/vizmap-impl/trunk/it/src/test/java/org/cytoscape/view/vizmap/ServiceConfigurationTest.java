package org.cytoscape.view.vizmap; 

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.MavenConfiguredJUnit4TestRunner;

import org.cytoscape.integration.ServiceTestSupport;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;

import java.util.Properties;

@RunWith(MavenConfiguredJUnit4TestRunner.class)
public class ServiceConfigurationTest extends ServiceTestSupport {

	@Before
	public void setup() {
		registerMockService(CyEventHelper.class);
		registerMockService(RenderingEngineManager.class);

		Properties p1 = new Properties();
		p1.setProperty("mapping.type","discrete");
		registerMockService(VisualMappingFunctionFactory.class, p1);

		Properties p2 = new Properties();
		p2.setProperty("mapping.type","continuous");
		registerMockService(VisualMappingFunctionFactory.class, p2);

		Properties p3 = new Properties();
		p3.setProperty("mapping.type","passthrough"); 
		registerMockService(VisualMappingFunctionFactory.class, p3);
	}

	@Test
	public void testExpectedServices() {
		checkService(VisualMappingManager.class);
		checkService(VisualStyleFactory.class);
		checkService(VisualStyleSerializer.class);
	}
}
