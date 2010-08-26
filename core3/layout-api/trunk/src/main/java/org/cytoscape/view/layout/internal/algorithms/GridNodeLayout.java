/*
  File: GridNodeLayout.java

  Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.layout.internal.algorithms;


import java.io.IOException;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayout;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.undo.UndoSupport;


/**
 * The GridNodeLayout provides a very simple layout, suitable as
 * the default layout for Cytoscape data readers.
 */
public class GridNodeLayout extends AbstractLayout implements TunableValidator {
	
	@Tunable(description="Vertical spacing between nodes")
	public double nodeVerticalSpacing = 40.0;

	@Tunable(description="Horizontal spacing between nodes")
	public double nodeHorizontalSpacing = 80.0;


	/**
	 * Creates a new GridNodeLayout object.
	 */
	public GridNodeLayout(UndoSupport un) {
		super(un);
	}

	public boolean tunablesAreValid(final Appendable errMsg) {
		if (nodeVerticalSpacing != 30.0 )
			return true;
		else {
			try {
				errMsg.append("This is a test : I don't want 30.0 for nodeVerticalSpacing value\nProvide something else!!!!");
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return false;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return CyLayouts.DEFAULT_LAYOUT_NAME;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return "Grid Layout";
	}

	/**
	 * We do support selected only
	 *
	 * @return true
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}

	/**
	 *  Perform actual layout task.
	 *  This creates the default square layout.
	 *  
	 */
	public void construct() {

		double currX = 0.0d;
		double currY = 0.0d;
		double initialX = 0.0d;
		double initialY = 0.0d;
		int columns;

		final VisualProperty<Double> xLoc = TwoDVisualLexicon.NODE_X_LOCATION;
		final VisualProperty<Double> yLoc = TwoDVisualLexicon.NODE_Y_LOCATION;

		// Selected only?
		if (selectedOnly) {
			// Yes, our size and starting points need to be different
			int nodeCount = networkView.getModel().getNodeCount() - staticNodes.size();
			columns = (int) Math.sqrt(nodeCount);
			// Calculate our starting point as the geographical center of the
			// selected nodes.
			for ( View<CyNode> nView : networkView.getNodeViews() ) {
				if (!isLocked(nView)) {
					initialX += (nView.getVisualProperty(xLoc) / nodeCount);
					//initialX += (xColumn.getValue(nView) / nodeCount);
					initialY += (nView.getVisualProperty(yLoc) / nodeCount);
					//initialY += (yColumn.getValue(nView) / nodeCount);
				}
			}

			// initialX and initialY reflect the center of our grid, so we
			// need to offset by distance*columns/2 in each direction
			initialX = initialX - ((nodeHorizontalSpacing * (columns - 1)) / 2);
			initialY = initialY - ((nodeVerticalSpacing * (columns - 1)) / 2);
			currX = initialX;
			currY = initialY;
		} else {
			columns = (int) Math.sqrt(networkView.getModel().getNodeCount());
		}

		int count = 0;
		
		// Set visual property.
		for (final View<CyNode> nView : networkView.getNodeViews() ) {
			// FIXME
//			edgeList = network.getAdjacentEdgeList(nView.getModel(),CyEdge.Type.ANY);
//			for (CyEdge edge: edgeList) {
//				networkView.getCyEdgeView(edge).clearBends();
//			}

			if (isLocked(nView))
				continue;

			// approach 1			
			nView.setVisualProperty(xLoc,currX);
			nView.setVisualProperty(yLoc,currY);
						
			count++;

			if (count == columns) {
				count = 0;
				currX = initialX;
				currY += nodeVerticalSpacing;
			} else {
				currX += nodeHorizontalSpacing;
			}
		}
	}

}
