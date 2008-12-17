
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.view.GraphView;
/**
 * 
 */
public interface NetworkViewAddedEvent extends CyEvent<CyNetworkManager> {
	GraphView getNetworkView();
}
