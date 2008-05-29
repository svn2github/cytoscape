
package org.cytoscape.event;

import java.util.List;

/**
 * The interface is intended to be used by the CyEventHelper
 * and just returns a list of event listeners of type T. 
 * This interface will likely be implmented once by a utility
 * class depending on how listeners are maintained.  The 
 * initial expectation is that there will be an OSGi implementation
 * that will search the Service Registry for Listeners.
 */
public interface ListenerProvider<T extends CyEventListener> {
	List<T> getListeners();
}
