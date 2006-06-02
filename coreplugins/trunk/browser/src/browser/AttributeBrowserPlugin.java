package browser;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

/**
 * @author kono
 * 
 * This Plugin is an attribute browser with smaller GUI. All functions are based
 * on Rowan's code.
 */
public class AttributeBrowserPlugin extends CytoscapePlugin {

	public static Color DEFAULT_NODE_COLOR = Color.YELLOW;
	public static Color DEFAULT_EDGE_COLOR = Color.RED;

	/**
	 * 
	 *
	 */
	public AttributeBrowserPlugin() {

		initialize();

		JCheckBoxMenuItem switchBrowserView = new JCheckBoxMenuItem(
				new DisplayNetworkPanelAction());

		JCheckBoxMenuItem switchNetworkTreeView = new JCheckBoxMenuItem(
				new DisplayAttributeBrowserAction());

		JCheckBoxMenuItem switchAdvancedView = new JCheckBoxMenuItem(
				new DisplayAdvancedWindowAction());
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("View").add(
				switchNetworkTreeView, 0);
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("View").add(
				switchBrowserView, 0);
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("View").add(
				switchAdvancedView, 0);
		
		switchBrowserView.setSelected(true);
		switchNetworkTreeView.setSelected(true);

		switchNetworkTreeView.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F5, 0));

	}
	
	// Call 3 tables, nodes, edges and network.
	// The DataTable class actually creates all CytoPanels.
	// For this version of browser, it creates Cytopanel3 only.
	// Filter functions are implemented as an Advanced Window.
	protected void initialize() {

		DataTable table_nodes = new DataTable(Cytoscape.getNodeAttributes(),
				DataTable.NODES);
		DataTable table_edges = new DataTable(Cytoscape.getEdgeAttributes(),
				DataTable.EDGES);
		DataTable table_network = new DataTable(Cytoscape
				.getNetworkAttributes(), DataTable.NETWORK);
	}

}
