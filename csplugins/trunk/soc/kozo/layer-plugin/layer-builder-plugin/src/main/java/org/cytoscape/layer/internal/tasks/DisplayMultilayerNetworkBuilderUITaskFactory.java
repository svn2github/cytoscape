package org.cytoscape.layer.internal.tasks;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

import org.cytoscape.session.CyNetworkManager;
import cytoscape.view.CySwingApplication;

public class DisplayMultilayerNetworkBuilderUITaskFactory implements TaskFactory {
	
	private CyNetworkManager netmgr;
	private CySwingApplication desktop;

	public DisplayMultilayerNetworkBuilderUITaskFactory(CySwingApplication desktop, CyNetworkManager netmgr) {
		this.netmgr = netmgr;
	}

	public Task getTask() {
		return new DisplayMultilayerNetworkBuilderUITask(desktop, netmgr);
	}
}
