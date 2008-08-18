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
package sampleLayout;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.Tunable;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.CyLayouts;

import cytoscape.task.Task;

import cytoscape.view.CyNetworkView;
import cytoscape.plugin.CytoscapePlugin;

import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;

import java.util.Iterator;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

/**
 * SampleLayout is a very simple plugin that registers a very simple
 * layout algorithm.
 */
public class SampleLayout extends CytoscapePlugin {

	/**
	 * The constructor does very little.  All we need to do is to register
	 * our layout algorithm.  The CyLayouts mechanism will worry about how
	 * to get it in the right menu, etc.
	 */
	public SampleLayout() {
		CyLayouts.addLayout(new SampleLayoutAlgorithm(), "Scooter's Layouts");
	}

	/**
	 * SampleLayoutAlgorithm provides a very simple layout.
	 */
	public class SampleLayoutAlgorithm extends AbstractLayout {
		double distanceBetweenNodes = 80.0d;
		LayoutProperties layoutProperties = null;

		/**
		 * Creates a new SampleLayoutAlgorithm object (doesn't do much).
		 */
		public SampleLayoutAlgorithm() {
			super();
			layoutProperties = new LayoutProperties(getName());
			layoutProperties.add(new Tunable("nodeSpacing",
			                                 "Spacing between nodes",
			                                 Tunable.DOUBLE, new Double(80.0)));

		  // We've now set all of our tunables, so we can read the property 
			// file now and adjust as appropriate
			layoutProperties.initializeProperties();

			// Finally, update everything.  We need to do this to update
			// any of our values based on what we read from the property file
			updateSettings(true);

		}

		/**
		 * External interface to update our settings
		 */
		public void updateSettings() {
			updateSettings(true);
		}

		/**
		 * Signal that we want to update our internal settings
		 *
		 * @param force force the settings to be updated, if true
		 */
		public void updateSettings(boolean force) {
			layoutProperties.updateValues();
			Tunable t = layoutProperties.get("nodeSpacing");
			if ((t != null) && (t.valueChanged() || force))
				distanceBetweenNodes = ((Double) t.getValue()).doubleValue();
		}

		/**
		 * Revert our settings back to the original.
		 */
		public void revertSettings() {
			layoutProperties.revertProperties();
		}

		public LayoutProperties getSettings() {
			return layoutProperties;
		}
	
		/**
		 * Return the short-hand name of this algorithm
		 *
		 * @return  short-hand name
		 */
		public String getName() {
			return "sample-layout";
		}
	
		/**
		 *  Return the user-visible name of this layout
		 *
		 * @return  user visible name
		 */
		public String toString() {
			return "Sample Layout";
		}
	
		/**
		 *  Return true if we support performing our layout on a 
		 * limited set of nodes
		 *
		 * @return  true if we support selected-only layout
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
		public JPanel getSettingsPanel() {
			JPanel panel = new JPanel(new GridLayout(0,1));
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
			int nodeCount = 0;

			taskMonitor.setStatus("Initializing");
			taskMonitor.setPercentCompleted(1);
	
			// Selected only?
			if (selectedOnly) {
				// Yes, our size and starting points need to be different
				nodeCount = networkView.nodeCount() - staticNodes.size();
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
				nodeCount = networkView.nodeCount();
			}

			taskMonitor.setStatus("Moving nodes");
	
			Iterator nodeViews = networkView.getNodeViewsIterator();
			int count = 0;
	
			while (nodeViews.hasNext()) {
				NodeView nView = (NodeView) nodeViews.next();
				taskMonitor.setPercentCompleted((count/nodeCount)*100);
	
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
}
