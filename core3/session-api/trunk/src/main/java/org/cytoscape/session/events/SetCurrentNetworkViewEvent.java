
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;

/**
 * 
 */
public final class SetCurrentNetworkViewEvent extends AbstractNetworkViewEvent {
	public SetCurrentNetworkViewEvent(final CyNetworkManager source, final CyNetworkView view) {
		super(source, SetCurrentNetworkViewListener.class, view);
	}
}
