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

import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import giny.model.GraphObject;
import giny.model.Node;
import giny.model.Edge;

import giny.view.NodeView;
import ding.view.DGraphView;
import ding.view.DNodeView;
import ding.view.ViewportChangeListener;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;

import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.ui.ChemInfoSettingsDialog;

/**
 * The CreateNodeGraphicsTask fetches the first compound defined by the
 * node GraphObject passed in its constructor and then creates a custom node graphic
 * for that node.
 */
public class CreateNodeGraphicsTask extends AbstractCompoundTask 
                                    implements ActionListener, ViewportChangeListener {
	Collection<GraphObject> nodeSelection;
	ChemInfoSettingsDialog settingsDialog;
	HashMap<NodeView, Compound> viewMap = null;
	HashMap<NodeView, CustomGraphic> graphMap = null;
	double zoom = 0.0;

	/**
 	 * Creates the task.
 	 *
 	 * @param selection the group of graph objects that should be included in the table
 	 * @param dialog the settings dialog, which we use to pull the attribute names that contain the compound descriptors
 	 */
	public CreateNodeGraphicsTask(Collection nodeSelection, 
	                              ChemInfoSettingsDialog settingsDialog) {
		this.nodeSelection = nodeSelection;
		this.canceled = false;
		this.compoundCount = 0;
		this.settingsDialog = settingsDialog;
	}

	public String getTitle() {
		return "Creating Custom Node Graphics";
	}

	public void actionPerformed(ActionEvent e) {
		if (nodeSelection == null) {
			// Nothing to do
			return;
		}
		// Execute
		TaskManager.executeTask(this, this.getDefaultTaskConfig());
	}

	public void viewportChanged(int w, int h, double xCenter, double yCenter, double scale) {
		// System.out.println("viewport: size="+w+"x"+h+", center = "+xCenter+", "+yCenter+" scale = "+scale);
		if (scale != zoom) {
			zoom = scale;
			CyNetworkView view = Cytoscape.getCurrentNetworkView();

			for (NodeView nv: viewMap.keySet()) {
				// Future -- only update nodes within the viewport
				CustomGraphic cg = graphMap.get(nv);
				((DNodeView)nv).removeCustomGraphic(cg);
				cg = drawImage(nv,viewMap.get(nv));
				if (cg == null)
					graphMap.remove(nv);
				else
					graphMap.put(nv, cg);
			}
			view.updateView();
		}
	}

	/**
 	 * Runs the task -- this will get all of the compounds, and compute the requested attributes
 	 */
	public void run() {

		List<Compound>cList = getCompounds(nodeSelection, Cytoscape.getNodeAttributes(),
					   							             settingsDialog.getCompoundAttributes("node",AttriType.smiles),
						   						             settingsDialog.getCompoundAttributes("node",AttriType.inchi));

		if (viewMap == null)
			viewMap = new HashMap();

		if (graphMap == null)
			graphMap = new HashMap();

		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		zoom = view.getZoom();

		for (Compound compound: cList) {
			if (canceled) return;

			// Get the node view for this compound's source
			NodeView nv = view.getNodeView((Node)compound.getSource());

			// Do we have one?
			if (nv == null) continue;

			// Have we already seen it?
			if (viewMap.containsKey(nv)) continue;

			CustomGraphic cg = drawImage(nv, compound);
			if (cg == null) continue;

			viewMap.put(nv, compound);
			graphMap.put(nv, cg);
		}

		view.updateView();

		// If we added any custom graphics, we need to listen for changes to the view zoom
		((DGraphView)view).addViewportChangeListener(this);

	}

	private CustomGraphic drawImage(NodeView nv, Compound cmpd) {
		// Get our width and height
		double width = nv.getWidth();
		double height = nv.getHeight();

		BufferedImage image = (BufferedImage) cmpd.getImage((int)(width*zoom), (int)(height*zoom));
		if (image == null) return null;

		// Create the image
		TexturePaint tp = new TexturePaint(image, new Rectangle2D.Double(0,0,width,height));

		// Add it to the view
		return ((DNodeView)nv).addCustomGraphic(new Rectangle2D.Double(0,0,width,height), tp, NodeDetails.ANCHOR_NORTHWEST);
	}
}
