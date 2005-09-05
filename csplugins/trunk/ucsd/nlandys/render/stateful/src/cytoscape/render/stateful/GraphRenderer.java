package cytoscape.render.stateful;

import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.geom.spacial.SpacialIndex2D;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import java.awt.Color;

/**
 * This class contains a chunk of procedural code that stiches together
 * several external modules in an effort to efficiently render graphs.
 */
public final class GraphRenderer
{

  private final static int LOD_HIGH_DETAIL = 0x1;
  private final static int LOD_NODE_BORDERS = 0x2;
  private final static int LOD_NODE_LABELS = 0x4;
  private final static int LOD_TEXT_AS_SHAPE = 0x8;
  private final static int LOD_EDGE_ARROWS = 0x10;
  private final static int LOD_DASHED_EDGES = 0x20;
  private final static int LOD_EDGE_ANCHORS = 0x40;

  // No constructor.
  private GraphRenderer() { }

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
   *   implementation of this method; this method starts by emptying this
   *   hashtable, and when this method returns, the hashtable will contain
   *   exactly the nodes that were rendered; an edge is rendered by this method
   *   if and only if it touches at least one node in this nodeBuff set.
   * @param grafx the graphics context that is to render this graph.
   * @param bgColor the background color to use when calling grafx.clear().
   * @param xCenter the xCenter parameter to use when calling grafx.clear().
   * @param yCenter the yCenter parameter to use when calling grafx.clear().
   * @param scaleFactor the scaleFactor parameter to use when calling
   *   grafx.clear().
   */
  public final static void renderGraph(final FixedGraph graph,
                                       final SpacialIndex2D nodePositions,
                                       final GraphLOD lod,
                                       final NodeDetails nodeDetails,
                                       final EdgeDetails edgeDetails,
                                       final IntHash nodeBuff,
                                       final GraphGraphics grafx,
                                       final Color bgColor,
                                       final double xCenter,
                                       final double yCenter,
                                       final double scaleFactor)
  {
    // Define the visible window in node coordinate space.
    final float xMin, yMin, xMax, yMax;
    {
      xMin = (float)
        (xCenter - 0.5d * grafx.image.getWidth(null) / scaleFactor);
      yMin = (float)
        (yCenter - 0.5d * grafx.image.getHeight(null) / scaleFactor);
      xMax = (float)
        (xCenter + 0.5d * grafx.image.getWidth(null) / scaleFactor);
      yMax = (float)
        (yCenter + 0.5d * grafx.image.getHeight(null) / scaleFactor);
    }

    // Determine the number of nodes and edges that we are about to render.
    final int visibleNodeCount;
    final int visibleEdgeCount;
    {
      nodeBuff.empty();
      final SpacialEntry2DEnumerator nodeHits = nodePositions.queryOverlap
        (xMin, yMin, xMax, yMax, null, 0, false);
      int runningEdgeCount = 0;
      final int nodeHitCount = nodeHits.numRemaining();
      for (int i = 0; i < nodeHitCount; i++) {
        final int node = nodeHits.nextInt();
        final IntEnumerator touchingEdges =
          graph.edgesAdjacent(node, true, true, true);
        final int touchingEdgeCount = touchingEdges.numRemaining();
        for (int j = 0; j < touchingEdgeCount; j++) {
          final int edge = touchingEdges.nextInt();
          final int otherNode =
            node ^ graph.edgeSource(edge) ^ graph.edgeTarget(edge);
          if (nodeBuff.get(otherNode) < 0) { runningEdgeCount++; } }
        nodeBuff.put(node); }
      visibleNodeCount = nodeBuff.size();
      visibleEdgeCount = runningEdgeCount;
    }

    // Based on number of objects we are going to render, determine LOD.
    final int lodBits;
    {
      int lodTemp = 0;
      if (lod.detail(visibleNodeCount, visibleEdgeCount)) {
        lodTemp |= LOD_HIGH_DETAIL;
        if (lod.nodeBorders(visibleNodeCount, visibleEdgeCount)) {
          lodTemp |= LOD_NODE_BORDERS; }
        if (lod.nodeLabels(visibleNodeCount, visibleEdgeCount)) {
          lodTemp |= LOD_NODE_LABELS;
          if (lod.textAsShape(visibleNodeCount, visibleEdgeCount)) {
            lodTemp |= LOD_TEXT_AS_SHAPE; } }
        if (lod.edgeArrows(visibleNodeCount, visibleEdgeCount)) {
          lodTemp |= LOD_EDGE_ARROWS; }
        if (lod.dashedEdges(visibleNodeCount, visibleEdgeCount)) {
          lodTemp |= LOD_DASHED_EDGES; }
        if (lod.edgeAnchors(visibleNodeCount, visibleEdgeCount)) {
          lodTemp |= LOD_EDGE_ANCHORS; } }
      lodBits = lodTemp;
    }

    // Clear the background.
    {
      grafx.clear(bgColor, xCenter, yCenter, scaleFactor);
    }

    // Define buffers.  These are of the few objects we're instantiating
    // directly in this method.
    final float[] floatBuff1, floatBuff2, floatBuff3;
    {
      floatBuff1 = new float[4];
      floatBuff2 = new float[4];
      floatBuff3 = new float[2];
    }

    // Render the edges first.  No edge shall be rendered twice.
    {
      nodeBuff.empty();
      final SpacialEntry2DEnumerator nodeHits = nodePositions.queryOverlap
        (xMin, yMin, xMax, yMax, null, 0, false);
      if ((lodBits & LOD_HIGH_DETAIL) == 0) {
        final int nodeHitCount = nodeHits.numRemaining();
        for (int i = 0; i < nodeHitCount; i++) {
          final int node = nodeHits.nextExtents(floatBuff1, 0);
          final float nodeX = (floatBuff1[0] + floatBuff1[2]) / 2;
          final float nodeY = (floatBuff1[1] + floatBuff1[3]) / 2;
          final IntEnumerator touchingEdges =
            graph.edgesAdjacent(node, true, true, true);
          final int touchingEdgeCount = touchingEdges.numRemaining();
          for (int j = 0; j < touchingEdgeCount; j++) {
            final int edge = touchingEdges.nextInt();
            final int otherNode =
              node ^ graph.edgeSource(edge) ^ graph.edgeTarget(edge);
            if (nodeBuff.get(otherNode) < 0) { // Has not yet been rendered.
              nodePositions.exists(otherNode, floatBuff2, 0);
              grafx.drawEdgeLow(nodeX, nodeY,
                                (floatBuff2[0] + floatBuff2[2]) / 2,
                                (floatBuff2[1] + floatBuff2[3]) / 2,
                                edgeDetails.colorLowDetail(edge)); } }
          nodeBuff.put(node); } }

      else { // High detail.
        while (nodeHits.numRemaining() > 0) {
          final int node = nodeHits.nextExtents(floatBuff1, 0);
          final byte nodeShape = nodeDetails.shape(node);
          final float nodeX = (float)
            ((((double) floatBuff1[0]) + floatBuff1[2]) / 2.0d);
          final float nodeY = (float)
            ((((double) floatBuff1[1]) + floatBuff1[3]) / 2.0d);
          final IntEnumerator touchingEdges =
            graph.edgesAdjacent(node, true, true, true);
          while (touchingEdges.numRemaining() > 0) {
            final int edge = touchingEdges.nextInt();
            final int otherNode =
              node ^ graph.edgeSource(edge) ^ graph.edgeTarget(edge);
            if (nodeBuff.get(otherNode) < 0) { // Has not yet been rendered.
              if (!nodePositions.exists(otherNode, floatBuff2, 0))
                throw new IllegalStateException
                  ("nodePositions not recognizing node that exists in graph");
              final byte otherNodeShape = nodeDetails.shape(otherNode);
              final float otherNodeX = (float)
                ((((double) floatBuff2[0]) + floatBuff2[2]) / 2.0d);
              final float otherNodeY = (float)
                ((((double) floatBuff2[1]) + floatBuff2[3]) / 2.0d);

              // Compute node shapes, center positions, and extents.
              final byte srcShape, trgShape;
              final float srcX, srcY, trgX, trgY;
              final float[] srcExtents, trgExtents;
              if (node == graph.edgeSource(edge)) {
                srcShape = nodeShape; trgShape = otherNodeShape;
                srcX = nodeX; srcY = nodeY;
                trgX = otherNodeX; trgY = otherNodeY;
                srcExtents = floatBuff1; trgExtents = floatBuff2; }
              else { // node == graph.edgeTarget(edge).
                srcShape = otherNodeShape; trgShape = nodeShape;
                srcX = otherNodeX; srcY = otherNodeY;
                trgX = nodeX; trgY = nodeY;
                srcExtents = floatBuff2; trgExtents = floatBuff1; }

              // Compute visual attributes that do not depend on LOD.
              final float thickness = edgeDetails.thickness(edge);
              final Color color = edgeDetails.color(edge);

              // Compute arrows.
              final byte srcArrow, trgArrow;
              final float srcArrowSize, trgArrowSize;
              final Color srcArrowColor, trgArrowColor;
              if ((lodBits & LOD_EDGE_ARROWS) == 0) { // Not rendering arrows.
                trgArrow = srcArrow = GraphGraphics.ARROW_NONE;
                trgArrowSize = srcArrowSize = 0.0f;
                trgArrowColor = srcArrowColor = null; }
              else { // Rendering edge arrows.
                srcArrow = edgeDetails.sourceArrow(edge);
                trgArrow = edgeDetails.targetArrow(edge);
                if (srcArrow == GraphGraphics.ARROW_NONE) {
                  srcArrowSize = 0.0f; }
                else {
                  srcArrowSize = edgeDetails.sourceArrowSize(edge); }
                if (trgArrow == GraphGraphics.ARROW_NONE ||
                    trgArrow == GraphGraphics.ARROW_MONO) {
                  trgArrowSize = 0.0f; }
                else {
                  trgArrowSize = edgeDetails.targetArrowSize(edge); }
                if (srcArrow == GraphGraphics.ARROW_NONE ||
                    srcArrow == GraphGraphics.ARROW_BIDIRECTIONAL) {
                  srcArrowColor = null; }
                else {
                  srcArrowColor = edgeDetails.sourceArrowColor(edge); }
                if (trgArrow == GraphGraphics.ARROW_NONE ||
                    trgArrow == GraphGraphics.ARROW_BIDIRECTIONAL ||
                    trgArrow == GraphGraphics.ARROW_MONO) {
                  trgArrowColor = null; }
                else {
                  trgArrowColor = edgeDetails.targetArrowColor(edge); } }

              // Compute dash length.
              final float dashLength;
              if ((lodBits & LOD_DASHED_EDGES) == 0) { // Not rendering dashes.
                dashLength = 0.0f; }
              else {
                dashLength = edgeDetails.dashLength(edge); }

              // Compute the anchors to use when rendering edge.
              final EdgeAnchors anchors;
              if ((lodBits & LOD_EDGE_ANCHORS) == 0) { anchors = null; }
              else {
                EdgeAnchors anchorsTemp = edgeDetails.anchors(edge);
                if (anchorsTemp != null && anchorsTemp.numAnchors() == 0) {
                  anchorsTemp = null; }
                anchors = anchorsTemp; }
              // Now anchors is null if and only if no anchors to be rendered.

              final float srcXOut, srcYOut, trgXOut, trgYOut;
              if (anchors == null) {
                srcXOut = trgX; srcYOut = trgY;
                trgXOut = srcX; trgYOut = srcY; }
              else {
                anchors.getAnchor(0, floatBuff3, 0);
                srcXOut = floatBuff3[0];
                srcYOut = floatBuff3[1];
                anchors.getAnchor(anchors.numAnchors() - 1, floatBuff3, 0);
                trgXOut = floatBuff3[0];
                trgYOut = floatBuff3[1]; }

              final float srcOffset;
              if (srcArrow == GraphGraphics.ARROW_DISC) {
                srcOffset = (float) (0.5d * srcArrowSize); }
              else if (srcArrow == GraphGraphics.ARROW_TEE) {
                srcOffset = (float) (2.0d * srcArrowSize); }
              else {
                srcOffset = 0.0f; }

              if (!grafx.computeEdgeIntersection
                  (srcShape,
                   srcExtents[0], srcExtents[1], srcExtents[2], srcExtents[3],
                   srcOffset, srcXOut, srcYOut, floatBuff3)) {
                continue; }
              final float srcXAdj = floatBuff3[0];
              final float srcYAdj = floatBuff3[1];

              final float trgOffset;
              if (trgArrow == GraphGraphics.ARROW_DISC) {
                trgOffset = (float) (0.5d * trgArrowSize); }
              else if (trgArrow == GraphGraphics.ARROW_TEE) {
                trgOffset = (float) (2.0d * trgArrowSize); }
              else {
                trgOffset = 0.0f; }

              if (!grafx.computeEdgeIntersection
                  (trgShape,
                   trgExtents[0], trgExtents[1], trgExtents[2], trgExtents[3],
                   trgOffset, trgXOut, trgYOut, floatBuff3)) {
                continue; }
              final float trgXAdj = floatBuff3[0];
              final float trgYAdj = floatBuff3[1];

              if (anchors == null &&
                  !((((double) srcX) - trgX) *
                    (((double) srcXAdj) - trgXAdj) +
                    (((double) srcY) - trgY) *
                    (((double) srcYAdj) - trgYAdj) > 0.0d)) {
                // The direction of the chopped segment has flipped.
                continue; }

              grafx.drawEdgeFull(srcArrow, srcArrowSize, srcArrowColor,
                                 trgArrow, trgArrowSize, trgArrowColor,
                                 srcXAdj, srcYAdj, anchors, trgXAdj, trgYAdj,
                                 thickness, color, dashLength); } }
          nodeBuff.put(node); } }
    }
  }

//   public final static boolean queryEdgeIntersect(
//                                             final GraphGraphics grafx,
//                                             final FixedGraph graph,
//                                             final SpacialIndex2D nodePositions,
//                                             final GraphLOD lod,
//                                             final NodeDetails nodeDetails,
//                                             final EdgeDetails edgeDetails,
//                                             final int edge,
//                                             final float xMinQuery,
//                                             final float yMinQuery,
//                                             final float xMaxQuery,
//                                             final float yMaxQuery)
//   {
//     return false;
//   }

}
