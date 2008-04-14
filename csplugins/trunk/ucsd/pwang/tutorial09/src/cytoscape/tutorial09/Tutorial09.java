package cytoscape.tutorial09;

import java.awt.event.ActionEvent;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import javax.swing.JOptionPane;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;

/**
 * 
 */
public class Tutorial09 extends CytoscapePlugin {

	/**
	 * 
	 */
	public Tutorial09() {
		// Create an Action,Add the action to Cytoscape menu
		MyPluginAction menuAction = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);
	}
	
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial09 myPlugin) {
			// Add the menu item under menu "Plugins"
			super("Tutorial09");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			if (Cytoscape.getCurrentNetworkView()== null || Cytoscape.getCurrentNetworkView().getTitle().equals("null")) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"There is no network view!");	
				return;
			}
			
			// Get a zoom scale factor from user
			String value = JOptionPane.showInputDialog("Please input a scale factor");

			double scaleFactor = 0.1;
			try {
				scaleFactor = new Double(value).doubleValue();
			}
			catch (Exception ex) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Not a numeric value");
				return;
			}

			// Get reference to the view						
			final CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
			
			networkView.setZoom(scaleFactor);
			networkView.updateView();
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
