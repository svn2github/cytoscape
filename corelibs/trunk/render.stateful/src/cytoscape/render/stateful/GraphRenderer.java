
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
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

package cytoscape.render.stateful;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Iterator;

import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.geom.spacial.SpacialIndex2D;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;


/**
 * This class contains a chunk of procedural code that stitches together
 * several external modules in an effort to efficiently render graphs.
 */
public final class GraphRenderer {
	/**
	 * A bit representing....
	 */
	public final static int LOD_HIGH_DETAIL = 0x1;

	/**
	 * DOCUMENT ME!
	 */
	public final static int LOD_NODE_BORDERS = 0x2;

	/**
	 * DOCUMENT ME!
	 */
	public final static int LOD_NODE_LABELS = 0x4;

	/**
	 * DOCUMENT ME!
	 */
	public final static int LOD_EDGE_ARROWS = 0x8;

	/**
	 * DOCUMENT ME!
	 */
	public final static int LOD_DASHED_EDGES = 0x10;

	/**
	 * DOCUMENT ME!
	 */
	public final static int LOD_EDGE_ANCHORS = 0x20;

	/**
	 * DOCUMENT ME!
	 */
	public final static int LOD_EDGE_LABELS = 0x40;

	/**
	 * DOCUMENT ME!
	 */
	public final static int LOD_TEXT_AS_SHAPE = 0x80;

	/**
	 * DOCUMENT ME!
	 */
	public final static int LOD_CUSTOM_GRAPHICS = 0x100;

	// No constructor.
	private GraphRenderer() {
	}

