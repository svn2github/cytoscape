package cytoscape.tutorial11;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import javax.swing.JOptionPane;


/**
 * 
 */
public class Tutorial11 extends CytoscapePlugin {

	/**
	 * create a menu item
	 */
	public Tutorial11() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial11 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial11");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			System.out.println("Tutorial11 is clicked!");
			
			String attributeName = "testAttribute";
			
			// If there is no such attribute, give a message to user
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			String[] names = cyNodeAttrs.getAttributeNames();
			boolean find = false;
			
			for (int i=0; i< names.length; i++) {
				if (names[i].equalsIgnoreCase("testAttribute")) {
					find = true;
					break;
				}
			}
			
			if (!find) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"There is no such attribute!");
				return;
			}
			
			// Delete the attribute
			cyNodeAttrs = Cytoscape.getNodeAttributes();
			cyNodeAttrs.deleteAttribute(attributeName);
			
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
