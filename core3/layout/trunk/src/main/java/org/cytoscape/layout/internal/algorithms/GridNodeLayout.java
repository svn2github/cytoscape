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
package org.cytoscape.layout.internal.algorithms;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.layout.AbstractLayout;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.ViewColumn;
import org.cytoscape.view.presentation.twod.TwoDVisualProperties;

import org.cytoscape.work.UndoSupport;
import org.cytoscape.work.Tunable;

//import javax.swing.*;
//import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 * The GridNodeLayout provides a very simple layout, suitable as
 * the default layout for Cytoscape data readers.
 */
public class GridNodeLayout extends AbstractLayout {

//	private ModuleProperties layoutProperties;

	@Tunable(description="Vertical spacing between nodes")
	public double nodeVerticalSpacing = 40.0; 

	@Tunable(description="Horizontal spacing between nodes")
	public double nodeHorizontalSpacing = 80.0; 

	/**
	 * Creates a new GridNodeLayout object.
	 */
	public GridNodeLayout(UndoSupport un) {
		super(un);
//		initProps();
	}

/*

	private void initProps() {
		layoutProperties = TunableFactory.getModuleProperties(getName(),"layout");
        layoutProperties.add(TunableFactory.getTunable("nodeHorizontalSpacing", "Horizontal spacing between nodes", Tunable.DOUBLE, new Double(80.0)));
        layoutProperties.add(TunableFactory.getTunable("nodeVerticalSpacing", "Vertical spacing between nodes", Tunable.DOUBLE, new Double(80.0)));
	}

    public void updateSettings() {
        updateSettings(false);
    }

    public void updateSettings(boolean force) {
        layoutProperties.updateValues();

        Tunable t = layoutProperties.get("nodeHorizontalSpacing");

        if ((t != null) && (t.valueChanged() || force))
            nodeHorizontalSpacing = ((Double) t.getValue()).doubleValue();

        t = layoutProperties.get("nodeVerticalSpacing");

        if ((t != null) && (t.valueChanged() || force))
            nodeVerticalSpacing = ((Double) t.getValue()).doubleValue();
	}

    public void revertSettings() {
        layoutProperties.revertProperties();
    }

    public JPanel getSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(layoutProperties.getTunablePanel());

       return panel;
	}
*/

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
	 *  DOCUMENT ME!
	 */
	public void construct() {
		// This creates the default square layout.
		double currX = 0.0d;
		double currY = 0.0d;
		double initialX = 0.0d;
		double initialY = 0.0d;
		int columns;

		// TODO figure out where these come from!
		VisualProperty<Double> xLoc = TwoDVisualProperties.NODE_X_LOCATION;
		VisualProperty<Double> yLoc = TwoDVisualProperties.NODE_Y_LOCATION;

		// needed for approach 2 and 3
		//ViewColumn<Double> xColumn = networkView.getColumn(xLoc);
		//ViewColumn<Double> yColumn = networkView.getColumn(yLoc);

		// needed for approach 3
		//Map<View<CyNode>,Double> xMap = new HashMap<View<CyNode>,Double>();
		//Map<View<CyNode>,Double> yMap = new HashMap<View<CyNode>,Double>();

		// Selected only?
		if (selectedOnly) {
			// Yes, our size and starting points need to be different
			int nodeCount = networkView.getSource().getNodeCount() - staticNodes.size();
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
			columns = (int) Math.sqrt(networkView.getSource().getNodeCount());
		}

		int count = 0;

		List<CyEdge> edgeList;
		for ( View<CyNode> nView : networkView.getNodeViews() ) {
			edgeList = network.getAdjacentEdgeList(nView.getSource(),CyEdge.Type.ANY);
// TODO
//			for (CyEdge edge: edgeList) {
//				networkView.getCyEdgeView(edge).clearBends(); 
//			}

			if (isLocked(nView)) {
				continue;
			}

			//nView.setOffset(currX, currY);

			// approach 1
			nView.setVisualProperty(xLoc,currX);
			nView.setVisualProperty(yLoc,currY);

			// approach 2
			//xColumn.setValue(nView,currX);
			//yColumn.setValue(nView,currY);

			// approach 3
			//xMap.put(nView,currX);
			//yMap.put(nView,currY);

			count++;

			if (count == columns) {
				count = 0;
				currX = initialX;
				currY += nodeVerticalSpacing;
			} else {
				currX += nodeHorizontalSpacing;
			}
		}

		// approach 3 cont.
		//xColumn.setValues(xMap,null);
		//yColumn.setValues(yMap,null);
	}
	
}
