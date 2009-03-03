
package cytoscape.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface SetSelectedNetworkViewsListener extends CyListener {
	public void handleEvent(SetSelectedNetworkViewsEvent e);
}
