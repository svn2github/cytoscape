package org.cytoscape.layer.internal.tasks;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

import cytoscape.CyNetworkManager;

public class DisplayMultilayerNetworkBuilderUITaskFactory implements TaskFactory {
	
	private CyNetworkManager netmgr;

	public DisplayMultilayerNetworkBuilderUITaskFactory(CyNetworkManager netmgr) {
		this.netmgr = netmgr;
	}

	public Task getTask() {
		return new DisplayMultilayerNetworkBuilderUITask(netmgr);
	}
}
