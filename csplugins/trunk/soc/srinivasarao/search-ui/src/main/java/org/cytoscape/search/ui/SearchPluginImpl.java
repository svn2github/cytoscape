package org.cytoscape.search.ui;

import javax.swing.SwingConstants;

import org.cytoscape.session.CyNetworkManager;

import org.cytoscape.work.TaskManager;

import cytoscape.view.CySwingApplication;

public class SearchPluginImpl implements SearchPlugin {

	private CySwingApplication desktop;
	private CyNetworkManager netmgr;
	private TaskManager taskmanager;

	public SearchPluginImpl(CySwingApplication desk, CyNetworkManager nm,
			TaskManager tm) {
		this.desktop = desk;
		this.netmgr = nm;
		this.taskmanager = tm;
		initPanel();
		System.out.println("I am in SearchPlugin of search-ui");
	}

	public void initPanel() {

		SearchPanel esp = SearchPanelFactory.getGlobalInstance(netmgr);
		esp.setTaskManager(taskmanager);
		desktop.getCytoPanel(SwingConstants.WEST).add("Query Builder", esp);
	}
}
