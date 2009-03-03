
package cytoscape.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface SetCurrentNetworkViewListener extends CyListener {
	public void handleEvent(SetCurrentNetworkViewEvent e);
}
