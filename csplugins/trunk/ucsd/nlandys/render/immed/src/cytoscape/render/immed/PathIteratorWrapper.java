package cytoscape.render.immed;

import java.awt.geom.PathIterator;

final class PathIteratorWrapper implements PathIterator
{

  private final PathIterator m_iter;

  PathIteratorWrapper(final PathIterator pathIter)
  {
    m_iter = pathIter;
  }

  public final int getWindingRule()
  {
    return m_iter.getWindingRule();
  }

  public final boolean isDone()
  {
    return m_iter.isDone();
  }

  public final void next()
  {
    m_iter.next();
  }

  public final int currentSegment(final float[] coords)
  {
    // This is the method that gets called by the native Java rendering
    // engine.
    return m_iter.currentSegment(coords);
  }

  public final int currentSegment(final double[] coords)
  {
    return m_iter.currentSegment(coords);
  }

}
