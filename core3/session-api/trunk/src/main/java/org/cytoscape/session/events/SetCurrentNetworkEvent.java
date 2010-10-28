package org.cytoscape.session.events;


import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.model.CyNetwork;


/**
 * An event signaling that the a network has been set to current.
 */
public final class SetCurrentNetworkEvent extends AbstractNetworkEvent {

	/**
	 * Constructor.
	 * @param source The application manager that is the source of the event.
	 * @param net The network that has been set to the current network.
	 */
	public SetCurrentNetworkEvent(final CyApplicationManager source, final CyNetwork net) {
		super(source, SetCurrentNetworkListener.class, net);
	}
}
