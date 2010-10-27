package org.cytoscape.session.events;


import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.model.CyNetwork;


/**
 * 
 */
public final class SetCurrentNetworkEvent extends AbstractNetworkEvent {
	public SetCurrentNetworkEvent(final CyApplicationManager source, final CyNetwork net) {
		super(source, SetCurrentNetworkListener.class, net);
	}
}
