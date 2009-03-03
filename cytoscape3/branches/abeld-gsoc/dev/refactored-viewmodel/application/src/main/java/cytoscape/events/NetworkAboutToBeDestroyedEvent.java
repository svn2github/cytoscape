
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public interface NetworkAboutToBeDestroyedEvent extends CyEvent<CyNetworkManager> {
	CyNetwork getNetwork();
}
