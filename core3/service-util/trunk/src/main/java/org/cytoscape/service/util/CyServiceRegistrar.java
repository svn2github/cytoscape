
package org.cytoscape.service.util;

import java.util.Dictionary;

/** 
 * An interface to hide the OSGi dependencies needed to register 
 * services dynamically at runtime.  You should only use this interface
 * if you need to register services while running based on data not 
 * available at startup. 
 */
public interface CyServiceRegistrar {
	void registerService(Object o, Class c, Dictionary props);
	void unregisterService(Object o, Class c);
}
