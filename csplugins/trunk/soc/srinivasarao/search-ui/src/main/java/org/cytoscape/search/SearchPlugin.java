package org.cytoscape.search;

import org.cytoscape.session.CyNetworkManager;

import cytoscape.view.CySwingApplication;

public abstract class SearchPlugin {

	protected CySwingApplication desktop;
	protected CyNetworkManager netmgr;

/*	public SearchPlugin(CySwingApplication desk, CyNetworkManager nm) {
		this.desktop = desk;
		this.netmgr = nm;
		initPanel();
		System.out.println("I am in SearchPlugin of search-ui");
	}
*/
	abstract public void initPanel() ;
}
