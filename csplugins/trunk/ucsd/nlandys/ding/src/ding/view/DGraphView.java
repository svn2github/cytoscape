package ding.view;

import cytoscape.geom.rtree.RTree;
import cytoscape.geom.spacial.MutableSpacialIndex2D;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;
import giny.model.GraphPerspective;
import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.GraphViewChangeListener;
import giny.view.NodeView;
import java.awt.Component;
import java.awt.Font;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DGraphView implements GraphView
{

  final Object m_lock = new Object();
  final float[] m_extentsBuff = new float[4];
  GraphPerspective m_perspective;

  // Throughout this code I am assuming that nodes or edges are never
  // removed from the underlying RootGraph.  This assumption was made in the
  // old GraphView implementation.  Removal from the RootGraph is the only
  // thing that can affect m_drawPersp and m_structPersp that is beyond our
  // control.
  GraphPerspective m_drawPersp; // Visible graph.
  GraphPerspective m_structPersp; // Graph of all views (even hidden ones).

  MutableSpacialIndex2D m_spacial;
  DNodeDetails m_nodeDetails;
  DEdgeDetails m_edgeDetails;
  HashMap m_nodeViewMap;
  HashMap m_edgeViewMap;
  String m_identifier;
  final float m_defaultNodeXMin;
  final float m_defaultNodeYMin;
  final float m_defaultNodeXMax;
  final float m_defaultNodeYMax;
  InnerCanvas m_canvas;
  boolean m_nodeSelection = true;
  boolean m_edgeSelection = false;
  final IntBTree m_selectedNodes;

  public DGraphView(GraphPerspective perspective)
  {
    m_perspective = perspective;
    m_drawPersp = m_perspective.getRootGraph().createGraphPerspective
      ((int[]) null, (int[]) null);
    m_structPersp = m_perspective.getRootGraph().createGraphPerspective
      ((int[]) null, (int[]) null);
    m_spacial = new RTree();
    m_nodeDetails = new DNodeDetails();
    m_edgeDetails = new DEdgeDetails(this);
    m_nodeViewMap = new HashMap();
    m_edgeViewMap = new HashMap();
    m_defaultNodeXMin = -10.0f;
    m_defaultNodeYMin = -10.0f;
    m_defaultNodeXMax = 10.0f;
    m_defaultNodeYMax = 10.0f;
    m_canvas = new InnerCanvas(m_lock, this);
    m_selectedNodes = new IntBTree();
  }

  public GraphPerspective getGraphPerspective()
  {
    return m_perspective;
  }

  public boolean nodeSelectionEnabled()
  {
    return m_nodeSelection;
  }

  public boolean edgeSelectionEnabled()
  {
    return m_edgeSelection;
  }

  public void enableNodeSelection()
  {
    synchronized (m_lock) {
      m_nodeSelection = true; }
  }

  public void disableNodeSelection()
  {
    
  }

  public void enableEdgeSelection()
  {
  }

  public void disableEdgeSelection()
  {
  }

  public int[] getSelectedNodeIndices()
  {
    synchronized (m_lock) {
      final IntEnumerator elms = m_selectedNodes.searchRange
        (Integer.MIN_VALUE, Integer.MAX_VALUE, false);
      final int[] returnThis = new int[elms.numRemaining()];
      for (int i = 0; i < returnThis.length; i++) {
        returnThis[i] = ~elms.nextInt(); }
      return returnThis; }
  }

  public List getSelectedNodes()
  {
    return null;
  }

  public int[] getSelectedEdgeIndices()
  {
    return null;
  }

  public List getSelectedEdges()
  {
    return null;
  }

  public void addGraphViewChangeListener(GraphViewChangeListener l)
  {
  }

  public void removeGraphViewChangeListener(GraphViewChangeListener l)
  {
  }

  public void setBackgroundPaint(Paint paint)
  {
    synchronized (m_lock) {
      m_canvas.m_bgPaint = paint; }
  }

  public Paint getBackgroundPaint()
  {
    return m_canvas.m_bgPaint;
  }

  public Component getComponent()
  {
    return m_canvas;
  }

  public NodeView addNodeView(int nodeInx)
  {
    synchronized (m_lock) {
      final NodeView oldView =
        (NodeView) m_nodeViewMap.get(new Integer(nodeInx));
      if (oldView != null) { return oldView; }
      if (m_drawPersp.restoreNode(nodeInx) == 0) {
        if (m_drawPersp.getNode(nodeInx) != null) {
          throw new IllegalStateException
            ("something weird is going on - node already existed in graph " +
             "but a view for it did not exist (debug)"); }
        throw new IllegalArgumentException
          ("node index specified does not exist in underlying RootGraph"); }
      m_structPersp.restoreNode(nodeInx);
      final NodeView returnThis = new DNodeView(this, nodeInx);
      m_nodeViewMap.put(new Integer(nodeInx), returnThis);
      m_spacial.insert(~nodeInx, m_defaultNodeXMin, m_defaultNodeYMin,
                       m_defaultNodeXMax, m_defaultNodeYMax);
      return returnThis; }
  }

  public EdgeView addEdgeView(int edgeInx)
  {
    synchronized (m_lock) {
      final EdgeView oldView =
        (EdgeView) m_edgeViewMap.get(new Integer(edgeInx));
      if (oldView != null) { return oldView; }
      final Edge edge = m_drawPersp.getRootGraph().getEdge(edgeInx);
      if (edge == null) {
        throw new IllegalArgumentException
          ("edge index specified does not exist in underlying RootGraph"); }
      addNodeView(edge.getSource().getRootGraphIndex());
      addNodeView(edge.getTarget().getRootGraphIndex());
      if (m_drawPersp.restoreEdge(edgeInx) == 0) {
        if (m_drawPersp.getEdge(edgeInx) != null) {
          throw new IllegalStateException
            ("something weird is going on - edge already existed in graph " +
             "but a view for it did not exist (debug)"); }
        throw new IllegalArgumentException
          ("edge index specified does not exist in underlying RootGraph"); }
      m_structPersp.restoreEdge(edgeInx);
      final EdgeView returnThis = new DEdgeView(this, edgeInx);
      m_edgeViewMap.put(new Integer(edgeInx), returnThis);
      return returnThis; }
  }

  public EdgeView addEdgeView(String className, int edgeInx)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  public NodeView addNodeView(String className, int nodeInx)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  public NodeView addNodeView(int nodeInx, NodeView replacement)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  public NodeView removeNodeView(NodeView nodeView)
  {
    return removeNodeView(nodeView.getRootGraphIndex());
  }

  public NodeView removeNodeView(Node node)
  {
    return removeNodeView(node.getRootGraphIndex());
  }

  public NodeView removeNodeView(int nodeInx)
  {
    synchronized (m_lock) {
      // We have to query edges in the m_structPersp, not m_drawPersp because
      // what if the node is hidden?
      final int[] edges =
        m_structPersp.getAdjacentEdgeIndicesArray(nodeInx, true, true, true);
      if (edges == null) { return null; }
      for (int i = 0; i < edges.length; i++) {
        removeEdgeView(edges[i]); }
      final DNodeView returnThis =
        (DNodeView) m_nodeViewMap.remove(new Integer(nodeInx));
      // If this node was hidden, it won't be in m_drawPersp.
      m_drawPersp.hideNode(nodeInx);
      m_structPersp.hideNode(nodeInx);
      m_nodeDetails.unregisterNode(~nodeInx);
      // If this node was hidden, it won't be in m_spacial.
      m_spacial.delete(~nodeInx);
      returnThis.m_view = null;
      return returnThis; }
  }

  public EdgeView removeEdgeView(EdgeView edgeView)
  {
    return removeEdgeView(edgeView.getRootGraphIndex());
  }

  public EdgeView removeEdgeView(Edge edge)
  {
    return removeEdgeView(edge.getRootGraphIndex());
  }

  public EdgeView removeEdgeView(int edgeInx)
  {
    synchronized (m_lock) {
      final DEdgeView returnThis =
        (DEdgeView) m_edgeViewMap.remove(new Integer(edgeInx));
      if (returnThis == null) { return returnThis; }
      // If this edge view was hidden, it won't be in m_drawPersp.
      m_drawPersp.hideEdge(edgeInx);
      m_structPersp.hideEdge(edgeInx);
      m_edgeDetails.unregisterEdge(~edgeInx);
      returnThis.m_view = null;
      return returnThis; }
  }

  public String getIdentifier()
  {
    return m_identifier;
  }

  public void setIdentifier(String id)
  {
    m_identifier = id;
  }

  public double getZoom()
  {
    return m_canvas.m_scaleFactor;
  }

  public void setZoom(double zoom)
  {
    synchronized (m_lock) {
      m_canvas.m_scaleFactor = zoom; }
  }

  public void fitContent()
  {
    synchronized (m_lock) {
      if (m_spacial.queryOverlap(Float.NEGATIVE_INFINITY,
                                 Float.NEGATIVE_INFINITY,
                                 Float.POSITIVE_INFINITY,
                                 Float.POSITIVE_INFINITY,
                                 m_extentsBuff, 0, false).numRemaining() > 0) {
        m_canvas.m_xCenter =
          (((double) m_extentsBuff[0]) + ((double) m_extentsBuff[2])) / 2.0d;
        m_canvas.m_yCenter =
          (((double) m_extentsBuff[1]) + ((double) m_extentsBuff[3])) / 2.0d;
        m_canvas.m_scaleFactor = Math.min
          (((double) m_canvas.getWidth()) /
           (((double) m_extentsBuff[2]) - ((double) m_extentsBuff[0])),
           ((double) m_canvas.getHeight()) /
           (((double) m_extentsBuff[3]) - ((double) m_extentsBuff[1]))); } }
  }

  public void updateView()
  {
    m_canvas.repaint();
  }

  public RootGraph getRootGraph()
  {
    return m_perspective.getRootGraph();
  }

  /*
   * Returns an iterator of all node views, including those that are
   * currently hidden.
   */
  public Iterator getNodeViewsIterator()
  {
    synchronized (m_lock) { return m_nodeViewMap.values().iterator(); }
  }

  /*
   * Returns the count of all node views, including those that are currently
   * hidden.
   */
  public int getNodeViewCount()
  {
    synchronized (m_lock) { return m_nodeViewMap.size(); }
  }

  /*
   * Returns the count of all edge views, including those that are currently
   * hidden.
   */
  public int getEdgeViewCount()
  {
    synchronized (m_lock) { return m_edgeViewMap.size(); }
  }

  public NodeView getNodeView(Node node)
  {
    return getNodeView(node.getRootGraphIndex());
  }

  public NodeView getNodeView(int nodeInx)
  {
    synchronized (m_lock) {
      return (NodeView) m_nodeViewMap.get(new Integer(nodeInx)); }
  }

  /*
   * Returns a list of all edge views, including those that are currently
   * hidden.
   */
  public List getEdgeViewsList()
  {
    synchronized (m_lock) {
      final ArrayList returnThis = new ArrayList(m_edgeViewMap.size());
      final Iterator values = m_edgeViewMap.values().iterator();
      while (values.hasNext()) {
        returnThis.add(values.next()); }
      return returnThis; }
  }

  /*
   * Returns all edge views (including the hidden ones) that are either 1.
   * directed, having oneNode as source and otherNode as target or 2.
   * undirected, having oneNode and otherNode as endpoints.  Note that
   * this behaviour is similar to that of
   * GraphPerspective.edgesList(Node, Node).
   */
  public List getEdgeViewsList(Node oneNode, Node otherNode)
  {
    synchronized (m_lock) {
      List edges = m_structPersp.edgesList
        (oneNode.getRootGraphIndex(), otherNode.getRootGraphIndex(), true);
      if (edges == null) { return null; }
      final ArrayList returnThis = new ArrayList();
      Iterator it = edges.iterator();
      while (it.hasNext()) {
        Edge e = (Edge) it.next();
        returnThis.add(getEdgeView(e)); }
      return returnThis; }
  }

  /*
   * Similar to getEdgeViewsList(Node, Node), only that one has control
   * of whether or not to include undirected edges.
   */
  public List getEdgeViewsList(int oneNodeInx, int otherNodeInx,
                               boolean includeUndirected)
  {
    synchronized (m_lock) {
      List edges = m_structPersp.edgesList
        (oneNodeInx, otherNodeInx, includeUndirected);
      if (edges == null) { return null; }
      final ArrayList returnThis = new ArrayList();
      Iterator it = edges.iterator();
      while (it.hasNext()) {
        Edge e = (Edge) it.next();
        returnThis.add(getEdgeView(e)); }
      return returnThis; }
  }

  /*
   * Returns an edge view with specified edge index whether or not the edge
   * view is hidden; null is returned if view does not exist.
   */
  public EdgeView getEdgeView(int edgeInx)
  {
    synchronized (m_lock) {
      return (EdgeView) m_edgeViewMap.get(new Integer(edgeInx)); }
  }

  /*
   * Returns an iterator of all edge views, including those that are
   * currently hidden.
   */
  public Iterator getEdgeViewsIterator()
  {
    synchronized (m_lock) { return m_edgeViewMap.values().iterator(); }
  }

  public EdgeView getEdgeView(Edge edge)
  {
    return getEdgeView(edge.getRootGraphIndex());
  }

  /*
   * Alias to getEdgeViewCount().
   */
  public int edgeCount()
  {
    return getEdgeViewCount();
  }

  /*
   * Alias to getNodeViewCount().
   */
  public int nodeCount()
  {
    return getNodeViewCount();
  }

  /*
   * obj should be either a DEdgeView or a DNodeView.
   */
  public boolean hideGraphObject(Object obj)
  {
    synchronized (m_lock) {
      if (obj instanceof DEdgeView) {
        final int edgeInx = ((DEdgeView) obj).getRootGraphIndex();
        if (m_drawPersp.hideEdge(edgeInx) == 0) { return false; }
        return true; }
      else if (obj instanceof DNodeView) {
        final DNodeView nView = (DNodeView) obj;
        final int nodeInx = nView.getRootGraphIndex();
        final int[] edges = m_drawPersp.getAdjacentEdgeIndicesArray
          (nodeInx, true, true, true);
        if (edges == null) { return false; }
        for (int i = 0; i < edges.length; i++) {
          hideGraphObject(m_edgeViewMap.get(new Integer(edges[i]))); }
        m_spacial.exists(~nodeInx, m_extentsBuff, 0);
        nView.m_hiddenXMin = m_extentsBuff[0];
        nView.m_hiddenYMin = m_extentsBuff[1];
        nView.m_hiddenXMax = m_extentsBuff[2];
        nView.m_hiddenYMax = m_extentsBuff[3];
        m_drawPersp.hideNode(nodeInx);
        m_spacial.delete(~nodeInx);
        return true; }
      else { return false; } }
  }

  /*
   * obj should be either a DEdgeView or a DNodeView.
   */
  public boolean showGraphObject(Object obj)
  {
    synchronized (m_lock) {
      if (obj instanceof DNodeView) {
        final DNodeView nView = (DNodeView) obj;
        final int nodeInx = nView.getRootGraphIndex();
        if (m_structPersp.getNode(nodeInx) == null) { return false; }
        if (m_drawPersp.restoreNode(nodeInx) == 0) { return false; }
        m_spacial.insert(~nodeInx, nView.m_hiddenXMin, nView.m_hiddenYMin,
                         nView.m_hiddenXMax, nView.m_hiddenYMax);
        return true; }
      else if (obj instanceof DEdgeView) {
        final Edge edge =
          m_structPersp.getEdge(((DEdgeView) obj).getRootGraphIndex());
        if (edge == null) { return false; }
        // The edge exists in m_structPersp, therefore its source and target
        // node views must also exist.
        showGraphObject(getNodeView(edge.getSource().getRootGraphIndex()));
        showGraphObject(getNodeView(edge.getTarget().getRootGraphIndex()));
        if (m_drawPersp.restoreEdge(edge.getRootGraphIndex()) == 0) {
          return false; }
        return true; }
      else { return false; } }
  }

  public boolean hideGraphObjects(List objects)
  {
    final Iterator it = objects.iterator();
    while (it.hasNext()) {
      hideGraphObject(it.next()); }
    return true;
  }

  public boolean showGraphObjects(List objects)
  {
    final Iterator it = objects.iterator();
    while (it.hasNext()) {
      showGraphObject(it.next()); }
    return true;
  }

  public Object[] getContextMethods(String className, boolean plusSuperclass)
  {
    return null;
  }

  public Object[] getContextMethods(String className, Object[] methods)
  {
    return null;
  }

  public boolean addContextMethod(String className, String methodClassName,
                                  String methodName, Object[] args,
                                  ClassLoader loader)
  {
    return false;
  }

  public void setAllNodePropertyData(int nodeInx, Object[] data)
  {
  }

  public Object[] getAllNodePropertyData(int nodeInx)
  {
    return null;
  }

  public void setAllEdgePropertyData(int edgeInx, Object[] data)
  {
  }

  public Object[] getAllEdgePropertyData(int edgeInx)
  {
    return null;
  }

  public Object getNodeObjectProperty(int nodeInx, int property)
  {
    return null;
  }

  public boolean setNodeObjectProperty(int nodeInx, int property, Object value)
  {
    return false;
  }

  public Object getEdgeObjectProperty(int edgeInx, int property)
  {
    return null;
  }

  public boolean setEdgeObjectProperty(int edgeInx, int property, Object value)
  {
    return false;
  }

  public double getNodeDoubleProperty(int nodeInx, int property)
  {
    return 0.0d;
  }

  public boolean setNodeDoubleProperty(int nodeInx, int property, double val)
  {
    return false;
  }

  public double getEdgeDoubleProperty(int edgeInx, int property)
  {
    return 0.0d;
  }

  public boolean setEdgeDoubleProperty(int edgeInx, int property, double val)
  {
    return false;
  }

  public float getNodeFloatProperty(int nodeInx, int property)
  {
    return 0.0f;
  }

  public boolean setNodeFloatProperty(int nodeInx, int property, float value)
  {
    return false;
  }

  public float getEdgeFloatProperty(int edgeInx, int property)
  {
    return 0.0f;
  }

  public boolean setEdgeFloatProperty(int edgeInx, int property, float value)
  {
    return false;
  }

  public boolean getNodeBooleanProperty(int nodeInx, int property)
  {
    return false;
  }

  public boolean setNodeBooleanProperty(int nodeInx, int property, boolean val)
  {
    return false;
  }

  public boolean getEdgeBooleanProperty(int edgeInx, int property)
  {
    return false;
  }

  public boolean setEdgeBooleanProperty(int edgeInx, int property, boolean val)
  {
    return false;
  }

  public int getNodeIntProperty(int nodeInx, int property)
  {
    return 0;
  }

  public boolean setNodeIntProperty(int nodeInx, int property, int value)
  {
    return false;
  }

  public int getEdgeIntProperty(int edgeInx, int property)
  {
    return 0;
  }

  public boolean setEdgeIntProperty(int edgeInx, int property, int value)
  {
    return false;
  }

  // Auxillary methods specific to this GraphView implementation:

  public void setGraphLOD(GraphLOD lod)
  {
    synchronized (m_lock) { m_canvas.m_lod = lod; }
  }

}
