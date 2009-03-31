
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.view.model.CyNetworkView;

import java.util.List;

/**
 * 
 */
public interface SetSelectedNetworkViewsEvent extends CyEvent<CyNetworkManager> {
	List<CyNetworkView> getNetworkViews();
}
