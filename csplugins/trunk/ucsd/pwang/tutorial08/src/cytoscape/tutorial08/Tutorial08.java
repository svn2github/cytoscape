package cytoscape.tutorial08;

import java.awt.event.ActionEvent;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.ding.DingNetworkView;
import ding.view.DGraphView;
import ding.view.DingCanvas;
import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
/**
 * 
 */
public class Tutorial08 extends CytoscapePlugin {

	/**
	 * 
	 */
	public Tutorial08() {
	
		// Create an Action and add it to Cytoscape menu
		MyPluginAction myAction = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) myAction);

	}
	
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial08 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial08");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
								
			if (Cytoscape.getCurrentNetworkView().getTitle() == null || Cytoscape.getCurrentNetworkView().getTitle().equalsIgnoreCase("null")) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Network view is not selected or empty!");	
				return;
			}
			//Let user choose a color
			Color bkColor = JColorChooser.showDialog(Cytoscape.getDesktop(), "Please choose a background color", Color.white);
				
			// Change the background color for current view			
			DingNetworkView  theView = (DingNetworkView) Cytoscape.getCurrentNetworkView();
			DingCanvas backgroundCanvas = theView.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
			backgroundCanvas.setBackground(bkColor);
		
			// Refresh the view
			Cytoscape.getCurrentNetworkView().updateView();
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
