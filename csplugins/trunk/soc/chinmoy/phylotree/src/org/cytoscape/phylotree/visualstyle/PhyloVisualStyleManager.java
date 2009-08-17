package org.cytoscape.phylotree.visualstyle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import org.cytoscape.phylotree.layout.CommonFunctions;
import org.cytoscape.phylotree.layout.cladograms.CircularCladogram;


public class PhyloVisualStyleManager {
	
	public void addVisualStyle(PhyloVisualStyle phyloVS)
	{
		// get the network and view
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		CommonFunctions cf = new CommonFunctions();
		
		if(cf.isTree(network))
		{
			// get the VisualMappingManager and CalculatorCatalog
		
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		// check to see if a visual style with this name already exists
		VisualStyle vs = catalog.getVisualStyle(phyloVS.getName());
		VisualStyle pvs;
		
		if (vs == null) {
			// if not, create it and add it to the catalog
			vs = phyloVS.createStyle(network);
			catalog.addVisualStyle(vs);
			manager.setVisualStyle(vs);
		}
		else
		{
			pvs = phyloVS.createStyle(network);
			manager.setVisualStyle(pvs);
			
		}
		
		networkView.redrawGraph(true,true);
		}
		else
			JOptionPane.showMessageDialog(new JFrame(), phyloVS.getName()+" visual style can only be applied to trees.", "Visual style error", JOptionPane.ERROR_MESSAGE);
	}

}
