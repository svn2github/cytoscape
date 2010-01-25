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
import cytoscape.CyNetwork;
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
	private static final String CustomGraphicsAttribute = "__has2DGraphics";
	private boolean removeCustomGraphics = false;
	double zoom = 0.0;
	double lastX = 0.0;
	double lastY = 0.0;

	/**
 	 * This method will check to see if a network has custom graphics
 	 * 
 	 * @param network the network we're checking
 	 * @return <b>true</b> if the network has custom graphics, <b>false</b> otherwise
 	 */
	public static boolean hasCustomGraphics(CyNetwork network) {
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

		if (!networkAttributes.hasAttribute(network.getIdentifier(), CustomGraphicsAttribute))
			return false;

		Boolean v = networkAttributes.getBooleanAttribute(network.getIdentifier(), 
		                                                  CustomGraphicsAttribute);
		if (v == null) return false;

		return v.booleanValue();
	}

	public static List<Node> getCustomGraphicsNodes(CyNetworkView view) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		List<Node> nList = new ArrayList();
		for (Object obj: view.getNetwork().nodesList()) {
			Node node = (Node)obj;
			if (nodeAttributes.hasAttribute(node.getIdentifier(), CustomGraphicsAttribute) &&
			    nodeAttributes.getBooleanAttribute(node.getIdentifier(), CustomGraphicsAttribute)) {
				nList.add(node);
			}
		}
		return nList;
	}

	/**
 	 * Creates the task.
 	 *
 	 * @param selection the group of graph objects that should be included in the table
 	 * @param dialog the settings dialog, which we use to pull the attribute names that contain the compound descriptors
 	 */
	public CreateNodeGraphicsTask(Collection nodeSelection, 
	                              ChemInfoSettingsDialog settingsDialog, boolean remove) {
		this.nodeSelection = nodeSelection;
		this.canceled = false;
		this.compoundCount = 0;
		this.settingsDialog = settingsDialog;
		this.removeCustomGraphics = remove;
	}

	public String getTitle() {
		return "Creating Custom Node Graphics";
	}

	public void setSelection(Collection selection) {
		this.nodeSelection = selection;
	}

	public void setRemove(boolean remove) {
		this.removeCustomGraphics = remove;
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
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		double lastScale = zoom;
		boolean needUpdate = false;

		zoom = scale;

		for (NodeView nv: viewMap.keySet()) {
			// Future -- only update nodes within the viewport
			if (!inViewport(nv, w, h, xCenter, yCenter, scale))
				continue;
			CustomGraphic cg = graphMap.get(nv);
			((DNodeView)nv).removeCustomGraphic(cg);
			cg = drawImage(nv,viewMap.get(nv));
			if (cg == null)
				graphMap.remove(nv);
			else {
				graphMap.put(nv, cg);
				needUpdate = true;
			}
		}
		if (needUpdate) view.updateView();
	}

	/**
 	 * Runs the task -- this will get all of the compounds, and compute the requested attributes
 	 */
	public void run() {
		CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();

		if (removeCustomGraphics) {
			for (NodeView nv: graphMap.keySet()) {
				if (nodeSelection == null || nodeSelection.contains(nv.getNode())) {
					// System.out.println("Removing cg for "+nv.getNode().getIdentifier());
					CustomGraphic cg = graphMap.get(nv);
					((DNodeView)nv).removeCustomGraphic(cg);
					nodeAttributes.deleteAttribute(nv.getNode().getIdentifier(), 
					                               CustomGraphicsAttribute); 
					graphMap.remove(nv);
					viewMap.remove(nv);
				}
			}

			if (nodeSelection == null) {
				networkAttributes.deleteAttribute(Cytoscape.getCurrentNetwork().getIdentifier(),
				                                  CustomGraphicsAttribute);
				((DGraphView)view).removeViewportChangeListener(this);
			}
			view.updateView();
			return;
		}

		List<Compound>cList = getCompounds(nodeSelection, nodeAttributes,
					   							             settingsDialog.getCompoundAttributes("node",AttriType.smiles),
						   						             settingsDialog.getCompoundAttributes("node",AttriType.inchi));

		if (viewMap == null)
			viewMap = new HashMap();

		if (graphMap == null)
			graphMap = new HashMap();

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

			// Update our node attribute
			nodeAttributes.setAttribute(nv.getNode().getIdentifier(), 
			                            CustomGraphicsAttribute,new Boolean(true));
		}


		view.updateView();

		// If we added any custom graphics, we need to listen for changes to the view zoom
		((DGraphView)view).addViewportChangeListener(this);

		// Update our network attribute to indicate we have node graphics
		networkAttributes.setAttribute(Cytoscape.getCurrentNetwork().getIdentifier(), 
		                               CustomGraphicsAttribute,new Boolean(true));
		nodeAttributes.setUserVisible(CustomGraphicsAttribute, false);
		networkAttributes.setUserVisible(CustomGraphicsAttribute, false);
	}

	private CustomGraphic drawImage(NodeView nv, Compound cmpd) {
		// Get our scale factor
		int scale = settingsDialog.getNodeStructureSize();
		// Get our anchor
		byte anchor = settingsDialog.getStructurePosition();

		// Get our width and height
		double width = (nv.getWidth()*scale)/100.;
		double height = (nv.getHeight()*scale)/100.;

		// Adjust our x and y starting points according to our anchor
		double x = 0.0;
		double y = 0.0;
		switch (anchor) {
		case NodeDetails.ANCHOR_CENTER:
			x = -width/2; y = -height/2;
			break;
		case NodeDetails.ANCHOR_NORTH:
			x = -width/2; y = -height;
			break;
		case NodeDetails.ANCHOR_SOUTH:
			x = -width/2; y = 0;
			break;
		case NodeDetails.ANCHOR_EAST:
			x = 0; y = -height/2;
			break;
		case NodeDetails.ANCHOR_WEST:
			x = -width; y = -height/2;
			break;
		case NodeDetails.ANCHOR_SOUTHWEST:
			x = -width; y = 0;
			break;
		case NodeDetails.ANCHOR_SOUTHEAST:
			x = 0; y = 0;
			break;
		case NodeDetails.ANCHOR_NORTHEAST:
			x = 0; y = -height;
			break;
		case NodeDetails.ANCHOR_NORTHWEST:
			x = -width; y = -height;
			break;
		default:
			x = 0.0; y = 0.0;
		}

		BufferedImage image = (BufferedImage) cmpd.getImage((int)(width*zoom), (int)(height*zoom));
		if (image == null) return null;

		// Create the image
		TexturePaint tp = new TexturePaint(image, new Rectangle2D.Double(x,y,width,height));

		// Add it to the view
		return ((DNodeView)nv).addCustomGraphic(new Rectangle2D.Double(x,y,width,height), tp, 
		                                        anchor);
	}

	private boolean inViewport(NodeView nv, int width, int height, 
	                           double xCenter, double yCenter, double scale) {

		double x = nv.getXPosition();
		double y = nv.getYPosition();

		double nodeWidth = nv.getWidth();
		double nodeHeight = nv.getHeight();

		double xMin = xCenter - ((double)width/(2.0*scale));
		double xMax = xCenter + ((double)width/(2.0*scale));
		double yMin = yCenter - ((double)height/(2.0*scale));
		double yMax = yCenter + ((double)height/(2.0*scale));

		// System.out.println("X range = "+xMin+"-"+xMax);
		// System.out.println("Y range = "+yMin+"-"+yMax);
		// System.out.println("Node is at: "+x+","+y+" and is "+nodeWidth+"x"+nodeHeight);

		if ( (x+nodeWidth) < xMin || (x-nodeWidth) > xMax) return false;

		if ( (y+nodeHeight) < yMin || (y-nodeHeight) > yMax) return false;

		return true;
	}
}
