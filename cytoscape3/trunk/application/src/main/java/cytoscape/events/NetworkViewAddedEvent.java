
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.view.model.CyNetworkView;
/**
 * 
 */
public interface NetworkViewAddedEvent extends CyEvent<CyNetworkManager> {
	CyNetworkView getNetworkView();
}
