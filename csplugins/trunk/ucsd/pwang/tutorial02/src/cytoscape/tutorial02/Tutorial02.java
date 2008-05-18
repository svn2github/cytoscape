
package cytoscape.tutorial02;

import java.awt.event.ActionEvent;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


/**
 * A sample plugin to show how to add an image icon (menu item) to the toolbar. 
 * Deploy this plugin (tutorial02.jar) to the plugins directory. An image icon 
 * (tiger) will show up on the toolbar. Click on the icon will trigger a message box.
 */
public class Tutorial02 extends CytoscapePlugin {

	protected ImageIcon icon = new ImageIcon(getClass().getResource("/tiger.jpg"));

	public Tutorial02() {
		
		// (1) Create an toolbarAction
		MyPluginToolBarAction toolbarAction = new MyPluginToolBarAction(icon, this);
		// (2) add the action to Cytoscape toolbar
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) toolbarAction);
	}
	
	
	public class MyPluginToolBarAction extends CytoscapeAction {

		public MyPluginToolBarAction(ImageIcon icon, Tutorial02 myPlugin) {
			super("", icon);
			//  Set SHORT_DESCRIPTION;  used to create tool-tip
			this.putValue(Action.SHORT_DESCRIPTION, "MyPlugin tool tip");
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"The Tiger icon is clicked!");	
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
