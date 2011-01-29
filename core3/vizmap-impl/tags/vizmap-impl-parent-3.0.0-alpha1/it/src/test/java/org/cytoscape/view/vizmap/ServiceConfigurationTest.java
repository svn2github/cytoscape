package org.cytoscape.view.vizmap; 

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.MavenConfiguredJUnit4TestRunner;

import org.cytoscape.integration.ServiceTestSupport;

import org.osgi.framework.Bundle;

@RunWith(MavenConfiguredJUnit4TestRunner.class)
public class ServiceConfigurationTest extends ServiceTestSupport {

	@Test
	public void testExpectedServices() {
		for ( Bundle b : bundleContext.getBundles() )
			System.out.println("XXXXXXXXXX bundle: " + b.getSymbolicName());
		checkService(VisualMappingManager.class);
		checkService(VisualStyleFactory.class);
		checkService(VisualStyleSerializer.class);
	}
}
