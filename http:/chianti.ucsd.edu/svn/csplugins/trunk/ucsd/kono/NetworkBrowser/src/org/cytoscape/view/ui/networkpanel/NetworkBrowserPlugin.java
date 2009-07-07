package org.cytoscape.view.ui.networkpanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import org.cytoscape.view.ui.networkpanel.internal.NetworkBrowserImpl;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import com.vlsolutions.swing.docking.DockingDesktop;

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
	private DockingDesktop desk;
	private DefaultTreeTableModel model;
	
	public NetworkBrowserPlugin() {
		
		model = buildModel();
		browser = new NetworkBrowserImpl(model);
		//desk = new DockingDesktop();
		//desk.addDockable(browser);
		final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
		cytoPanel.add("Network Browser", browser);
	}
	
	public DefaultTreeTableModel buildModel() {
		DefaultTreeTableModel newModel = new DefaultTreeTableModel();
		final List<String> columnIDs = new ArrayList<String>();
		columnIDs.add("Network");
		columnIDs.add("Image");
		columnIDs.add("Nodes");
		columnIDs.add("Edges");
		
		newModel.setColumnIdentifiers(columnIDs);
		
		DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();
		root.setValueAt("Networks", 0);
		newModel.setRoot(root);
		return newModel;
	}
	

}
