
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.view.model.CyNetworkView;
/**
 * 
 */
public interface NetworkViewAboutToBeDestroyedEvent extends CyEvent<CyNetworkManager> {
	CyNetworkView getNetworkView();
}
