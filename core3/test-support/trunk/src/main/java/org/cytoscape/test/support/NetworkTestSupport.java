
package org.cytoscape.test.support;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.StubTableManager;
import org.cytoscape.model.CyTableManager;

import org.cytoscape.model.internal.CyNetworkFactoryImpl;

public class NetworkTestSupport {

	protected CyNetworkFactory networkFactory;
	protected CyEventHelper eventHelper;
	protected CyTableManager tableMgr;

	public NetworkTestSupport() {
		eventHelper = new StubEventHelper();
		tableMgr = new StubTableManager();
		networkFactory = new CyNetworkFactoryImpl( eventHelper, tableMgr );
	}

	public CyNetwork getNetwork() {
		return networkFactory.getInstance();
	}
}


