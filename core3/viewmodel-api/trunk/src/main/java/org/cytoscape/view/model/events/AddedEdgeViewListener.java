
package org.cytoscape.view.model.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for {@linkplain AddedEdgeViewEvent}.
 *
 */
public interface AddedEdgeViewListener extends CyListener {
	
	/**
	 * Process event
	 * 
	 * @param e
	 */
	public void handleEvent(AddedEdgeViewEvent e);

}
