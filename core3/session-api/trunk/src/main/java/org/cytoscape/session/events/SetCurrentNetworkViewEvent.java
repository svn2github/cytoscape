
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.view.model.CyNetworkView;

/**
 * 
 */
public interface SetCurrentNetworkViewEvent extends CyEvent<CyNetworkManager> {
	CyNetworkView getNetworkView();	
}
