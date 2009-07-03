package org.cytoscape.layer.internal.tasks;

import org.cytoscape.layer.internal.ui.LayerBuilderDialog;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.session.CyNetworkManager;
import cytoscape.view.CySwingApplication;

public class DisplayMultilayerNetworkBuilderUITask implements Task {
	
	// This should be injected.
	private CyNetworkManager manager;
	private CySwingApplication desktop;
	
	public DisplayMultilayerNetworkBuilderUITask(CySwingApplication desktop, CyNetworkManager manager) {
		this.manager = manager;
		this.desktop = desktop;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		LayerBuilderDialog dialog = new LayerBuilderDialog(desktop.getJFrame(), true, manager);
		dialog.setTitle("Multilayer Network Builder");
		dialog.setVisible(true);
	}

	public void cancel() {
		// TODO Auto-generated method stub
		
	}
}
