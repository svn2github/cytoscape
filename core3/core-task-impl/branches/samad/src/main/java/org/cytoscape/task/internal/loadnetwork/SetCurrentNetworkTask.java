package org.cytoscape.task.internal.loadnetwork;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;


public class SetCurrentNetworkTask implements Task{

	CyNetworkManager netmgr;
	TaskMonitor taskMonitor;
	
	public SetCurrentNetworkTask(CyNetworkManager netmgr) {
		this.netmgr = netmgr;
	}


	public void run(TaskMonitor taskMonitor) throws Exception {
		this.taskMonitor = taskMonitor;
		Object[] setNetworks = netmgr.getNetworkSet().toArray();
		netmgr.setCurrentNetwork(((CyNetwork) setNetworks[setNetworks.length-1]).getSUID());
		System.out.println("current network ID : " + netmgr.getCurrentNetwork().getSUID());
	}

	
	public void cancel() {
		// TODO Auto-generated method stub		
	}
}
