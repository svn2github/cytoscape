package org.cytoscape.task.internal.loadnetwork;


import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


public class SetCurrentNetworkTaskFactoryImpl implements TaskFactory {

	private CyNetworkManager netmgr;


	public SetCurrentNetworkTaskFactoryImpl(CyNetworkManager netmgr) {
		this.netmgr = netmgr;
	}


	public TaskIterator getTaskIterator() {
		return new TaskIterator(new SetCurrentNetworkTask(netmgr));
	}
}
