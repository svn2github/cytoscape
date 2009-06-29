package org.cytoscape.layer.internal.tasks;

import org.cytoscape.layer.internal.ui.LoadMultilayerNetworkTaskPanel;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cytoscape.CyNetworkManager;

public class DisplayMultilayerNetworkBuilderUITask implements Task {
	
	// This should be injected.
	private CyNetworkManager manager;
	
	public DisplayMultilayerNetworkBuilderUITask(CyNetworkManager manager) {
		this.manager = manager;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		LoadMultilayerNetworkTaskPanel panel = new LoadMultilayerNetworkTaskPanel();
		panel.setTitle("Multilayer Network Builder");
		panel.setVisible(true);
	}

	public void cancel() {
		// TODO Auto-generated method stub
		
	}
}
