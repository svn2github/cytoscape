
package cytoscape.tutorial02;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.filters.FilterPlugin;
import cytoscape.filters.FilterPluginToolBarAction;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelState;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JPanel;

/**
 * 
 */
public class Tutorial02 extends CytoscapePlugin {

	/**
	 * add an image icon (menu item) to the toolbar
	 */
	
	protected ImageIcon icon = new ImageIcon(getClass().getResource("/tiger.jpg"));

	public Tutorial02() {

		// Add an icon to tool-bar
		MyPluginToolBarAction toolbarAction = new MyPluginToolBarAction(
				icon, this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction(
				(CytoscapeAction) toolbarAction);
	}
	
	
	public class MyPluginToolBarAction extends CytoscapeAction {

		public MyPluginToolBarAction(ImageIcon icon, Tutorial02 myPlugin) {
			super("", icon);
			//  Set SHORT_DESCRIPTION;  used to create tool-tip
			this.putValue(Action.SHORT_DESCRIPTION, "MyPlugin tool tip");
		}

		public void actionPerformed(ActionEvent e) {
			System.out.println("The tiger icon on toolbar is clicked");
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInToolBar() {
			return true;
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInMenuBar() {
			return false;
		}
	}
}
