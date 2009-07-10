package org.cytoscape.phylotree;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutProperties;
import org.cytoscape.phylotree.actions.PhyloTreeImportAction;
import org.cytoscape.phylotree.layout.BasicCladogramLayout;
/**
 * 
 */
public class PhylotreePlugin extends CytoscapePlugin {
	
	private int groupcount = 2;
	private LayoutProperties layoutProperties;

	
	/**
	 * 
	 */
	public PhylotreePlugin() {
		
		// (1) add an menuItem: File->Import->Phylogenetic Tree
		PhyloTreeImportAction menuAction = new PhyloTreeImportAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);

		//(2) add another menu item: Layout->Phylotree layouts
		CyLayouts.addLayout(new BasicCladogramLayout(), "PhyloTree Layouts");
	}
	
}
