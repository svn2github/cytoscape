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

  final Object m_lock = new Object();
  final float[] m_extentsBuff = new float[4];
  final double[] m_doubleBuff = new double[2];
  private final Canvas m_canvas = null;
  final FungDynamicGraph m_graphModel = new FungDynamicGraph();
  final RTree m_rtree = new RTree();
  final ObjArray m_nodeViewStorage = new ObjArray();
  final ObjArray m_edgeViewStorage = new ObjArray();

  private TopologyChangeListener m_topLis = null;
  final NodeViewDefaults m_nodeDefaults;
  final EdgeViewDefaults m_directedEdgeDefaults;
  final EdgeViewDefaults m_undirectedEdgeDefaults;
  final SpecificNodeDetails m_nodeDetails;
  final SpecificEdgeDetails m_edgeDetails;

  // Is there a way to do this without this hack?
  // I want the instance of Fung to be accessible from within the
  // FungDynamicGraph instance.
  private final Fung m_this = this;

  public Fung()
  {
    this(null, null, null);
  }

  public Fung(NodeViewDefaults nodeDefaults,
              EdgeViewDefaults directedEdgeDefaults,
              EdgeViewDefaults undirectedEdgeDefaults)
  {
    if (nodeDefaults == null) {
      nodeDefaults = new NodeViewDefaults(); }
    m_nodeDefaults = nodeDefaults;
    if (directedEdgeDefaults == null) {
      directedEdgeDefaults = new EdgeViewDefaults(); }
    m_directedEdgeDefaults = directedEdgeDefaults;
    if (undirectedEdgeDefaults == null) {
      undirectedEdgeDefaults = new EdgeViewDefaults(); }
    m_undirectedEdgeDefaults = undirectedEdgeDefaults;
    m_nodeDetails = new SpecificNodeDetails(this);
    m_edgeDetails = new SpecificEdgeDetails(this);
  }

  public final NodeViewDefaults getNodeViewDefaults()
  {
    return m_nodeDefaults;
  }

  public final EdgeViewDefaults getDirectedEdgeViewDefaults()
  {
    return m_directedEdgeDefaults;
  }

  public final EdgeViewDefaults getUndirectedEdgeViewDefaults()
  {
    return m_undirectedEdgeDefaults;
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

  public final NodeView getNodeView(final int node)
  {
    synchronized (m_lock) {
      return (NodeView) m_nodeViewStorage.getObjAtIndex(node); }
  }

  public final EdgeView getEdgeView(final int edge)
  {
    synchronized (m_lock) {
      return (EdgeView) m_edgeViewStorage.getObjAtIndex(edge); }
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
        m_rtree.insert
          (rtnVal,
           -m_nodeDefaults.m_widthDiv2, -m_nodeDefaults.m_heightDiv2,
           m_nodeDefaults.m_widthDiv2, m_nodeDefaults.m_heightDiv2);
        m_nodeViewStorage.setObjAtIndex
          (new NodeView(m_this, rtnVal), rtnVal); }
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
        m_graph.nodeRemove(node);
        m_rtree.delete(node);
        ((NodeView) m_nodeViewStorage.getObjAtIndex(node)).m_fung = null;
        m_nodeViewStorage.setObjAtIndex(null, node); }
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
        rtnVal = m_graph.edgeCreate(sourceNode, targetNode, directed);
        if (rtnVal < 0) { return -1; }
        m_edgeViewStorage.setObjAtIndex
          (new EdgeView(m_this, rtnVal), rtnVal); }
      final TopologyChangeListener topLis = m_topLis;
      if (topLis != null) { topLis.edgeCreated(rtnVal); }
      return rtnVal; }

    public final boolean edgeRemove(final int edge) {
      synchronized (m_lock) {
        if (!m_graph.edgeRemove(edge)) { return false; }
        ((EdgeView) m_edgeViewStorage.getObjAtIndex(edge)).m_fung = null;
        m_edgeViewStorage.setObjAtIndex(null, edge); }
      final TopologyChangeListener topLis = m_topLis;
      if (topLis != null) { topLis.edgeRemoved(edge); }
      return true; }

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
