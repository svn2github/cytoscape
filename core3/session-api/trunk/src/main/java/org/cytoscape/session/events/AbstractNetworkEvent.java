package org.cytoscape.session.events;


import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.model.CyNetwork;


/**
 *  Base class for all derived concrete event classes classes in this package that require a CyNetwork.
 */
class AbstractNetworkEvent extends AbstractCyEvent<CyApplicationManager> {
	private final CyNetwork net;

	AbstractNetworkEvent(final CyApplicationManager source, final Class listenerClass, final CyNetwork net) {
		super(source, listenerClass);

		if (net == null)
			throw new NullPointerException("the \"net\" parameter must never be null!");
		this.net = net;
	}

	public final CyNetwork getNetwork() {
		return net;
	}
}
