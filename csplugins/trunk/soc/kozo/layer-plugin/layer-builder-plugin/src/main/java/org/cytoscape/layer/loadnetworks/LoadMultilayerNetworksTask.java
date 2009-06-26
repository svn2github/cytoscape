package org.cytoscape.layer.loadnetworks;

import static org.cytoscape.io.DataCategory.NETWORK;

import java.io.File;
import java.util.Properties;

import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.layer.loadnetworks.panel.LoadMultilayerNetworkTaskPanel;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.Tunable.Param;

import cytoscape.CyNetworkManager;
import cytoscape.util.CyNetworkNaming;

public class LoadMultilayerNetworksTask extends AbstractLoadMultilayerNetworksTask {
	
	public LoadMultilayerNetworksTask(CyNetworkManager netmgr) {
		
	}

	/**
	 * Executes Task.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		LoadMultilayerNetworkTaskPanel panel = new LoadMultilayerNetworkTaskPanel();
	}
}
