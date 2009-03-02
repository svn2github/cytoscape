
package org.cytoscape.session;

import org.cytoscape.event.CyListener;

/**
 * 
 */
public interface SessionLoadedListener extends CyListener {
	public void handleEvent(SessionLoadedEvent e);
}
