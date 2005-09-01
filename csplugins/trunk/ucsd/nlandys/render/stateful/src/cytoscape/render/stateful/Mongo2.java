package cytoscape.render.stateful;

import cytoscape.geom.spacial.SpacialIndex2D;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntHash;
import java.awt.Color;

public final class Mongo2
{

  // No constructor.
  private Mongo2() { }

  /**
   * @return the edges that were rendered.
   */
  public final static IntHash renderGraph(final GraphGraphics grafx,
                                          final FixedGraph graph,
                                          final SpacialIndex2D nodePositions,
                                          final GraphLOD lod,
                                          final NodeDetails nodeDetails,
                                          final EdgeDetails edgeDetails,
                                          final Color bgColor,
                                          final double xCenter,
                                          final double yCenter,
                                          final double scaleFactor)
  {
    return null;
  }

  public final static boolean queryEdgeIntersect(
                                            final GraphGraphics grafx,
                                            final FixedGraph graph,
                                            final SpacialIndex2D nodePositions,
                                            final GraphLOD lod,
                                            final NodeDetails nodeDetails,
                                            final EdgeDetails edgeDetails,
                                            final int edge,
                                            final float xMinQuery,
                                            final float yMinQuery,
                                            final float xMaxQuery,
                                            final float yMaxQuery)
  {
    return false;
  }

}
