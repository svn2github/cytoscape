/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package edu.ucsd.bioeng.coreplugin.tableImport;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyMenus;
import edu.ucsd.bioeng.coreplugin.tableImport.actions.ImportAnnotationAndOntologyAction;
import edu.ucsd.bioeng.coreplugin.tableImport.actions.ImportAttributeTableAction;
import edu.ucsd.bioeng.coreplugin.tableImport.actions.ImportNetworkTableAction;

/**
 * Main class for Table Import plugin.
 * 
 * @version 0.7
 * @since Cytoscape 2.4
 * @author Keiichiro Ono
 * 
 */
public class TableImportPlugin extends CytoscapePlugin {
	/**
	 * Constructor for Table Import plugin.
	 * 
	 */
	public TableImportPlugin() {

		final CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();

		// Register each menu item
		if (cyMenus != null) {
			cyMenus.addAction(new ImportNetworkTableAction(), 1);
			cyMenus.addAction(new ImportAttributeTableAction(), 5);
			cyMenus.addAction(new ImportAnnotationAndOntologyAction(), 7);
		} else
			throw new IllegalStateException(
					"Could not register Table Import Plugin to the menu bar.");
	}
}
