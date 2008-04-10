package cytoscape.tutorial06;

import java.awt.event.ActionEvent;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.CyNetwork;
import java.util.Set;
import java.util.Iterator;
import giny.model.Node;

/**
 * 
 */
public class Tutorial06 extends CytoscapePlugin {

	/**
	 * Description
	 * Determine which nodes are currently selected in a network,
	 * Add a menu item "Tutorial06" under menu "Plugins", select the menu item
	 * will trigger the printout of the selected nodes.
	 *   
	 * (1) get the reference to a network
	 * (2) get the list of selected network
	 * (3) print the list of selected node on the console
	 */
	public Tutorial06() {
		// Create an Action, add it to Cytoscape menu
		MyPluginAction myAction = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) myAction);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial06 myPlugin) {
			// Add a menu item "Tutorial06" under menu "Plugins"
			super("Tutorial06");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			CyNetwork current_network = Cytoscape.getCurrentNetwork();
			if (current_network != null) {
				Set selectedNodes = current_network.getSelectedNodes();
				
				if (selectedNodes.size() == 0) {
					System.out.println("Nothing is selected!");
				}
				
				Iterator<Node> it = selectedNodes.iterator();
				
				while (it.hasNext()) {
					Node aNode = (Node) it.next();
					System.out.println(aNode.getIdentifier());
				}
			}
			else {
				System.out.println("There is no currentNetowk!");
			}
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
