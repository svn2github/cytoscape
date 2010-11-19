package org.cytoscape.integration; 

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import org.junit.Test;
import org.junit.Before;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A Pax Exam support class that provides methods useful for verifying 
 * the existance of OSGi services.
 */
public abstract class ServiceTestSupport {

	/**
	 * The OSGi BundleContext made available for additional testing.
	 */
	@Inject
	protected BundleContext bundleContext;

	/**
	 * Asserts that a service of the specified type exists. Uses
	 * a wait time of 1 second.
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
}
