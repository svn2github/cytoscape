
package org.cytoscape.test.support;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.StubTableManager;
import org.cytoscape.model.CyTableManager;

import org.cytoscape.model.internal.CyNetworkFactoryImpl;
import org.cytoscape.model.internal.CyDataTableFactoryImpl;

public class NetworkTestSupport {

	protected CyNetworkFactory networkFactory;
	protected CyEventHelper eventHelper;
	protected CyTableManager tableMgr;

	public NetworkTestSupport() {
		eventHelper = new DummyCyEventHelper();
		tableMgr = new StubTableManager();
		CyDataTableFactory tableFactory = new CyDataTableFactoryImpl(eventHelper);

		networkFactory = new CyNetworkFactoryImpl( eventHelper, tableMgr, tableFactory );
	}

	public CyNetwork getNetwork() {
		return networkFactory.getInstance();
	}

	public CyNetworkFactory getNetworkFactory() {
		return networkFactory;	
	}
}


