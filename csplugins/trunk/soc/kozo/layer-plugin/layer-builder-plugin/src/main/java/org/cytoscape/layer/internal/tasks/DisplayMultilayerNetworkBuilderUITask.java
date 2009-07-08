package org.cytoscape.layer.internal.tasks;

import java.util.Set;

import org.cytoscape.layer.MultiLayerNetworkBuilder;
import org.cytoscape.layer.internal.ui.LayerBuilderDialog;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.session.CyNetworkManager;
import cytoscape.view.CySwingApplication;
import org.cytoscape.model.CyNetwork;

public class DisplayMultilayerNetworkBuilderUITask implements Task {

	// This should be injected.
	private CyNetworkManager manager;
	private CySwingApplication desktop;
	private MultiLayerNetworkBuilder builder;

	private TaskMonitor taskMonitor;

	public DisplayMultilayerNetworkBuilderUITask(CySwingApplication desktop,
			CyNetworkManager manager, MultiLayerNetworkBuilder builder) {
		this.manager = manager;
		this.desktop = desktop;
		this.builder = builder;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {

		this.taskMonitor = taskMonitor;
		this.taskMonitor.setProgress(-1.0);
		this.taskMonitor.setStatusMessage("Building MultiLayer Network...");

		final Set<CyNetwork> targetNetworks = manager.getNetworkSet();
		System.out
				.println("* Show Dialog for Building MultiLayer Network for: "
						+ targetNetworks.size());

		System.out.println("Desktop = " + desktop);
		LayerBuilderDialog dialog = new LayerBuilderDialog(desktop.getJFrame(),
				true, manager, targetNetworks, builder);
		dialog.setTitle("Multilayer Network Builder");
		dialog.setVisible(true);
	}

	public void cancel() {
		// TODO Auto-generated method stub

	}
}
