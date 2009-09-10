package org.cytoscape.task.internal.loadnetwork;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;



public class SetCurrentNetworkTaskFactoryImpl implements TaskFactory {

	private CyNetworkManager netmgr;


	public SetCurrentNetworkTaskFactoryImpl(CyNetworkManager netmgr) {
		this.netmgr = netmgr;
	}


	public Task getTask() {
		return new SetCurrentNetworkTask(netmgr);
	}
}
