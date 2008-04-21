package cytoscape.tutorial04;

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
public class Tutorial04 extends CytoscapePlugin {

	/**
	 * create a menu item
	 */
	public Tutorial04() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial04 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial04");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			//create a network without a view
			CyNetwork cyNetwork = Cytoscape.createNetwork("network1", false);

			CyNode node0 = Cytoscape.getCyNode("rain", true);
			CyNode node1 = Cytoscape.getCyNode("rainbow", true);
			CyNode node2 = Cytoscape.getCyNode("rabbit", true);
			CyNode node3 = Cytoscape.getCyNode("yellow", true);

			cyNetwork.addNode(node0);
			cyNetwork.addNode(node1);
			cyNetwork.addNode(node2);
			cyNetwork.addNode(node3);
			
			CyEdge edge0 = Cytoscape.getCyEdge(node0, node1, Semantics.INTERACTION, "pp", true);
			CyEdge edge1 = Cytoscape.getCyEdge(node0, node2, Semantics.INTERACTION, "pp", true);
			CyEdge edge2 = Cytoscape.getCyEdge(node0, node3, Semantics.INTERACTION, "pp", true);
			cyNetwork.addEdge(edge0);
			cyNetwork.addEdge(edge1);
			cyNetwork.addEdge(edge2);

			// remove a node
			//cyNetwork.removeNode(node1.getRootGraphIndex(), true);
			//Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, cyNetwork);
			
			// destroy the network
			//Cytoscape.destroyNetwork(cyNetwork);
			//Cytoscape.firePropertyChange(Cytoscape.NETWORK_DESTROYED, cyNetwork, null);
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInToolBar() {
			return false;
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInMenuBar() {
			return true;
		}
	}

}