	/**
	 * Renders a graph.
	 * @param graph the graph topology; nodes in this graph must correspond to
	 *   objKeys in nodePositions (the SpacialIndex2D parameter) and vice versa.
	 * @param nodePositions defines the positions and extents of nodes in graph;
	 *   each entry (objKey) in this structure must correspond to a node in graph
	 *   (the FixedGraph parameter) and vice versa; the order in which nodes are
	 *   rendered is defined by a non-reversed overlap query on this structure.
	 * @param lod defines the different levels of detail; an appropriate level
	 *   of detail is chosen based on the results of method calls on this
	 *   object.
	 * @param nodeDetails defines details of nodes such as colors, node border
	 *   thickness, and shape; the node arguments passed to methods on this
	 *   object will be nodes in the graph parameter.
	 * @param edgeDetails defines details of edges such as colors, thickness,
	 *   and arrow type; the edge arguments passed to methods on this
	 *   object will be edges in the graph parameter.
	 * @param nodeBuff this is a computational helper that is required in the
	 *   implementation of this method; when this method returns, nodeBuff is
	 *   in a state such that an edge in graph has been rendered by this method
	 *   if and only if it touches at least one node in this nodeBuff set;
	 *   no guarantee made regarding edgeless nodes.
	 * @param grafx the graphics context that is to render this graph.
	 * @param bgPaint the background paint to use when calling grafx.clear().
	 * @param xCenter the xCenter parameter to use when calling grafx.clear().
	 * @param yCenter the yCenter parameter to use when calling grafx.clear().
	 * @param scaleFactor the scaleFactor parameter to use when calling
	 *   grafx.clear().
	 * @return bits representing the level of detail that was rendered; the
	 *   return value is a bitwise-or'ed value of the LOD_* constants.
	 */
	public final static int renderGraph(final FixedGraph graph, final SpacialIndex2D nodePositions,
	                                    final GraphLOD lod, final NodeDetails nodeDetails,
	                                    final EdgeDetails edgeDetails, final IntHash nodeBuff,
	                                    final GraphGraphics grafx, final Paint bgPaint,
	                                    final double xCenter, final double yCenter,
	                                    final double scaleFactor) {
		nodeBuff.empty(); // Make sure we keep our promise.

		// Define the visible window in node coordinate space.
		final float xMin;

		// Define the visible window in node coordinate space.
		final float yMin;

		// Define the visible window in node coordinate space.
		final float xMax;

		// Define the visible window in node coordinate space.
		final float yMax;
		xMin = (float) (xCenter - ((0.5d * grafx.image.getWidth(null)) / scaleFactor));
		yMin = (float) (yCenter - ((0.5d * grafx.image.getHeight(null)) / scaleFactor));
		xMax = (float) (xCenter + ((0.5d * grafx.image.getWidth(null)) / scaleFactor));
		yMax = (float) (yCenter + ((0.5d * grafx.image.getHeight(null)) / scaleFactor));

		// Define buffers.  These are of the few objects we're instantiating
		// directly in this method.
		final float[] floatBuff1;

		// Define buffers.  These are of the few objects we're instantiating
		// directly in this method.
		final float[] floatBuff2;

		// Define buffers.  These are of the few objects we're instantiating
		// directly in this method.
		final float[] floatBuff3;

		// Define buffers.  These are of the few objects we're instantiating
		// directly in this method.
		final float[] floatBuff4;

		// Define buffers.  These are of the few objects we're instantiating
		// directly in this method.
		final float[] floatBuff5;
		final double[] doubleBuff1;
		final double[] doubleBuff2;
		final GeneralPath path2d;
		floatBuff1 = new float[4];
		floatBuff2 = new float[4];
		floatBuff3 = new float[2];
		floatBuff4 = new float[2];
		floatBuff5 = new float[8];
		doubleBuff1 = new double[4];
		doubleBuff2 = new double[2];
		path2d = new GeneralPath();

		// Determine the number of nodes and edges that we are about to render.
		final int renderNodeCount;
		final int renderEdgeCount;
		final byte renderEdges;

		{
			final SpacialEntry2DEnumerator nodeHits = nodePositions.queryOverlap(xMin, yMin, xMax,
			                                                                     yMax, null, 0,
			                                                                     false);
			final int visibleNodeCount = nodeHits.numRemaining();
			final int totalNodeCount = graph.nodes().numRemaining();
			final int totalEdgeCount = graph.edges().numRemaining();
			renderEdges = lod.renderEdges(visibleNodeCount, totalNodeCount, totalEdgeCount);

			if (renderEdges > 0) {
				int runningNodeCount = 0;

				for (int i = 0; i < visibleNodeCount; i++) {
					nodeHits.nextExtents(floatBuff1, 0);

					if ((floatBuff1[0] != floatBuff1[2]) && (floatBuff1[1] != floatBuff1[3]))
						runningNodeCount++;
				}

				renderNodeCount = runningNodeCount;
				renderEdgeCount = totalEdgeCount;
			} else if (renderEdges < 0) {
				int runningNodeCount = 0;

				for (int i = 0; i < visibleNodeCount; i++) {
					nodeHits.nextExtents(floatBuff1, 0);

					if ((floatBuff1[0] != floatBuff1[2]) && (floatBuff1[1] != floatBuff1[3]))
						runningNodeCount++;
				}

				renderNodeCount = runningNodeCount;
				renderEdgeCount = 0;
			} else {
				int runningNodeCount = 0;
				int runningEdgeCount = 0;

				for (int i = 0; i < visibleNodeCount; i++) {
					final int node = nodeHits.nextExtents(floatBuff1, 0);

					if ((floatBuff1[0] != floatBuff1[2]) && (floatBuff1[1] != floatBuff1[3]))
						runningNodeCount++;

					final IntEnumerator touchingEdges = graph.edgesAdjacent(node, true, true, true);
					final int touchingEdgeCount = touchingEdges.numRemaining();

					for (int j = 0; j < touchingEdgeCount; j++) {
						final int edge = touchingEdges.nextInt();
						final int otherNode = node ^ graph.edgeSource(edge)
						                      ^ graph.edgeTarget(edge);

						if (nodeBuff.get(otherNode) < 0)
							runningEdgeCount++;
					}

					nodeBuff.put(node);
				}

				renderNodeCount = runningNodeCount;
				renderEdgeCount = runningEdgeCount;
				nodeBuff.empty();
			}
		}

		// Based on number of objects we are going to render, determine LOD.
		final int lodBits;

		{
			int lodTemp = 0;

			if (lod.detail(renderNodeCount, renderEdgeCount)) {
				lodTemp |= LOD_HIGH_DETAIL;

				if (lod.nodeBorders(renderNodeCount, renderEdgeCount))
					lodTemp |= LOD_NODE_BORDERS;

				if (lod.nodeLabels(renderNodeCount, renderEdgeCount))
					lodTemp |= LOD_NODE_LABELS;

				if (lod.edgeArrows(renderNodeCount, renderEdgeCount))
					lodTemp |= LOD_EDGE_ARROWS;

				if (lod.dashedEdges(renderNodeCount, renderEdgeCount))
					lodTemp |= LOD_DASHED_EDGES;

				if (lod.edgeAnchors(renderNodeCount, renderEdgeCount))
					lodTemp |= LOD_EDGE_ANCHORS;

				if (lod.edgeLabels(renderNodeCount, renderEdgeCount))
					lodTemp |= LOD_EDGE_LABELS;

				if ((((lodTemp & LOD_NODE_LABELS) != 0) || ((lodTemp & LOD_EDGE_LABELS) != 0))
				    && lod.textAsShape(renderNodeCount, renderEdgeCount))
					lodTemp |= LOD_TEXT_AS_SHAPE;

				if (lod.customGraphics(renderNodeCount, renderEdgeCount))
					lodTemp |= LOD_CUSTOM_GRAPHICS;
			}

			lodBits = lodTemp;
		}
		// Clear the background.
		{
			grafx.clear(bgPaint, xCenter, yCenter, scaleFactor);
		}

		// Render the edges first.  No edge shall be rendered twice.  Render edge
		// labels.  A label is not necessarily on top of every edge; it is only
		// on top of the edge it belongs to.
		if (renderEdges >= 0) {
			final SpacialEntry2DEnumerator nodeHits;

			if (renderEdges > 0)
				// We want to render edges in the same order (back to front) that
				// we would use to render just edges on visible nodes; this is assuming
				// that our spacial index has the subquery order-preserving property.
				nodeHits = nodePositions.queryOverlap(Float.NEGATIVE_INFINITY,
				                                      Float.NEGATIVE_INFINITY,
				                                      Float.POSITIVE_INFINITY,
				                                      Float.POSITIVE_INFINITY, null, 0, false);
			else
				nodeHits = nodePositions.queryOverlap(xMin, yMin, xMax, yMax, null, 0, false);

			if ((lodBits & LOD_HIGH_DETAIL) == 0) { // Low detail.

				final int nodeHitCount = nodeHits.numRemaining();

				for (int i = 0; i < nodeHitCount; i++) {
					final int node = nodeHits.nextExtents(floatBuff1, 0);

					// Casting to double and then back we could achieve better accuracy
					// at the expense of performance.
					final float nodeX = (floatBuff1[0] + floatBuff1[2]) / 2;
					final float nodeY = (floatBuff1[1] + floatBuff1[3]) / 2;

					final IntEnumerator touchingEdges = graph.edgesAdjacent(node, true, true, true);
					final int touchingEdgeCount = touchingEdges.numRemaining();

					for (int j = 0; j < touchingEdgeCount; j++) {
						final int edge = touchingEdges.nextInt();
						final int otherNode = node ^ graph.edgeSource(edge)
						                      ^ graph.edgeTarget(edge);

						if (nodeBuff.get(otherNode) < 0) { // Has not yet been rendered.
							nodePositions.exists(otherNode, floatBuff2, 0);
							grafx.drawEdgeLow(nodeX, nodeY, 
							// Again, casting issue - tradeoff between
							// accuracy and performance.
							(floatBuff2[0] + floatBuff2[2]) / 2,
							                  (floatBuff2[1] + floatBuff2[3]) / 2,
							                  edgeDetails.colorLowDetail(edge));
						}
					}

					nodeBuff.put(node);
				}
			} else { // High detail.

				while (nodeHits.numRemaining() > 0) {
					final int node = nodeHits.nextExtents(floatBuff1, 0);
					final byte nodeShape = nodeDetails.shape(node);
					final IntEnumerator touchingEdges = graph.edgesAdjacent(node, true, true, true);

					while (touchingEdges.numRemaining() > 0) {
						final int edge = touchingEdges.nextInt();
						final int otherNode = node ^ graph.edgeSource(edge)
						                      ^ graph.edgeTarget(edge);

						if (nodeBuff.get(otherNode) < 0) { // Has not yet been rendered.

							if (!nodePositions.exists(otherNode, floatBuff2, 0))
								throw new IllegalStateException("nodePositions not recognizing node that exists in graph");

							final byte otherNodeShape = nodeDetails.shape(otherNode);

							// Compute node shapes, center positions, and extents.
							final byte srcShape;

							// Compute node shapes, center positions, and extents.
							final byte trgShape;
							final float[] srcExtents;
							final float[] trgExtents;

							if (node == graph.edgeSource(edge)) {
								srcShape = nodeShape;
								trgShape = otherNodeShape;
								srcExtents = floatBuff1;
								trgExtents = floatBuff2;
							} else { // node == graph.edgeTarget(edge).
								srcShape = otherNodeShape;
								trgShape = nodeShape;
								srcExtents = floatBuff2;
								trgExtents = floatBuff1;
							}

							// Compute visual attributes that do not depend on LOD.
							final float thickness = edgeDetails.segmentThickness(edge);
							final Stroke edgeStroke = edgeDetails.segmentStroke(edge);
							final Paint segPaint = edgeDetails.segmentPaint(edge);

							// Compute arrows.
							final byte srcArrow;

							// Compute arrows.
							final byte trgArrow;
							final float srcArrowSize;
							final float trgArrowSize;
							final Paint srcArrowPaint;
							final Paint trgArrowPaint;

							if ((lodBits & LOD_EDGE_ARROWS) == 0) { // Not rendering arrows.
								trgArrow = srcArrow = GraphGraphics.ARROW_NONE;
								trgArrowSize = srcArrowSize = 0.0f;
								trgArrowPaint = srcArrowPaint = null;
							} else { // Rendering edge arrows.
								srcArrow = edgeDetails.sourceArrow(edge);
								trgArrow = edgeDetails.targetArrow(edge);
								srcArrowSize = ((srcArrow == GraphGraphics.ARROW_NONE) 
								                 ? 0.0f
								                 : edgeDetails.sourceArrowSize(edge));
								trgArrowSize = ((trgArrow == GraphGraphics.ARROW_NONE)
								                 ? 0.0f
								                 : edgeDetails.targetArrowSize(edge));
								srcArrowPaint = ((srcArrow == GraphGraphics.ARROW_NONE)
								                 ? null : edgeDetails.sourceArrowPaint(edge));
								trgArrowPaint = ((trgArrow == GraphGraphics.ARROW_NONE)
								                 ? null : edgeDetails.targetArrowPaint(edge));
							}

							// Compute dash length.
//							final float dashLength = (((lodBits & LOD_DASHED_EDGES) == 0) ? 0.0f
//							                                                              : edgeDetails
//							                                                                .segmentDashLength(edge));

							// Compute the anchors to use when rendering edge.
							final EdgeAnchors anchors = (((lodBits & LOD_EDGE_ANCHORS) == 0) ? null
							                                                                 : edgeDetails
							                                                                   .anchors(edge));

							if (!computeEdgeEndpoints(grafx, srcExtents, srcShape, srcArrow,
							                          srcArrowSize, anchors, trgExtents, trgShape,
							                          trgArrow, trgArrowSize, floatBuff3, floatBuff4))
								continue;

							final float srcXAdj = floatBuff3[0];
							final float srcYAdj = floatBuff3[1];
							final float trgXAdj = floatBuff4[0];
							final float trgYAdj = floatBuff4[1];
//							grafx.drawEdgeFull(srcArrow, srcArrowSize, srcArrowPaint, trgArrow,
//							                   trgArrowSize, trgArrowPaint, srcXAdj, srcYAdj,
//							                   anchors, trgXAdj, trgYAdj, thickness, segPaint,
//							                   dashLength);
							grafx.drawEdgeFull(srcArrow, srcArrowSize, srcArrowPaint, trgArrow,
							                   trgArrowSize, trgArrowPaint, srcXAdj, srcYAdj,
							                   anchors, trgXAdj, trgYAdj, thickness, edgeStroke, segPaint);

							// Take care of edge anchor rendering.
							if (anchors != null) {
								for (int k = 0; k < anchors.numAnchors(); k++) {
									final float anchorSize;

									if ((anchorSize = edgeDetails.anchorSize(edge, k)) > 0.0f) {
										anchors.getAnchor(k, floatBuff4, 0);
										grafx.drawNodeFull(GraphGraphics.SHAPE_RECTANGLE,
										                   (float) (floatBuff4[0]
										                   - (anchorSize / 2.0d)),
										                   (float) (floatBuff4[1]
										                   - (anchorSize / 2.0d)),
										                   (float) (floatBuff4[0]
										                   + (anchorSize / 2.0d)),
										                   (float) (floatBuff4[1]
										                   + (anchorSize / 2.0d)),
										                   edgeDetails.anchorPaint(edge, k), 0.0f,
										                   null);
									}
								}
							}

							// Take care of label rendering.
							if ((lodBits & LOD_EDGE_LABELS) != 0) {
								final int labelCount = edgeDetails.labelCount(edge);

								for (int labelInx = 0; labelInx < labelCount; labelInx++) {
									final String text = edgeDetails.labelText(edge, labelInx);
									final Font font = edgeDetails.labelFont(edge, labelInx);
									final double fontScaleFactor = edgeDetails.labelScaleFactor(edge,
									                                                            labelInx);
									final Paint paint = edgeDetails.labelPaint(edge, labelInx);
									final byte textAnchor = edgeDetails.labelTextAnchor(edge,
									                                                    labelInx);
									final byte edgeAnchor = edgeDetails.labelEdgeAnchor(edge,
									                                                    labelInx);
									final float offsetVectorX = edgeDetails.labelOffsetVectorX(edge,
									                                                           labelInx);
									final float offsetVectorY = edgeDetails.labelOffsetVectorY(edge,
									                                                           labelInx);
									final byte justify;

									if (text.indexOf('\n') >= 0)
										justify = edgeDetails.labelJustify(edge, labelInx);
									else
										justify = NodeDetails.LABEL_WRAP_JUSTIFY_CENTER;

									final double edgeAnchorPointX;
									final double edgeAnchorPointY;

									final double edgeLabelWidth = edgeDetails.labelWidth(edge);

									if (edgeAnchor == EdgeDetails.EDGE_ANCHOR_SOURCE) {
										edgeAnchorPointX = srcXAdj;
										edgeAnchorPointY = srcYAdj;
									} else if (edgeAnchor == EdgeDetails.EDGE_ANCHOR_TARGET) {
										edgeAnchorPointX = trgXAdj;
										edgeAnchorPointY = trgYAdj;
									} else if (edgeAnchor == EdgeDetails.EDGE_ANCHOR_MIDPOINT) {
										grafx.getEdgePath(srcArrow, srcArrowSize, trgArrow,
										                  trgArrowSize, srcXAdj, srcYAdj, anchors,
										                  trgXAdj, trgYAdj, path2d);

										// Count the number of path segments.  This count
										// includes the initial SEG_MOVETO.  So, for example, a
										// path composed of 2 cubic curves would have a numPaths
										// of 3.  Note that numPaths will be at least 2 in all
										// cases.
										final int numPaths;

										{
											final PathIterator pathIter = path2d.getPathIterator(null);
											int numPathsTemp = 0;

											while (!pathIter.isDone()) {
												numPathsTemp++; // pathIter.currentSegment().
												pathIter.next();
											}

											numPaths = numPathsTemp;
										}

										// Compute "midpoint" of edge.
										if ((numPaths % 2) != 0) {
											final PathIterator pathIter = path2d.getPathIterator(null);

											for (int i = numPaths / 2; i > 0; i--)
												pathIter.next();

											final int subPathType = pathIter.currentSegment(floatBuff5);

											if (subPathType == PathIterator.SEG_LINETO) {
												edgeAnchorPointX = floatBuff5[0];
												edgeAnchorPointY = floatBuff5[1];
											} else if (subPathType == PathIterator.SEG_QUADTO) {
												edgeAnchorPointX = floatBuff5[2];
												edgeAnchorPointY = floatBuff5[3];
											} else if (subPathType == PathIterator.SEG_CUBICTO) {
												edgeAnchorPointX = floatBuff5[4];
												edgeAnchorPointY = floatBuff5[5];
											} else
												throw new IllegalStateException("got unexpected PathIterator segment type: "
												                                + subPathType);
										} else { // numPaths % 2 == 0.

											final PathIterator pathIter = path2d.getPathIterator(null);

											for (int i = numPaths / 2; i > 0; i--) {
												if (i == 1) {
													final int subPathType = pathIter.currentSegment(floatBuff5);

													if ((subPathType == PathIterator.SEG_MOVETO)
													    || (subPathType == PathIterator.SEG_LINETO)) {
														floatBuff5[6] = floatBuff5[0];
														floatBuff5[7] = floatBuff5[1];
													} else if (subPathType == PathIterator.SEG_QUADTO) {
														floatBuff5[6] = floatBuff5[2];
														floatBuff5[7] = floatBuff5[3];
													} else if (subPathType == PathIterator.SEG_CUBICTO) {
														floatBuff5[6] = floatBuff5[4];
														floatBuff5[7] = floatBuff5[5];
													} else
														throw new IllegalStateException("got unexpected PathIterator segment type: "
														                                + subPathType);
												}

												pathIter.next();
											}

											final int subPathType = pathIter.currentSegment(floatBuff5);

											if (subPathType == PathIterator.SEG_LINETO) {
												edgeAnchorPointX = (0.5d * floatBuff5[6])
												                   + (0.5d * floatBuff5[0]);
												edgeAnchorPointY = (0.5d * floatBuff5[7])
												                   + (0.5d * floatBuff5[1]);
											} else if (subPathType == PathIterator.SEG_QUADTO) {
												edgeAnchorPointX = (0.25d * floatBuff5[6])
												                   + (0.5d * floatBuff5[0])
												                   + (0.25d * floatBuff5[2]);
												edgeAnchorPointY = (0.25d * floatBuff5[7])
												                   + (0.5d * floatBuff5[1])
												                   + (0.25d * floatBuff5[3]);
											} else if (subPathType == PathIterator.SEG_CUBICTO) {
												edgeAnchorPointX = (0.125d * floatBuff5[6])
												                   + (0.375d * floatBuff5[0])
												                   + (0.375d * floatBuff5[2])
												                   + (0.125d * floatBuff5[4]);
												edgeAnchorPointY = (0.125d * floatBuff5[7])
												                   + (0.375d * floatBuff5[1])
												                   + (0.375d * floatBuff5[3])
												                   + (0.125d * floatBuff5[5]);
											} else
												throw new IllegalStateException("got unexpected PathIterator segment type: "
												                                + subPathType);
										}
									} else
										throw new IllegalStateException("encountered an invalid EDGE_ANCHOR_* constant: "
										                                + edgeAnchor);

									final MeasuredLineCreator measuredText = 
										new MeasuredLineCreator(text,font,
										                         grafx.getFontRenderContextFull(),
										                         fontScaleFactor, 
										                         (lodBits&LOD_TEXT_AS_SHAPE)!= 0, 
										                         edgeLabelWidth);

									doubleBuff1[0] = -0.5d * measuredText.getMaxLineWidth();
									doubleBuff1[1] = -0.5d * measuredText.getTotalHeight(); 
									doubleBuff1[2] = 0.5d * measuredText.getMaxLineWidth(); 
									doubleBuff1[3] = 0.5d * measuredText.getTotalHeight(); 
									lemma_computeAnchor(textAnchor, doubleBuff1, doubleBuff2);

									final double textXCenter = edgeAnchorPointX - doubleBuff2[0]
									                           + offsetVectorX;
									final double textYCenter = edgeAnchorPointY - doubleBuff2[1]
									                           + offsetVectorY;
									TextRenderingUtils.renderHorizontalText(grafx, measuredText, 
									                                        font, fontScaleFactor,
									                                        (float) textXCenter,
									                                        (float) textYCenter,
									                                        justify, paint,
									                                        (lodBits
									                                        & LOD_TEXT_AS_SHAPE) != 0);
								}
							}
						}
					}

					nodeBuff.put(node);
				}
			}
		}
		// Render nodes and labels.  A label is not necessarily on top of every
		// node; it is only on top of the node it belongs to.
		{
			final SpacialEntry2DEnumerator nodeHits = nodePositions.queryOverlap(xMin, yMin, xMax,
			                                                                     yMax, null, 0,
			                                                                     false);

			if ((lodBits & LOD_HIGH_DETAIL) == 0) { // Low detail.

				final int nodeHitCount = nodeHits.numRemaining();

				for (int i = 0; i < nodeHitCount; i++) {
					final int node = nodeHits.nextExtents(floatBuff1, 0);

					if ((floatBuff1[0] != floatBuff1[2]) && (floatBuff1[1] != floatBuff1[3]))
						grafx.drawNodeLow(floatBuff1[0], floatBuff1[1], floatBuff1[2],
						                  floatBuff1[3], nodeDetails.colorLowDetail(node));
				}
			} else { // High detail.
				while (nodeHits.numRemaining() > 0) {
					final int node = nodeHits.nextExtents(floatBuff1, 0);
					
					renderNodeHigh(graph, grafx, node, floatBuff1, doubleBuff1, doubleBuff2, nodeDetails, lodBits);
				

					// Take care of label rendering.
					if ((lodBits & LOD_NODE_LABELS) != 0) { // Potential label rendering.

						final int labelCount = nodeDetails.labelCount(node);

						for (int labelInx = 0; labelInx < labelCount; labelInx++) {
							final String text = nodeDetails.labelText(node, labelInx);
							final Font font = nodeDetails.labelFont(node, labelInx);
							final double fontScaleFactor = nodeDetails.labelScaleFactor(node,
							                                                            labelInx);
							final Paint paint = nodeDetails.labelPaint(node, labelInx);
							final byte textAnchor = nodeDetails.labelTextAnchor(node, labelInx);
							final byte nodeAnchor = nodeDetails.labelNodeAnchor(node, labelInx);
							final float offsetVectorX = nodeDetails.labelOffsetVectorX(node,
							                                                           labelInx);
							final float offsetVectorY = nodeDetails.labelOffsetVectorY(node,
							                                                           labelInx);
							final byte justify;

							if (text.indexOf('\n') >= 0)
								justify = nodeDetails.labelJustify(node, labelInx);
							else
								justify = NodeDetails.LABEL_WRAP_JUSTIFY_CENTER;

							final double nodeLabelWidth = nodeDetails.labelWidth(node);

							doubleBuff1[0] = floatBuff1[0];
							doubleBuff1[1] = floatBuff1[1];
							doubleBuff1[2] = floatBuff1[2];
							doubleBuff1[3] = floatBuff1[3];
							lemma_computeAnchor(nodeAnchor, doubleBuff1, doubleBuff2);

							final double nodeAnchorPointX = doubleBuff2[0];
							final double nodeAnchorPointY = doubleBuff2[1];
							final MeasuredLineCreator measuredText = new MeasuredLineCreator(
							    text, font, grafx.getFontRenderContextFull(), fontScaleFactor,
							    (lodBits & LOD_TEXT_AS_SHAPE) != 0, nodeLabelWidth);

							doubleBuff1[0] = -0.5d * measuredText.getMaxLineWidth();
							doubleBuff1[1] = -0.5d * measuredText.getTotalHeight();
							doubleBuff1[2] = 0.5d * measuredText.getMaxLineWidth();
							doubleBuff1[3] = 0.5d * measuredText.getTotalHeight();
							lemma_computeAnchor(textAnchor, doubleBuff1, doubleBuff2);

							final double textXCenter = nodeAnchorPointX - doubleBuff2[0]
							                           + offsetVectorX;
							final double textYCenter = nodeAnchorPointY - doubleBuff2[1]
							                           + offsetVectorY;
							TextRenderingUtils.renderHorizontalText(grafx, measuredText, font,
							                                        fontScaleFactor,
							                                        (float) textXCenter,
							                                        (float) textYCenter, justify,
							                                        paint,
							                                        (lodBits & LOD_TEXT_AS_SHAPE) != 0);
						}
					}
				}
			}
		}

		return lodBits;
	}

