package cytoscape.fung;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

public final class NodeView
{

  public final static byte SHAPE_RECTANGLE = GraphGraphics.SHAPE_RECTANGLE;
  public final static byte SHAPE_DIAMOND = GraphGraphics.SHAPE_DIAMOND;
  public final static byte SHAPE_ELLIPSE = GraphGraphics.SHAPE_ELLIPSE;
  public final static byte SHAPE_HEXAGON = GraphGraphics.SHAPE_HEXAGON;
  public final static byte SHAPE_OCTAGON = GraphGraphics.SHAPE_OCTAGON;
  public final static byte SHAPE_PARALLELOGRAM =
    GraphGraphics.SHAPE_PARALLELOGRAM;
  public final static byte SHAPE_ROUNDED_RECTANGLE =
    GraphGraphics.SHAPE_ROUNDED_RECTANGLE;
  public final static byte SHAPE_TRIANGLE =
    GraphGraphics.SHAPE_TRIANGLE;

  Fung m_fung; // Not final so that we can destroy reference.
  private final int m_node;

  NodeView(final Fung fung, final int node)
  {
    m_fung = fung;
    m_node = node;
  }

  public final int getNode()
  {
    return m_node;
  }

  public final void getLocation(final Point2D p)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      p.setLocation((((double) m_fung.m_extentsBuff[0]) +
                     m_fung.m_extentsBuff[2]) / 2.0d,
                    (((double) m_fung.m_extentsBuff[1]) +
                     m_fung.m_extentsBuff[3]) / 2.0d); }
  }

  public final void setLocation(final double x, final double y)
  {
    // As a node changes location, its width and height may change as a
    // result of floating point imprecision.  However, we're not going to
    // error check this shifting size against border width.
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      final double wDiv2 =
        (((double) m_fung.m_extentsBuff[2]) - m_fung.m_extentsBuff[0]) / 2.0d;
      final double hDiv2 =
        (((double) m_fung.m_extentsBuff[3]) - m_fung.m_extentsBuff[1]) / 2.0d;
      final float xMin = (float) (x - wDiv2);
      final float yMin = (float) (y - hDiv2);
      final float xMax = (float) (x + wDiv2);
      final float yMax = (float) (y + hDiv2);
      if (!(xMax > xMin)) {
        throw new IllegalStateException
          ("width of node has degenerated to zero after rounding"); }
      if (!(yMax > yMin)) {
        throw new IllegalStateException
          ("height of node has degenerated to zero after rounding"); }
      m_fung.m_rtree.delete(m_node);
      m_fung.m_rtree.insert(m_node, xMin, yMin, xMax, yMax); }
  }

  /**
   * @param d the width and height of this dimension are set to the
   *   width and height of this node, respectively.
   */
  public final void getSize(final Dimension2D d)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      d.setSize
        (((double) m_fung.m_extentsBuff[2]) - m_fung.m_extentsBuff[0],
         ((double) m_fung.m_extentsBuff[3]) - m_fung.m_extentsBuff[1]); }
  }

  public final void setSize(final double width, final double height)
  {
    // TODO: Reconcile width and height with current node border width and
    // if SHAPE_ROUNDED_RECTANGLE then check for necessary constraint.
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      final double xCenter =
        (((double) m_fung.m_extentsBuff[0]) + m_fung.m_extentsBuff[2]) / 2.0d;
      final double yCenter =
        (((double) m_fung.m_extentsBuff[1]) + m_fung.m_extentsBuff[3]) / 2.0d;
      final double wDiv2 = width / 2.0d;
      final double hDiv2 = height / 2.0d;
      final float xMin = (float) (xCenter - wDiv2);
      final float yMin = (float) (yCenter - hDiv2);
      final float xMax = (float) (xCenter + wDiv2);
      final float yMax = (float) (yCenter + hDiv2);
      if (!(xMax > xMin)) {
        throw new IllegalArgumentException("width is too small"); }
      if (!(yMax > yMin)) {
        throw new IllegalArgumentException("height is too small"); }
      m_fung.m_rtree.delete(m_node);
      m_fung.m_rtree.insert(m_node, xMin, yMin, xMax, yMax); }
  }

  public final Color getColorLowDetail()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_nodeDetails.colorLowDetail(m_node); }
  }

  public final void setColorLowDetail(final Color colorLowDetail)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_nodeDetails.overrideColorLowDetail(m_node, colorLowDetail); }
  }

  public final byte getShape()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_nodeDetails.shape(m_node); }
  }

  public final void setShape(final byte shape)
  {
    synchronized (m_fung.m_lock) {
      switch (shape) {
      case SHAPE_RECTANGLE:
      case SHAPE_DIAMOND:
      case SHAPE_ELLIPSE:
      case SHAPE_HEXAGON:
      case SHAPE_OCTAGON:
      case SHAPE_PARALLELOGRAM:
      case SHAPE_ROUNDED_RECTANGLE:
      case SHAPE_TRIANGLE:
        break;
      default:
        throw new IllegalArgumentException("shape is not recognized"); }
      m_fung.m_nodeDetails.overrideShape(m_node, shape); }
  }

  public final Paint getFillPaint()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_nodeDetails.fillPaint(m_node); }
  }

  public final void setFillPaint(final Paint fillPaint)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_nodeDetails.overrideFillPaint(m_node, fillPaint); }
  }

  public final double getBorderWidth()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_nodeDetails.borderWidth(m_node); }
  }

  public final void setBorderWidth(final double borderWidth)
  {
  }

}
