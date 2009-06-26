package org.cytoscape.layer.loadnetworks;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

import cytoscape.CyNetworkManager;

public class LoadMultilayerNetworksTaskFactoryImpl implements TaskFactory {
	
	private CyNetworkManager netmgr;

	public LoadMultilayerNetworksTaskFactoryImpl(CyNetworkManager netmgr) {
		this.netmgr = netmgr;
	}

	public Task getTask() {
		return new LoadMultilayerNetworksTask(netmgr);
	}
}
