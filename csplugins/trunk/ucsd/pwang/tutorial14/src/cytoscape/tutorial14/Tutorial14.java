package cytoscape.tutorial14;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.VisualStyleBuilder;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;

import java.awt.Color;
import javax.swing.JOptionPane;


/**
 * 
 */
public class Tutorial14 extends CytoscapePlugin {

	/**
	 * create a menu item
	 */
	public Tutorial14() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial14 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial14");
			setPreferredMenu("Plugins");
		}

		public void actionPerformed(ActionEvent e) {
			
			System.out.println("Tutorial14 is clicked!");
			
			// If there is no network, give a message to user
			CyNetwork network = Cytoscape.getCurrentNetwork();		
			if (network.getNodeCount() == 0) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"There is no node!");
				return;
			}
			
			// Select two nodes
			Object [] nodeArray = network.nodesList().toArray();
			List<Node> nodeList = network.nodesList();
			Node node1 = nodeList.get(0);
			Node node2 = nodeList.get(1);

			// Create a visual style "myVisualStyle"
			String styleName = "myVisualStyle";
			VisualStyleBuilder graphStyle = new VisualStyleBuilder(styleName, false);
			graphStyle.setNodeSizeLocked(false);
									
			// set some visual property for two nodes
			graphStyle.addProperty(node1.getIdentifier(), VisualPropertyType.NODE_WIDTH, "30");
			graphStyle.addProperty(node1.getIdentifier(), VisualPropertyType.NODE_FILL_COLOR, "#E1E1E1");
			graphStyle.addProperty(node1.getIdentifier(), VisualPropertyType.NODE_SHAPE, NodeShape.DIAMOND.getShapeName());

			graphStyle.addProperty(node2.getIdentifier(), VisualPropertyType.NODE_WIDTH, "80");
			graphStyle.addProperty(node2.getIdentifier(), VisualPropertyType.NODE_FILL_COLOR, "#0000E1");
			graphStyle.addProperty(node2.getIdentifier(), VisualPropertyType.NODE_SHAPE, NodeShape.TRIANGLE.getShapeName());

			// Create the visual style
			graphStyle.buildStyle();
			
			// Set the background color for this visual style
			GlobalAppearanceCalculator gac = Cytoscape.getVisualMappingManager().getVisualStyle().getGlobalAppearanceCalculator();
			gac.setDefaultBackgroundColor(Color.red);

			// Refresh the view
			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
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
