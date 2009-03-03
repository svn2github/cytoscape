
package cytoscape.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface SetSelectedNetworksListener extends CyListener {
	public void handleEvent(SetSelectedNetworksEvent e);
}
