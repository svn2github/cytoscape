package org.cytoscape.layer.internal.tasks;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

import org.cytoscape.layer.MultiLayerNetworkBuilder;
import org.cytoscape.session.CyNetworkManager;
import cytoscape.view.CySwingApplication;

public class DisplayMultilayerNetworkBuilderUITaskFactory implements TaskFactory {
	
	private CyNetworkManager netmgr;
	private CySwingApplication desktop;
	private MultiLayerNetworkBuilder builder;

	public DisplayMultilayerNetworkBuilderUITaskFactory(CySwingApplication desktop, CyNetworkManager netmgr, MultiLayerNetworkBuilder builder) {
		this.netmgr = netmgr;
		this.desktop = desktop;
		this.builder = builder;
	}

	public Task getTask() {
		return new DisplayMultilayerNetworkBuilderUITask(desktop, netmgr, builder);
	}
}
