package cytoscape.render.stateful;

import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.geom.spacial.SpacialIndex2D;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import java.awt.Color;
import java.awt.Font;

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
   *   implementation of this method; when this method returns, nodeBuff is
   *   in a state such that an edge in graph has been rendered by this method
   *   if and only if it touches at least one node in this nodeBuff set;
   *   no guarantee made regarding edgeless nodes.
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
    nodeBuff.empty(); // Make sure we keep our promise.

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
    final int renderNodeCount;
    final int renderEdgeCount;
    final byte renderEdges;
    SpacialEntry2DEnumerator nodeHits;
    {
      nodeHits = nodePositions.queryOverlap
        (xMin, yMin, xMax, yMax, null, 0, false);
      renderNodeCount = nodeHits.numRemaining();
      final int totalNodeCount = graph.nodes().numRemaining();
      final int totalEdgeCount = graph.edges().numRemaining();
      renderEdges = lod.renderEdges
        (renderNodeCount, totalNodeCount, totalEdgeCount);
      if (renderEdges > 0) { renderEdgeCount = totalEdgeCount; }
      else if (renderEdges < 0) { renderEdgeCount = 0; }
      else {
        int runningEdgeCount = 0;
        for (int i = 0; i < renderNodeCount; i++) {
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
        renderEdgeCount = runningEdgeCount;
        nodeHits = null;
        nodeBuff.empty(); }
    }

    // Based on number of objects we are going to render, determine LOD.
    final int lodBits;
    {
      int lodTemp = 0;
      if (lod.detail(renderNodeCount, renderEdgeCount)) {
        lodTemp |= LOD_HIGH_DETAIL;
        if (lod.nodeBorders(renderNodeCount, renderEdgeCount)) {
          lodTemp |= LOD_NODE_BORDERS; }
        if (lod.nodeLabels(renderNodeCount, renderEdgeCount)) {
          lodTemp |= LOD_NODE_LABELS;
          if (lod.textAsShape(renderNodeCount, renderEdgeCount)) {
            lodTemp |= LOD_TEXT_AS_SHAPE; } }
        if (lod.edgeArrows(renderNodeCount, renderEdgeCount)) {
          lodTemp |= LOD_EDGE_ARROWS; }
        if (lod.dashedEdges(renderNodeCount, renderEdgeCount)) {
          lodTemp |= LOD_DASHED_EDGES; }
        if (lod.edgeAnchors(renderNodeCount, renderEdgeCount)) {
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
    if (renderEdges >= 0) {
      final SpacialEntry2DEnumerator nodeHitsTemp;
      if (renderEdges > 0) {
        // We want to render edges in the same order (back to front) that
        // we would use to render just edges on visible nodes; this is assuming
        // that our spacial index has the subquery order-preserving property.
        nodeHitsTemp = nodePositions.queryOverlap
          (Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
           Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
           null, 0, false); }
      else { // renderEdges == 0.
        if (nodeHits == null) {
          nodeHitsTemp = nodePositions.queryOverlap
            (xMin, yMin, xMax, yMax, null, 0, false); }
        else {
          nodeHitsTemp = nodeHits;
          nodeHits = null; } }

      if ((lodBits & LOD_HIGH_DETAIL) == 0) { // Low detail.
        final int nodeHitCount = nodeHitsTemp.numRemaining();
        for (int i = 0; i < nodeHitCount; i++) {
          final int node = nodeHitsTemp.nextExtents(floatBuff1, 0);
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
        while (nodeHitsTemp.numRemaining() > 0) {
          final int node = nodeHitsTemp.nextExtents(floatBuff1, 0);
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
                srcOffset = (float) (srcArrowSize); }
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
                trgOffset = (float) (trgArrowSize); }
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

    // Render nodes and labels.  A label is not necessarily on top of every
    // node; it is only on top of the node it belongs to.
    {
      if (nodeHits == null) {
        nodeHits = nodePositions.queryOverlap
          (xMin, yMin, xMax, yMax, null, 0, false); }

      if ((lodBits & LOD_HIGH_DETAIL) == 0) { // Low detail.
        final int nodeHitCount = nodeHits.numRemaining();
        for (int i = 0; i < nodeHitCount; i++) {
          final int node = nodeHits.nextExtents(floatBuff1, 0);
          grafx.drawNodeLow(floatBuff1[0], floatBuff1[1],
                            floatBuff1[2], floatBuff1[3],
                            nodeDetails.colorLowDetail(node)); } }

      else { // High detail.
        while (nodeHits.numRemaining() > 0) {
          final int node = nodeHits.nextExtents(floatBuff1, 0);

          // Compute visual attributes that do not depend on LOD.
          final byte shape = nodeDetails.shape(node);
          final Color fillColor = nodeDetails.fillColor(node);

          // Compute node border information.
          final float borderWidth;
          final Color borderColor;
          if ((lodBits & LOD_NODE_BORDERS) == 0) { // Not rendering borders.
            borderWidth = 0.0f;
            borderColor = null; }
          else { // Rendering node borders.
            borderWidth = nodeDetails.borderWidth(node);
            if (borderWidth == 0.0f) {
              borderColor = null; }
            else {
              borderColor = nodeDetails.borderColor(node); } }

          // Draw the node.
          grafx.drawNodeFull(shape, floatBuff1[0], floatBuff1[1],
                             floatBuff1[2], floatBuff1[3], fillColor,
                             borderWidth, borderColor);

          // Take care of label rendering.
          if ((lodBits & LOD_NODE_LABELS) != 0) { // Potential label rendering.

            // Compute node label, font, scale factor, and label color.
            final String label;
            final Font font;
            final double fontScaleFactor;
            final Color labelColor;
            {
              String labelTemp = nodeDetails.label(node);
              if ("".equals(labelTemp)) { labelTemp = null; }
              label = labelTemp;
              if (label == null) {
                font = null;
                fontScaleFactor = 1.0d;
                labelColor = null; }
              else {
                font = nodeDetails.font(node);
                fontScaleFactor = nodeDetails.fontScaleFactor(node);
                labelColor = nodeDetails.labelColor(node); }
            }
            // Now label is not null if and only if we need to render label.

            if (label != null) {
              grafx.drawTextFull
                (font, fontScaleFactor, label,
                 (float) ((((double) floatBuff1[0]) + floatBuff1[2]) / 2.0d),
                 (float) ((((double) floatBuff1[1]) + floatBuff1[3]) / 2.0d),
                 labelColor, (lodBits & LOD_TEXT_AS_SHAPE) != 0); } } } }
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
