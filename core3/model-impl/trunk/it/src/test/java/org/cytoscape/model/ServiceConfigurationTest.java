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
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.event.CyEventHelper;
import java.util.Properties; 
//import org.cytoscape.integration.ServiceTestSupport;

//@RunWith(MavenConfiguredJUnit4TestRunner.class)
@RunWith(JUnit4TestRunner.class)
public class ServiceConfigurationTest /*extends ServiceTestSupport*/ {

    /**
     * The OSGi BundleContext made available for additional testing.
     */
    @Inject
    protected BundleContext bundleContext;

    /**
     * Asserts that a service of the specified type exists.
     * @param clazz The service interface type to be checked. 
     */
    protected void checkService(Class<?> clazz) {
        checkService(clazz,1000);
    }

    /**
     * Asserts that a service of the specified type exists.
     * @param clazz The service interface type to be checked. 
     * @param waitTime The time the service tracker should wait to 
     * find the specified service in milliseconds.
     */
    protected void checkService(Class<?> clazz, int waitTime) {
        try {
            ServiceTracker tracker = new ServiceTracker(bundleContext, clazz.getName(), null);
            tracker.open();
            Object service = tracker.waitForService(waitTime);
            tracker.close();
            assertNotNull(service);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

	@Configuration
	public static Option[] configuration() {      
		return options(felix(), 
		               profile("spring.dm"), 
		               provision( 
		                         mavenBundle().groupId("org.cytoscape").artifactId("model-impl").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("model-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("event-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("event-impl").versionAsInProject()
		));
	}  

	@Test
	public void testExpectedServices() {
		for ( Bundle b : bundleContext.getBundles() )
			System.out.println("bundle: " + b.getSymbolicName());
		checkService(CyNetworkFactory.class);
		checkService(CyTableFactory.class);
		checkService(CyRootNetworkFactory.class);
		checkService(CyTableManager.class);
	}
}
