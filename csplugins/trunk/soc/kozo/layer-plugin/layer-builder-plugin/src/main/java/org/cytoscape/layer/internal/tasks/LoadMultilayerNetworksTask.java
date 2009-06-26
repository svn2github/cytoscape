package org.cytoscape.layer.internal.tasks;

import static org.cytoscape.io.DataCategory.NETWORK;

import java.io.File;
import java.util.Properties;

import javax.swing.JDialog;

import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.layer.internal.ui.LoadMultilayerNetworkTaskPanel;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;

import cytoscape.CyNetworkManager;
import cytoscape.util.CyNetworkNaming;

public class LoadMultilayerNetworksTask implements Task {
	
	// This should be injected.
	private CyNetworkManager manager;
	
	public LoadMultilayerNetworksTask(CyNetworkManager manager) {
		this.manager = manager;
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		LoadMultilayerNetworkTaskPanel panel = new LoadMultilayerNetworkTaskPanel();
		
		JDialog dialog = new JDialog();
		dialog.setTitle("Multilayer Network Builder");
		dialog.setVisible(true);
	}

	public void cancel() {
		// TODO Auto-generated method stub
		
	}
}
