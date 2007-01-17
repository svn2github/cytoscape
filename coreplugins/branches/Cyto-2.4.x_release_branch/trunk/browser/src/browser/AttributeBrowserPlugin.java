package browser;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

/**
 * Attribute browser's main class.<br>
 * 
 * @version 0.9
 * @since 2.2
 * @author kono
 * 	Change history
 * 		Combine CytoPanel_2 and CytoPanle_3      Peng-Liang Wang	9/12/2006
 * 
 */
public class AttributeBrowserPlugin extends CytoscapePlugin {

	public static Color DEFAULT_NODE_COLOR = Color.YELLOW;
	public static Color DEFAULT_EDGE_COLOR = Color.RED;

	/**
	 * Constructor for this plugin.
	 *
	 */
	public AttributeBrowserPlugin() {

		initialize();

		JCheckBoxMenuItem switchBrowserView = new JCheckBoxMenuItem(
				new DisplayNetworkPanelAction());

		JCheckBoxMenuItem switchNetworkTreeView = new JCheckBoxMenuItem(
				new DisplayAttributeBrowserAction());

		//JCheckBoxMenuItem switchAdvancedView = new JCheckBoxMenuItem(
		//		new DisplayAdvancedWindowAction());
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("View").add(
				switchNetworkTreeView, 0);
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("View").add(
				switchBrowserView, 0);
		
		//Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("View").add(
		//		switchAdvancedView, 0);
		
		switchBrowserView.setSelected(true);
		switchNetworkTreeView.setSelected(true);

		switchNetworkTreeView.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F5, 0));

	}
	
	/**
	 * Call 3 tables, nodes, edges and network.<br>
	 *  The DataTable class actually creates all CytoPanels.<br>
	 *  Filter functions are implemented in Advanced Window.
	 */
	private void initialize() {

		DataTable table_nodes = new DataTable(Cytoscape.getNodeAttributes(), DataTable.NODES);
		DataTable table_edges = new DataTable(Cytoscape.getEdgeAttributes(), DataTable.EDGES);
		DataTable table_network = new DataTable(Cytoscape.getNetworkAttributes(), DataTable.NETWORK);
	}
}
