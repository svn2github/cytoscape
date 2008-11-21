

package org.cytoscape.event;

/**
 * Some static utility methods that help you fire events. 
 */
public interface CyEventHelper {

	/**
	 * Calls each listener found in the Service Registry identified by
	 * the listenerClass and filter with the supplied event.
	 */
	public <E extends CyEvent, L extends CyEventListener> void fireSynchronousEvent( final E event, final Class<L> listener ); 

	/**
	 * Calls each listener found in the Service Registry identified by
	 * the listenerClass and filter with the supplied event in a new
	 * thread.
	 * <p>
	 * This method should <b>ONLY</b> ever be called with a thread safe event object!
	 */
	public <E extends CyEvent, L extends CyEventListener> void fireAsynchronousEvent( final E event, final Class<L> listener ); 
}
