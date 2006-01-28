package cytoscape.fung;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

public final class NodeView
{

  Fung m_fung; // Not final so that we can destroy reference.
  final int m_node;

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
  }

}
