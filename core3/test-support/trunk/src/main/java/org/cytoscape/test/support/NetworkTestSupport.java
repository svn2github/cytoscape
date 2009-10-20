
package org.cytoscape.test.support;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;

import org.cytoscape.model.internal.CyNetworkFactoryImpl;

public class NetworkTestSupport {

	protected CyNetworkFactory networkFactory;
	protected CyEventHelper eventHelper;

	public NetworkTestSupport() {
		eventHelper = new StubEventHelper();
		networkFactory = new CyNetworkFactoryImpl( eventHelper );
	}

	public CyNetwork getNetwork() {
		return networkFactory.getInstance();
	}
}


