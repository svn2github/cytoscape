package org.genmapp.golayout;

import giny.model.Node;
import giny.model.RootGraph;
import giny.view.NodeView;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.LabelPosition;
import ding.view.DGraphView;

/**
 * CellularLayoutAlgorithm will layout nodes according to a template of cellular
 * regions mapped by node attribute.
 */
public class CellAlgorithm extends AbstractLayout  {
	public double distanceBetweenNodes = 30.0d;
	LayoutProperties layoutProperties = null;

	
	/**
	 * Creates a new CellularLayoutAlgorithm object.
	 */
	public CellAlgorithm() {
		super();
		layoutProperties = new LayoutProperties(getName());
		layoutProperties.add(new Tunable("nodeSpacing",
				"Spacing between nodes", Tunable.DOUBLE, new Double(30.0)));

		/*
		 * We've now set all of our tunables, so we can read the property file
		 * now and adjust as appropriate
		 */
		layoutProperties.initializeProperties();

		/*
		 * Finally, update everything. We need to do this to update any of our
		 * values based on what we read from the property file
		 */
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
		return "cell-layout";
	}

	/**
	 * Returns the user-visible name of this layout
	 * 
	 * @return user visible name
	 */
	public String toString() {
		return "Cell-based";
	}

	/**
	 * Return true if we support performing our layout on a limited set of nodes
	 * 
	 * @return true if we support selected-only layout
	 */
	public boolean supportsSelectedOnly() {
		return false;
	}

	/**
	 * Returns the types of node attributes supported by this algorithm.
	 * 
	 * @return the list of supported attribute types, or null if node attributes
	 *         are not supported
	 */
	public byte[] supportsNodeAttributes() {
		return null;
	}

