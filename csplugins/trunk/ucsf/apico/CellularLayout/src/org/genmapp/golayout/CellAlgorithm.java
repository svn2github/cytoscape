package org.genmapp.golayout;

import giny.model.Node;
import giny.model.RootGraph;
import giny.view.NodeView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeModifiedNetworkManager;
import cytoscape.data.CyAttributes;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;

/**
 * CellularLayoutAlgorithm will layout nodes according to a template of cellular
 * regions mapped by node attribute.
 */
public class CellAlgorithm extends AbstractLayout {
	protected static boolean pruneEdges = false;
	protected static double distanceBetweenNodes = 30.0d;
	protected static String attributeName = "annotation.GO CELLULAR_COMPONENT";
	protected static final String LAYOUT_NAME = "cell-layout";

	// store region assignment as node attribute
	protected static final String REGION_ATT = "_cellularLayoutRegion";
	// store whether a node is copied
	protected static final String NODE_COPIED = "_isInMultipleRegions";
	// store whether a node is copied
	protected static final String UNASSIGNED_EDGE_ATT = "_isEdgeToUnassigned";

	/**
	 * Creates a new CellularLayoutAlgorithm object.
	 */
	public CellAlgorithm() {
		super();

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
		// Nothing here... see GOLayout
	}

	/**
	 * Returns the short-hand name of this algorithm NOTE: is related to the
	 * menu item order
	 * 
	 * @return short-hand name
	 */
	public String getName() {
		return LAYOUT_NAME;
	}

