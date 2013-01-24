package org.cytoscape.integration;

/*
 * #%L
 * org.cytoscape.integration-test-support
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Dictionary;
import java.util.Properties;

import org.ops4j.pax.exam.Inject;
import org.osgi.framework.BundleContext;
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
	 * Registers a mock service of type clazz with the OSGi service registry. 
	 * This is meant to be used to provide prerequisite services for testing
	 * purposes. Will register the service with an empty Properties object. 
	 * @param clazz The class type describing the type of mock service to register.
	 */
	protected void registerMockService(Class<?> clazz) {
		registerMockService(clazz, new Properties());
	}

	/**
	 * Registers a mock service of type clazz with the OSGi service registry. 
	 * This is meant to be used to provide prerequisite services for testing
	 * purposes.
	 * @param clazz The class type describing the type of mock service to register.
	 * @param d The service properties. 
	 */
	protected void registerMockService(Class<?> clazz, Dictionary d) {
		bundleContext.registerService(clazz.getName(), createMock(clazz), d);
	}

	/**
	 * Registers a mock service of type clazz with the OSGi service registry. 
	 * This is meant to be used to provide prerequisite services for testing
	 * purposes. Will register the service with an empty Properties object. 
	 * @param clazz The class type describing the type of mock service to register.
	 */
	protected void registerMockService(final Class<?> clazz, final Object mockObject) {
		registerMockService(clazz, mockObject, new Properties());
	}

	/**
	 * Registers a mock service of type clazz with the OSGi service registry. 
	 * This is meant to be used to provide prerequisite services for testing
	 * purposes.
	 * @param clazz The class type describing the type of mock service to register.
	 * @param d The service properties. 
	 */
	protected void registerMockService(final Class<?> clazz, final Object mockObject,
					   final Dictionary d)
	{
		if (!clazz.isAssignableFrom(mockObject.getClass()))
			throw new IllegalArgumentException("the mock object is an instance of the wrong class.");
		bundleContext.registerService(clazz.getName(), mockObject, d);
	}

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
