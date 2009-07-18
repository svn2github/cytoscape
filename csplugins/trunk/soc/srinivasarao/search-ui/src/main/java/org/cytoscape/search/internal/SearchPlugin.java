package org.cytoscape.search.internal;

import javax.swing.SwingConstants;

import org.cytoscape.session.CyNetworkManager;

import cytoscape.view.CySwingApplication;
import cytoscape.view.CytoPanel;


public class SearchPlugin {
	
		private CySwingApplication desktop;
		private CyNetworkManager netmgr;
		CytoPanel cp;
		public SearchPlugin(CySwingApplication desk,CyNetworkManager nm){
			this.desktop = desk;
			this.netmgr = nm;
			initPanel();
		}
		public void initPanel(){
			SearchPanel esp = SearchPanelFactory.getGlobalInstance(netmgr);
			desktop.getCytoPanel(SwingConstants.WEST).add(esp);
			
		}
	
}
