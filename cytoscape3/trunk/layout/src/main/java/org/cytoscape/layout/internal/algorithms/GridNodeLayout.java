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
import org.cytoscape.layout.AbstractLayout;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.NodeView;

import org.cytoscape.work.UndoSupport;
import org.cytoscape.tunable.TunableFactory;
import org.cytoscape.tunable.Tunable;
import org.cytoscape.tunable.ModuleProperties;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.List;


/**
 * The GridNodeLayout provides a very simple layout, suitable as
 * the default layout for Cytoscape data readers.
 */
public class GridNodeLayout extends AbstractLayout {
	private ModuleProperties layoutProperties;
	private double nodeVerticalSpacing = 80.0; 
	private double nodeHorizontalSpacing = 80.0; 
	/**
	 * Creates a new GridNodeLayout object.
	 */
	public GridNodeLayout(UndoSupport un) {
		super(un);
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

	// We do support selected only
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}

    public JPanel getSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(layoutProperties.getTunablePanel());

       return panel;
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

		NodeView nView = null;

		// Selected only?
		if (selectedOnly) {
			// Yes, our size and starting points need to be different
			int nodeCount = networkView.nodeCount() - staticNodes.size();
			columns = (int) Math.sqrt(nodeCount);
			// Calculate our starting point as the geographical center of the
			// selected nodes.
			Iterator<NodeView> nodeViews = networkView.getNodeViewsIterator();

			while (nodeViews.hasNext()) {
				nView = (NodeView) nodeViews.next();

				if (!isLocked(nView)) {
					initialX += (nView.getXPosition() / nodeCount);
					initialY += (nView.getYPosition() / nodeCount);
				}
			}

			// initialX and initialY reflect the center of our grid, so we
			// need to offset by distance*columns/2 in each direction
			initialX = initialX - ((nodeHorizontalSpacing * (columns - 1)) / 2);
			initialY = initialY - ((nodeVerticalSpacing * (columns - 1)) / 2);
			currX = initialX;
			currY = initialY;
		} else {
			columns = (int) Math.sqrt(networkView.nodeCount());
		}

		Iterator<NodeView> nodeViews = networkView.getNodeViewsIterator();
		int count = 0;

		List<CyEdge> edgeList;
		EdgeView ev;
		while (nodeViews.hasNext()) {
			nView = (NodeView) nodeViews.next();
			edgeList = network.getAdjacentEdgeList(nView.getNode(),CyEdge.Type.ANY);
			for (CyEdge edge: edgeList) {
				ev = networkView.getEdgeView(edge);
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
				currY += nodeVerticalSpacing;
			} else {
				currX += nodeHorizontalSpacing;
			}
		}
	}
}
