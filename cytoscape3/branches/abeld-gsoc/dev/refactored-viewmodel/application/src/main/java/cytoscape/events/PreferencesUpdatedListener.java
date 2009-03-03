
package cytoscape.events;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface PreferencesUpdatedListener extends CyListener {
	public void handleEvent(PreferencesUpdatedEvent e);
}
