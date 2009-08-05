package org.cytoscape.phylotree.visualstyle;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;


public class PhyloVisualStyleManager {
	
	public void addVisualStyle(PhyloVisualStyle phyloVS)
	{
		// get the network and view
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();

		// get the VisualMappingManager and CalculatorCatalog
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		// check to see if a visual style with this name already exists
		VisualStyle vs = catalog.getVisualStyle(phyloVS.getName());
		if (vs == null) {
			// if not, create it and add it to the catalog
			vs = phyloVS.createStyle(network);
			catalog.addVisualStyle(vs);
		}
		
		networkView.setVisualStyle(vs.getName()); // not strictly necessary

		// actually apply the visual style
		manager.setVisualStyle(vs);
		networkView.redrawGraph(true,true);
	}

}
