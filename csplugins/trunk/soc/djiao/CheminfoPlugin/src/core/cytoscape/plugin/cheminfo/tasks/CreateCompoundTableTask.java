/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.plugin.cheminfo.tasks;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import giny.model.GraphObject;
import giny.model.Node;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;

import cytoscape.plugin.cheminfo.model.Compound;
import cytoscape.plugin.cheminfo.model.Compound.AttriType;
import cytoscape.plugin.cheminfo.ui.ChemInfoSettingsDialog;
import cytoscape.plugin.cheminfo.ui.CompoundTable;

/**
 * The CreateCompoundTableTask fetches all of the compounds defined by the
 * objects passed in its constructor and then creates a JTable that provides
 * an interface to view the compound information.
 */
public class CreateCompoundTableTask extends AbstractCompoundTask {
	Collection<GraphObject> selection;
	ChemInfoSettingsDialog settingsDialog;

	/**
 	 * Creates the task.
 	 *
 	 * @param selection the group of graph objects that should be included in the table
 	 * @param dialog the settings dialog, which we use to pull the attribute names that contain the compound descriptors
 	 */
	public CreateCompoundTableTask(Collection<GraphObject> selection, ChemInfoSettingsDialog dialog, int maxCompounds) {
		this.selection = selection;
		this.settingsDialog = dialog;
		this.canceled = false;
		this.maxCompounds = maxCompounds;
		this.compoundCount = 0;
	}

	public String getTitle() {
		return "Creating Table";
	}

	/**
 	 * Runs the task -- this will get all of the compounds, fetching the images (if necessary) and creates the table.
 	 */
	public void run() {
		CyAttributes attributes = null;
		String type = null;
		Iterator<GraphObject>iter = selection.iterator();
		if (!iter.hasNext())
			return;

		GraphObject go = iter.next();
		if (go instanceof Node) {
			attributes = Cytoscape.getNodeAttributes();
			type = "node";
		} else {
			attributes = Cytoscape.getEdgeAttributes();
			type = "edge";
		}

		List<Compound> cList = getCompounds(selection, attributes, 
				   															settingsDialog.getCompoundAttributes(type,AttriType.smiles),
					   														settingsDialog.getCompoundAttributes(type,AttriType.inchi));
		if (cList.size() > 0 && !canceled) {
			CompoundTable cTable = new CompoundTable(cList);
		}
	}

}
