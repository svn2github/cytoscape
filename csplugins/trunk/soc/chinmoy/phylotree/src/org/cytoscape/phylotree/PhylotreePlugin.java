package org.cytoscape.phylotree;



import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import org.cytoscape.phylotree.actions.PhyloTreeImportAction;
import org.cytoscape.phylotree.actions.PhyloTreeLayoutAction;

import org.cytoscape.phylotree.layout.cladograms.RadialCladogram;
import org.cytoscape.phylotree.layout.cladograms.RectangularCladogram;
import org.cytoscape.phylotree.layout.cladograms.SlantedCladogram;
import org.cytoscape.phylotree.layout.cladograms.CircularCladogram;
import org.cytoscape.phylotree.layout.phylograms.RectangularPhylogram;
import org.cytoscape.phylotree.layout.phylograms.SlantedPhylogram;
import org.cytoscape.phylotree.layout.phylograms.RadialPhylogram;
import org.cytoscape.phylotree.layout.phylograms.CircularPhylogram;
import org.cytoscape.phylotree.visualstyle.DepthwiseColor;
import org.cytoscape.phylotree.visualstyle.DepthwiseSize;
import org.cytoscape.phylotree.visualstyle.PhyloVisualStyleManager;

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
		
		PhyloTreeLayoutAction layoutAction = new PhyloTreeLayoutAction(new RectangularCladogram());
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) layoutAction);
		
		layoutAction = new PhyloTreeLayoutAction(new SlantedCladogram());
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) layoutAction);
		
		layoutAction = new PhyloTreeLayoutAction(new RadialCladogram());
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) layoutAction);
		
		layoutAction = new PhyloTreeLayoutAction(new CircularCladogram());
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) layoutAction);
		
		layoutAction = new PhyloTreeLayoutAction(new RectangularPhylogram());
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) layoutAction);

		layoutAction = new PhyloTreeLayoutAction(new SlantedPhylogram());
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) layoutAction);
		
		layoutAction = new PhyloTreeLayoutAction(new RadialPhylogram());
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) layoutAction);
		
		layoutAction = new PhyloTreeLayoutAction(new CircularPhylogram());
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) layoutAction);
				
		
		
		
	}
	
}
