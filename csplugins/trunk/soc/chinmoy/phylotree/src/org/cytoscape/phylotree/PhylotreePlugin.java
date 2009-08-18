package org.cytoscape.phylotree;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.ui.LayoutMenuManager;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
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
	
	// Similar to root graph index in the root graph. Allows each CyNode to be created with a unique ID.
	public static int universalNodeIndexCounter = -1;
	/**
	 * 
	 */
	public PhylotreePlugin() {
		
		
		
		
		// (1) add an menuItem: File->Import->Phylogenetic Tree
		PhyloTreeImportAction menuAction = new PhyloTreeImportAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);

		//(2) add another menu item: Layout->Phylotree layouts
		// Add the layouts
		CyLayouts.addLayout(new SlantedCladogram(), "Phylotree layouts");
		CyLayouts.addLayout(new RectangularCladogram(), "Phylotree layouts");
		CyLayouts.addLayout(new CircularCladogram(), "Phylotree layouts");
		CyLayouts.addLayout(new RadialCladogram(), "Phylotree layouts");

		CyLayouts.addLayout(new RectangularPhylogram(), "Phylotree layouts");
		CyLayouts.addLayout(new SlantedPhylogram(), "Phylotree layouts");
		CyLayouts.addLayout(new CircularPhylogram(), "Phylotree layouts");
		CyLayouts.addLayout(new RadialPhylogram(), "Phylotree layouts");


					
	}
	
}