	/**
	 * Returns the types of edge attributes supported by this algorithm.
	 * 
	 * @return the list of supported attribute types, or null if edge attributes
	 *         are not supported
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


	// List used to unselect register nodes.
	// TODO: This doesn't work with ^A or other non-mouse based selections...
	private List<NodeView> nvRegList = new ArrayList<NodeView>();
	
	/**
	 * The layout protocol...
	 */
	public void construct() {

		GOLayout.createVisualStyle(Cytoscape.getCurrentNetworkView());
		Cytoscape.getVisualMappingManager().setVisualStyle(
				PartitionNetworkVisualStyleFactory.PartitionNetwork_VS);

		taskMonitor.setStatus("Sizing up subcellular regions");
		taskMonitor.setPercentCompleted(1);

		// CREATE REGIONS:
		CellTemplate.buildRegionsFromTepmlate(distanceBetweenNodes);

		// LAYOUT REGIONS:
		double nextX = 0.0d;
		double nextY = 0.0d;
		double startX = 0.0d;
		double startY = 0.0d;
		int nodeCount = 0; // count nodes per region
		List<NodeView> nodeViews;

		HashMap<NodeView, Integer> nvSeen = new HashMap<NodeView, Integer>();
		int taskNodeCount = networkView.nodeCount();
		int taskCount = 0; // count all nodes in all regions to layout
		Region[] sra = RegionManager.getSortedRegionArray();
		for (int i = sra.length - 1; i >= 0; i--) { // count down from
			// largest to smallest
			Region r = sra[i];

			// Place register nodes at region corners (for fit to screen)
			for (int j = 0; j < 4; j++) {
				String regId = r.getAttValue().concat("_" + j);
				CyNode regNode = Cytoscape.getCyNode(regId, true);
				Cytoscape.getCurrentNetwork().addNode(regNode);
				NodeView regNv = Cytoscape.getCurrentNetworkView().getNodeView(
						regNode);
				nvRegList.add(regNv);
				regNv.setHeight(0.1);
				regNv.setWidth(0.1);
				
				Cytoscape.getNodeAttributes().setAttribute(regId,
						"canonicalName", "");
				Cytoscape.getNodeAttributes().setAttribute(regId,
						"register_node", true);
				Cytoscape.getNodeAttributes().setUserVisible("register_node",
						false);
				Cytoscape.getNodeAttributes().setUserEditable("register_node",
						false);
				// lock against future layout
				// TODO: doesn't work!
				lockNode(regNv);
				switch (j) {
				case 0:
					regNv.setOffset(r.getRegionLeft(), r.getRegionTop());
					//Cytoscape.getNodeAttributes().setAttribute(regId,
					//		"canonicalName", r.getAttValue());
					Cytoscape.getNodeAttributes().setAttribute(regId,
							"region_name", r.getAttValue());
					Cytoscape.getNodeAttributes().setUserVisible("region_name",
							false);
					Cytoscape.getNodeAttributes().setUserEditable(
							"region_name", false);
					double length = r.getAttValue().length();
					/*
					 * dynamically define discrete mapping for node label
					 * position
					 */
					LabelPosition lp = new LabelPosition();
					if (r.getShape() == Region.COMPARTMENT_OVAL) {
						lp.setOffsetX(r.getRegionWidth() / 2);
					} else {
						lp.setOffsetX(14 * length / 2 + 10);
					}
					if (r.getShape() == Region.MEMBRANE_LINE) {
						lp.setOffsetY(-15);
					} else if (r.getShape() == Region.COMPARTMENT_OVAL) {
						lp.setOffsetY(40);
					} else {
						lp.setOffsetY(15);
					}
					lp.setJustify(1);
					PartitionNetworkVisualStyleFactory.disMappingLabelPosition
							.putMapValue(r.getAttValue(), lp);
					break;
				case 1:
					regNv.setOffset(r.getRegionLeft(), r.getRegionBottom());
					break;
				case 2:
					regNv.setOffset(r.getRegionRight(), r.getRegionTop());
					break;
				case 3:
					regNv.setOffset(r.getRegionRight(), r.getRegionBottom());
					break;
				}

			}

			nodeViews = r.getNodeViews();
			nodeCount = r.getNodeCount();

			/*
			 * start x at left plus spacer start y at center minus half of the
			 * number of rows rounded down, e.g., if the linear layout of nodes
			 * is 2.8 times the width of the scaled region, then there will be 3
			 * rows and you will want to shift up 1 row from center to start.
			 */
			startX = r.getFreeCenterX() - r.getFreeWidth() / 2
					+ distanceBetweenNodes;
			startY = r.getCenterY()
					- Math.floor((nodeCount / Math.floor(r.getFreeWidth()
							/ distanceBetweenNodes - 1)) - 0.3)
					* distanceBetweenNodes / 2;
			if (r.getShape() == Region.MEMBRANE_LINE) {
				startY = r.getCenterY();
			}
			nextX = startX;
			nextY = startY;

			taskMonitor.setPercentCompleted(50);
			taskMonitor.setStatus("Moving nodes");

			int remainingCount = nodeCount; // count nodes left to layout
			int colCount = 0; // count nodes per row
			double fillPotential = ((nodeCount + 2) * distanceBetweenNodes)
					/ r.getFreeWidth(); // check for full row
			double bump = ((nodeCount + 1) * distanceBetweenNodes)
					/ r.getFreeWidth();

			for (NodeView nv : nodeViews) {
				taskMonitor
						.setPercentCompleted((taskCount / taskNodeCount) * 50);

				if (isLocked(nv)) {
					continue;
				}

				// have we already placed this node view?
				if (nvSeen.containsKey(nv)) {
					// yes, then create copy
					Node oldNode = nv.getNode();
					String oldId = oldNode.getIdentifier();
					String newId = oldId.concat("__").concat(
							nvSeen.get(nv).toString());
					CyNode newNode = Cytoscape.getCyNode(newId, true);

					// copy attributes
					/*
					 * TODO: copy only the attribute relevant to the specific
					 * node so that populateNodeViews will work properly
					 */
					CyAttributes attributes = Cytoscape.getNodeAttributes();
					String[] atts = attributes.getAttributeNames();
					for (String att : atts) {
						/*
						 * skip hidden attributes
						 */
						if (attributes.getUserVisible(att)
								&& attributes.hasAttribute(oldId, att)) {
							byte type = attributes.getType(att);
							if (type == CyAttributes.TYPE_BOOLEAN) {
								attributes.setAttribute(newId, att, attributes
										.getBooleanAttribute(oldId, att));
							} else if (type == CyAttributes.TYPE_INTEGER) {
								attributes.setAttribute(newId, att, attributes
										.getIntegerAttribute(oldId, att));
							} else if (type == CyAttributes.TYPE_FLOATING) {
								attributes.setAttribute(newId, att, attributes
										.getDoubleAttribute(oldId, att));
							} else if (type == CyAttributes.TYPE_STRING) {
								attributes.setAttribute(newId, att, attributes
										.getStringAttribute(oldId, att));
							} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
								attributes
										.setListAttribute(newId, att,
												attributes.getListAttribute(
														oldId, att));
							} else if (type == CyAttributes.TYPE_SIMPLE_MAP) {
								attributes.setMapAttribute(newId, att,
										attributes.getMapAttribute(oldId, att));
							}
						}
					}

					// copy edges
					RootGraph rootGraph = Cytoscape.getRootGraph();
					int[] edges = Cytoscape.getCurrentNetwork()
							.getAdjacentEdgeIndicesArray(
									oldNode.getRootGraphIndex(), true, true,
									true);
					for (int oldEdge : edges) {
						int source = rootGraph.getEdgeSourceIndex(oldEdge);
						int target = rootGraph.getEdgeTargetIndex(oldEdge);
						if (source == oldNode.getRootGraphIndex()) {
							source = newNode.getRootGraphIndex();
						} else if (target == oldNode.getRootGraphIndex()) {
							target = newNode.getRootGraphIndex();
						}
						int newEdge = rootGraph.createEdge(source, target);
						Cytoscape.getCurrentNetwork().addEdge(newEdge);

						// TODO: copy edge attributes??
					}

					// increment hashmap count
					nvSeen.put(nv, (nvSeen.get(nv) + 1));
					// update nv reference
					Cytoscape.getCurrentNetwork().addNode(newNode);
					nv = Cytoscape.getCurrentNetworkView().getNodeView(newNode);
					// adding new node to region nvList
					r.addFilteredNodeView(nv);
				} else {
					// no, then add to tracking list
					nvSeen.put(nv, 1);
					r.addFilteredNodeView(nv);
				}

				nv.setOffset(nextX, nextY);
				remainingCount--;
				colCount++;
				taskCount++;

				// check for end of row
				double fillRatio = ((colCount + 2) * distanceBetweenNodes)
						/ r.getFreeWidth();
				if (fillRatio >= 1) { // reached end of row
					colCount = 0;
					nextX = startX;
					nextY += distanceBetweenNodes;
					// check fill potential of next row
					fillPotential = ((remainingCount + 2) * distanceBetweenNodes)
							/ r.getFreeWidth();
					bump = (remainingCount * distanceBetweenNodes)
							/ r.getFreeWidth();
				} else if (fillPotential < 1) { // short row
					nextX += (distanceBetweenNodes / bump);
				} else { // next column in normal row
					nextX += distanceBetweenNodes;
				}
			}

			List<NodeView> filteredNodeViews = r.getFilteredNodeViews();

			// Uncross edges
			if (filteredNodeViews.size() < 30) {
				UnCrossAction.unCross(filteredNodeViews, false);
			}
			r.repaint();

			// transform nv list to node collection
			Collection<Node> regionNodes = new ArrayList<Node>();
			for (NodeView nv : filteredNodeViews) {
				regionNodes.add(nv.getNode());
			}

			// force directed, selected-only, then scale
			if (r.getShape() != Region.MEMBRANE_LINE
					&& filteredNodeViews.size() > 3) {

				// TODO: There must be a better way of deselecting all nodes!?
				Collection allNodes = Cytoscape.getCyNodesList();
				Cytoscape.getCurrentNetwork().setSelectedNodeState(allNodes,
						false);

				Cytoscape.getCurrentNetwork().setSelectedNodeState(regionNodes,
						true);

				// layout
				CyLayoutAlgorithm layout = CyLayouts
						.getLayout("force-directed");
				layout.setSelectedOnly(true);
				layout.doLayout();

				// scale: find boundaries of layout result
				double north = Double.MAX_VALUE;
				double south = Double.MIN_VALUE;
				double east = Double.MIN_VALUE;
				double west = Double.MAX_VALUE;

				for (NodeView nv : filteredNodeViews) {
					double nodeY = nv.getYPosition();
					double nodeX = nv.getXPosition();

					if (nodeY < north)
						north = nodeY;
					if (nodeY > south)
						south = nodeY;
					if (nodeX > east)
						east = nodeX;
					if (nodeX < west)
						west = nodeX;
				}

				double layoutHeight = south - north;
				double layoutWidth = east - west;
				double unitX = layoutWidth / 2;
				double unitY = layoutHeight / 2;
				double layoutCenterX = west + unitX;
				double layoutCenterY = north + unitY;
				double regionUnitX = r.getFreeWidth() / 2;
				double regionUnitY = r.getFreeHeight() / 2;

				for (NodeView nv : filteredNodeViews) {
					double nodeXunits = (nv.getXPosition() - layoutCenterX);
					double nodeYunits = (nv.getYPosition() - layoutCenterY);
					double scaleX = nodeXunits / unitX;
					double scaleY = nodeYunits / unitY;

					nv.setOffset(r.getFreeCenterX() + regionUnitX * scaleX, r
							.getFreeCenterY()
							+ regionUnitY * scaleY);
				}

				// cleanup
				Cytoscape.getCurrentNetwork().setSelectedNodeState(regionNodes,
						false);
				regionNodes.clear();
			}

			// oil & water
			if (r.getRegionsOverlapped().size() > 0) {
				List<NodeView> nvToCheck = new ArrayList<NodeView>();
				List<NodeView> nvToMove = new ArrayList<NodeView>();
				for (Region or : r.getRegionsOverlapped()) {
					nvToCheck.addAll(or.getFilteredNodeViews());
					nvToCheck.removeAll(r.getFilteredNodeViews());

					/*
					 * returns node views that are within the bounds of this
					 * region
					 */
					nvToMove = Region.bounded(nvToCheck, r);

					/*
					 * determine closest edge per excludedNodeView and move node
					 * to mirror distance relative to new region
					 */
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

						/*
						 * distance between node and boundary around all regions
						 */
						double bufferX = MFNodeAppearanceCalculator.FEATURE_NODE_WIDTH;
						double bufferY = MFNodeAppearanceCalculator.FEATURE_NODE_HEIGHT;
						double farNorth = nvY - (r.getRegionTop() - bufferY);
						double farSouth = (r.getRegionBottom() + bufferY) - nvY;
						double farWest = nvX - (r.getRegionLeft() - bufferX);
						double farEast = (r.getRegionRight() + bufferX) - nvX;

						/*
						 * rule out directions where there is not enough room in
						 * overlapped region
						 */
						if (!(((r.getRegionTop() - bufferY) - or.getFreeTop()) > bufferY)) {
							farNorth = Double.MAX_VALUE;
						}
						if (!(((r.getRegionLeft() - bufferX) - or.getFreeLeft()) > bufferX)) {
							farWest = Double.MAX_VALUE;
						}
						if (!((or.getFreeBottom() - (r.getRegionBottom() + bufferY)) > bufferY)) {
							farSouth = Double.MAX_VALUE;
						}
						if (!((or.getFreeRight() - (r.getRegionRight() + bufferX)) > bufferX)) {
							farEast = Double.MAX_VALUE;
						}

						if (farNorth < farSouth) {
							if (farNorth < farEast) {
								if (farNorth < farWest) {
									countN = 1;
									for (double pN : pastN) {
										if (pN <= nvX + distanceBetweenNodes
												/ 2
												&& pN >= nvX
														- distanceBetweenNodes
														/ 2) {
											countN++;
										}
									}
									nv
											.setYPosition(nvY
													- (farNorth + countN
															* distanceBetweenNodes
															/ 2));
									pastN.add(nvX);
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
													- (farWest + countW
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
										+ (farEast + countE
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
										- (farWest + countW
												* distanceBetweenNodes / 2));
								pastW.add(nvY);
							}

						} else if (farSouth < farEast) {
							if (farSouth < farWest) {
								countS = 1;
								for (double pS : pastN) {
									if (pS <= nvX + distanceBetweenNodes / 2
											&& pS >= nvX - distanceBetweenNodes
													/ 2) {
										countS++;
									}
								}
								nv.setYPosition(nvY
										+ (farSouth + countS
												* distanceBetweenNodes / 2));
								pastS.add(nvX);
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
										- (farWest + countW
												* distanceBetweenNodes / 2));
								pastW.add(nvY);
							}
						} else if (farEast < farWest) {
							countE = 1;
							for (double pE : pastE) {
								if (pE <= nvY + distanceBetweenNodes / 2
										&& pE >= nvY - distanceBetweenNodes / 2) {
									countE++;
								}
							}
							nv.setXPosition(nvX
									+ (farEast + countE * distanceBetweenNodes
											/ 2));
							pastE.add(nvY);
						} else {
							countW = 1;
							for (double pW : pastW) {
								if (pW <= nvY + distanceBetweenNodes / 2
										&& pW >= nvY - distanceBetweenNodes / 2) {
									countW++;
								}
							}
							nv.setXPosition(nvX
									- (farWest + countW * distanceBetweenNodes
											/ 2));
							pastW.add(nvY);
						}
					}

				}
			}
		}
		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		
		// destroy all register nodes
		for (NodeView regNv : nvRegList){
			Cytoscape.getCurrentNetwork().removeNode(regNv.getNode().getRootGraphIndex(), true);
		}
	}





}