/*
  File: GridNodeLayout.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
package cytoscape.layout.algorithms;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.layout.AbstractLayout;

import cytoscape.task.Task;

import cytoscape.util.*;

import cytoscape.view.CyNetworkView;

import giny.model.*;

import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Dimension;

import java.util.*;

import javax.swing.JPanel;


/**
 * The GridNodeLayout provides a very simple layout, suitable as
 * the default layout for Cytoscape data readers.
 */
public class GridNodeLayout extends AbstractLayout {
	/**
	 * Creates a new GridNodeLayout object.
	 */
	public GridNodeLayout() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return "grid";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return "Grid Layout";
	}

	// We do support selected only
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}

	// We dont support node or edge attribute-based layouts
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte[] supportsNodeAttributes() {
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte[] supportsEdgeAttributes() {
		return null;
	}

	/**
	 * Returns a JPanel to be used as part of the Settings dialog for this layout
	 * algorithm.
	 *
	 */
	public JPanel createSettings() {
		return null;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void construct() {
		// This creates the default square layout.
		double distanceBetweenNodes = 80.0d;
		double currX = 0.0d;
		double currY = 0.0d;
		double initialX = 0.0d;
		double initialY = 0.0d;
		int columns;

		// Selected only?
		if (selectedOnly) {
			// Yes, our size and starting points need to be different
			int nodeCount = networkView.nodeCount() - staticNodes.size();
			columns = (int) Math.sqrt(nodeCount);

			// Calculate our starting point as the geographical center of the
			// selected nodes.
			Iterator nodeViews = networkView.getNodeViewsIterator();

			while (nodeViews.hasNext()) {
				NodeView nView = (NodeView) nodeViews.next();

				if (!isLocked(nView)) {
					initialX += (nView.getXPosition() / nodeCount);
					initialY += (nView.getYPosition() / nodeCount);
				}
			}

			// initialX and initialY reflect the center of our grid, so we
			// need to offset by distance*columns/2 in each direction
			initialX = initialX - ((distanceBetweenNodes * (columns - 1)) / 2);
			initialY = initialY - ((distanceBetweenNodes * (columns - 1)) / 2);
			currX = initialX;
			currY = initialY;
		} else {
			columns = (int) Math.sqrt(networkView.nodeCount());
		}

		Iterator nodeViews = networkView.getNodeViewsIterator();
		int count = 0;

		while (nodeViews.hasNext()) {
			NodeView nView = (NodeView) nodeViews.next();
			List<CyEdge>edgeList = network.getAdjacentEdgesList(nView.getNode(),true,true,true);
			for (CyEdge edge: edgeList) { 
				EdgeView ev = networkView.getEdgeView(edge);
				ev.clearBends(); 
			}

			if (isLocked(nView)) {
				continue;
			}

			nView.setOffset(currX, currY);
			count++;

			if (count == columns) {
				count = 0;
				currX = initialX;
				currY += distanceBetweenNodes;
			} else {
				currX += distanceBetweenNodes;
			}
		}
	}
}
