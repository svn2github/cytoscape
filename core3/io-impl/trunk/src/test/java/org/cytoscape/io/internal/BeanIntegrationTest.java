package org.cytoscape.io.internal;

import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Integration test the bundle locally (outside of OSGi). Use AbstractOsgiTests
 * and a separate integration test project for testing inside of OSGi.
 */
@ContextConfiguration(locations = "META-INF/spring/bundle-context.xml")
public class BeanIntegrationTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private CyReaderManager manager;
	
	@Autowired
	private CyReader sifReader;


	@Before
	public void init() {
	}

	@Test
	public void testManager() {
		
	}
}
