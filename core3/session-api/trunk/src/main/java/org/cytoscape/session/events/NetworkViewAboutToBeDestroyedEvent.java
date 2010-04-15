
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
/**
 * 
 */
public final class NetworkViewAboutToBeDestroyedEvent extends AbstractNetworkViewEvent {
	public NetworkViewAboutToBeDestroyedEvent(final CyNetworkManager source, final CyNetworkView view) {
		super(source, NetworkViewAboutToBeDestroyedListener.class, view);
	}
}