	/**
	 * Returns the user-visible name of this layout
	 * 
	 * @return user visible name
	 */
	public String toString() {
		return "Floorplan Only";
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
	 * Sets the attribute to use for the weights
	 * 
	 * @param value
	 *            the name of the attribute
	 */
	public void setLayoutAttribute(String value) {
		attributeName = value;
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
	 * The layout protocol...
	 */
	public void construct() {

		// if "(none)" was selected in setting, then skip partitioning
		if (null == attributeName) {
			// skip the whole ordeal
		} else {

			// CREATE REGIONS:
			CellTemplate.buildRegionsFromTepmlate(distanceBetweenNodes);
			Region[] sra = RegionManager.getSortedRegionArray();

			// Check for valid region-annotation mapping
			boolean valid = false;
			for (Region r : sra) {
				if (r.getAttValue() != "unassigned" && r.getNodeCount() > 0) {
					valid = true;

				}
			}
			taskMonitor.setStatus("Checking attribute mappings");
			taskMonitor.setPercentCompleted(1);
			if (!valid) {
				RegionManager.clearAll();
				//(java.awt.Window) taskMonitor
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"Selected attribute does not match floorplan");
			} else {

				if (PartitionNetworkVisualStyleFactory.attributeName != null) {
					// if a non-null attribute has been selection for node
					// coloring
					GOLayout.createVisualStyle(Cytoscape
							.getCurrentNetworkView());
				}
				if (PartitionNetworkVisualStyleFactory.attributeName != null) {
					Cytoscape.getVisualMappingManager().setVisualStyle(
							PartitionNetworkVisualStyleFactory.attributeName);
				}

				taskMonitor.setStatus("Sizing up floorplan regions");
				//taskMonitor.setPercentCompleted(1);

				List<NodeView> nvRegList = new ArrayList<NodeView>();

				// LAYOUT REGIONS:
				double nextX = 0.0d;
				double nextY = 0.0d;
				double startX = 0.0d;
				double startY = 0.0d;
				int nodeCount = 0; // count nodes per region
				List<NodeView> nodeViews;

				HashMap<NodeView, Integer> nvSeen = new HashMap<NodeView, Integer>();
				List<Node> firstCopies = new ArrayList<Node>();

				int taskNodeCount = networkView.nodeCount();
				int taskCount = 0; // count all nodes in all regions to layout

				for (int i = sra.length - 1; i >= 0; i--) { // count down from
					// largest to smallest
					Region r = sra[i];

					// Place register nodes at region corners (for fit to
					// screen)
					for (int j = 0; j < 4; j++) {
						String regId = r.getAttValue().concat("_" + j);
						CyNode regNode = Cytoscape.getCyNode(regId, true);
						Cytoscape.getCurrentNetwork().addNode(regNode);
						NodeView regNv = Cytoscape.getCurrentNetworkView()
								.getNodeView(regNode);
						nvRegList.add(regNv);
						regNv.setHeight(0.1);
						regNv.setWidth(0.1);

						Cytoscape.getNodeAttributes().setAttribute(regId,
								"canonicalName", "");
						// lock against future layout
						// TODO: doesn't work!
						lockNode(regNv);
						switch (j) {
						case 0:
							regNv
									.setOffset(r.getRegionLeft(), r
											.getRegionTop());
							break;
						case 1:
							regNv.setOffset(r.getRegionLeft(), r
									.getRegionBottom());
							break;
						case 2:
							regNv.setOffset(r.getRegionRight(), r
									.getRegionTop());
							break;
						case 3:
							regNv.setOffset(r.getRegionRight(), r
									.getRegionBottom());
							break;
						}

					}

					nodeViews = r.getNodeViews();
					nodeCount = r.getNodeCount();

					/*
					 * start x at left plus spacer start y at center minus half
					 * of the number of rows rounded down, e.g., if the linear
					 * layout of nodes is 2.8 times the width of the scaled
					 * region, then there will be 3 rows and you will want to
					 * shift up 1 row from center to start.
					 */
					startX = r.getFreeCenterX() - r.getFreeWidth() / 2
							+ distanceBetweenNodes;
					startY = r.getCenterY()
							- Math.floor((nodeCount / Math.floor(r
									.getFreeWidth()
									/ distanceBetweenNodes - 1)) - 0.3)
							* distanceBetweenNodes / 2;
					if (r.getShape() == Region.MEMBRANE_LINE) {
						startY = r.getCenterY();
					}
					nextX = startX;
					nextY = startY;

					taskMonitor.setPercentCompleted(50);
					taskMonitor.setStatus("Moving nodes");

					int remainingCount = nodeCount; // count nodes left to
					// layout
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
						CyAttributes attributes = Cytoscape.getNodeAttributes();
						// attributes.setUserVisible(NODE_COPIED, false);

						// have we already placed this node view?
						if (nvSeen.containsKey(nv)) {
							// yes, then create copy
							Node oldNode = nv.getNode();
							String oldId = oldNode.getIdentifier();
							// collect unique list to rename later
							if (!firstCopies.contains(oldNode))
								firstCopies.add(oldNode);
							Integer next = nvSeen.get(nv) + 1;
							String cleanOldId = oldId;
							if (oldId.contains("__1")) {
								// clean up name from prior runs of GO Layout
								cleanOldId = oldId.substring(0, oldId
										.lastIndexOf("__1"));
							}
							String newId = cleanOldId.concat("__").concat(
									next.toString());
							CyNode newNode = Cytoscape.getCyNode(newId, true);

							// copy attributes
							String[] atts = attributes.getAttributeNames();
							for (String att : atts) {
								/*
								 * skip hidden attributes
								 */
								if (attributes.getUserVisible(att)
										&& attributes.hasAttribute(oldId, att)) {
									byte type = attributes.getType(att);
									if (type == CyAttributes.TYPE_BOOLEAN) {
										attributes.setAttribute(newId, att,
												attributes.getBooleanAttribute(
														oldId, att));
									} else if (type == CyAttributes.TYPE_INTEGER) {
										attributes.setAttribute(newId, att,
												attributes.getIntegerAttribute(
														oldId, att));
									} else if (type == CyAttributes.TYPE_FLOATING) {
										attributes.setAttribute(newId, att,
												attributes.getDoubleAttribute(
														oldId, att));
									} else if (type == CyAttributes.TYPE_STRING) {
										if (att == REGION_ATT) {
											attributes.setAttribute(newId, att,
													r.getAttValue());
										} else {
											attributes
													.setAttribute(
															newId,
															att,
															attributes
																	.getStringAttribute(
																			oldId,
																			att));
										}
									} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
										attributes.setListAttribute(newId, att,
												attributes.getListAttribute(
														oldId, att));
									} else if (type == CyAttributes.TYPE_SIMPLE_MAP) {
										attributes.setMapAttribute(newId, att,
												attributes.getMapAttribute(
														oldId, att));
									}
								}
							}

							// add attribute for copied nodes, including the
							// original
							attributes.setAttribute(oldId, NODE_COPIED, true);
							attributes.setAttribute(newId, NODE_COPIED, true);

							// copy edges
							RootGraph rootGraph = Cytoscape.getRootGraph();

							int[] edges = Cytoscape.getCurrentNetwork()
									.getAdjacentEdgeIndicesArray(
											oldNode.getRootGraphIndex(), true,
											true, true);
							for (int oldEdge : edges) {
								int source = rootGraph
										.getEdgeSourceIndex(oldEdge);
								int target = rootGraph
										.getEdgeTargetIndex(oldEdge);
								if (source == oldNode.getRootGraphIndex()) {
									source = newNode.getRootGraphIndex();
								} else if (target == oldNode
										.getRootGraphIndex()) {
									target = newNode.getRootGraphIndex();
								}

								int newEdge = rootGraph.createEdge(source,
										target);
								Cytoscape.getCurrentNetwork().addEdge(newEdge);

								// TODO: copy edge attributes??
							}

							// increment hashmap count
							nvSeen.put(nv, (nvSeen.get(nv) + 1));
							// update nv reference
							Cytoscape.getCurrentNetwork().addNode(newNode);
							nv = Cytoscape.getCurrentNetworkView().getNodeView(
									newNode);
							// adding new node to region nvList
							r.addFilteredNodeView(nv);
						} else {
							// no, then add to tracking list
							nvSeen.put(nv, 1);
							r.addFilteredNodeView(nv);
							attributes.setAttribute(nv.getNode()
									.getIdentifier(), NODE_COPIED, false);
							attributes.setAttribute(nv.getNode()
									.getIdentifier(), REGION_ATT, r
									.getAttValue());
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

					taskMonitor.setStatus("Performing layout within regions");
					// force directed, selected-only, then scale
					if (r.getShape() != Region.MEMBRANE_LINE
							&& filteredNodeViews.size() > 3) {

						// TODO: There must be a better way of deselecting all
						// nodes!?
						Collection<CyNode> allNodes = Cytoscape
								.getCyNodesList();
						Cytoscape.getCurrentNetwork().setSelectedNodeState(
								allNodes, false);

						Cytoscape.getCurrentNetwork().setSelectedNodeState(
								regionNodes, true);

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

							nv.setOffset(r.getFreeCenterX() + regionUnitX
									* scaleX, r.getFreeCenterY() + regionUnitY
									* scaleY);
						}

						// cleanup
						Cytoscape.getCurrentNetwork().setSelectedNodeState(
								regionNodes, false);
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
							 * returns node views that are within the bounds of
							 * this region
							 */
							nvToMove = Region.bounded(nvToCheck, r);

							/*
							 * determine closest edge per excludedNodeView and
							 * move node to mirror distance relative to new
							 * region
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
								 * distance between node and boundary around all
								 * regions
								 */
								double bufferX = MFNodeAppearanceCalculator.FEATURE_NODE_WIDTH;
								double bufferY = MFNodeAppearanceCalculator.FEATURE_NODE_HEIGHT;
								double farNorth = nvY
										- (r.getRegionTop() - bufferY);
								double farSouth = (r.getRegionBottom() + bufferY)
										- nvY;
								double farWest = nvX
										- (r.getRegionLeft() - bufferX);
								double farEast = (r.getRegionRight() + bufferX)
										- nvX;

								/*
								 * rule out directions where there is not enough
								 * room in overlapped region
								 */
								if (!(((r.getRegionTop() - bufferY) - or
										.getFreeTop()) > bufferY)) {
									farNorth = Double.MAX_VALUE;
								}
								if (!(((r.getRegionLeft() - bufferX) - or
										.getFreeLeft()) > bufferX)) {
									farWest = Double.MAX_VALUE;
								}
								if (!((or.getFreeBottom() - (r
										.getRegionBottom() + bufferY)) > bufferY)) {
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
												if (pN <= nvX
														+ distanceBetweenNodes
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
												if (pW <= nvY
														+ distanceBetweenNodes
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
											if (pE <= nvY
													+ distanceBetweenNodes / 2
													&& pE >= nvY
															- distanceBetweenNodes
															/ 2) {
												countE++;
											}
										}
										nv.setXPosition(nvX
												+ (farEast + countE
														* distanceBetweenNodes
														/ 2));
										pastE.add(nvY);
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
												- (farWest + countW
														* distanceBetweenNodes
														/ 2));
										pastW.add(nvY);
									}

								} else if (farSouth < farEast) {
									if (farSouth < farWest) {
										countS = 1;
										for (double pS : pastN) {
											if (pS <= nvX
													+ distanceBetweenNodes / 2
													&& pS >= nvX
															- distanceBetweenNodes
															/ 2) {
												countS++;
											}
										}
										nv.setYPosition(nvY
												+ (farSouth + countS
														* distanceBetweenNodes
														/ 2));
										pastS.add(nvX);
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
												- (farWest + countW
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
													+ (farEast + countE
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
													- (farWest + countW
															* distanceBetweenNodes
															/ 2));
									pastW.add(nvY);
								}
							}

						}
					} // end oil & water

				} // end for each region

				// destroy all register nodes
				for (NodeView regNv : nvRegList) {
					Cytoscape.getCurrentNetwork().removeNode(
							regNv.getNode().getRootGraphIndex(), true);
				}

				// rename first copies of replicated nodes
				CyAttributes attributes = Cytoscape.getNodeAttributes();
				for (Node n : firstCopies) {

					String oldId = n.getIdentifier();
					HashMap<String, Object> atthash = new HashMap<String, Object>();

					// copy attributes
					String[] atts = attributes.getAttributeNames();
					for (String att : atts) {
						/*
						 * skip hidden attributes
						 */
						if (attributes.getUserVisible(att)
								&& attributes.hasAttribute(oldId, att)) {
							byte type = attributes.getType(att);
							if (type == CyAttributes.TYPE_BOOLEAN) {
								atthash.put(att, attributes
										.getBooleanAttribute(oldId, att));
							} else if (type == CyAttributes.TYPE_INTEGER) {
								atthash.put(att, attributes
										.getIntegerAttribute(oldId, att));
							} else if (type == CyAttributes.TYPE_FLOATING) {
								atthash.put(att, attributes.getDoubleAttribute(
										oldId, att));
							} else if (type == CyAttributes.TYPE_STRING) {
								atthash.put(att, attributes.getStringAttribute(
										oldId, att));

							} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
								atthash.put(att, attributes.getListAttribute(
										oldId, att));
							} else if (type == CyAttributes.TYPE_SIMPLE_MAP) {
								atthash.put(att, attributes.getMapAttribute(
										oldId, att));
							}
						}
					}

					String cleanOldId = n.getIdentifier();
					if (oldId.contains("__1")) {
						// clean up name from prior runs of GO Layout
						cleanOldId = n.getIdentifier().substring(0,
								oldId.lastIndexOf("__1"));
					}
					n.setIdentifier(cleanOldId.concat("__1"));
					String newId = n.getIdentifier();

					for (String att : atthash.keySet()) {
						/*
						 * skip hidden attributes
						 */
						if (attributes.getUserVisible(att)
								&& attributes.hasAttribute(oldId, att)) {
							byte type = attributes.getType(att);
							if (type == CyAttributes.TYPE_BOOLEAN) {
								attributes.setAttribute(newId, att,
										(Boolean) atthash.get(att));
							} else if (type == CyAttributes.TYPE_INTEGER) {
								attributes.setAttribute(newId, att,
										(Integer) atthash.get(att));
							} else if (type == CyAttributes.TYPE_FLOATING) {
								attributes.setAttribute(newId, att,
										(Double) atthash.get(att));
							} else if (type == CyAttributes.TYPE_STRING) {
								attributes.setAttribute(newId, att,
										(String) atthash.get(att));
							} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
								attributes.setListAttribute(newId, att,
										(List) atthash.get(att));
							} else if (type == CyAttributes.TYPE_SIMPLE_MAP) {
								attributes.setMapAttribute(newId, att,
										(HashMap) atthash.get(att));
							}
						}
					}
				}
				
