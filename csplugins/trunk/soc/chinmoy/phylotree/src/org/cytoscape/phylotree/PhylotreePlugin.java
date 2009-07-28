package org.cytoscape.phylotree;

import javax.swing.JMenu;

import cytoscape.Cytoscape;
import cytoscape.view.CyMenus;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.ui.LayoutMenuManager;

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
		
		JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getLayoutMenu();
		JMenu phyloLayoutMenu = new JMenu("Phylotree Layouts");
		layoutMenu.add(phyloLayoutMenu);
		JMenu phylogramLayoutMenu = new JMenu("Phylogram Layouts");
		JMenu cladogramLayoutMenu = new JMenu("Cladogram Layouts");
		phyloLayoutMenu.add(phylogramLayoutMenu);		
		phyloLayoutMenu.add(cladogramLayoutMenu);
		
//		CyLayouts.addLayout(new RectangularCladogram(), null);
//      CyLayouts.addLayout(new SlantedCladogram(), "PhyloTree Layouts");
//		CyLayouts.addLayout(new RadialCladogram(), "PhyloTree Layouts");
//		CyLayouts.addLayout(new CircularCladogram(), "PhyloTree Layouts");
//
//		CyLayouts.addLayout(new RectangularPhylogram(), "PhyloTree Layouts");
//		CyLayouts.addLayout(new SlantedPhylogram(), "PhyloTree Layouts");
//		CyLayouts.addLayout(new RadialPhylogram(), "PhyloTree Layouts");
//		CyLayouts.addLayout(new CircularPhylogram(), "PhyloTree Layouts");
//		
		
	}
	
}
