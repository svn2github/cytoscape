
package cytoscape.events;

import cytoscape.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.view.GraphView;

/**
 * 
 */
public interface SetCurrentNetworkViewEvent extends CyEvent<CyNetworkManager> {
	GraphView getNetworkView();	
}
