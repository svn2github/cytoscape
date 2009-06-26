package org.cytoscape.layer.creation;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cytoscape.CyNetworkManager;

public class CreateMultilayerNetworkTask implements Task{
	private CyNetworkManager manager;
	private TaskMonitor taskMonitor;
//	private LayerFactory lFactory;
	
	public CreateMultilayerNetworkTask(CyNetworkManager manager) {
		this.manager=manager;
//		this.lFactory=lFactory;
	}
	
	public void cancel() {
		
	}
	
	public void run(TaskMonitor taskMonitor) throws Exception {
		this.taskMonitor = taskMonitor;
		taskMonitor.setProgress(-1.0);
		taskMonitor.setStatusMessage("Creating Multi Layer Network...");
		
//		lFactory.addLayer()
	}

}
