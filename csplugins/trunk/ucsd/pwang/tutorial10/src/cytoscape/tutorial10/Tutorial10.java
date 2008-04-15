package cytoscape.tutorial10;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import giny.model.Node;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.data.CyAttributes;
import java.util.List;
/**
 * 
 */
public class Tutorial10 extends CytoscapePlugin {

	/**
	 * This plugin will add a menu item "tutorial10" under menu "plugins"
	 * If a network exist, it will add an attribute for a node.
	 * After attribute is added, it can be find at the attribute browser
	 */
	public Tutorial10() {
	
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial10 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial10");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			// If there is no network, give a message to user
			CyNetwork network = Cytoscape.getCurrentNetwork();		
			if (network.getNodeCount() == 0) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"There is no node!");
				return;
			}
			
			// Select a node
			Object [] nodeArray = network.nodesList().toArray();
			List<Node> nodeList = network.nodesList();
			Node node = nodeList.get(0);
			
			// Add an attribute for the node
			String attributeName = "testAttribute";
			String AttributeValue = "testValue"; 
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			cyNodeAttrs.setAttribute(node.getIdentifier(), attributeName, AttributeValue);
			
			// Inform others via property change event.
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
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
