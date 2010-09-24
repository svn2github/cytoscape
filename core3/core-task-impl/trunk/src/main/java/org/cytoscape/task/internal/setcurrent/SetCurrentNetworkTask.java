package org.cytoscape.task.internal.setcurrent;


import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


/**
 * This class exists for (possible) use in headless mode.  The associated 
 * TaskFactory should not be registered in Swing mode, since this task doesn't 
 * make sense in GUI mode.
 */
public class SetCurrentNetworkTask extends AbstractTask {
	CyNetworkManager netmgr;
	TaskMonitor taskMonitor;
	
	public SetCurrentNetworkTask(CyNetworkManager netmgr) {
		this.netmgr = netmgr;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Verify that we want an essentially random network and that this
		// task shouldn't be NetworkTask instead.
		this.taskMonitor = taskMonitor;
		Object[] setNetworks = netmgr.getNetworkSet().toArray();
		netmgr.setCurrentNetwork(((CyNetwork) setNetworks[setNetworks.length-1]).getSUID());
	}

	public void cancel() {
	}
}
