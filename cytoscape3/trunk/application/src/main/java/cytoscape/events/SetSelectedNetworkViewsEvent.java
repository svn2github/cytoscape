
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.view.GraphView;

import java.util.List;

/**
 * 
 */
public interface SetSelectedNetworkViewsEvent extends CyEvent<CyNetworkManager> {
	List<GraphView> getNetworkViews();
}