	private final static void lemma_computeAnchor(final byte anchor, final double[] input4x,
	                                              final double[] rtrn2x) {
		switch (anchor) {
			case NodeDetails.ANCHOR_CENTER:
				rtrn2x[0] = (input4x[0] + input4x[2]) / 2.0d;
				rtrn2x[1] = (input4x[1] + input4x[3]) / 2.0d;

				break;

			case NodeDetails.ANCHOR_SOUTH:
				rtrn2x[0] = (input4x[0] + input4x[2]) / 2.0d;
				rtrn2x[1] = input4x[3];

				break;

			case NodeDetails.ANCHOR_SOUTHEAST:
				rtrn2x[0] = input4x[2];
				rtrn2x[1] = input4x[3];

				break;

			case NodeDetails.ANCHOR_EAST:
				rtrn2x[0] = input4x[2];
				rtrn2x[1] = (input4x[1] + input4x[3]) / 2.0d;

				break;

			case NodeDetails.ANCHOR_NORTHEAST:
				rtrn2x[0] = input4x[2];
				rtrn2x[1] = input4x[1];

				break;

			case NodeDetails.ANCHOR_NORTH:
				rtrn2x[0] = (input4x[0] + input4x[2]) / 2.0d;
				rtrn2x[1] = input4x[1];

				break;

			case NodeDetails.ANCHOR_NORTHWEST:
				rtrn2x[0] = input4x[0];
				rtrn2x[1] = input4x[1];

				break;

			case NodeDetails.ANCHOR_WEST:
				rtrn2x[0] = input4x[0];
				rtrn2x[1] = (input4x[1] + input4x[3]) / 2.0d;

				break;

			case NodeDetails.ANCHOR_SOUTHWEST:
				rtrn2x[0] = input4x[0];
				rtrn2x[1] = input4x[3];

				break;

			default:
				throw new IllegalStateException("encoutered an invalid ANCHOR_* constant: "
				                                + anchor);
		}
	}

