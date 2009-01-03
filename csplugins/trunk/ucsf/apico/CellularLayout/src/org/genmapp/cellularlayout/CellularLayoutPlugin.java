package org.genmapp.cellularlayout;

import giny.view.NodeView;

import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JPanel;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.plugin.CytoscapePlugin;

/**
 * CellularLayoutPlugin is the main plugin class. It sets the layout menu items,
 * compares available attribute values with available cellular templates, and
 * performs the layout. Basically, it does it all!
 * 
 * @author Alexander Pico, Allan Kuchinsky, Scooter Morris.
 * 
 */
public class CellularLayoutPlugin extends CytoscapePlugin {

	/**
	 * The constructor registers our layout algorithm.  The CyLayouts mechanism 
	 * will worry about how to get it in the right menu, etc.
	 */
	public CellularLayoutPlugin() {
		CyLayouts.addLayout(new CellularLayoutAlgorithm(), "Cellular Layout");
	}

	/**
	 * CellularLayoutAlgorithm will layout nodes according to a template of
	 * cellular regions mapped by node attribute.
	 */
	public class CellularLayoutAlgorithm extends AbstractLayout {
		double distanceBetweenNodes = 80.0d;
		LayoutProperties layoutProperties = null;

		/**
		 * Creates a new CellularLayoutAlgorithm object.
		 */
		public CellularLayoutAlgorithm() {
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
		 * Signals that we want to update our internal settings
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
		 * Reverts our settings back to the original.
		 */
		public void revertSettings() {
			layoutProperties.revertProperties();
		}

		public LayoutProperties getSettings() {
			return layoutProperties;
		}
	
		/**
		 * Returns the short-hand name of this algorithm
		 *
		 * @return  short-hand name
		 */
		public String getName() {
			return "cellular-layout";
		}
	
		/**
		 *  Returns the user-visible name of this layout
		 *
		 * @return  user visible name
		 */
		public String toString() {
			return "Cellular Layout";
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
	
		/**
		 * Returns the types of node attributes supported by
		 * this algorithm. 
		 *
		 * @return the list of supported attribute types, or null
		 * if node attributes are not supported
		 */
		public byte[] supportsNodeAttributes() {
			return null;
		}

		/**
		 * Returns the types of edge attributes supported by
		 * this algorithm.  
		 *
		 * @return the list of supported attribute types, or null
		 * if edge attributes are not supported
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
