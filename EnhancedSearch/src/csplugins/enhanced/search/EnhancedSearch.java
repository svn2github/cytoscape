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

package csplugins.enhanced.search;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;

import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.search.Hits;
import csplugins.enhanced.search.util.EnhancedSearchUtils;

;

public class EnhancedSearch {

	public EnhancedSearch() {
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(
				new MenuItemEnhancedSearchAction());
	}

	public class MenuItemEnhancedSearchAction extends CytoscapeAction {

		public MenuItemEnhancedSearchAction() {
			super("Enhanced Search");
		}

		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent e){

			// Display dialog
			EnhancedSearchDialog dialog = new EnhancedSearchDialog();
			dialog.setVisible(true);

			// Perform search
			if (!dialog.isCancelled()) {
				String query = dialog.getQuery();
				final CyNetwork currNetwork = Cytoscape.getCurrentNetwork();
				indexAndSearch(currNetwork, query);

			} else {
				return;
			}
		}

	}

	public void indexAndSearch(CyNetwork network, String query) {

		// Index the given network
		EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(network);
		RAMDirectory idx = indexHandler.getIndex();

		// Perform search
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(idx);
		Hits hits = queryHandler.ExecuteQuery(query);
		
		// Display results
		EnhancedSearchUtils.displayResults(network, hits);
	}
}
