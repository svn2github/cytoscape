package cytoscape.render.stateful;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

import java.awt.Graphics2D;

public final class Mongo implements FixedGraph
{

  private final DynamicGraph m_graph;

  public Mongo(final int width, final int height,
               final ImageSource imgSource)
  {
    m_graph = DynamicGraphFactory.instantiateDynamicGraph();
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
