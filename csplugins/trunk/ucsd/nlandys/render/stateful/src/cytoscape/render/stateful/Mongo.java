package cytoscape.render.stateful;

import cytoscape.geom.rtree.RTree;
import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

import java.awt.Graphics2D;

/**
 * This module aggregates several smaller modules into a very powerful
 * framework for rendering graphs.  To understand the public API of this
 * framework, it helps to understand the public API of the package
 * cytoscape.render.immed.
 */
public final class Mongo implements FixedGraph
{

  private final ImageSource m_imgSource;
  private final DynamicGraph m_graph;
  private final RTree m_tree;
  private GraphGraphics m_grafx;

  public Mongo(final int width, final int height,
               final ImageSource imgSource)
  {
    m_imgSource = imgSource;
    m_graph = DynamicGraphFactory.instantiateDynamicGraph();
    m_tree = new RTree();
    m_grafx = new GraphGraphics
      (m_imgSource.createImageBuffer(width, height), true);
  }

  public final IntEnumerator nodes() {
    return m_graph.nodes(); }

  public final IntEnumerator edges() {
    return m_graph.edges(); }

  public final boolean nodeExists(final int node) {
    return m_graph.nodeExists(node); }

  public final byte edgeType(final int edge) {
    return m_graph.edgeType(edge); }

  public final int edgeSource(final int edge) {
    return m_graph.edgeSource(edge); }

  public final int edgeTarget(final int edge) {
    return m_graph.edgeTarget(edge); }

  public final IntEnumerator edgesAdjacent(final int node,
                                           final boolean outgoing,
                                           final boolean incoming,
                                           final boolean undirected) {
    return m_graph.edgesAdjacent(node, outgoing, incoming, undirected); }

  public final IntIterator edgesConnecting(final int node0, final int node1,
                                           final boolean outgoing,
                                           final boolean incoming,
                                           final boolean und) {
    return m_graph.edgesConnecting(node0, node1, outgoing, incoming, und); }

  public final void print(Graphics2D graphics, boolean textAsShapes,
                          GraphLOD lod)
  {
  }

  public final void resize(final int newWidth, final int newHeight)
  {
    // Create new GraphGraphics with Image gotten from ImageSource.
  }

}
