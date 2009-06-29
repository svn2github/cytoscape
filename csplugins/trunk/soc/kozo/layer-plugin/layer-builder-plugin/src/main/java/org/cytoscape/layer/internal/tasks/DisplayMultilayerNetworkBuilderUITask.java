package org.cytoscape.layer.internal.tasks;

import javax.swing.JDialog;

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
//		LoadMultilayerNetworkTaskPanel panel = new LoadMultilayerNetworkTaskPanel();
		
		JDialog dialog = new JDialog();
		dialog.setTitle("Multilayer Network Builder");
		dialog.setVisible(true);
	}

	public void cancel() {
		// TODO Auto-generated method stub
		
	}
}
