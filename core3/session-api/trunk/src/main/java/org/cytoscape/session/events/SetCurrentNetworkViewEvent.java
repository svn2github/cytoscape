package org.cytoscape.session.events;


import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;


/**
 * 
 */
public final class SetCurrentNetworkViewEvent extends AbstractNetworkViewEvent {
	public SetCurrentNetworkViewEvent(final CyApplicationManager source, final CyNetworkView view) {
		super(source, SetCurrentNetworkViewListener.class, view);
	}
}
