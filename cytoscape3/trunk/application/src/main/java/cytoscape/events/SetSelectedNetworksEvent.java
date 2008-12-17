
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.model.CyNetwork;

import java.util.List;

/**
 * 
 */
public interface SetSelectedNetworksEvent extends CyEvent<CyNetworkManager> {
	List<CyNetwork> getNetworks();
}
