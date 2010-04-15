
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
/**
 * 
 */
public final class NetworkViewAddedEvent extends AbstractNetworkViewEvent {
	public NetworkViewAddedEvent(final CyNetworkManager source, final CyNetworkView view) {
		super(source, NetworkViewAddedListener.class, view);
	}
}
