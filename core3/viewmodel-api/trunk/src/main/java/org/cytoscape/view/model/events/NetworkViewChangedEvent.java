package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;

/**
 * If something had been changed in the view model, presentation layer should listening to the change and update itself.
 * This event will be used in such objects in the presentation layer. 
 * 
 * @author kono
 *
 */
public final class NetworkViewChangedEvent extends AbstractCyEvent<CyNetworkView> {
	
	public NetworkViewChangedEvent(final CyNetworkView source) {
		super(source, NetworkViewChangedListener.class);
	}
}
