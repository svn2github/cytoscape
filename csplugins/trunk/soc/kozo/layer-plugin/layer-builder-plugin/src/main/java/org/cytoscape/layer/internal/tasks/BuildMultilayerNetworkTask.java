package org.cytoscape.layer.internal.tasks;

import org.cytoscape.layer.MultiLayerNetworkBuilder;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

/**
 * Build multi layer task here.
 * 
 * @author kozo
 */

public class BuildMultilayerNetworkTask implements Task {

	private CyNetworkManager manager;
	private TaskMonitor taskMonitor;
	private MultiLayerNetworkBuilder builder;

	public BuildMultilayerNetworkTask(CyNetworkManager manager,
			MultiLayerNetworkBuilder builder) {
		this.manager = manager;
		this.builder = builder;
	}

	public void cancel() {

	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		this.taskMonitor = taskMonitor;
		taskMonitor.setProgress(-1.0);
		taskMonitor.setStatusMessage("Creating Multi Layer Network...");

	}

}
