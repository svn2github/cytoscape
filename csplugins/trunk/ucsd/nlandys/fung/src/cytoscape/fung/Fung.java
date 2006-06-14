package cytoscape.fung;

import cytoscape.geom.rtree.RTree;
import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.util.intr.IntBTree;
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

  public static final int CUSTOM_NODE_SHAPE_MAX_VERTICES =
    GraphGraphics.CUSTOM_SHAPE_MAX_VERTICES;

  final Object m_lock = new Object();
  final float[] m_extentsBuff = new float[4];
  final GeneralPath m_path = new GeneralPath();
  final FungDynamicGraph m_graphModel = new FungDynamicGraph();
  final RTree m_rtree = new RTree();
  final ObjArray m_nodeViewStorage = new ObjArray();
  final ObjArray m_edgeViewStorage = new ObjArray();

  private TopologyChangeListener m_topLis = null;
  private Object m_selLis = null;
  private ViewportChangeListener m_viewLis = null;
  final NodeViewDefaults m_nodeDefaults;
  final EdgeViewDefaults m_directedEdgeDefaults;
  final EdgeViewDefaults m_undirectedEdgeDefaults;
  final SpecificNodeDetails m_nodeDetails;
  final SpecificEdgeDetails m_edgeDetails;
  private final InnerCanvas m_canvas;
  final IntBTree m_selectedNodes;
  final IntBTree m_selectedEdges;

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
    m_selectedNodes = new IntBTree();
    m_selectedEdges = new IntBTree();
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
      m_topLis = TopologyChangeListenerChain.remove(m_topLis, listener); }
  }

  public final void addSelectionListener(final SelectionListener listener)
  {
    synchronized (m_lock) {
      m_selLis = SelectionListenerChain.add(m_selLis, listener); }
  }

  public final void removeSelectionListener(final SelectionListener listener)
  {
    synchronized (m_lock) {
      m_selLis = SelectionListenerChain.remove(m_selLis, listener); }
  }

  public final void addViewportChangeListener(
                                         final ViewportChangeListener listener)
  {
    synchronized (m_lock) {
      m_viewLis = ViewportChangeListenerChain.add(m_viewLis, listener); }
  }

  public final void removeViewportChangeListener(
                                         final ViewportChangeListener listener)
  {
    synchronized (m_lock) {
      m_viewLis = ViewportChangeListenerChain.remove(m_viewLis, listener); }
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
      m_canvas.m_yCenter = pt.getY();
      m_canvas.m_viewportChanged = true; }
  }

  public final Point2D getCenter()
  {
    return new Point2D.Double(m_canvas.m_xCenter, m_canvas.m_yCenter);
  }

  public final void setScaleFactor(final double scaleFactor)
  {
    synchronized (m_lock) {
      m_canvas.m_scaleFactor = scaleFactor;
      m_canvas.m_viewportChanged = true; }
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

  /**
   * Places all selected nodes onto the stack specified.
   */
  public final void getSelectedNodes(final IntStack returnVal)
  {
    synchronized (m_lock) {
      final IntEnumerator selectedNodes = m_selectedNodes.searchRange
        (Integer.MIN_VALUE, Integer.MAX_VALUE, false);
      final int count = selectedNodes.numRemaining();
      for (int i = 0; i < count; i++) {
        returnVal.push(selectedNodes.nextInt()); } }
  }

  /**
   * Places all selected edges onto the stack specified.
   */
  public final void getSelectedEdges(final IntStack returnVal)
  {
    synchronized (m_lock) {
      final IntEnumerator selectedEdges = m_selectedEdges.searchRange
        (Integer.MIN_VALUE, Integer.MAX_VALUE, false);
      final int count = selectedEdges.numRemaining();
      for (int i = 0; i < count; i++) {
        returnVal.push(selectedEdges.nextInt()); } }
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

  /**
   * The custom node shape that is defined is a polygon specified
   * by the coordinates supplied.  The polygon must meet several constraints
   * listed below.<p>
   * If we define the value xCenter to be the average of the minimum and
   * maximum X values of the vertices and if we define yCenter likewise, then
   * the specified polygon must meet the following constraints:
   * <ol>
   *   <li>Each polygon line segment must have nonzero length.</li>
   *   <li>No two consecutive polygon line segments can be parallel (this
   *     essentially implies that the polygon must have at least three
   *     vertices).</li>
   *   <li>No two distinct non-consecutive polygon line segments may
   *     intersect (not even at the endpoints); this makes possible the
   *     notion of interior of the polygon.</li>
   *   <li>The polygon must be star-shaped with respect to the point
   *     (xCenter, yCenter); a polygon is said to be <i>star-shaped with
   *     respect to a point (a,b)</i> if and only if for every point (x,y)
   *     in the interior or on the boundary of the polygon, the interior of
   *     the segment (a,b)->(x,y) lies in the interior of the polygon.</li>
   *   <li>The path traversed by the polygon must be clockwise where
   *     +X points right and +Y points down.</li>
   * </ol><p>
   * In addition to these constraints, when rendering custom nodes with
   * nonzero border width, possible problems may arise if the border width
   * is large with respect to the kinks in the polygon.
   * @param coords defines the coords.length / 2 vertices of the polygon;
   *   coords[0], coords[1], coords[2], coords[3] and so on
   *   are interpreted as x0, y0, x1, y1, and so on; the initial vertex need
   *   not be repeated as the last vertex specified.
   * @return the node shape identifier to be used in future calls to
   *   NodeView.setShape().
   * @exception IllegalArgumentException if any of the constraints are not met,
   *   or if the specified polygon has more than CUSTOM_NODE_SHAPE_MAX_VERTICES
   *   vertices.
   * @exception IllegalStateException if too many custom node shapes are
   *   already defined; a little over one hundered custom node shapes can be
   *   defined.
   */
  public final byte defineCustomNodeShape(final double[] coords)
  {
    if ((coords.length % 2) != 0) {
      throw new IllegalArgumentException
        ("coords array must be of even length"); }
    final float[] fCoords = new float[coords.length];
    for (int i = 0; i < coords.length; i++) {
      fCoords[i] = (float) coords[i]; }
    synchronized (m_lock) {
      return m_canvas.m_grafx.defineCustomNodeShape
        (fCoords, 0, fCoords.length / 2); }
  }

  public final boolean customNodeShapeExists(final byte shape)
  {
    synchronized (m_lock) {
      return m_canvas.m_grafx.customNodeShapeExists(shape); }
  }

  public final byte[] getCustomNodeShapes()
  {
    synchronized (m_lock) {
      return m_canvas.m_grafx.getCustomNodeShapes(); }
  }

  public final double[] getCustomNodeShape(final byte shape)
  {
    synchronized (m_lock) {
      final float[] fCoords = m_canvas.m_grafx.getCustomNodeShape(shape);
      if (fCoords == null) { return null; }
      final double[] returnThis = new double[fCoords.length];
      for (int i = 0; i < returnThis.length; i++) {
        returnThis[i] = fCoords[i]; }
      return returnThis; }
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
