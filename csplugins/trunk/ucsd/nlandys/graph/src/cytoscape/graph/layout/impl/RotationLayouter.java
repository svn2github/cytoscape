package cytoscape.graph.layout.impl;

import com.nerius.math.xform.AffineTransform3D;
import com.nerius.math.xform.AxisRotation3D;
import com.nerius.math.xform.Translation3D;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;

public final class RotationLayouter
{

  private final MutablePolyEdgeGraphLayout m_graph;
  private final Translation3D m_translationToOrig;
  private final Translation3D m_translationFromOrig;

  /**
   * This operation does not affect edge anchor points which belong to edges
   * containing at least one non-movable node.
   *
   * @exception IllegalStateException
   *   if the minimum bounding rectangle containing all movable nodes and
   *   respective edge anchor points
   *   is not fully free to rotate around its center while staying within
   *   allowable area for specified
   *   <code>MutableGraphLayout</code>.
   **/
  public RotationLayouter(MutablePolyEdgeGraphLayout graph)
  {
    m_graph = graph;
    double xMin = Double.MAX_VALUE; double xMax = Double.MIN_VALUE;
    double yMin = Double.MAX_VALUE; double yMax = Double.MIN_VALUE;
    final int numEdges = m_graph.getNumEdges();
    for (int i = 0; i < numEdges; i++)
    {
      if (!(m_graph.isMovableNode(m_graph.getEdgeNodeIndex(i, true)) &&
            m_graph.isMovableNode(m_graph.getEdgeNodeIndex(i, false))))
        continue;
      final int numAnchors = m_graph.getNumAnchors(i);
      for (int j = 0; j < numAnchors; j++)
      {
        double anchXPosition = m_graph.getAnchorPosition(i, j, true);
        double anchYPosition = m_graph.getAnchorPosition(i, j, false);
        xMin = Math.min(xMin, anchXPosition);
        xMax = Math.max(xMax, anchXPosition);
        yMin = Math.min(yMin, anchYPosition);
        yMax = Math.max(yMax, anchYPosition);
      }
    }
    final int numNodes = m_graph.getNumNodes();
    for (int i = 0; i < numNodes; i++)
    {
      if (!m_graph.isMovableNode(i)) continue;
      double nodeXPosition = m_graph.getNodePosition(i, true);
      double nodeYPosition = m_graph.getNodePosition(i, false);
      xMin = Math.min(xMin, nodeXPosition);
      xMax = Math.max(xMax, nodeXPosition);
      yMin = Math.min(yMin, nodeYPosition);
      yMax = Math.max(yMax, nodeYPosition);
    }
    if (xMax < 0) // Nothing is movable.
    {
      m_translationToOrig = null;
      m_translationFromOrig = null;
    }
    else
    {
      final double xRectCenter = (xMin + xMax) / 2.0d;
      final double yRectCenter = (yMin + yMax) / 2.0d;
      double rectWidth = xMax - xMin;
      double rectHeight = yMax - yMin;
      double hypotenuse =
        0.5d * Math.sqrt(rectWidth * rectWidth + rectHeight * rectHeight);
      if (xRectCenter - hypotenuse < 0.0d ||
          xRectCenter + hypotenuse > m_graph.getMaxWidth() ||
          yRectCenter - hypotenuse < 0.0d ||
          yRectCenter + hypotenuse > m_graph.getMaxHeight())
        throw new IllegalStateException
          ("minimum bounding rectangle of movable nodes and edge anchors " +
           "not free to rotate within MutableGraphLayout boundaries");
      m_translationToOrig =
        new Translation3D(-xRectCenter, -yRectCenter, 0.0d);
      m_translationFromOrig =
        new Translation3D(xRectCenter, yRectCenter, 0.0d);
    }
  }

  private final double[] m_pointBuff = new double[3];

  public void rotateGraph(double radians)
  {
    if (m_translationToOrig == null) return;
    final AffineTransform3D xform = m_translationToOrig.concatenatePost
      ((new AxisRotation3D(AxisRotation3D.Z_AXIS, radians)).concatenatePost
       (m_translationFromOrig));
    final int numNodes = m_graph.getNumNodes();
    for (int i = 0; i < numNodes; i++)
    {
      if (!m_graph.isMovableNode(i)) continue;
      m_pointBuff[0] = m_graph.getNodePosition(i, true);
      m_pointBuff[1] = m_graph.getNodePosition(i, false);
      m_pointBuff[2] = 0.0d;
      xform.transformArr(m_pointBuff);
      m_graph.setNodePosition(i, m_pointBuff[0], m_pointBuff[1]);
    }
    final int numEdges = m_graph.getNumEdges();
    for (int i = 0; i < numEdges; i++)
    {
      if (!(m_graph.isMovableNode(m_graph.getEdgeNodeIndex(i, true)) &&
            m_graph.isMovableNode(m_graph.getEdgeNodeIndex(i, false))))
        continue;
      final int numAnchors = m_graph.getNumAnchors(i);
      for (int j = 0; j < numAnchors; j++)
      {
        m_pointBuff[0] = m_graph.getAnchorPosition(i, j, true);
        m_pointBuff[1] = m_graph.getAnchorPosition(i, j, false);
        m_pointBuff[2] = 0.0d;
        xform.transformArr(m_pointBuff);
        m_graph.setAnchorPosition(i, j, m_pointBuff[0], m_pointBuff[1]);
      }
    }
  }

}
