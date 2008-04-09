package cytoscape.tutorial03;

import java.awt.event.ActionEvent;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import javax.swing.JOptionPane;


/**
 * 
 */
public class Tutorial03 extends CytoscapePlugin {

	/**
	 * Add a menu item "Tutorial03" under menu "Plugins"
	 * Select it through Plugins-->Tutorial03 will trigger a message box.
	 */
	public Tutorial03() {
		
		// (1) Create an Action
		MyPluginMenuAction menuAction = new MyPluginMenuAction(this);
		// (2) Add the action to Cytoscape menu
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);

	}
	
	public class MyPluginMenuAction extends CytoscapeAction {

		public MyPluginMenuAction(Tutorial03 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial03");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"MenuItem Plugins->Tutorial03 is selected!");	
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
