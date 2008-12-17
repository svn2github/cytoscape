
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.model.CyNetwork;
/**
 * 
 */
public interface SetCurrentNetworkEvent extends CyEvent<CyNetworkManager> {
	CyNetwork getNetwork();
}