	private final static float[] s_floatBuff = new float[2];

	/**
	 * DOCUMENT ME!
	 *
	 * @param grafx DOCUMENT ME!
	 * @param srcNodeExtents DOCUMENT ME!
	 * @param srcNodeShape DOCUMENT ME!
	 * @param srcArrow DOCUMENT ME!
	 * @param srcArrowSize DOCUMENT ME!
	 * @param anchors DOCUMENT ME!
	 * @param trgNodeExtents DOCUMENT ME!
	 * @param trgNodeShape DOCUMENT ME!
	 * @param trgArrow DOCUMENT ME!
	 * @param trgArrowSize DOCUMENT ME!
	 * @param rtnValSrc DOCUMENT ME!
	 * @param rtnValTrg DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final static boolean computeEdgeEndpoints(final GraphGraphics grafx,
	                                                 final float[] srcNodeExtents,
	                                                 final byte srcNodeShape, final byte srcArrow,
	                                                 final float srcArrowSize, EdgeAnchors anchors,
	                                                 final float[] trgNodeExtents,
	                                                 final byte trgNodeShape, final byte trgArrow,
	                                                 final float trgArrowSize,
	                                                 final float[] rtnValSrc,
	                                                 final float[] rtnValTrg) {
		final boolean alwaysCompute = true;

		if ((anchors != null) && (anchors.numAnchors() == 0))
			anchors = null;

		final float srcX = (float) ((((double) srcNodeExtents[0]) + srcNodeExtents[2]) / 2.0d);
		final float srcY = (float) ((((double) srcNodeExtents[1]) + srcNodeExtents[3]) / 2.0d);
		final float trgX = (float) ((((double) trgNodeExtents[0]) + trgNodeExtents[2]) / 2.0d);
		final float trgY = (float) ((((double) trgNodeExtents[1]) + trgNodeExtents[3]) / 2.0d);
		final float srcXOut;
		final float srcYOut;
		final float trgXOut;
		final float trgYOut;

		synchronized (s_floatBuff) {
			if (anchors == null) {
				srcXOut = trgX;
				srcYOut = trgY;
				trgXOut = srcX;
				trgYOut = srcY;
			} else {
				anchors.getAnchor(0, s_floatBuff, 0);
				srcXOut = s_floatBuff[0];
				srcYOut = s_floatBuff[1];
				anchors.getAnchor(anchors.numAnchors() - 1, s_floatBuff, 0);
				trgXOut = s_floatBuff[0];
				trgYOut = s_floatBuff[1];
			}
		}

		final float srcOffset;

		if (srcArrow == GraphGraphics.ARROW_DISC)
			srcOffset = 0.5f * srcArrowSize;
		else if (srcArrow == GraphGraphics.ARROW_TEE)
			srcOffset = srcArrowSize;
		else
			srcOffset = 0.0f;

		final float srcXAdj;
		final float srcYAdj;

		synchronized (s_floatBuff) {
			if ((srcNodeExtents[0] == srcNodeExtents[2])
			    || (srcNodeExtents[1] == srcNodeExtents[3])) {
				if (!_computeEdgeIntersection(srcX, srcY, srcOffset, srcXOut, srcYOut,
				                              alwaysCompute, s_floatBuff))
					return false;
			} else {
				if (!grafx.computeEdgeIntersection(srcNodeShape, srcNodeExtents[0],
				                                   srcNodeExtents[1], srcNodeExtents[2],
				                                   srcNodeExtents[3], srcOffset, srcXOut, srcYOut,
				                                   s_floatBuff)) {
					if (!alwaysCompute)
						return false;

					final float newSrcXOut;
					final float newSrcYOut;

					{ // Compute newSrcXOut and newSrcYOut.

						final double srcXCenter = (((double) srcNodeExtents[0]) + srcNodeExtents[2]) / 2.0d;
						final double srcYCenter = (((double) srcNodeExtents[1]) + srcNodeExtents[3]) / 2.0d;
						final double desiredDist = Math.max(((double) srcNodeExtents[2])
						                                    - srcNodeExtents[0],
						                                    ((double) srcNodeExtents[3])
						                                    - srcNodeExtents[1]) + srcOffset;
						final double dX = srcXOut - srcXCenter;
						final double dY = srcYOut - srcYCenter;
						final double len = Math.sqrt((dX * dX) + (dY * dY));

						if (len == 0.0d) {
							newSrcXOut = (float) (srcXOut + desiredDist);
							newSrcYOut = srcYOut;
						} else {
							newSrcXOut = (float) (((dX / len) * desiredDist) + srcXOut);
							newSrcYOut = (float) (((dY / len) * desiredDist) + srcYOut);
						}
					}

					grafx.computeEdgeIntersection(srcNodeShape, srcNodeExtents[0],
					                              srcNodeExtents[1], srcNodeExtents[2],
					                              srcNodeExtents[3], srcOffset, newSrcXOut,
					                              newSrcYOut, s_floatBuff);
				}
			}

			srcXAdj = s_floatBuff[0];
			srcYAdj = s_floatBuff[1];
		}

		final float trgOffset;

		if (trgArrow == GraphGraphics.ARROW_DISC)
			trgOffset = 0.5f * trgArrowSize;
		else if (trgArrow == GraphGraphics.ARROW_TEE)
			trgOffset = trgArrowSize;
		else
			trgOffset = 0.0f;

		final float trgXAdj;
		final float trgYAdj;

		synchronized (s_floatBuff) {
			if ((trgNodeExtents[0] == trgNodeExtents[2])
			    || (trgNodeExtents[1] == trgNodeExtents[3])) {
				if (!_computeEdgeIntersection(trgX, trgY, trgOffset, trgXOut, trgYOut,
				                              alwaysCompute, s_floatBuff))
					return false;
			} else {
				if (!grafx.computeEdgeIntersection(trgNodeShape, trgNodeExtents[0],
				                                   trgNodeExtents[1], trgNodeExtents[2],
				                                   trgNodeExtents[3], trgOffset, trgXOut, trgYOut,
				                                   s_floatBuff)) {
					if (!alwaysCompute)
						return false;

					final float newTrgXOut;
					final float newTrgYOut;

					{ // Compute newTrgXOut and newTrgYOut.

						final double trgXCenter = (((double) trgNodeExtents[0]) + trgNodeExtents[2]) / 2.0d;
						final double trgYCenter = (((double) trgNodeExtents[1]) + trgNodeExtents[3]) / 2.0d;
						final double desiredDist = Math.max(((double) trgNodeExtents[2])
						                                    - trgNodeExtents[0],
						                                    ((double) trgNodeExtents[3])
						                                    - trgNodeExtents[1]) + trgOffset;
						final double dX = trgXOut - trgXCenter;
						final double dY = trgYOut - trgYCenter;
						final double len = Math.sqrt((dX * dX) + (dY * dY));

						if (len == 0.0d) {
							newTrgXOut = (float) (trgXOut - desiredDist);
							newTrgYOut = trgYOut;
						} else {
							newTrgXOut = (float) (((dX / len) * desiredDist) + trgXOut);
							newTrgYOut = (float) (((dY / len) * desiredDist) + trgYOut);
						}
					}

					grafx.computeEdgeIntersection(trgNodeShape, trgNodeExtents[0],
					                              trgNodeExtents[1], trgNodeExtents[2],
					                              trgNodeExtents[3], trgOffset, newTrgXOut,
					                              newTrgYOut, s_floatBuff);
				}
			}

			trgXAdj = s_floatBuff[0];
			trgYAdj = s_floatBuff[1];
		}

		if ((anchors == null) && (!alwaysCompute)
		    && !((((((double) srcX) - trgX) * (((double) srcXAdj) - trgXAdj))
		         + ((((double) srcY) - trgY) * (((double) srcYAdj) - trgYAdj))) > 0.0d))

			// The direction of the chopped segment has flipped.
			return false;

		rtnValSrc[0] = srcXAdj;
		rtnValSrc[1] = srcYAdj;
		rtnValTrg[0] = trgXAdj;
		rtnValTrg[1] = trgYAdj;

		return true;
	}

	private final static float[] s_floatTemp = new float[6];
	private final static int[] s_segTypeBuff = new int[200];
	private final static float[] s_floatBuff2 = new float[1200];

	/**
	 * DOCUMENT ME!
	 *
	 * @param origPath DOCUMENT ME!
	 * @param rtnVal DOCUMENT ME!
	 */
	public final static void computeClosedPath(final PathIterator origPath, final GeneralPath rtnVal) {
		synchronized (s_floatTemp) {
			// First fill our buffers with the coordinates and segment types.
			int segs = 0;
			int offset = 0;

			if ((s_segTypeBuff[segs++] = origPath.currentSegment(s_floatTemp)) != PathIterator.SEG_MOVETO)
				throw new IllegalStateException("expected a SEG_MOVETO at the beginning of origPath");

			for (int i = 0; i < 2; i++)
				s_floatBuff2[offset++] = s_floatTemp[i];

			origPath.next();

			while (!origPath.isDone()) {
				final int segType = origPath.currentSegment(s_floatTemp);
				s_segTypeBuff[segs++] = segType;

				if ((segType == PathIterator.SEG_MOVETO) || (segType == PathIterator.SEG_CLOSE))
					throw new IllegalStateException("did not expect SEG_MOVETO or SEG_CLOSE");

				// This is a rare case where I rely on the actual constant values
				// to do a computation efficiently.
				final int coordCount = segType * 2;

				for (int i = 0; i < coordCount; i++)
					s_floatBuff2[offset++] = s_floatTemp[i];

				origPath.next();
			}

			rtnVal.reset();
			offset = 0;

			// Now add the forward path to rtnVal.
			for (int i = 0; i < segs; i++) {
				switch (s_segTypeBuff[i]) {
					case PathIterator.SEG_MOVETO:
						rtnVal.moveTo(s_floatBuff2[offset++], s_floatBuff2[offset++]);

						break;

					case PathIterator.SEG_LINETO:
						rtnVal.lineTo(s_floatBuff2[offset++], s_floatBuff2[offset++]);

						break;

					case PathIterator.SEG_QUADTO:
						rtnVal.quadTo(s_floatBuff2[offset++], s_floatBuff2[offset++],
						              s_floatBuff2[offset++], s_floatBuff2[offset++]);

						break;

					default: // PathIterator.SEG_CUBICTO.
						rtnVal.curveTo(s_floatBuff2[offset++], s_floatBuff2[offset++],
						               s_floatBuff2[offset++], s_floatBuff2[offset++],
						               s_floatBuff2[offset++], s_floatBuff2[offset++]);

						break;
				}
			}

			// Now add the return path.
			for (int i = segs - 1; i > 0; i--) {
				switch (s_segTypeBuff[i]) {
					case PathIterator.SEG_LINETO:
						offset -= 2;
						rtnVal.lineTo(s_floatBuff2[offset - 2], s_floatBuff2[offset - 1]);

						break;

					case PathIterator.SEG_QUADTO:
						offset -= 4;
						rtnVal.quadTo(s_floatBuff2[offset], s_floatBuff2[offset + 1],
						              s_floatBuff2[offset - 2], s_floatBuff2[offset - 1]);

						break;

					default: // PathIterator.SEG_CUBICTO.
						offset -= 6;
						rtnVal.curveTo(s_floatBuff2[offset + 2], s_floatBuff2[offset + 3],
						               s_floatBuff2[offset], s_floatBuff2[offset + 1],
						               s_floatBuff2[offset - 2], s_floatBuff2[offset - 1]);

						break;
				}
			}

			rtnVal.closePath();
		}
	}

