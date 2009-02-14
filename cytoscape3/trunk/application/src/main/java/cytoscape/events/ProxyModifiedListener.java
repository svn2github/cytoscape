
package cytoscape.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface ProxyModifiedListener extends CyListener {
	public void handleEvent(ProxyModifiedEvent e);
}
