package cytoscape.render.immed;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

final class GeneralPathWrapper implements Shape
{

  private final GeneralPath m_path;

  GeneralPathWrapper()
  {
    m_path = new GeneralPath();
  }

  public final Rectangle getBounds()
  {
    return m_path.getBounds();
  }

  public final Rectangle2D getBounds2D()
  {
    return m_path.getBounds2D();
  }

  public final boolean contains(final double x, final double y)
  {
    return m_path.contains(x, y);
  }

  public final boolean contains(final Point2D pt)
  {
    return m_path.contains(pt);
  }

  public final boolean intersects(final double x, final double y,
                                  final double w, final double h)
  {
    return m_path.intersects(x, y, w, h);
  }

  public final boolean intersects(final Rectangle2D rect)
  {
    return m_path.intersects(rect);
  }

  public final boolean contains(final double x, final double y,
                                final double w, final double h)
  {
    return m_path.contains(x, y, w, h);
  }

  public final boolean contains(final Rectangle2D rect)
  {
    return m_path.contains(rect);
  }

  public final PathIterator getPathIterator(final AffineTransform xform)
  {
    return m_path.getPathIterator(xform);
  }

  public final PathIterator getPathIterator(final AffineTransform xform,
                                            final double flatness)
  {
    return m_path.getPathIterator(xform, flatness);
  }

  public final void moveTo(final float x, final float y)
  {
    m_path.moveTo(x, y);
  }

  public final void lineTo(final float x, final float y)
  {
    m_path.lineTo(x, y);
  }

  public final void closePath()
  {
    m_path.closePath();
  }

  public final void append(final Shape s, final boolean connect)
  {
    m_path.append(s, connect);
  }
  public final void setWindingRule(final int rule)
  {
    m_path.setWindingRule(rule);
  }

  public final void reset()
  {
    m_path.reset();
  }

  public final void transform(final AffineTransform xform)
  {
    m_path.transform(xform);
  }

}
