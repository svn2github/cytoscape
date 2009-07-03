package org.cytoscape.layer.internal.tasks;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.session.CyNetworkManager;

public class BuildMultilayerNetworkTask implements Task {
	private CyNetworkManager manager;
	private TaskMonitor taskMonitor;

	// private LayerFactory lFactory;

	public BuildMultilayerNetworkTask(CyNetworkManager manager) {
		this.manager = manager;
	}

	public void cancel() {

	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		this.taskMonitor = taskMonitor;
		taskMonitor.setProgress(-1.0);
		taskMonitor.setStatusMessage("Creating Multi Layer Network...");

		// lFactory.addLayer()
	}

}
