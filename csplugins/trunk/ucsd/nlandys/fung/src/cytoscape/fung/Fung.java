package cytoscape.fung;

import cytoscape.geom.rtree.RTree;
import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntStack;
import java.awt.Component;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class Fung
{

  final Object m_lock = new Object();
  final float[] m_extentsBuff = new float[4];
  final double[] m_doubleBuff = new double[2];
  final GeneralPath m_path = new GeneralPath();
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
  private final InnerCanvas m_canvas;

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
    m_canvas = new InnerCanvas(this);
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

  public final void setBackgroundPaint(final Paint paint)
  {
    if (paint == null) { throw new NullPointerException("paint is null"); }
    synchronized (m_lock) {
      m_canvas.m_bgPaint = paint; }
  }

  public final Paint getBackgroundPaint()
  {
    return m_canvas.m_bgPaint;
  }

  public final void setGraphLOD(final GraphLOD lod)
  {
    if (lod == null) { throw new NullPointerException("lod is null"); }
    synchronized (m_lock) {
      m_canvas.m_lod = lod; }
  }

  public final GraphLOD getGraphLOD()
  {
    return m_canvas.m_lod;
  }

  public final void setCenter(final Point2D pt)
  {
    synchronized (m_lock) {
      m_canvas.m_xCenter = pt.getX();
      m_canvas.m_yCenter = pt.getY(); }
  }

  public final Point2D getCenter()
  {
    return new Point2D.Double(m_canvas.m_xCenter, m_canvas.m_yCenter);
  }

  public final void setScaleFactor(final double scaleFactor)
  {
    synchronized (m_lock) {
      m_canvas.m_scaleFactor = scaleFactor; }
  }

  public final double getScaleFactor()
  {
    return m_canvas.m_scaleFactor;
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

  public final void updateView()
  {
    m_canvas.repaint();
  }

  public final void getNodesIntersectingRectangle(
                                      final Rectangle2D queryRect,
                                      final boolean treatNodeShapesAsRectangle,
                                      final IntStack returnVal)
  {
    synchronized (m_lock) {
      final SpacialEntry2DEnumerator under = m_rtree.queryOverlap
        ((float) queryRect.getMinX(), (float) queryRect.getMinY(),
         (float) queryRect.getMaxX(), (float) queryRect.getMaxY(),
         null, 0, false);
      final int totalHits = under.numRemaining();
      if (treatNodeShapesAsRectangle) {
        for (int i = 0; i < totalHits; i++) {
          returnVal.push(under.nextInt()); } }
      else {
        final double minX = queryRect.getMinX();
        final double minY = queryRect.getMinY();
        final double maxX = queryRect.getMaxX();
        final double maxY = queryRect.getMaxY();
        final double w = maxX - minX;
        final double h = maxY - minY;
        for (int i = 0; i < totalHits; i++) {
          final int node = under.nextExtents(m_extentsBuff, 0);
          // The only way that the node can miss the intersection query is
          // if it intersects one of the four query rectangle's corners.
          if ((m_extentsBuff[0] < minX && m_extentsBuff[1] < minY) ||
              (m_extentsBuff[0] < minX && m_extentsBuff[3] > maxY) ||
              (m_extentsBuff[2] > maxX && m_extentsBuff[3] > maxY) ||
              (m_extentsBuff[2] > maxX && m_extentsBuff[1] < minY)) {
            m_canvas.m_grafx.getNodeShape
              (m_nodeDetails.shape(node), m_extentsBuff[0], m_extentsBuff[1],
               m_extentsBuff[2], m_extentsBuff[3], m_path);
            if (w > 0 && h > 0) {
              if (m_path.intersects(minX, minY, w, h)) {
                returnVal.push(node); } }
            else {
              if (m_path.contains(minX, minY)) {
                returnVal.push(node); } } }
          else {
            returnVal.push(node); } } } }
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
