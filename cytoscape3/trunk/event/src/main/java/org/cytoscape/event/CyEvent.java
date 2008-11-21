
package org.cytoscape.event;

/**
 * All Cytoscape events should extend this interface.  The events
 * should add additional methods to provide access to the information
 * relevant to the event. 
 */
public interface CyEvent<T> {

	/**
	 * @return The object that fired the event.
	 */
	T getSource();
}
