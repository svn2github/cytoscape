
package org.cytoscape.session;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface SessionSavedListener extends CyListener {
	public void handleEvent(SessionSavedEvent e);
}
