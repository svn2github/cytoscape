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
