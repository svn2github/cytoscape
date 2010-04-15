

package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;

/**
 * 
 */
class AbstractNetworkViewEvent extends AbstractCyEvent<CyNetworkManager> {
	private final CyNetworkView view;
	AbstractNetworkViewEvent(final CyNetworkManager source, final Class listenerClass, final CyNetworkView view) {
		super(source, listenerClass);
		this.view = view;
	}

	public CyNetworkView getNetworkView() {
		return view;
	}
}
