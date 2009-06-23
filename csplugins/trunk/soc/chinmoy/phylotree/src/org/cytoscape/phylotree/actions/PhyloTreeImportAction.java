package org.cytoscape.phylotree.actions;

import java.awt.event.ActionEvent;
import cytoscape.util.CytoscapeAction;
import org.cytoscape.phylotree.PhylotreePlugin;
import org.cytoscape.phylotree.ui.PhyloFileDialog;
import java.io.File;

public class PhyloTreeImportAction extends CytoscapeAction{
	
	public PhyloTreeImportAction(PhylotreePlugin p) {
		// Add the menu item under menu pulldown "File->Import"
		super("Phylogenetic tree...");
		setPreferredMenu("File.Import");
	}

	public void actionPerformed(ActionEvent e) {
		PhyloFileDialog fileDialog = new PhyloFileDialog(this);	
	}


	public void ImportTreeFromFile(File pFile, String pFormat){
		if (pFile == null){
			System.out.println("pFile = null");
			return;
		}
				
		if (pFormat.equalsIgnoreCase("Phylip")){
		
			// use the parser to read the file
			
			
			// create a network
			
			
			// create attribute
			
		}
	}
}
