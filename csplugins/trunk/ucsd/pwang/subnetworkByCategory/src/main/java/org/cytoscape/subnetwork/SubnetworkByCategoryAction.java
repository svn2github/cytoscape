package org.cytoscape.subnetwork;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;
import java.net.URL;

import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.visual.VisualStyle;

/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class SubnetworkByCategoryAction extends CytoscapeAction {

		private static final long serialVersionUID = 123456789023451239L;

		private static final URL vizmapPropsLocation = SubnetworkByCategoryAction.class.getResource("/subnetwork_overview_VS.props");
		private VisualStyle overviewVS = null;
		
		public SubnetworkByCategoryAction() {
			// Give your action a name here
			super("Subnetwork by Category");

			// Set the menu you'd like here.  Plugins don't need
			// to live in the Plugins menu, so choose whatever
			// is appropriate!
	        setPreferredMenu("Plugins");
	        
			// Create visualStyles based on the definition in property files
	        if (vizmapPropsLocation != null){
				Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null, vizmapPropsLocation);
				overviewVS = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle("subnetwork_overview");	        	
	        }

		}
		
		public void actionPerformed(ActionEvent e) {

//			String attributeName = "Category";// this is the default attribute name			
			// get attributeName (Type String or int) from user
			
			
        	String[] attrNames = Cytoscape.getNodeAttributes().getAttributeNames();        	

			ChooseCategoryAttributeDialog dlg = new ChooseCategoryAttributeDialog(Cytoscape.getDesktop(), true, attrNames);
			dlg.setVisible(true);
			
			if (dlg.getSelectedAttribute() == null){
				// User clicked cancel button
				return;
			}

			CyNetwork net = Cytoscape.getCurrentNetwork();
			
			SubnetworkByCategory w = new SubnetworkByCategory();
			
			CyNetwork overviewNetwork = w.execute(net, dlg.getSelectedAttribute());
			CyNetworkView view = Cytoscape.getNetworkView(overviewNetwork.getIdentifier());
			
			if (view != null){
				//Apply VisualStyle
				if (this.overviewVS != null){
					view.setVisualStyle(this.overviewVS.getName());
					view.redrawGraph(false, true);					
				}

				// apply layout -- force-directed
				CyLayoutAlgorithm alg = cytoscape.layout.CyLayouts.getLayout("force-directed");
				view.applyLayout(alg);
			}
		}
}
