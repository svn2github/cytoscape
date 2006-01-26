package cytoscape.fung;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntStack;
import java.awt.Canvas;
import java.awt.Component;

public final class Fung
{

  private final Object m_lock;
  private final TopologyChangeListener[] m_topLis;
  private final Canvas m_canvas = null;
  private final DynamicGraph m_graphModel;

  public Fung()
  {
    m_lock = new Object();
    m_topLis = new TopologyChangeListener[1];
    m_graphModel = new FungDynamicGraph(m_lock);
  }

  public final void addTopologyChangeListener(
                                         final TopologyChangeListener listener)
  {
    synchronized (m_lock) {
      m_topLis[0] = TopologyChangeListenerChain.add(m_topLis[0], listener); }
  }

  public final void removeTopologyChangeListener(
                                         final TopologyChangeListener listener)
  {
    synchronized (m_lock) {
      m_topLis[0] = TopologyChangeListenerChain.add(m_topLis[0], listener); }
  }

  public final Component getComponent()
  {
    return m_canvas;
  }

  public final DynamicGraph getGraphModel()
  {
    return m_graphModel;
  }

  private final static class FungDynamicGraph implements DynamicGraph
  {

    final Object m_lock;
    final DynamicGraph m_graph;

    FungDynamicGraph(final Object lock) {
      m_lock = lock;
      m_graph = DynamicGraphFactory.instantiateDynamicGraph(); }

    public final IntEnumerator nodes() {
      synchronized (m_lock) { return m_graph.nodes(); } }

    public final IntEnumerator edges() {
      synchronized (m_lock) { return m_graph.edges(); } }

    public final int nodeCreate() {
      final int rtnVal;
      synchronized (m_lock) {
        rtnVal = m_graph.nodeCreate(); }
      // Add listener code here.
      return rtnVal; }

    public final boolean nodeRemove(final int node) {
      final IntStack removedEdges = new IntStack();
      synchronized (m_lock) {
        final IntEnumerator edgesTouching =
          m_graph.edgesAdjacent(node, true, true, true);
        if (edgesTouching == null) { return false; }
        while (edgesTouching.numRemaining() > 0) {
          removedEdges.push(edgesTouching.nextInt()); }
        m_graph.nodeRemove(node); }
      // Add listener code here.       
      return true; }

    public final int edgeCreate(final int sourceNode,
                                final int targetNode,
                                final boolean directed) {
      final int rtnVal;
      synchronized (m_lock) {
        rtnVal = m_graph.edgeCreate(sourceNode, targetNode, directed); }
      if (rtnVal >= 0) {
        // Add listener code here.
      }
      return rtnVal; }

    public final boolean edgeRemove(final int edge) {
      final boolean rtnVal;
      synchronized (m_lock) {
        rtnVal = m_graph.edgeRemove(edge); }
      if (rtnVal) {
        // Add listener code here.
      }
      return rtnVal; }

    public final boolean nodeExists(final int node) {
      synchronized (m_lock) { return m_graph.nodeExists(node); } }

    public final byte edgeType(final int edge) {
      synchronized (m_lock) { return m_graph.edgeType(edge); } }

    public final int edgeSource(final int edge) {
      synchronized (m_lock) { return m_graph.edgeSource(edge); } }

    public final int edgeTarget(final int edge) {
      synchronized (m_lock) { return m_graph.edgeTarget(edge); } }

    public final IntEnumerator edgesAdjacent(final int node,
                                             final boolean outgoing,
                                             final boolean incoming,
                                             final boolean undirected) {
      synchronized (m_lock) {
        return m_graph.edgesAdjacent(node, outgoing, incoming, undirected); } }

    public final IntIterator edgesConnecting(final int node0, final int node1,
                                             final boolean outgoing,
                                             final boolean incoming,
                                             final boolean undirected) {
      synchronized (m_lock) {
        return m_graph.edgesConnecting(node0, node1,
                                       outgoing, incoming, undirected); } }

  }

}
