package org.cytoscape.view.ui.networkpanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import org.cytoscape.view.ui.networkpanel.internal.NetworkBrowserImpl;
import org.cytoscape.view.ui.networkpanel.internal.NetworkTreeNode;
import org.cytoscape.view.ui.networkpanel.internal.NetworkTreeTableModel;
import org.cytoscape.view.ui.networkpanel.internal.VLDockTest;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.cytopanels.CytoPanel;

/**
 * In 3.0, this setting will be done by Spring.
 * 
 * @author kono
 *
 */
public class NetworkBrowserPlugin extends CytoscapePlugin {
	
	private NetworkBrowserImpl browser;
	private NetworkTreeTableModel model;
	
	private VLDockTest frame;
	
	public NetworkBrowserPlugin() {
		
		model = buildModel();
		browser = new NetworkBrowserImpl(model);
		final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH);
		cytoPanel.add("Network/Group Browser", browser);
        
        System.out.println("--------Done!!!");
	}
	
	public NetworkTreeTableModel buildModel() {
		NetworkTreeNode root = new NetworkTreeNode("Networks", "root", 4);
		NetworkTreeTableModel newModel = new NetworkTreeTableModel(root);
		
		final List<String> columnIDs = new ArrayList<String>();
		columnIDs.add("Network");
		columnIDs.add("Nodes");
		columnIDs.add("Edges");
		columnIDs.add("Attribute 1");
		
		newModel.setColumnIdentifiers(columnIDs);
		
		
		return newModel;
	}
	

}
