package org.cytoscape.view.ui.networkpanel;

import javax.swing.SwingConstants;

import org.cytoscape.view.ui.networkpanel.internal.NetworkBrowserImpl;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import com.vlsolutions.swing.docking.DockingDesktop;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.cytopanels.CytoPanel;

public class NetworkBrowserPlugin extends CytoscapePlugin {
	
	private NetworkBrowserImpl browser;
	private DockingDesktop desk;
	private DefaultTreeTableModel model;
	
	public NetworkBrowserPlugin() {
		
		model = new DefaultTreeTableModel();
		browser = new NetworkBrowserImpl(model);
		//desk = new DockingDesktop();
		//desk.addDockable(browser);
		final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
		cytoPanel.add("Network Browser", browser);
	}

}
