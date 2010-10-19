package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;


/**
 * TODO: Is this necessary?
 * 
 * @author kono
 *
 */
public class NetworkViewCreatedEvent extends AbstractCyEvent<CyNetworkView> {

	public NetworkViewCreatedEvent(CyNetworkView source, Class listenerClass) {
		super(source, listenerClass);
		// TODO Auto-generated constructor stub
	}

}
