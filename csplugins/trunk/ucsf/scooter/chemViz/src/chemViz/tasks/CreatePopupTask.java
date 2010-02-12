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

import java.util.ArrayList;
import java.util.List;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.CyNode;
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
	List<GraphObject> objectList;
	ChemInfoSettingsDialog dialog;
	String labelAttribute;

	/**
 	 * Creates the task.
 	 *
 	 * @param view the view object that we're creating the popup for
 	 * @param dialog the settings dialog, which we use to pull the attribute names that contain the compound descriptors
 	 */
  public CreatePopupTask(Object view, ChemInfoSettingsDialog dialog, int maxCompounds) {
		GraphObject object;
		if (view instanceof NodeView) {
			object = ((NodeView)view).getNode();
		} else {
			object = ((EdgeView)view).getEdge();
		}
		this.objectList = new ArrayList();
		objectList.add(object);
		this.dialog = dialog;
		this.canceled = false;
		this.maxCompounds = maxCompounds;
		this.compoundCount = 0;
		this.labelAttribute = dialog.getLabelAttribute();
	}

	/**
 	 * Creates the task.
 	 *
 	 * @param objects the graph objects that we're creating the popup for
 	 * @param dialog the settings dialog, which we use to pull the attribute names that contain the compound descriptors
 	 */
  public CreatePopupTask(List<GraphObject>selection, ChemInfoSettingsDialog dialog, int maxCompounds) {
		this.objectList = selection;
		this.dialog = dialog;
		this.canceled = false;
		this.maxCompounds = maxCompounds;
		this.compoundCount = 0;
		this.labelAttribute = dialog.getLabelAttribute();
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

		// Get the first object so we can do the typing
		GraphObject go = objectList.get(0);
		if (go instanceof CyNode) {
			attributes = Cytoscape.getNodeAttributes();
			type = "node";
		} else {
			attributes = Cytoscape.getEdgeAttributes();
			type = "edge";
		}

		List<Compound> cList = getCompounds(objectList, attributes,
                                        dialog.getCompoundAttributes(type,AttriType.smiles),
                                        dialog.getCompoundAttributes(type,AttriType.inchi));
		if (cList.size() > 0 && !canceled) {
			if (objectList.size() == 1) {
				CompoundPopup popup = new CompoundPopup(cList, objectList, null);
			} else {
				CompoundPopup popup = new CompoundPopup(cList, objectList, labelAttribute);
			}
		}
	}
}
