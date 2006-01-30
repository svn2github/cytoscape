package cytoscape.fung;

import cytoscape.geom.rtree.RTree;
import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntStack;
import java.awt.Canvas;
import java.awt.Component;

public final class Fung
{

  private final static double INITIAL_DEFAULT_NODE_SIZE = 10.0d;
  private final static byte INITIAL_DEFAULT_NODE_SHAPE =
    NodeView.SHAPE_ELLIPSE;
  private final static double INITIAL_DEFAULT_NODE_BORDER_WIDTH = 1.0d;

  final Object m_lock = new Object();
  final float[] m_extentsBuff = new float[4];
  private final Canvas m_canvas = null;
  private final DynamicGraph m_graphModel = new FungDynamicGraph();
  final RTree m_rtree = new RTree();

  private TopologyChangeListener m_topLis = null;
  private float m_defaultNodeWidthDiv2 =
    (float) (INITIAL_DEFAULT_NODE_SIZE / 2);
  private float m_defaultNodeHeightDiv2 =
    (float) (INITIAL_DEFAULT_NODE_SIZE / 2);
  byte m_defaultNodeShape = INITIAL_DEFAULT_NODE_SHAPE;
  private float m_defaultNodeBorderWidth =
    (float) INITIAL_DEFAULT_NODE_BORDER_WIDTH;

  final SpecificNodeDetails m_nodeDetails;

  public Fung()
  {
    m_nodeDetails = new SpecificNodeDetails(this);
  }

  /**
   * When new nodes are created, they are placed at the origin and have
   * width equal to the return value.
   */
  public final double getDefaultNodeWidth()
  {
    return 2.0d * m_defaultNodeWidthDiv2;
  }

  public final void setDefaultNodeWidth(final double defaultNodeWidth)
  {
    final float defaultNodeWidthDiv2 = (float) (defaultNodeWidth / 2.0d);
    if (!(defaultNodeWidthDiv2 > 0.0f)) {
      throw new IllegalArgumentException("defaultNodeWidth is too small"); }
    synchronized (m_lock) { m_defaultNodeWidthDiv2 = defaultNodeWidthDiv2; }
  }

  /**
   * When new nodes are created, they are placed at the origin and have
   * height equal to the return value.
   */
  public final double getDefaultNodeHeight()
  {
    return 2.0d * m_defaultNodeHeightDiv2;
  }

  public final void setDefaultNodeHeight(final double defaultNodeHeight)
  {
    final float defaultNodeHeightDiv2 = (float) (defaultNodeHeight / 2.0d);
    if (!(defaultNodeHeightDiv2 > 0.0f)) {
      throw new IllegalArgumentException("defaultNodeHeight is too small"); }
    synchronized (m_lock) { m_defaultNodeHeightDiv2 = defaultNodeHeightDiv2; }
  }

  public final byte getDefaultNodeShape()
  {
    return m_defaultNodeShape;
  }

  public final void setDefaultNodeShape(final byte defaultNodeShape)
  {
    switch (defaultNodeShape) {
    case NodeView.SHAPE_RECTANGLE:
    case NodeView.SHAPE_DIAMOND:
    case NodeView.SHAPE_ELLIPSE:
    case NodeView.SHAPE_HEXAGON:
    case NodeView.SHAPE_OCTAGON:
    case NodeView.SHAPE_PARALLELOGRAM:
    case NodeView.SHAPE_ROUNDED_RECTANGLE:
    case NodeView.SHAPE_TRIANGLE:
      break;
    default:
      throw new IllegalArgumentException("defaultNodeShape is unrecognized"); }
    synchronized (m_lock) { m_defaultNodeShape = defaultNodeShape; }
  }

  public final double getDefaultNodeBorderWidth()
  {
    return m_defaultNodeBorderWidth;
  }

  public final void setDefaultNodeBorderWidth(final double defaultBorderWidth)
  {
  }

  public final void addTopologyChangeListener(
                                         final TopologyChangeListener listener)
  {
    synchronized (m_lock) {
      m_topLis = TopologyChangeListenerChain.add(m_topLis, listener); }
  }

  public final void removeTopologyChangeListener(
                                         final TopologyChangeListener listener)
  {
    synchronized (m_lock) {
      m_topLis = TopologyChangeListenerChain.add(m_topLis, listener); }
  }

  public final Component getComponent()
  {
    return m_canvas;
  }

  public final DynamicGraph getGraphModel()
  {
    return m_graphModel;
  }

  private final class FungDynamicGraph implements DynamicGraph
  {

    final DynamicGraph m_graph;

    FungDynamicGraph() {
      m_graph = DynamicGraphFactory.instantiateDynamicGraph(); }

    public final IntEnumerator nodes() {
      synchronized (m_lock) { return m_graph.nodes(); } }

    public final IntEnumerator edges() {
      synchronized (m_lock) { return m_graph.edges(); } }

    public final int nodeCreate() {
      final int rtnVal;
      synchronized (m_lock) {
        rtnVal = m_graph.nodeCreate();
        m_rtree.insert(rtnVal,
                       -m_defaultNodeWidthDiv2, -m_defaultNodeHeightDiv2,
                       m_defaultNodeWidthDiv2, m_defaultNodeHeightDiv2); }
      final TopologyChangeListener topLis = m_topLis;
      if (topLis != null) {
        topLis.nodeCreated(rtnVal); }
      return rtnVal; }

    public final boolean nodeRemove(final int node) {
      final IntStack removedEdges = new IntStack();
      synchronized (m_lock) {
        final IntEnumerator edgesTouching =
          m_graph.edgesAdjacent(node, true, true, true);
        if (edgesTouching == null) { return false; }
        while (edgesTouching.numRemaining() > 0) {
          removedEdges.push(edgesTouching.nextInt()); }
        m_rtree.delete(node);
        m_graph.nodeRemove(node); }
      final TopologyChangeListener topLis = m_topLis;
      if (topLis != null) {
        final IntEnumerator removedEdgeEnum = removedEdges.elements();
        while (removedEdgeEnum.numRemaining() > 0) {
          topLis.edgeRemoved(removedEdgeEnum.nextInt()); }
        topLis.nodeRemoved(node); }
      return true; }

    public final int edgeCreate(final int sourceNode,
                                final int targetNode,
                                final boolean directed) {
      final int rtnVal;
      synchronized (m_lock) {
        rtnVal = m_graph.edgeCreate(sourceNode, targetNode, directed); }
      if (rtnVal >= 0) {
        final TopologyChangeListener topLis = m_topLis;
        if (topLis != null) {
          topLis.edgeCreated(rtnVal); } }
      return rtnVal; }

    public final boolean edgeRemove(final int edge) {
      final boolean rtnVal;
      synchronized (m_lock) {
        rtnVal = m_graph.edgeRemove(edge); }
      if (rtnVal) {
        final TopologyChangeListener topLis = m_topLis;
        if (topLis != null) {
          topLis.edgeRemoved(edge); } }
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
