package org.cytoscape.layer.creation;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cytoscape.CyNetworkManager;

public abstract class AbstractCreateMultilayerNetwork implements Task{
	
	protected CyNetworkManager netmgr;
	
	public AbstractCreateMultilayerNetwork(CyNetworkManager netmgr){
		this.netmgr=netmgr;
	}
	public abstract void run(TaskMonitor monitor) throws Exception;
	
	public void cancel() {}
}
