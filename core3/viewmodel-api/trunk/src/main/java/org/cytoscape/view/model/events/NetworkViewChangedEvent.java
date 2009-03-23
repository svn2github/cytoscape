
package org.cytoscape.view.model.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.view.model.CyNetworkView;

/**
 * 
 */
public interface NetworkViewChangedEvent extends CyEvent<Object> {
	CyNetworkView getNetworkView();
}