	static final boolean _computeEdgeIntersection(final float nodeX, final float nodeY,
	                                              final float offset, final float ptX,
	                                              final float ptY, final boolean alwaysCompute,
	                                              final float[] returnVal) {
		if (offset == 0.0f) {
			returnVal[0] = nodeX;
			returnVal[1] = nodeY;

			return true;
		} else {
			final double dX = ptX - nodeX;
			final double dY = ptY - nodeY;
			final double len = Math.sqrt((dX * dX) + (dY * dY));

			if (len < offset) {
				if (!alwaysCompute)
					return false;

				if (len == 0.0d) {
					returnVal[0] = offset + nodeX;
					returnVal[1] = nodeY;

					return true;
				}
			}

			returnVal[0] = (float) (((dX / len) * offset) + nodeX);
			returnVal[1] = (float) (((dY / len) * offset) + nodeY);

			return true;
		}
	}
	
	
	private static final void renderNodeHigh(final FixedGraph graph, final GraphGraphics grafx, final int node, final float[] floatBuff1, final double[] doubleBuff1, final double[] doubleBuff2, final NodeDetails nodeDetails, final int lodBits) {
		if ((floatBuff1[0] != floatBuff1[2]) && (floatBuff1[1] != floatBuff1[3])) {
			// Compute visual attributes that do not depend on LOD.
			final byte shape = nodeDetails.shape(node);
			final Paint fillPaint = nodeDetails.fillPaint(node);

			// Compute node border information.
			final float borderWidth;
			final Paint borderPaint;

			if ((lodBits & LOD_NODE_BORDERS) == 0) { // Not rendering borders.
				borderWidth = 0.0f;
				borderPaint = null;
			} else { // Rendering node borders.
				borderWidth = nodeDetails.borderWidth(node);

				if (borderWidth == 0.0f)
					borderPaint = null;
				else
					borderPaint = nodeDetails.borderPaint(node);
			}

			// Draw the node.
			grafx.drawNodeFull(shape, floatBuff1[0], floatBuff1[1], floatBuff1[2], floatBuff1[3], fillPaint, borderWidth, borderPaint);
		}

		// Take care of custom graphic rendering.
		if ((lodBits & LOD_CUSTOM_GRAPHICS) != 0) {

			// draw any nested networks first
			final TexturePaint nestedNetworkPaint = nodeDetails.getNestedNetworkTexturePaint(node);
			if (nestedNetworkPaint != null) {
				doubleBuff1[0] = floatBuff1[0];
				doubleBuff1[1] = floatBuff1[1];
				doubleBuff1[2] = floatBuff1[2];
				doubleBuff1[3] = floatBuff1[3];
				lemma_computeAnchor(NodeDetails.ANCHOR_CENTER, doubleBuff1, doubleBuff2);
				grafx.drawCustomGraphicFull(nestedNetworkPaint.getAnchorRect(),  (float)doubleBuff2[0],  (float)doubleBuff2[1], nestedNetworkPaint); 
			}

			// draw custom graphics on top of nested networks 
			// don't allow our custom graphics to mutate while we iterate over them:
			synchronized (nodeDetails.customGraphicsLock(node)) {
				// This iterator will return CustomGraphics in rendering order:
				Iterator<CustomGraphic> dNodeIt = nodeDetails.customGraphics(node);
				CustomGraphic cg = null;
				// The graphic index used to retrieve non custom graphic info corresponds to the zero-based
				// index of the CustomGraphic returned by the iterator:
				int graphicInx = 0;
				while (dNodeIt.hasNext()) {
					cg = dNodeIt.next();
					final float offsetVectorX = nodeDetails.graphicOffsetVectorX(node, graphicInx);
					final float offsetVectorY = nodeDetails.graphicOffsetVectorY(node, graphicInx);
					doubleBuff1[0] = floatBuff1[0];
					doubleBuff1[1] = floatBuff1[1];
					doubleBuff1[2] = floatBuff1[2];
					doubleBuff1[3] = floatBuff1[3];
					lemma_computeAnchor(cg.getAnchor(), doubleBuff1, doubleBuff2);
					grafx.drawCustomGraphicFull(cg.getShape(), (float) (doubleBuff2[0] + offsetVectorX), (float) (doubleBuff2[1] + offsetVectorY),
								    cg.getPaint());
					graphicInx++;
				}
			}
		}
	}
}