				// prune and annotate edges
				List<Integer> edgesToRemove = new ArrayList<Integer>();
				CyAttributes nAttributes = Cytoscape.getNodeAttributes();
				CyAttributes eAttributes = Cytoscape.getEdgeAttributes();

				int[] edges = Cytoscape.getCurrentNetwork()
						.getEdgeIndicesArray();
				for (int edgeInt : edges) {
					int nodeInt1 = Cytoscape.getRootGraph().getEdgeSourceIndex(
							edgeInt);
					int nodeInt2 = Cytoscape.getRootGraph().getEdgeTargetIndex(
							edgeInt);
					String node1 = Cytoscape.getCurrentNetwork().getNode(
							nodeInt1).getIdentifier();
					String node2 = Cytoscape.getCurrentNetwork().getNode(
							nodeInt2).getIdentifier();
					String nodeRegion1 = nAttributes.getStringAttribute(node1,
							REGION_ATT);
					String nodeRegion2 = nAttributes.getStringAttribute(node2,
							REGION_ATT);

					// annotate edges to and from unassigned nodes

					String edgeId = Cytoscape.getRootGraph().getEdge(edgeInt)
							.getIdentifier();
					if (nodeRegion1 == "unassigned"
							|| nodeRegion2 == "unassigned") {
						eAttributes.setAttribute(edgeId, UNASSIGNED_EDGE_ATT,
								true);
					} else {
						eAttributes.setAttribute(edgeId, UNASSIGNED_EDGE_ATT,
								false);
					}
					
		            /* Delete edge anchors */
					Cytoscape.getCurrentNetworkView().getEdgeView(Cytoscape.getRootGraph().getEdge(edgeInt)).getBend().removeAllHandles();

					if (pruneEdges) {
						taskMonitor.setStatus("Pruning cross-region edges");

						boolean doRemoveEdges = false;
						List<Integer> edgesToHold = new ArrayList<Integer>();
						if (nodeRegion1 != nodeRegion2) {
							edgesToHold.add(edgeInt);
							// add to remove list and search all other copies
						}
						int m = 1;
						if (node2.contains("__")) {
							node2 = node2.substring(0, node2.lastIndexOf("__"));
						}
						node2 = node2.concat("__").concat("" + m);
						CyNode cyn = Cytoscape.getCyNode(node2);
						while (cyn != null) {
							String nodeRegion2B = nAttributes
									.getStringAttribute(node2, REGION_ATT);
							if (nodeRegion1 != nodeRegion2B
									&& nodeRegion2B != null) {
								int[] copyEdges = Cytoscape.getCurrentNetwork()
										.getAdjacentEdgeIndicesArray(
												cyn.getRootGraphIndex(), true,
												true, true);
								for (int copyEdgeInt : copyEdges) {
									int copyNodeInt1 = Cytoscape.getRootGraph()
											.getEdgeSourceIndex(copyEdgeInt);
									int copyNodeInt2 = Cytoscape.getRootGraph()
											.getEdgeTargetIndex(copyEdgeInt);
									if (nodeInt1 == copyNodeInt1
											|| nodeInt1 == copyNodeInt2) {
										edgesToHold.add(copyEdgeInt);
										break; // there will only be a
										// single hit
									}

								}
							} else if (nodeRegion2B != null) { // edge
								// within
								// same region!
								doRemoveEdges = true;
							}

							m++;
							node2 = node2.substring(0, node2.lastIndexOf("__"));
							node2 = node2.concat("__").concat("" + m);
							cyn = Cytoscape.getCyNode(node2);
						}
						if (doRemoveEdges) {
							for (int e : edgesToHold) {
								Cytoscape.getCurrentNetwork().removeEdge(e,
										false);
							}
						}
						// now check for the reverse case
						m = 1;
						if (node1.contains("__")) {
							node1 = node1.substring(0, node1.lastIndexOf("__"));
						}
						node1 = node1.concat("__").concat("" + m);
						cyn = Cytoscape.getCyNode(node1);
						while (cyn != null) {
							String nodeRegion1B = nAttributes
									.getStringAttribute(node1, REGION_ATT);
							if (nodeRegion1B != nodeRegion2
									&& nodeRegion1B != null) {
								int[] copyEdges = Cytoscape.getCurrentNetwork()
										.getAdjacentEdgeIndicesArray(
												cyn.getRootGraphIndex(), true,
												true, true);
								for (int copyEdgeInt : copyEdges) {
									int copyNodeInt1 = Cytoscape.getRootGraph()
											.getEdgeSourceIndex(copyEdgeInt);
									int copyNodeInt2 = Cytoscape.getRootGraph()
											.getEdgeTargetIndex(copyEdgeInt);
									if (nodeInt2 == copyNodeInt1
											|| nodeInt2 == copyNodeInt2) {
										edgesToHold.add(copyEdgeInt);
										break; // there will only be a
										// single hit
									}

								}
							} else if (nodeRegion1B != null) { // edge
								// within
								// same region!
								doRemoveEdges = true;
							}

							m++;
							node1 = node1.substring(0, node1.lastIndexOf("__"));
							node1 = node1.concat("__").concat("" + m);
							cyn = Cytoscape.getCyNode(node1);
						}
						if (doRemoveEdges) {
							for (int e : edgesToHold) {
								edgesToRemove.add(e);
							}
						}

					}

				}
				for (int e : edgesToRemove) {
					Cytoscape.getCurrentNetwork().removeEdge(e, false);
				}

				Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
				
