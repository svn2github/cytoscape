
package org.cytoscape.session.events;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyNetwork;
/**
 * 
 */
public final class SetCurrentNetworkEvent extends AbstractNetworkEvent {
	public SetCurrentNetworkEvent(final CyNetworkManager source, final CyNetwork net) {
		super(source, SetCurrentNetworkListener.class, net);
	}
}
