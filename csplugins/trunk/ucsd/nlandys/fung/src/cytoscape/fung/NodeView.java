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

  public final void setLocation(final Point2D p)
  {
  }

  public final void getSize(final Dimension2D d)
  {
  }

  public final void setSize(final Dimension2D d)
  {
  }

}
