
package cytoscape.events;

import org.cytoscape.event.CyEvent;

/**
 * An event fired immediately before Cytoscape will be shutdown. 
 */
public interface CytoscapeShutdownEvent extends CyEvent<Object> {

	/**
	 * A callback to the firing class that allows a listener to
	 * abort the shutdown.  This can cause conflicts if abused.
	 * @param why A user comprehensible message describing why the shutdown
	 * was aborted.
	 */
	void abortShutdown(String why);
}
