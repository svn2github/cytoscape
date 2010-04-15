
package org.cytoscape.test.support;

import java.util.Dictionary;

import org.cytoscape.service.util.CyServiceRegistrar;


public class StubServiceRegistrar implements CyServiceRegistrar {

	public void registerService(Object o, Class c, Dictionary props) {}
	public void unregisterService(Object o, Class c) {}

	public void registerAllServices(Object o, Dictionary props) {}
	public void unregisterAllServices(Object o) {}
	
}


