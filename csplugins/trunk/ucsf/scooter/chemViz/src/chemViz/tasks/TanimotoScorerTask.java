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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.similarity.CDKTanimotoScore;
import chemViz.ui.ChemInfoSettingsDialog;

/**
 * The TanimotoScorerTask fetches all of the compounds defined by the
 * object passed in its constructor and then calculates the tanimoto distances
 * between each of them, storing the results in attributes or by creating edges
 * in a new network.
 */
public class TanimotoScorerTask implements Task {
	List<GraphObject> selection;
	ChemInfoSettingsDialog settingsDialog;
	TaskMonitor monitor;
	boolean createNewNetwork = false;
	boolean canceled = false;

	/**
 	 * Creates the task.
 	 *
 	 * @param selection the graph objects that we're comparing
 	 * @param dialog the settings dialog, which we use to pull the attribute names that contain the compound descriptors
 	 * @param newNetwork if 'true' create a new network
 	 */
	public TanimotoScorerTask(Collection<GraphObject> selection, ChemInfoSettingsDialog dialog, boolean newNetwork) {
		this.selection = new ArrayList(selection);
		this.settingsDialog = dialog;
		this.createNewNetwork = newNetwork;
	}

	public void halt() {
		canceled = true;
	};

	public void setTaskMonitor(TaskMonitor monitor) {
		this.monitor = monitor;
	}

	public String getTitle() {
		return "Creating Scores Table";
	}

	/**
 	 * Runs the task -- this will get all of the compounds, and compute the tanimoto values
 	 */
	public void run() {
		// Set up
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		CyNetwork origNetwork = Cytoscape.getCurrentNetwork();
		CyNetworkView origNetworkView = Cytoscape.getCurrentNetworkView();
		CyNetworkView newNetworkView = null;
		CyNetwork newNet = null;
		VisualStyle vs = null;
		double tcCutoff = 0.25;
		if (settingsDialog != null)
			tcCutoff = settingsDialog.getTcCutoff();

		setPercentComplete(0);

		if (createNewNetwork) {
		// Create a new network if we're supposed to
			newNet = Cytoscape.createNetwork(selection, new ArrayList(), origNetwork.getTitle()+" copy",
																 origNetwork, true); 
			newNetworkView = Cytoscape.getNetworkView(newNet.getIdentifier());
			vs = Cytoscape.getVisualMappingManager().getVisualStyle();
		}

		double count = 0;
		int selectionCount = selection.size();
		for (int index1 = 0; index1 < selectionCount; index1++) {
			CyNode node1 = (CyNode)selection.get(index1);
			if (canceled) break;
			setStatus("Calculating tanimoto coefficients for "+node1.getIdentifier());
			// System.out.println("Calculating tanimoto coefficients for "+node1.getIdentifier());
			List<Compound> cList1 = Compound.getCompounds(node1, attributes, 
																										settingsDialog.getCompoundAttributes("node",AttriType.smiles),
																										settingsDialog.getCompoundAttributes("node",AttriType.inchi), true);
			if (cList1 == null || cList1.size() == 0)
				continue;

			for (int index2 = 0; index2 < index1; index2++) {
				if (canceled) break;
				CyNode node2 = (CyNode)selection.get(index2);

				if (node2 == node1 && cList1.size() <= 1) 
					continue;

				List<Compound> cList2 = Compound.getCompounds(node2, attributes, 
																											settingsDialog.getCompoundAttributes("node",AttriType.smiles),
																											settingsDialog.getCompoundAttributes("node",AttriType.inchi), true);
				if (cList2 == null || cList2.size() == 0)
					continue;

				int nScores = cList1.size()*cList2.size();
				double maxScore = -1;
				double minScore = 10000000;
				double averageScore = 0;
				for (Compound compound1: cList1) {
					if (compound1 == null) continue;
					for (Compound compound2: cList2) {
						if (canceled) break;
						if (compound2 == null) continue;

						// System.out.print("   Calculating tc for "+node1.getIdentifier()+" vs. "+node2.getIdentifier());
						CDKTanimotoScore scorer = new CDKTanimotoScore(compound1, compound2);
						double score = scorer.calculateSimilarity();
						// System.out.println("...done");
						averageScore = averageScore + score/nScores;
						if (score > maxScore) maxScore = score;
						if (score < minScore) minScore = score;
					}
				}

				// Create the edge if we're supposed to
				CyEdge edge = null;
				if (createNewNetwork) {
					if (averageScore <= tcCutoff)
						continue;
					// System.out.print("   Creating and edge between "+node1.getIdentifier()+" and "+node2.getIdentifier());
					edge = Cytoscape.getCyEdge(node1, node2, "interaction", "similarity", true, true);
					// Add it to our new network
					newNet.addEdge(edge);
					// System.out.println("...done");
				} else {
					// Otherwise, get the edges connecting these nodes (if any)
					int[] node_indices = {node1.getRootGraphIndex(), node2.getRootGraphIndex()};
					int[] edge_indices = origNetwork.getConnectingEdgeIndicesArray(node_indices);
					if (edge_indices == null || edge_indices.length == 0) continue;
					edge = (CyEdge)origNetwork.getEdge(edge_indices[0]);
				}

				if (nScores > 1) {
					edgeAttributes.setAttribute(edge.getIdentifier(), "AverageTanimotoSimilarity", Double.valueOf(averageScore));
					edgeAttributes.setAttribute(edge.getIdentifier(), "MaxTanimotoSimilarity", Double.valueOf(maxScore));
					edgeAttributes.setAttribute(edge.getIdentifier(), "MinTanimotoSimilarity", Double.valueOf(minScore));
				} else {
					edgeAttributes.setAttribute(edge.getIdentifier(), "TanimotoSimilarity", Double.valueOf(averageScore));
				}
			}

			if (createNewNetwork) {
				NodeView orig = origNetworkView.getNodeView(node1);
				NodeView newv = newNetworkView.getNodeView(node1);
				newv.setXPosition(orig.getXPosition());
				newv.setYPosition(orig.getYPosition());
			}
			count++;
			setPercentComplete((count/selection.size())*100);
		}

		if (createNewNetwork) {
			// All done -- create and update the view
			newNetworkView.fitContent();
			newNetworkView.setVisualStyle(vs.getName());
		}

	}

	public JTaskConfig getDefaultTaskConfig() {
		JTaskConfig result = new JTaskConfig();

		result.displayCancelButton(true);
		result.displayCloseButton(false);
		result.displayStatus(true);
		result.displayTimeElapsed(false);
		result.setAutoDispose(true);
		result.setModal(false);
		result.setOwner(Cytoscape.getDesktop());

		return result;
	}

	private void setStatus(String status) {
		if (monitor != null) monitor.setStatus(status);
	}

	private void setPercentComplete(double pct) {
		if (monitor != null) monitor.setPercentCompleted((int)pct);
	}

}
