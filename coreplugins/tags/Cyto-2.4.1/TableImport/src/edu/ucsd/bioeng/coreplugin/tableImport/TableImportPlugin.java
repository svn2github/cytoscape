package edu.ucsd.bioeng.coreplugin.tableImport;

import javax.swing.JSeparator;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyMenus;
import cytoscape.view.CytoscapeDesktop;
import edu.ucsd.bioeng.coreplugin.tableImport.actions.ImportAnnotationAndOntologyAction;
import edu.ucsd.bioeng.coreplugin.tableImport.actions.ImportAttributeTableAction;
import edu.ucsd.bioeng.coreplugin.tableImport.actions.ImportNetworkTableAction;

/**
 * Main class for Table Import plugin.
 * 
 * @version 0.5
 * @since Cytoscape 2.4
 * @author Keiichiro Ono
 *
 */
public class TableImportPlugin extends CytoscapePlugin {
	/**
	 * Constructor for this plugin.
	 * 
	 */
	public TableImportPlugin() {
		/*
		 * Add menu items.
		 */
		final CytoscapeDesktop desktop = Cytoscape.getDesktop();
		final CyMenus cyMenus = desktop.getCyMenus();

		cyMenus.addAction(new ImportNetworkTableAction(), 1);
		cyMenus.addAction(new ImportAttributeTableAction(), 5);
		cyMenus.addAction(new ImportAnnotationAndOntologyAction(), 7);
	}
}
