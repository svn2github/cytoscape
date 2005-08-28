package cytoscape.render.stateful;

import cytoscape.geom.rtree.RTree;
import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * This module aggregates several smaller modules into a very powerful
 * framework for rendering graphs.  To understand the public API of this
 * framework, it helps to understand the public API of the package
 * cytoscape.render.immed.
 */
public final class Mongo
{

  private final boolean m_debug;
  private final DynamicGraph m_graph;
  private final RTree m_tree;
  private GraphGraphics m_grafx;

  public Mongo(final Image img, final boolean debug)
  {
    m_debug = debug;
    m_graph = DynamicGraphFactory.instantiateDynamicGraph();
    m_tree = new RTree();
    m_grafx = new GraphGraphics(img, m_debug);
  }

  public final void setViewingTransform(final double xCenter,
                                        final double yCenter,
                                        final double scaleFactor)
  {
  }

  public final IntIterator getExactNodeHits(final int imageX,
                                            final int imageY)
  {
    return null;
  }

  public final IntEnumerator getRoughNodeHits(final int imageXMin,
                                              final int imageYMin,
                                              final int imageXMax,
                                              final int imageYMax)
  {
    return null;
  }

  public final void print(final Graphics2D graphics,
                          final Color bgColor,
                          final GraphLOD lod,
                          final NodeDetails nodeDetails,
                          final EdgeDetails edgeDetails)
  {
  }

  public final void render(final Color bgColor,
                           final GraphLOD lod,
                           final NodeDetails nodeDetails,
                           final EdgeDetails edgeDetails)
  {
  }

  public final void resize(final Image img)
  {
  }

}
