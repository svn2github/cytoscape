package org.cytoscape.model; 

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.MavenConfiguredJUnit4TestRunner;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.integration.ServiceTestSupport;

//@RunWith(MavenConfiguredJUnit4TestRunner.class)
@RunWith(JUnit4TestRunner.class)
public class ServiceConfigurationTest extends ServiceTestSupport {

	@Configuration
	public static Option[] configuration() {      
		return options(felix(), 
		               profile("spring.dm"), 
		               provision( 
		                         mavenBundle().groupId("org.cytoscape").artifactId("model-impl").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("model-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("event-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("event-impl").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("integration-test-support").versionAsInProject()
		));
	}  

	@Test
	public void testExpectedServices() {
		checkService(CyNetworkFactory.class);
		checkService(CyTableFactory.class);
		checkService(CyRootNetworkFactory.class);
		checkService(CyTableManager.class);
		checkService(CyNetworkManager.class);
	}
}
