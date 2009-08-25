package org.cytoscape.view.ui.networkpanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import org.cytoscape.view.ui.networkpanel.internal.NetworkBrowserImpl;
import org.cytoscape.view.ui.networkpanel.internal.NetworkTreeNode;
import org.cytoscape.view.ui.networkpanel.internal.NetworkTreeTableModel;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesImpl;
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

	// Attributes for groups
	private static CyAttributes groupAttributes = new CyAttributesImpl();

	public static CyAttributes getGroupAttributes() {
		return groupAttributes;
	}
	
	public NetworkBrowserPlugin() {
		
		model = buildModel();
		browser = new NetworkBrowserImpl(model);
		
		final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH);
		cytoPanel.add("Network/Group Browser", browser);
        
	}
	
	public NetworkTreeTableModel buildModel() {
		NetworkTreeNode root = new NetworkTreeNode("Networks", "root", 4);
		NetworkTreeTableModel newModel = new NetworkTreeTableModel(root);
		
		final List<String> columnIDs = new ArrayList<String>();
		columnIDs.add("Network");
		columnIDs.add("Nodes");
		columnIDs.add("Edges");
		columnIDs.add("Score");
		
		newModel.setColumnIdentifiers(columnIDs);
		
		
		return newModel;
	}
	

}
