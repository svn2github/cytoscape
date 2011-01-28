
package org.cytoscape.view.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for {@linkplain AddedNodeViewEvent}.
 *
 */
public interface AddedNodeViewListener extends CyListener {
	
	/**
	 * Process event.
	 * 
	 * @param e
	 */
	public void handleEvent(AddedNodeViewEvent e);
}
