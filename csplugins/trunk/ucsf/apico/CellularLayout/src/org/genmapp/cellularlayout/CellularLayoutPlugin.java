package org.genmapp.cellularlayout;

import giny.view.NodeView;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.plugin.CytoscapePlugin;
import ding.view.DGraphView;
import ding.view.DingCanvas;

/**
 * CellularLayoutPlugin is the main plugin class. It sets the layout menu items,
 * compares available attribute values with available cellular templates, and
 * performs the layout. Basically, it does it all!
 * 
 * @author Alexander Pico, Allan Kuchinsky, Scooter Morris, Thomas Kelder.
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
			int nodeCount = 0; // count nodes per region
			List<NodeView> nodeViews;

			taskMonitor.setStatus("Sizing up subcellular regions");
			taskMonitor.setPercentCompleted(1);

			// CREATE REGIONS:
			RegionManager.clearRegionAttMap();

			// Hard-coded templates
			Region a = new Region("Rectangle", "000000", 6254.75, 1837.25,
					8670.5, 1185.5, 0.0, "extracellular region");
			Region b = new Region("Line", "000000", 6232.25, 2677.25, 8535.5,
					29.0, 0.0, "plasma membrane");
			Region c = new Region("Rectangle", "000000", 6269.75, 4747.25,
					8640.5, 3765.5, 0.0, "cytoplasm");
			Region d = new Region("Oval", "000000", 7979.75, 5002.25, 4620.5,
					2685.5, 0.0, "nucleus");
			Region e = new Region("Rectangle", "000000", 11797.25, 3719.75,
					1335.5, 2340.5, 0.0, "unassigned");

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
					// System.out.println("col: "+r.getAttValue()+col);
					double scaleX = ((col + 1) * distanceBetweenNodes)
							/ r.getRegionWidth();
					double scaleY = ((col + 1) * distanceBetweenNodes)
							/ r.getRegionHeight();
					double scaleAreaSqrt = Math.sqrt(scaleX * scaleY);
					System.out.println("scaleX,Y,Area: " + scaleX + ","
							+ scaleY + "," + scaleAreaSqrt);
					// use area to scale regions efficiently
					if (scaleAreaSqrt > maxScaleFactor)
						maxScaleFactor = scaleAreaSqrt;
				} else { // handle linear regions
					int col = r.getNodeCount(); // columns == count
					// width == length, for a line
					double scaleX = ((col + 1) * distanceBetweenNodes)
							/ r.getRegionWidth();
					if (scaleX > maxScaleFactor)
						maxScaleFactor = scaleX;
				}

				// min pan
				double x = r.getCenterX() - r.getRegionWidth() / 2;
				double y = r.getCenterY() - r.getRegionHeight() / 2;
				if (x < minPanX)
					minPanX = x;
				if (y < minPanY)
					minPanY = y;
			}

			// apply max scale and min pan to all regions
			for (Region r : allRegions) {
				if (r.getShape() != "Line") {
					r.setRegionWidth(r.getRegionWidth() * maxScaleFactor);
					r.setRegionHeight(r.getRegionHeight() * maxScaleFactor);
				} else { // handle linear regions
					r.setRegionWidth(r.getRegionWidth() * maxScaleFactor);
					// width == length, for a line
				}

				r.setCenterX(r.getCenterX() - minPanX);
				r.setCenterY(r.getCenterY() - minPanY);

				r.setCenterX(r.getCenterX() * maxScaleFactor);
				r.setCenterY(r.getCenterY() * maxScaleFactor);
			}

			// GRAPHICS
			DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
			DingCanvas aLayer = dview
					.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
			aLayer.add(a);
			aLayer.add(b);
			aLayer.add(c);
			aLayer.add(d);
			aLayer.add(e);
			Cytoscape.getCurrentNetworkView().fitContent();

			// LAYOUT REGIONS:
			int taskNodeCount = networkView.nodeCount();
			int taskCount = 0; // count all nodes in all regions to layout
			for (Region r : allRegions) {
				nodeViews = r.getNodeViews();
				nodeCount = r.getNodeCount();

				// start x at left plus spacer
				// start y at center minus half of the number of rows rounded
				// down, e.g., if the linear layout of nodes is 2.8 times
				// the width of the scaled region, then there will be 3 rows
				// and you will want to shift up 1 row from center to start.
				startX = r.getCenterX() - r.getRegionWidth() / 2
						+ distanceBetweenNodes;
				startY = r.getCenterY()
						- Math.floor((nodeCount
								/ Math.floor(r.getRegionWidth()
										/ distanceBetweenNodes -1 )) - 0.3)
						* distanceBetweenNodes / 2;
				System.out.println("Region: " + r.getAttValue() + "("
						+ r.getNodeCount() + ")" + " startX,Y: "
						+ startX + "," + startY + "   X,Y,W,H: "
						+ r.getCenterX() + "," + r.getCenterY() + ","
						+ r.getRegionWidth() + "," + r.getRegionHeight() +","+ ((nodeCount
								/ Math.floor(r.getRegionWidth()
										/ distanceBetweenNodes -1 )) - 0.6));
				nextX = startX;
				nextY = startY;

				taskMonitor.setStatus("Moving nodes");

				int remainingCount = nodeCount; // count nodes left to layout
				int colCount = 0; // count nodes per row
				double fillPotential = ((nodeCount + 2) * distanceBetweenNodes)
						/ r.getRegionWidth(); // check for full row
				double bump = ((nodeCount + 1) * distanceBetweenNodes)
						/ r.getRegionWidth();

				for (NodeView nv : nodeViews) {
					taskMonitor
							.setPercentCompleted((taskCount / taskNodeCount) * 100);

					if (isLocked(nv)) {
						continue;
					}

					nv.setOffset(nextX, nextY);
					remainingCount--;
					colCount++;
					taskCount++;

					// check for end of row
					double fillRatio = ((colCount + 2) * distanceBetweenNodes)
							/ r.getRegionWidth();
					System.out.println("Count: " + colCount + ","
							+ remainingCount + "::" + fillRatio + "::"
							+ fillPotential);

					if (fillRatio >= 1) { // reached end of row
						colCount = 0;
						nextX = startX;
						nextY += distanceBetweenNodes;
						// check fill potential of next row
						fillPotential = ((remainingCount + 1) * distanceBetweenNodes)
								/ r.getRegionWidth();
						bump = (remainingCount * distanceBetweenNodes)
								/ r.getRegionWidth();
					} else if (fillPotential < 1 ){ // short row
						nextX += (distanceBetweenNodes / bump);
					} else { // next column in normal row
						nextX += distanceBetweenNodes;
					}
				}
				r.repaint();
			}
			Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
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
