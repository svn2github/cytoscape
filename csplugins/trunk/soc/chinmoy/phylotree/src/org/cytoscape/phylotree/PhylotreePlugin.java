package org.cytoscape.phylotree;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import org.cytoscape.phylotree.actions.PhyloTreeImportAction;

import org.cytoscape.phylotree.layout.cladograms.RadialCladogram;
import org.cytoscape.phylotree.layout.cladograms.RectangularCladogram;
import org.cytoscape.phylotree.layout.cladograms.SlantedCladogram;
import org.cytoscape.phylotree.layout.cladograms.CircularCladogram;
import org.cytoscape.phylotree.layout.phylograms.RectangularPhylogram;
import org.cytoscape.phylotree.layout.phylograms.SlantedPhylogram;
import org.cytoscape.phylotree.layout.phylograms.RadialPhylogram;
import org.cytoscape.phylotree.layout.phylograms.CircularPhylogram;

/**
 * 
 */
public class PhylotreePlugin extends CytoscapePlugin {
	
	
	/**
	 * 
	 */
	public PhylotreePlugin() {
		
		// (1) add an menuItem: File->Import->Phylogenetic Tree
		PhyloTreeImportAction menuAction = new PhyloTreeImportAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);

		
		
		//(2) add another menu item: Layout->Phylotree layouts
		

		CyLayouts.addLayout(new RectangularCladogram(), "PhyloTree Layouts");
		CyLayouts.addLayout(new SlantedCladogram(), "PhyloTree Layouts");
		CyLayouts.addLayout(new RadialCladogram(), "PhyloTree Layouts");
		CyLayouts.addLayout(new CircularCladogram(), "PhyloTree Layouts");

		CyLayouts.addLayout(new RectangularPhylogram(), "PhyloTree Layouts");
		CyLayouts.addLayout(new SlantedPhylogram(), "PhyloTree Layouts");
		CyLayouts.addLayout(new RadialPhylogram(), "PhyloTree Layouts");
		CyLayouts.addLayout(new CircularPhylogram(), "PhyloTree Layouts");
		
		
	}
	
}
