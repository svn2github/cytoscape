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

package chemViz.tasks;

import java.util.List;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;

import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.ui.CompoundPopup;


/**
 * The CreatePopupTask fetches all of the compounds defined by the
 * object passed in its constructor and then creates a popup Dialog that provides
 * a 2D image of all of the compuonds defined.
 */
public class CreatePopupTask extends AbstractCompoundTask {
	Object view;
	ChemInfoSettingsDialog dialog;

	/**
 	 * Creates the task.
 	 *
 	 * @param object the graph object that we're creating the popup for
 	 * @param dialog the settings dialog, which we use to pull the attribute names that contain the compound descriptors
 	 */
  public CreatePopupTask(Object object, ChemInfoSettingsDialog dialog, int maxCompounds) {
		this.view = object;
		this.dialog = dialog;
		this.canceled = false;
		this.maxCompounds = maxCompounds;
		this.compoundCount = 0;
	}

	public String getTitle() {
		return "Creating 2D Images";
	}

	/**
 	 * Runs the task -- this will get all of the compounds, fetching the images (if necessary) and creates the popup.
 	 */
	public void run() {
		CyAttributes attributes = null;
		String type = null;
		GraphObject go = null;
		if (view instanceof NodeView) {
			attributes = Cytoscape.getNodeAttributes();
			type = "node";
			go = ((NodeView)view).getNode();
		} else {
			attributes = Cytoscape.getEdgeAttributes();
			type = "edge";
			go = ((EdgeView)view).getEdge();
		}

		List<Compound> cList = getCompounds(go, attributes,
                                        dialog.getCompoundAttributes(type,AttriType.smiles),
                                        dialog.getCompoundAttributes(type,AttriType.inchi), false);
		if (cList.size() > 0 && !canceled) {
    	CompoundPopup popup = new CompoundPopup(cList, go);
		}
	}
}
