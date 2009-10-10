/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 *
 * This file is part of PaxtoolsPlugin.
 *
 *  PaxtoolsPlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.coreplugins.biopax;

import java.awt.event.ActionEvent;
import java.util.*;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.coreplugins.biopax.action.ExportAsBioPAXAction;
import cytoscape.coreplugins.biopax.action.IntegrateBioPAXAction;
import cytoscape.coreplugins.biopax.action.MergeBioPAXAction;
import cytoscape.coreplugins.biopax.util.BioPAXUtilRex;
import cytoscape.data.ImportHandler;
import cytoscape.*;
import cytoscape.util.*;
import cytoscape.layout.*;
import cytoscape.view.CyMenus;

/**
 * @author Rex Dwyer
 * 
 * @author rodch
 * 
 * TODO check and merge with BiopaxPlugin
 * 
 * @deprecated
 */
public class PaxtoolsPlugin extends CytoscapePlugin {

	public PaxtoolsPlugin() throws Exception {
		ImportHandler importHandler = new ImportHandler();
		importHandler.addFilter(new PaxtoolsFileFilter());

		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		cyMenus.addAction(new ExportAsBioPAXAction());
		cyMenus.addAction(new MergeBioPAXAction());
		cyMenus.addAction(new IntegrateBioPAXAction());
		cyMenus.addAction(new CreateNodesForControlsAction());
		CyLayoutAlgorithm allLayouts[] = CyLayouts.getAllLayouts().toArray(
				new CyLayoutAlgorithm[1]);
		// Put layout names in alphabetical order.
		Arrays.sort(allLayouts, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((CyLayoutAlgorithm) o1).getName().compareTo(
						((CyLayoutAlgorithm) o2).getName());
			}
		});
		// For each layout algorithm, create a menu item to select it as default when reading.
		for (CyLayoutAlgorithm algo : allLayouts) {
			cyMenus.addAction(new SelectDefaultLayoutAction(algo));
		}
	}

	/**
	 * For "Plugins->BioPaX Import->Create Nodes for Controls" menu item.
	 * If checked, the reader creates cytoscape nodes for Controls (Catalysis, etc.) 
	 * in the BioPaX file.  If not checked, the reader represent Controls by edges.	 *
	 */
	public class CreateNodesForControlsAction extends CytoscapeAction {
		public CreateNodesForControlsAction() {
			super("Create Nodes for Controls");
			this.setPreferredMenu("Plugins.BioPaX Import");
			BioPAXUtilRex.setCreateNodesForControls(false);
			useCheckBoxMenuItem = true;
		}

		public void actionPerformed(ActionEvent ae) {
			BioPAXUtilRex.toggleCreateNodesForControls();
		}
	}

	/**
	 * For "Plugins->BioPaX Import->Default Layout" menu.
	 * One of these actions exists for each known layout.
	 * When the layout name is clicked, it becomes the default initial layout for future biopax reads.
	 */
	public class SelectDefaultLayoutAction extends CytoscapeAction {

		CyLayoutAlgorithm algo;

		public SelectDefaultLayoutAction(CyLayoutAlgorithm algo) {
			super(algo.getName());
			this.algo = algo;
			this.setPreferredMenu("Plugins.BioPaX Import.Default Layout");
		}

		public void actionPerformed(ActionEvent ae) {
			BioPAXUtilRex.setDefaultLayoutAlgorithm(algo);
		}
	}

	/**
	 * For "Plugins->BioPaX Import->Read Level 3"
	 * Not currently using this.
	 */
	public class ReadLevel3Action extends CytoscapeAction {

		public ReadLevel3Action() {
			super("Inputs are Level 3");
			this.setPreferredMenu("Plugins.BioPaX Import");
			BioPAXUtilRex.setInputLevel3(true);
			useCheckBoxMenuItem = true;
		}

		public void actionPerformed(ActionEvent ae) {
			BioPAXUtilRex.toggleInputLevel3();
		}
	}
}
