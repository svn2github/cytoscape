package org.cytoscape.layer.loadnetworks;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

abstract class AbstractLoadMultilayerNetworksTask implements Task {
	public AbstractLoadMultilayerNetworksTask(){
		
	}
	
	protected void loadMultilayerNetworks() throws Exception {
			
	}
	
	abstract public void run(TaskMonitor taskMonitor) throws Exception;
	
	public void cancel() {
		
	}
}
