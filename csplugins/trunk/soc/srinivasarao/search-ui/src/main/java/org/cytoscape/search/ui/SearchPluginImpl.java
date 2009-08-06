package org.cytoscape.search.ui;

import javax.swing.SwingConstants;

import org.cytoscape.session.CyNetworkManager;

import cytoscape.view.CySwingApplication;

public class SearchPluginImpl implements SearchPlugin{

	private CySwingApplication desktop;
	private CyNetworkManager netmgr;
	
	public SearchPluginImpl(CySwingApplication desk, CyNetworkManager nm) {
		this.desktop = desk;
		this.netmgr = nm;
		initPanel();
		System.out.println("I am in SearchPlugin of search-ui");
	}

	public void initPanel() {
		SearchPanel esp = SearchPanelFactory.getGlobalInstance(netmgr);
		desktop.getCytoPanel(SwingConstants.EAST).add(esp);
	}
}
