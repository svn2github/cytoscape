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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import giny.model.GraphObject;
import giny.model.Node;
import giny.model.Edge;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;

import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.model.Compound.DescriptorType;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.ui.CompoundTable;

/**
 * The CreateAttributesTask fetches all of the compounds defined by the
 * objects passed in its constructor and then creates an attribute for each
 * GraphObject refered to by the compounds
 */
public class CreateAttributesTask extends AbstractCompoundTask implements ActionListener {
	Collection<GraphObject> edgeSelection;
	Collection<GraphObject> nodeSelection;
	DescriptorType descriptor;
	ChemInfoSettingsDialog settingsDialog;

	/**
 	 * Creates the task.
 	 *
 	 * @param selection the group of graph objects that should be included in the table
 	 * @param dialog the settings dialog, which we use to pull the attribute names that contain the compound descriptors
 	 */
	public CreateAttributesTask(Collection nodeSelection, Collection edgeSelection,
	                           DescriptorType descriptor, ChemInfoSettingsDialog settingsDialog) {
		this.nodeSelection = nodeSelection;
		this.edgeSelection = edgeSelection;
		this.canceled = false;
		this.compoundCount = 0;
		this.descriptor = descriptor;
		this.settingsDialog = settingsDialog;
	}

	public String getTitle() {
		return "Creating Attributes";
	}

	public void actionPerformed(ActionEvent e) {
		if (nodeSelection == null && edgeSelection == null) {
			// Nothing to do
			return;
		}
		// Execute
		TaskManager.executeTask(this, this.getDefaultTaskConfig());
	}

	/**
 	 * Runs the task -- this will get all of the compounds, and compute the requested attributes
 	 */
	public void run() {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		objectCount = 0;
		totalObjects = 0;

		if (nodeSelection != null && nodeSelection.size() > 0)
			totalObjects = nodeSelection.size();
		if (edgeSelection != null && edgeSelection.size() > 0)
			totalObjects += edgeSelection.size();

		if (nodeSelection != null && nodeSelection.size() > 0)
			calculateDescriptors(nodeSelection, nodeAttributes, "node");


		if (edgeSelection != null && edgeSelection.size() > 0)
			calculateDescriptors(edgeSelection, edgeAttributes, "edge");

	}

	private void calculateDescriptors(Collection<GraphObject> selection, CyAttributes attributes, String type) {

		boolean needList = false;

		if (monitor != null)
			monitor.setStatus("Getting compounds for "+type+"s");
		
		List<Compound>cList = getCompounds(selection, attributes, 
					   							             settingsDialog.getCompoundAttributes(type,AttriType.smiles),
						   						             settingsDialog.getCompoundAttributes(type,AttriType.inchi));

		if (cList == null || cList.size() == 0) return;

		HashMap<GraphObject, List<Object>> valueMap = new HashMap();

		if (monitor != null)
			monitor.setStatus("Calculating descriptors for "+type+"s");

		// Second, calculate the descriptors
		for (Compound compound: cList) {
			if (canceled) return;

			Object result = compound.getDescriptor(descriptor);
			if (result == null) continue;
			GraphObject source = compound.getSource();
			if (!valueMap.containsKey(source))
				valueMap.put(source, new ArrayList());
			else {
				needList = true;
			}

			List<Object> vL = valueMap.get(source);
			vL.add(result);
			valueMap.put(source, vL);
		}

		if (monitor != null)
			monitor.setStatus("Creating "+type+" attributes");

		// Finally, write them out
		String attributeName = descriptor.toString();
		for (GraphObject gObject: valueMap.keySet()) {
			if (needList) {
				attributes.setListAttribute(gObject.getIdentifier(), attributeName, valueMap.get(gObject));
			} else {
				Object obj = valueMap.get(gObject).get(0);
				if (obj instanceof Double)
					attributes.setAttribute(gObject.getIdentifier(), attributeName, (Double)obj);
				else if (obj instanceof Integer)
					attributes.setAttribute(gObject.getIdentifier(), attributeName, (Integer)obj);
				else if (obj instanceof Boolean)
					attributes.setAttribute(gObject.getIdentifier(), attributeName, (Boolean)obj);
				else
					attributes.setAttribute(gObject.getIdentifier(), attributeName, obj.toString());
			}
		}
	}

}
