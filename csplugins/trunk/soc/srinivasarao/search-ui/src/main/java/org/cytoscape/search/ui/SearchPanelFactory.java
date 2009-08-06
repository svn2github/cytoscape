package org.cytoscape.search.ui;

import org.cytoscape.session.CyNetworkManager;

public class SearchPanelFactory {
	private static SearchPanel sp = null;

	/**
	 * 
	 * @param netmgr
	 * @return
	 */
	public static SearchPanel getGlobalInstance(CyNetworkManager netmgr) {
		if (sp == null) {
			sp = new SearchPanelImpl(netmgr);
		}
		return sp;
	}
}
