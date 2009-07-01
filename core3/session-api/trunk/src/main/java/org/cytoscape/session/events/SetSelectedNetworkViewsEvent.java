
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.CyEvent;
import org.cytoscape.view.model.CyNetworkView;

import java.util.List;

/**
 * 
 */
public interface SetSelectedNetworkViewsEvent extends CyEvent<CyNetworkManager> {
	List<CyNetworkView> getNetworkViews();
}
