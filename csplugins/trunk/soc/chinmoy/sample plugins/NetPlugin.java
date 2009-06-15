package netplugin;

import java.awt.event.ActionEvent;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

/**
 * 
 */
public class NetPlugin extends CytoscapePlugin {

	/**
	 * 
	 */
	public NetPlugin() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(NetPlugin myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("NetPlugin");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			//create a network without a view
			CyNetwork cyNetwork = Cytoscape.createNetwork("network1", false);
			
			CyNode nodeA = Cytoscape.getCyNode("rain", true);
			cyNetwork.addNode(nodeA);
			
			nodeA = Cytoscape.getCyNode("smell", true);
			cyNetwork.addNode(nodeA);
			
			
			nodeA = Cytoscape.getCyNode("rocks", true);
			cyNetwork.addNode(nodeA);
			
//			CyNode node1 = Cytoscape.getCyNode("rainbow", true);
//			CyNode node2 = Cytoscape.getCyNode("rabbit", true);
//			CyNode node3 = Cytoscape.getCyNode("yellow", true);
//
//			cyNetwork.addNode(node0);
//			cyNetwork.addNode(node1);
//			cyNetwork.addNode(node2);
//			cyNetwork.addNode(node3);
			
/*			CyEdge edge0 = Cytoscape.getCyEdge(node0, node1, Semantics.INTERACTION, "pp", true);
			CyEdge edge1 = Cytoscape.getCyEdge(node0, node2, Semantics.INTERACTION, "pp", true);
			CyEdge edge2 = Cytoscape.getCyEdge(node0, node3, Semantics.INTERACTION, "pp", true);
			cyNetwork.addEdge(edge0);
			cyNetwork.addEdge(edge1);
			cyNetwork.addEdge(edge2);
*/
			
			CyEdge edgeA = Cytoscape.getCyEdge("rain", "edge1", "smell", "pp");
			cyNetwork.addEdge(edgeA);
			
			edgeA = Cytoscape.getCyEdge("smell","edge2","rocks","bb");
			cyNetwork.addEdge(edgeA);
			
			// remove a node
			//cyNetwork.removeNode(node1.getRootGraphIndex(), true);
			//Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, cyNetwork);
			
			// destroy the network
			//Cytoscape.destroyNetwork(cyNetwork);
			//Cytoscape.firePropertyChange(Cytoscape.NETWORK_DESTROYED, cyNetwork, null);
		}
	}
}
