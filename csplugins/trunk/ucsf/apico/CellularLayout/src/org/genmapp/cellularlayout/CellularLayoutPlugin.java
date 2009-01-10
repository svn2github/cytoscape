package org.genmapp.cellularlayout;

import giny.view.NodeView;

import java.awt.GridLayout;
import java.util.ArrayList;
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
			String Color;
			String CenterX;
			String CenterY;
			String Width;
			String Height;
			String ZOrder;
			String Rotation;
			String cG;
			Double xG;
			Double yG;
			Double wG;
			Double hG;
			int zG;
			Double rG;

			 Color="999999";
			 CenterX="3254.75";
			 CenterY="3337.25";
			 Width="1200.5";
			 Height="400.5" ;
			 ZOrder="16384" ;
			 Rotation="0.0";
			cG = Color;
			xG = Double.parseDouble(CenterX);
			yG = Double.parseDouble(CenterY);
			wG = Double.parseDouble(Width);
			hG = Double.parseDouble(Height);
			zG = Integer.parseInt(ZOrder);
			rG = Double.parseDouble(Rotation);

			Region a = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
					"extracellular region");

			 Color="000000" ;
			 CenterX="6232.25";
			 CenterY="2690.25";
			 Width="8535.5" ;
			 Height="100.5" ;
			 ZOrder="16384" ;
			 Rotation="0.0";
			cG = Color;
			xG = Double.parseDouble(CenterX);
			yG = Double.parseDouble(CenterY);
			wG = Double.parseDouble(Width);
			hG = Double.parseDouble(Height);
			zG = Integer.parseInt(ZOrder);
			rG = Double.parseDouble(Rotation);

			Region b = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
					"plasma membrane");

			Color="000000";
			CenterX="7979.75" ;
			CenterY="5002.25";
			Width="2620.5" ;
			Height="2685.5";
			ZOrder="16384";
			Rotation="0.0";
			cG = Color;
			xG = Double.parseDouble(CenterX);
			yG = Double.parseDouble(CenterY);
			wG = Double.parseDouble(Width);
			hG = Double.parseDouble(Height);
			zG = Integer.parseInt(ZOrder);
			rG = Double.parseDouble(Rotation);

			Region d = new Region("Oval", cG, xG, yG, wG, hG, zG, rG, "nucleus");

			Color="999999";
			CenterX="6269.75";
			CenterY="4747.25";
			Width="8640.5" ;
			Height="3765.5" ;
			ZOrder="16384" ;
			Rotation="0.0" ;
			cG = Color;
			xG = Double.parseDouble(CenterX);
			yG = Double.parseDouble(CenterY);
			wG = Double.parseDouble(Width);
			hG = Double.parseDouble(Height);
			zG = Integer.parseInt(ZOrder);
			rG = Double.parseDouble(Rotation);

			Region c = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
					"cytoplasm");

			Color="999999";
			CenterX="11797.25" ;
			CenterY="3719.75";
			Width="1335.5";
			Height="2340.5";
			ZOrder="16384" ;
			Rotation="0.0";
			cG = Color;
			xG = Double.parseDouble(CenterX);
			yG = Double.parseDouble(CenterY);
			wG = Double.parseDouble(Width);
			hG = Double.parseDouble(Height);
			zG = Integer.parseInt(ZOrder);
			rG = Double.parseDouble(Rotation);

			Region e = new Region("Rectangle", cG, xG, yG, wG, hG, zG, rG,
					"unassigned");

			// Set additional parameters
			RegionManager.getRegionByAtt("extracellular region")
					.setVisibleBorder(false);
			RegionManager.getRegionByAtt("plasma membrane").setVisibleBorder(
					true);
			RegionManager.getRegionByAtt("cytoplasm").setVisibleBorder(false);
			RegionManager.getRegionByAtt("nucleus").setVisibleBorder(true);

			// SIZE UP REGIONS:
			Collection<Region> allRegions = RegionManager.getAllRegions();
			
			// calculate free space in overlapped regions
			for (Region r : allRegions){
				
				Double comX = 0.0d;
				Double comY = 0.0d;
				
				List<Region> orList = r.getOverlappingRegions();
				int orListSize = orList.size();
				Double[][] xy = new Double[orListSize*4][2];
				int i = 0;
				for (Region or : orList){
					// define points to exclude
					System.out.println("Check: "+ i +","+orListSize+":"+or.getAttValue());
					xy[i][0] = or.getRegionLeft();
					xy[i][1] = or.getRegionTop();
					i++;
					xy[i][0] = or.getRegionLeft();
					xy[i][1] = or.getRegionBottom();
					i++;
					xy[i][0] = or.getRegionRight();
					xy[i][1] = or.getRegionTop();
					i++;
					xy[i][0] = or.getRegionRight();
					xy[i][1] = or.getRegionBottom();
					i++;
		
					// define center of overlapped region
					comX += or.getCenterX();
					comY += or.getCenterY();
				}
				if (orListSize > 1){
					comX = comX / orList.size();
					comY = comY / orList.size();
				} else {
					comX = r.getCenterX();
					comY = r.getCenterY();
				}
				// check center against overlapping regions
				boolean skip = false;
				for (Region or : orList){
					if (comX > or.getRegionLeft() && comX < or.getRegionRight() && comY > or.getRegionTop() && comY < or.getRegionBottom()){
						skip = true;
						System.out.println("check2: skip!");
					}
				}
				if (skip)
					continue;
				
				// initialize with full rectangle;
				Double freeL = r.getRegionLeft();
				Double freeR = r.getRegionRight();
				Double freeT = r.getRegionTop();
				Double freeB = r.getRegionBottom();
				
				// shrink to fit free area around center
				// adapted from ex2_1.m by E. Alpaydin, i2ml, Learning a rectangle
				for (i = 0; i < orListSize * 4; i++){
					Double x = xy[i][0];
					Double y = xy[i][1];
					if (x > freeL && x < freeR && y > freeT && y < freeB){
						if (x < comX)
							freeL = x;
						else if (x > comX)
							freeR = x;
						else if (y < comY)
							freeT = y;
						else if (y > comY)
							freeB = y;
					}
				}
				r.setFreeCenterX((freeL + freeR) / 2);
				r.setFreeCenterY((freeT + freeB) /2);
				r.setFreeWidth(freeR - freeL);
				r.setFreeHeight(freeB - freeT);
			}

			// calculate the maximum scale factor minimum pan among all regions
			double maxScaleFactor = Double.MIN_VALUE;
			double minPanX = Double.MAX_VALUE;
			double minPanY = Double.MAX_VALUE;
			for (Region r : allRegions) {
				// max scale
				if (r.getShape() != "Line") {
					int col = r.getColumns();
					System.out.println("col: " + r.getAttValue() + col);
					double scaleX = ((col + 1) * distanceBetweenNodes)
							/ r.getFreeWidth();
					double scaleY = ((col + 1) * distanceBetweenNodes)
							/ r.getFreeHeight();
					double scaleAreaSqrt = Math.sqrt(scaleX * scaleY);
					System.out.println("scaleX,Y,Area: " + scaleX + ","
							+ scaleY + "," + scaleAreaSqrt);
					// use area to scale regions efficiently
					if (scaleAreaSqrt > maxScaleFactor)
						maxScaleFactor = scaleAreaSqrt;
				} else { // TODO: handle linear regions
					int col = r.getNodeCount(); // columns == count
					// max(width,height) == length, for a line
					double scaleX;
					if (r.getFreeWidth() > r.getFreeHeight()) {
						scaleX = ((col + 1) * distanceBetweenNodes)
								/ r.getFreeWidth();
					} else {
						scaleX = ((col + 1) * distanceBetweenNodes)
								/ r.getFreeHeight();
					}

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
					r.setFreeWidth(r.getFreeWidth() * maxScaleFactor);
					r.setFreeHeight(r.getFreeHeight() * maxScaleFactor);
				} else { // TODO: handle linear regions
					if (r.getRegionWidth() > r.getRegionHeight()) {
						r.setRegionWidth(r.getRegionWidth() * maxScaleFactor);
						r.setFreeWidth(r.getFreeWidth() * maxScaleFactor);
					} else {
						r.setRegionHeight(r.getRegionHeight() * maxScaleFactor);
						r.setFreeHeight(r.getFreeHeight() * maxScaleFactor);
					}

				}

				r.setCenterX(r.getCenterX() - minPanX);
				r.setCenterY(r.getCenterY() - minPanY);

				r.setCenterX(r.getCenterX() * maxScaleFactor);
				r.setCenterY(r.getCenterY() * maxScaleFactor);

				r.setFreeCenterX(r.getFreeCenterX() - minPanX);
				r.setFreeCenterY(r.getFreeCenterY() - minPanY);

				r.setFreeCenterX(r.getFreeCenterX() * maxScaleFactor);
				r.setFreeCenterY(r.getFreeCenterY() * maxScaleFactor);
}

			// GRAPHICS
			DGraphView dview = (DGraphView) Cytoscape.getCurrentNetworkView();
			DingCanvas bCanvas = dview
					.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
			bCanvas.add(a);
			bCanvas.add(b);
			bCanvas.add(c);
			bCanvas.add(d);
			bCanvas.add(e);
			Cytoscape.getCurrentNetworkView().fitContent();

			// LAYOUT REGIONS:
			int taskNodeCount = networkView.nodeCount();
			int taskCount = 0; // count all nodes in all regions to layout
			Region[] sra = RegionManager.getSortedRegionArray();
			for (int i = sra.length - 1; i >= 0; i--) { // count down from
				// largest to smallest
				Region r = sra[i];
				// System.out.println("Sorted: "+
				// sra[i].getAttValue()+"="+sra[i].getArea());

				nodeViews = r.getNodeViews();
				nodeCount = r.getNodeCount();

				// start x at left plus spacer
				// start y at center minus half of the number of rows rounded
				// down, e.g., if the linear layout of nodes is 2.8 times
				// the width of the scaled region, then there will be 3 rows
				// and you will want to shift up 1 row from center to start.
				startX = r.getFreeCenterX() - r.getFreeWidth() / 2
						+ distanceBetweenNodes;
				startY = r.getCenterY()
						- Math.floor((nodeCount / Math.floor(r.getFreeWidth()
								/ distanceBetweenNodes - 1)) - 0.3)
						* distanceBetweenNodes / 2;
				System.out.println("Region: "
						+ r.getAttValue()
						+ "("
						+ r.getNodeCount()
						+ ")"
						+ " startX,Y: "
						+ startX
						+ ","
						+ startY
						+ "   X,Y,W,H: "
						+ r.getFreeCenterX()
						+ ","
						+ r.getFreeCenterY()
						+ ","
						+ r.getFreeWidth()
						+ ","
						+ r.getFreeHeight()
						+ ","
						+ ((nodeCount / Math.floor(r.getFreeWidth()
								/ distanceBetweenNodes - 1)) - 0.6));
				nextX = startX;
				nextY = startY;

				taskMonitor.setStatus("Moving nodes");

				int remainingCount = nodeCount; // count nodes left to layout
				int colCount = 0; // count nodes per row
				double fillPotential = ((nodeCount + 2) * distanceBetweenNodes)
						/ r.getFreeWidth(); // check for full row
				double bump = ((nodeCount + 1) * distanceBetweenNodes)
						/ r.getFreeWidth();

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
							/ r.getFreeWidth();
					// System.out.println("Count: " + colCount + ","
					// + remainingCount + "::" + fillRatio + "::"
					// + fillPotential);

					if (fillRatio >= 1) { // reached end of row
						colCount = 0;
						nextX = startX;
						nextY += distanceBetweenNodes;
						// check fill potential of next row
						fillPotential = ((remainingCount + 1) * distanceBetweenNodes)
								/ r.getFreeWidth();
						bump = (remainingCount * distanceBetweenNodes)
								/ r.getFreeWidth();
					} else if (fillPotential < 1) { // short row
						nextX += (distanceBetweenNodes / bump);
					} else { // next column in normal row
						nextX += distanceBetweenNodes;
					}
				}
				r.repaint();

				// oil & water
				if (r.getRegionsOverlapped().size() > 0) {
					List<NodeView> nvToCheck = new ArrayList<NodeView>();
					List<NodeView> nvToMove = new ArrayList<NodeView>();
					for (Region or : r.getRegionsOverlapped()) {
						nvToCheck.addAll(or.getNodeViews());
						nvToCheck.removeAll(r.getNodeViews());

						// returns node views that are within the bounds of this
						// region
						nvToMove = Region.bounded(nvToCheck, r);

						// determine closest edge per excludedNodeView and move
						// node to mirror distance relative to new region
						int countN = 0;
						int countS = 0;
						int countE = 0;
						int countW = 0;
						List<Double> pastN = new ArrayList<Double>();
						List<Double> pastS = new ArrayList<Double>();
						List<Double> pastE = new ArrayList<Double>();
						List<Double> pastW = new ArrayList<Double>();

						for (NodeView nv : nvToMove) {

							double nvX = nv.getXPosition();
							double nvY = nv.getYPosition();

							// distance between node and boundary around all
							// regions
							double farNorth = nvY - r.getRegionTop();
							double farSouth = r.getRegionBottom() - nvY;
							double farWest = nvX - r.getRegionLeft();
							double farEast = r.getRegionRight() - nvX;

							// rule out directions where there is not enough
							// room in overlapped region
							if (!((r.getRegionTop() - or.getRegionTop()) > distanceBetweenNodes)) {
								farNorth = Double.MAX_VALUE;
							}
							if (!((r.getRegionLeft() - or.getRegionLeft()) > distanceBetweenNodes)) {
								farWest = Double.MAX_VALUE;
							}
							if (!((or.getRegionBottom() - r.getRegionBottom()) > distanceBetweenNodes)) {
								farSouth = Double.MAX_VALUE;
							}
							if (!((or.getRegionRight() - r.getRegionRight()) > distanceBetweenNodes)) {
								farEast = Double.MAX_VALUE;
							}

							if (farNorth < farSouth) {
								if (farNorth < farEast) {
									if (farNorth < farWest) {
										countN = 1;
										for (double pN : pastN) {
											if (pN <= nvX
													+ distanceBetweenNodes / 2
													&& pN >= nvX
															- distanceBetweenNodes
															/ 2) {
												countN++;
											}
										}
										nv.setYPosition(nvY
												- (farNorth + countN
														* distanceBetweenNodes
														/ 2));
										pastN.add(nvX);
									} else {
										countW = 1;
										for (double pW : pastW) {
											if (pW <= nvY
													+ distanceBetweenNodes / 2
													&& pW >= nvY
															- distanceBetweenNodes
															/ 2) {
												countW++;
											}
										}
										nv.setXPosition(nvX
												+ (farWest + countW
														* distanceBetweenNodes
														/ 2));
										pastW.add(nvY);
									}
								} else if (farEast < farWest) {
									countE = 1;
									for (double pE : pastE) {
										if (pE <= nvY + distanceBetweenNodes
												/ 2
												&& pE >= nvY
														- distanceBetweenNodes
														/ 2) {
											countE++;
										}
									}
									nv
											.setXPosition(nvX
													- (farEast + countE
															* distanceBetweenNodes
															/ 2));
									pastE.add(nvY);
								} else {
									countW = 1;
									for (double pW : pastW) {
										if (pW <= nvY + distanceBetweenNodes
												/ 2
												&& pW >= nvY
														- distanceBetweenNodes
														/ 2) {
											countW++;
										}
									}
									nv
											.setXPosition(nvX
													+ (farWest + countW
															* distanceBetweenNodes
															/ 2));
									pastW.add(nvY);
								}

							} else if (farSouth < farEast) {
								if (farSouth < farWest) {
									countS = 1;
									for (double pS : pastN) {
										if (pS <= nvX + distanceBetweenNodes
												/ 2
												&& pS >= nvX
														- distanceBetweenNodes
														/ 2) {
											countS++;
										}
									}
									nv
											.setYPosition(nvY
													+ (farSouth + countS
															* distanceBetweenNodes
															/ 2));
									pastS.add(nvX);
								} else {
									countW = 1;
									for (double pW : pastW) {
										if (pW <= nvY + distanceBetweenNodes
												/ 2
												&& pW >= nvY
														- distanceBetweenNodes
														/ 2) {
											countW++;
										}
									}
									nv
											.setXPosition(nvX
													+ (farWest + countW
															* distanceBetweenNodes
															/ 2));
									pastW.add(nvY);
								}
							} else if (farEast < farWest) {
								countE = 1;
								for (double pE : pastE) {
									if (pE <= nvY + distanceBetweenNodes / 2
											&& pE >= nvY - distanceBetweenNodes
													/ 2) {
										countE++;
									}
								}
								nv.setXPosition(nvX
										- (farEast + countE
												* distanceBetweenNodes / 2));
								pastE.add(nvY);
							} else {
								countW = 1;
								for (double pW : pastW) {
									if (pW <= nvY + distanceBetweenNodes / 2
											&& pW >= nvY - distanceBetweenNodes
													/ 2) {
										countW++;
									}
								}
								nv.setXPosition(nvX
										+ (farWest + countW
												* distanceBetweenNodes / 2));
								pastW.add(nvY);
							}
						}

					}
				}
			}
			Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		}
	}
}