				// update quick find indexes
				Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, "GO Layout", Cytoscape.getCurrentNetwork());
			}
		}
	}

	public static List<NodeView> populateNodeViews(Region r) {
		CyAttributes attribs = Cytoscape.getNodeAttributes();
		// attribs.setUserVisible(REGION_ATT, false);
		Iterator<Node> it = Cytoscape.getCurrentNetwork().nodesIterator();
		List<NodeView> nvList = new ArrayList<NodeView>();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			String val = null;
			String terms[] = new String[1];
			// add support for parsing List type attributes
			if (attribs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
				List<Object> valList = attribs.getListAttribute(node
						.getIdentifier(), attributeName);
				// iterate through all elements in the list
				if (valList != null && valList.size() > 0) {
					terms = new String[valList.size()];
					for (int i = 0; i < valList.size(); i++) {
						Object o = valList.get(i);
						terms[i] = o.toString();
					}
				}
				val = join(terms);
			} else {
				String valCheck = attribs.getStringAttribute(node
						.getIdentifier(), attributeName);
				if (valCheck != null && !valCheck.equals("")) {
					val = valCheck;
				}
			}

			// loop through elements in array below and match
			if ((!(val == null) && (!val.equals("null")) && (val.length() > 0))) {
				for (Object o : r.getNestedAttValues()) {
					if (val.indexOf(o.toString()) >= 0) {
						if (r.getAttValue().equals("unassigned")
								&& val.length() > 1) {
							break; // skip to next, more specific, value
						} else {
							nvList.add(Cytoscape.getCurrentNetworkView()
									.getNodeView(node));
							break; // stop searching after first hit
						}
					}

				}
			} else if (r.getAttValue().equals("unassigned")) {
				nvList.add(Cytoscape.getCurrentNetworkView().getNodeView(node));
			}

		}
		return nvList;
	}

	/**
	 * generates comma-separated list as a single string
	 * 
	 * @param values
	 *            array
	 * @return string
	 */
	private static String join(String values[]) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			buf.append(values[i]);
			if (i < values.length - 1) {
				buf.append(", ");
			}
		}
		return buf.toString();
	}

}
