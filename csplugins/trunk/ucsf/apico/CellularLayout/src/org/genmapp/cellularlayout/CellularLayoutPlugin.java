package org.genmapp.cellularlayout;

import giny.view.NodeView;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.List;

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
	 * The constructor registers our layout algorithm. The CyLayouts mechanism
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
					"Spacing between nodes", Tunable.DOUBLE, new Double(80.0)));

			// We've now set all of our tunables, so we can read the property
			// file now and adjust as appropriate
			layoutProperties.initializeProperties();

			// Finally, update everything. We need to do this to update
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
		 * @param force
		 *            force the settings to be updated, if true
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
		 * @return short-hand name
		 */
		public String getName() {
			return "cellular-layout";
		}

		/**
		 * Returns the user-visible name of this layout
		 * 
		 * @return user visible name
		 */
		public String toString() {
			return "Cellular Layout";
		}

		/**
		 * Return true if we support performing our layout on a limited set of
		 * nodes
		 * 
		 * @return true if we support selected-only layout
		 */
		public boolean supportsSelectedOnly() {
			return false;
		}

		/**
		 * Returns the types of node attributes supported by this algorithm.
		 * 
		 * @return the list of supported attribute types, or null if node
		 *         attributes are not supported
		 */
		public byte[] supportsNodeAttributes() {
			return null;
		}

		/**
		 * Returns the types of edge attributes supported by this algorithm.
		 * 
		 * @return the list of supported attribute types, or null if edge
		 *         attributes are not supported
		 */
		public byte[] supportsEdgeAttributes() {
			return null;
		}

		/**
		 * Returns a JPanel to be used as part of the Settings dialog for this
		 * layout algorithm.
		 * 
		 */
		public JPanel getSettingsPanel() {
			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(layoutProperties.getTunablePanel());

			return panel;
		}

		/**
		 * The layout protocol...
		 */
		public void construct() {

			double nextX = 0.0d;
			double nextY = 0.0d;
			double startX = 0.0d;
			double startY = 0.0d;
			int columns;
			int nodeCount = 0;
			List<NodeView> nodeViews;

			taskMonitor.setStatus("Sizing up subcellular regions");
			taskMonitor.setPercentCompleted(1);

			// CREATE REGIONS:
			RegionManager.clearRegionAttMap();

			// Hard-coded templates
			new Region("RECT", "000000", 5000.0, 500.0, 10000.0, 1000.0, 0.0,
					"extracellular region");
			new Region("ARC", "000000", 5000.0, 1300.0, 10000.0, 600.0, 3.14,
					"plasma membrane");
			new Region("RECT", "000000", 5000.0, 3300.0, 10000.0, 4000.0, 0.0,
					"cytoplasm");
			new Region("OVAL", "000000", 7500.0, 4300.0, 4000.0, 3000.0, 0.0,
					"nucleus");
			new Region("RECT", "000000", 11000.0, 2300.0, 1000.0, 3000.0, 0.0,
			"unassigned");

			// Set additional parameters
			RegionManager.getRegionByAtt("extracellular region")
					.setVisibleBorder(false);
			RegionManager.getRegionByAtt("plasma membrane").setVisibleBorder(
					true);
			RegionManager.getRegionByAtt("cytoplasm").setVisibleBorder(false);
			RegionManager.getRegionByAtt("nucleus").setVisibleBorder(true);

			// SIZE UP REGIONS:
			// calculate the maximum scale factor minimum pan among all regions
			double maxScaleFactor = Double.MIN_VALUE;
			double minPanX = Double.MAX_VALUE;
			double minPanY = Double.MAX_VALUE;
			Collection<Region> allRegions = RegionManager.getAllRegions();
			for (Region r : allRegions) {
				// max scale
				if (r.getShape() != "Line") {
					int col = r.getColumns();
					double scaleX = (col + (col + 1) * distanceBetweenNodes)
							/ r.getWidth();
					double scaleY = (col + (col + 1) * distanceBetweenNodes)
							/ r.getHeight();
					double scaleAreaSqrt = Math.sqrt(scaleX * scaleY);
					System.out.println("scaleX,Y,Area: "+scaleX+","+scaleY+","+scaleAreaSqrt);
					// use area to scale regions efficiently
					if (scaleAreaSqrt > maxScaleFactor)
						maxScaleFactor = scaleAreaSqrt;
				} else { // handle linear regions
					int col = r.getNodeViews().size() + 1; // columns == count
					double scaleX = (col + (col + 1) * distanceBetweenNodes)
							/ r.getWidth(); // width == length, for a line
					if (scaleX > maxScaleFactor)
						maxScaleFactor = scaleX;
				}

				// min pan
				double x = r.getCenterX() - r.getWidth() / 2;
				double y = r.getCenterY() - r.getHeight() / 2;
				System.out.println("panX,Y: "+x+","+y);
				if (x < minPanX)
					minPanX = x;
				if (y < minPanY)
					minPanY = y;
			}

			// apply max scale and min pan to all regions
			for (Region r : allRegions) {
				if (r.getShape() != "Line") {
					r.setWidth(r.getWidth() * maxScaleFactor);
					r.setHeight(r.getHeight() * maxScaleFactor);
				} else { // handle linear regions
					r.setWidth(r.getWidth() * maxScaleFactor);
					// width == length, for a line
				}

				r.setCenterX(r.getCenterX() - minPanX);
				r.setCenterY(r.getCenterY() - minPanY);
				
				r.setCenterX(r.getCenterX() * maxScaleFactor);
				r.setCenterY(r.getCenterY() * maxScaleFactor);
			}

			// LAYOUT REGIONS:
			int taskNodeCount = networkView.nodeCount();
			int taskCount = 0;
			for (Region r : allRegions) {
				nodeViews = r.getNodeViews();
				nodeCount = r.getNodeCount();
				columns = r.getColumns();

				// start x at left plus spacer
				// start y at center minus half of the number of rows rounded
				// down, e.g., if the linear layout of nodes is 2.8 times
				// the width of the scaled region, then there will be 3 rows
				// and you will want to shift up 1 row from center to start.
				startX = r.getCenterX() - r.getWidth() / 2
						+ distanceBetweenNodes; 
				startY = r.getCenterY()
						- Math.floor((nodeCount + (nodeCount + 1)
								* distanceBetweenNodes)
								/ r.getWidth()) * distanceBetweenNodes / 2;
				System.out.println("Region: " + r.getAttValue() + "("
						+ r.getNodeViews().size() + ")" + " startX,Y: "
						+ startX + "," + startY + "   X,Y,W,H: "
						+ r.getCenterX() + "," + r.getCenterY() + ","
						+ r.getWidth() + "," + r.getHeight());
				nextX = startX;
				nextY = startY;

				taskMonitor.setStatus("Moving nodes");

				int count = 0;
				for (NodeView nv : nodeViews) {
					taskMonitor
							.setPercentCompleted((taskCount / taskNodeCount) * 100);

					if (isLocked(nv)) {
						continue;
					}

					nv.setOffset(nextX, nextY);
					count++;
					taskCount++;
					

					if ((count + (count + 1) * distanceBetweenNodes)
							/ r.getWidth() >= 1) { // reached end of row
						count = 0;
						nextX = startX;
						nextY += distanceBetweenNodes;
					} else if ((nodeCount + (nodeCount + 1) * distanceBetweenNodes)
							/ r.getWidth() < 1){ // a short row
						nextX += distanceBetweenNodes / ((nodeCount + (nodeCount + 1) * distanceBetweenNodes)
						/ r.getWidth()); //adjusted for fewer nodes
					} else {
						nextX += distanceBetweenNodes;
					}
				}
			}
		}
		/**
		 * // Selected only? if (selectedOnly) { // Yes, our size and starting
		 * points need to be different nodeCount = networkView.nodeCount() -
		 * staticNodes.size(); columns = (int) Math.sqrt(nodeCount);
		 * 
		 * // Calculate our starting point as the geographical center of // the
		 * // selected nodes. Iterator nodeViews =
		 * networkView.getNodeViewsIterator();
		 * 
		 * while (nodeViews.hasNext()) { NodeView nView = (NodeView)
		 * nodeViews.next();
		 * 
		 * if (!isLocked(nView)) { initialX += (nView.getXPosition() /
		 * nodeCount); initialY += (nView.getYPosition() / nodeCount); } }
		 * 
		 * // initialX and initialY reflect the center of our grid, so we //
		 * need to offset by distance*columns/2 in each direction initialX =
		 * initialX - ((distanceBetweenNodes * (columns - 1)) / 2); initialY =
		 * initialY - ((distanceBetweenNodes * (columns - 1)) / 2); currX =
		 * initialX; currY = initialY; } else { columns = (int)
		 * Math.sqrt(networkView.nodeCount()); nodeCount =
		 * networkView.nodeCount(); }
		 * 
		 * taskMonitor.setStatus("Moving nodes");
		 * 
		 * Iterator nodeViews = networkView.getNodeViewsIterator(); int count =
		 * 0;
		 * 
		 * while (nodeViews.hasNext()) { NodeView nView = (NodeView)
		 * nodeViews.next(); taskMonitor.setPercentCompleted((count / nodeCount)
		 * * 100);
		 * 
		 * if (isLocked(nView)) { continue; }
		 * 
		 * nView.setOffset(currX, currY); count++;
		 * 
		 * if (count == columns) { count = 0; currX = initialX; currY +=
		 * distanceBetweenNodes; } else { currX += distanceBetweenNodes; } } }
		 */
	}
}
