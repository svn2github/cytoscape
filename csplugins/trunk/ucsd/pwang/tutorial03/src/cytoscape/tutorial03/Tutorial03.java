package cytoscape.tutorial03;

import java.awt.event.ActionEvent;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import javax.swing.JOptionPane;

/**
 * This sample plugin shows how to create a submenu and add action listener to it.
 * After this plugin is deployed (tutorial03.jar) to the plugins directory, 
 * a menu item "tutorial03" will appear at Plugins menu. Click on the menu
 * item "tutorial03" (Plugins-->tutorial03), a message dialog will show up.
 */
public class Tutorial03 extends CytoscapePlugin {

	public Tutorial03() {
		
		// (1) Create an Action
		MyPluginMenuAction menuAction = new MyPluginMenuAction(this);
		// (2) Add the action to Cytoscape menu
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);
	}
	
	public class MyPluginMenuAction extends CytoscapeAction {

		public MyPluginMenuAction(Tutorial03 myPlugin) {
			// Add the menu item under menu "Plugins"
			super("Tutorial03");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"MenuItem Plugins->Tutorial03 is selected!");	
		}
	}
}
