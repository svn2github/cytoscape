package fing.model;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.util.intr.IntArray;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.MinIntHeap;

import giny.filter.Filter;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeListener;
import giny.model.Node;
import giny.model.RootGraph;
import giny.model.RootGraphChangeEvent;
import giny.model.RootGraphChangeListener;

import java.util.Iterator;

// Package visible class.
class FGraphPerspective implements GraphPerspective
{

  public void addGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener)
  { // This method is not thread safe; synchronize on an object to make it so.
    m_lis[0] = GraphPerspectiveChangeListenerChain.add(m_lis[0], listener);
  }

  public void removeGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener)
  { // This method is not thread safe; synchronize on an object to make it so.
    m_lis[0] = GraphPerspectiveChangeListenerChain.remove(m_lis[0], listener);
  }

  // The object returned shares the same RootGraph with this object.
  public Object clone()
  {
    final IntEnumerator nativeNodes = m_graph.nodes();
    final IntEnumerator rootGraphNodeInx = new IntEnumerator() {
        public int numRemaining() { return nativeNodes.numRemaining(); }
        public int nextInt() {
          return m_nativeToRootNodeInxMap.getIntAtIndex
            (nativeNodes.nextInt()); } };
    final IntEnumerator nativeEdges = m_graph.edges();
    final IntEnumerator rootGraphEdgeInx = new IntEnumerator() {
        public int numRemaining() { return nativeEdges.numRemaining(); }
        public int nextInt() {
          return m_nativeToRootEdgeInxMap.getIntAtIndex
            (nativeEdges.nextInt()); } };
    return new FGraphPerspective(m_root, rootGraphNodeInx, rootGraphEdgeInx);
  }

  public RootGraph getRootGraph()
  {
    return m_root;
  }

  public int getNodeCount()
  {
    return m_graph.nodes().numRemaining();
  }

  public int getEdgeCount()
  {
    return m_graph.edges().numRemaining();
  }

  public Iterator nodesIterator()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List nodesList()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getNodeIndicesArray()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Iterator edgesIterator()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List edgesList()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getEdgeIndicesArray()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getEdgeIndicesArray(int perspFromNodeInx,
                                   int perspToNodeInx,
                                   boolean includeUndirected,
                                   boolean includeBothDirections)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Node hideNode(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int hideNode(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List hideNodes(java.util.List nodes)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] hideNodes(int[] perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Node restoreNode(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int restoreNode(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List restoreNodes(java.util.List nodes)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List restoreNodes(java.util.List nodes,
                                     boolean restoreIncidentEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] restoreNodes(int[] perspNodeInx,
                            boolean restoreIncidentEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] restoreNodes(int[] perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Edge hideEdge(Edge edge)
  {
    if (edge.getRootGraph() == m_root &&
        m_weeder.hideEdge(this, edge.getRootGraphIndex()) < 0) return edge;
    else return null;
  }

  public int hideEdge(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List hideEdges(java.util.List edges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] hideEdges(int[] perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Edge restoreEdge(Edge edge)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int restoreEdge(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List restoreEdges(java.util.List edges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] restoreEdges(int[] perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean containsNode(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean containsNode(Node node, boolean recurse)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean containsEdge(Edge edge)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean containsEdge(Edge edge, boolean recurse)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective join(GraphPerspective persp)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective createGraphPerspective(Node[] nodes, Edge[] edges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective createGraphPerspective(int[] perspNodeInx,
                                                 int[] perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective createGraphPerspective(Filter filter)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List neighborsList(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] neighborsArray(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean isNeighbor(Node aNodel, Node anotherNode)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean isNeighbor(int perspNodeInx, int perspAnotherNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean edgeExists(Node from, Node to)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean edgeExists(int perspFromNodeInx, int perspToNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeCount(Node from, Node to, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeCount(int perspFromNodeInx, int perspToNodeInx,
                          boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List edgesList(Node from, Node to)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List edgesList(int perspFromNodeInx,
                                  int perspToNodeInx,
                                  boolean includeUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getEdgeIndicesArray(int perspFromNodeInx,
                                   int perspToNodeInx,
                                   boolean includeUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getInDegree(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getInDegree(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getInDegree(Node node, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getInDegree(int perspNodeInx, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getOutDegree(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getOutDegree(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getOutDegree(Node node, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getOutDegree(int perspNodeInx, boolean countUndirectedEdges)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getDegree(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getDegree(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getIndex(Node node)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getNodeIndex(int rootGraphNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getRootGraphNodeIndex(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Node getNode(int perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getIndex(Edge edge)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeIndex(int rootGraphEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getRootGraphEdgeIndex(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public Edge getEdge(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeSourceIndex(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeTargetIndex(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean isEdgeDirected(int perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public boolean isMetaParent(Node child, Node parent)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isNodeMetaParent(int perspChildNodeInx,
                                  int perspParentNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List metaParentsList(Node node)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List nodeMetaParentsList(int perspNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] getNodeMetaParentIndicesArray(int perspNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isMetaChild(Node parent, Node child)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isNodeMetaChild(int perspNodeInx, int perspChildInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List nodeMetaChildrenList(Node node)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List nodeMetaChildrenList(int perspParentInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] getNodeMetaChildIndicesArray(int perspNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isMetaParent(Edge child, Node parent)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isEdgeMetaParent(int perspChildEdgeInx,
                                  int perspParentNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List metaParentsList(Edge edge)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List edgeMetaParentsList(int perspEdgeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] getEdgeMetaParentIndicesArray(int perspEdgeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isMetaChild(Node parent, Edge child)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean isEdgeMetaChild(int perspParentNodeInx,
                                 int perspChildEdgeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List edgeMetaChildrenList(Node node)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List edgeMetaChildrenList(int perspParentNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] getEdgeMetaChildIndicesArray(int perspParentNodeInx)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public java.util.List getAdjacentEdgesList(Node node,
                                             boolean undirected,
                                             boolean incoming,
                                             boolean outgoing)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getAdjacentEdgeIndicesArray(int perspNodeInx,
                                           boolean undirected,
                                           boolean incoming,
                                           boolean outgoing)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public java.util.List getConnectingEdges(java.util.List nodes)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getConnectingEdgeIndicesArray(int[] perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int[] getConnectingNodeIndicesArray(int[] perspEdgeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public GraphPerspective createGraphPerspective(int[] perspNodeInx)
  {
    throw new IllegalStateException("not implemented yet");
  }

  public void finalize()
  {
    m_root.removeRootGraphChangeListener(m_changeSniffer);
  }

  // Nodes and edges in this graph are called "native indices" throughout
  // this class.
  private final DynamicGraph m_graph;

  private final FRootGraph m_root;

  // This is an array of length 1 - we need an array as an extra reference
  // to a reference because some other inner classes need to know what the
  // current listener is.
  private final GraphPerspectiveChangeListener[] m_lis;

  // RootGraph indices are negative in these arrays.
  private final IntArray m_nativeToRootNodeInxMap;
  private final IntArray m_nativeToRootEdgeInxMap;

  // RootGraph indices are ~ (complements) of the real RootGraph indices
  // in these hashtables.
  private final IntIntHash m_rootToNativeNodeInxMap;
  private final IntIntHash m_rootToNativeEdgeInxMap;

  private final GraphWeeder m_weeder;

  // We need to remove this listener from the RootGraph during finalize().
  private final RootGraphChangeSniffer m_changeSniffer;

  // Package visible constructor.  rootGraphNodeInx
  // must contain all endpoint nodes corresponding to edges in
  // rootGraphEdgeInx.  All indices must correspond to existing nodes
  // and edges.  The indices lists must be non-repeating.
  FGraphPerspective(FRootGraph root,
                    IntEnumerator rootGraphNodeInx,
                    IntEnumerator rootGraphEdgeInx)
  {
    m_graph = DynamicGraphFactory.instantiateDynamicGraph();
    m_root = root;
    m_lis = new GraphPerspectiveChangeListener[1];
    m_nativeToRootNodeInxMap = new IntArray();
    m_nativeToRootEdgeInxMap = new IntArray();
    m_rootToNativeNodeInxMap = new IntIntHash();
    m_rootToNativeEdgeInxMap = new IntIntHash();
    m_weeder = new GraphWeeder(m_root, m_graph,
                               m_nativeToRootNodeInxMap,
                               m_nativeToRootEdgeInxMap,
                               m_rootToNativeNodeInxMap,
                               m_rootToNativeEdgeInxMap, m_lis);
    m_changeSniffer = new RootGraphChangeSniffer(m_weeder);
    while (rootGraphNodeInx.numRemaining() > 0) {
      final int rootNodeInx = rootGraphNodeInx.nextInt();
      final int nativeNodeInx = m_graph.createNode();
      m_nativeToRootNodeInxMap.setIntAtIndex(rootNodeInx, nativeNodeInx);
      m_rootToNativeNodeInxMap.put(~rootNodeInx, nativeNodeInx); }
    while (rootGraphEdgeInx.numRemaining() > 0) {
      final int rootEdgeInx = rootGraphEdgeInx.nextInt();
      final int rootEdgeSourceInx = m_root.getEdgeSourceIndex(rootEdgeInx);
      final int rootEdgeTargetInx = m_root.getEdgeTargetIndex(rootEdgeInx);
      final boolean rootEdgeDirected = m_root.isEdgeDirected(rootEdgeInx);
      final int nativeEdgeSourceInx =
        m_rootToNativeNodeInxMap.get(~rootEdgeSourceInx);
      final int nativeEdgeTargetInx =
        m_rootToNativeNodeInxMap.get(~rootEdgeTargetInx);
      final int nativeEdgeInx =
        m_graph.createEdge(nativeEdgeSourceInx, nativeEdgeTargetInx,
                           rootEdgeDirected);
      m_nativeToRootEdgeInxMap.setIntAtIndex(rootEdgeInx, nativeEdgeInx);
      m_rootToNativeEdgeInxMap.put(~rootEdgeInx, nativeEdgeInx); }
    m_root.addRootGraphChangeListener(m_changeSniffer);
  }

  // Cannot have any recursize reference to a FGraphPerspective in this
  // object instance - we want to allow garbage collection of unused
  // GraphPerspective objects.
  private final static class RootGraphChangeSniffer
    implements RootGraphChangeListener
  {

    private final GraphWeeder m_weeder;

    private RootGraphChangeSniffer(GraphWeeder weeder)
    {
      m_weeder = weeder;
    }

    public final void rootGraphChanged(RootGraphChangeEvent evt)
    {
      if ((evt.getType() & RootGraphChangeEvent.NODES_REMOVED_TYPE) != 0)
        m_weeder.hideNodes(evt.getSource(), evt.getRemovedNodes());
      if ((evt.getType() & RootGraphChangeEvent.EDGES_REMOVED_TYPE) != 0)
        m_weeder.hideEdges(evt.getSource(), evt.getRemovedEdges());
    }

  }

  // An instance of this class cannot have any recursive reference to a
  // FGraphPerspective object.  The idea behind this class is to allow
  // garbage collection of unused GraphPerspective objects.  This class
  // is used by the RootGraphChangeSniffer to remove nodes/edges from
  // a GraphPerspective; this class is also used by this GraphPerspective
  // implementation itself.
  private final static class GraphWeeder
  {

    private final RootGraph m_root;
    private final DynamicGraph m_graph;
    private final IntArray m_nativeToRootNodeInxMap;
    private final IntArray m_nativeToRootEdgeInxMap;
    private final IntIntHash m_rootToNativeNodeInxMap;
    private final IntIntHash m_rootToNativeEdgeInxMap;

    // This is an array of length 1 - we need an array as an extra reference
    // to a reference because the surrounding GraphPerspective will be
    // modifying the entry at index 0 in this array.
    private final GraphPerspectiveChangeListener[] m_lis;

    private GraphWeeder(RootGraph root,
                        DynamicGraph graph,
                        IntArray nativeToRootNodeInxMap,
                        IntArray nativeToRootEdgeInxMap,
                        IntIntHash rootToNativeNodeInxMap,
                        IntIntHash rootToNativeEdgeInxMap,
                        GraphPerspectiveChangeListener[] listener)
    {
      m_root = root;
      m_graph = graph;
      m_nativeToRootNodeInxMap = nativeToRootNodeInxMap;
      m_nativeToRootEdgeInxMap = nativeToRootEdgeInxMap;
      m_rootToNativeNodeInxMap = rootToNativeNodeInxMap;
      m_rootToNativeEdgeInxMap = rootToNativeEdgeInxMap;
      m_lis = listener;
    }

    // RootGraphChangeSniffer is not to call this method.  We rely on
    // the specified node still existing in the RootGraph in this method.
    private final int hideNode(GraphPerspective source, int rootGraphNodeInx)
    {
      final int returnThis = _hideNode(source, rootGraphNodeInx);
      if (returnThis != 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final Node removedNode = m_root.getNode(rootGraphNodeInx);
          listener.graphPerspectiveChanged
            (new GraphPerspectiveNodesHiddenEvent
             (source, new Node[] { removedNode })); } }
      return returnThis;
    }

    // Don't call this method from outside this inner class.
    // Returns 0 if and only if hiding this node was unsuccessful.
    // Otherwise returns the input parameter, the root node index.
    private int _hideNode(Object source, int rootGraphNodeInx)
    {
      final int nativeNodeIndex =
        m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);
      final IntEnumerator edgeInxEnum;
      try { edgeInxEnum = m_graph.adjacentEdges
              (nativeNodeIndex, true, true, true); }
      catch (IllegalArgumentException e) { return 0; }
      if (edgeInxEnum == null) return 0;
      final Edge[] edgeRemoveArr = new Edge[edgeInxEnum.numRemaining()];
      for (int i = 0; i < edgeRemoveArr.length; i++) {
        final int rootGraphEdgeInx =
          m_nativeToRootEdgeInxMap.getIntAtIndex(edgeInxEnum.nextInt());
        // The edge returned by the RootGraph won't be null even if this
        // hideNode operation is triggered by a node being removed from
        // the underlying RootGraph - this is because when a node is removed
        // from an underlying RootGraph, all touching edges to that node are
        // removed first from that RootGraph, and corresponding edge removal
        // events are fired before the node removal event is fired.
        edgeRemoveArr[i] = m_root.getEdge(rootGraphEdgeInx); }
      hideEdges(source, edgeRemoveArr);
      // nativeNodeIndex tested for validity with adjacentEdges() above.
      if (m_graph.removeNode(nativeNodeIndex)) {
        m_rootToNativeNodeInxMap.put(~rootGraphNodeInx, Integer.MAX_VALUE);
        m_nativeToRootNodeInxMap.setIntAtIndex(0, nativeNodeIndex);
        return rootGraphNodeInx; }
      else throw new IllegalStateException
             ("internal error - node didn't exist, its adjacent edges did");
    }

    private final int[] hideNodes(Object source, Node[] nodes)
    {
      // We can't use m_heap here because it's used be every _hideNode().
      final MinIntHeap successes = new MinIntHeap();
      final int[] returnThis = new int[nodes.length];
      for (int i = 0; i < nodes.length; i++) {
        returnThis[i] = _hideNode(source, nodes[i].getRootGraphIndex());
        if (returnThis[i] != 0) successes.toss(i); }
      if (successes.size() > 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final Node[] successArr = new Node[successes.size()];
          final IntEnumerator enum = successes.elements();
          int index = -1;
          while (enum.numRemaining() > 0)
            successArr[++index] = nodes[enum.nextInt()];
          listener.graphPerspectiveChanged
            (new GraphPerspectiveNodesHiddenEvent(source, successArr)); } }
      return returnThis;          
    }

    // RootGraphChangeSniffer is not to call this method.  We rely on
    // the specified edge still existing in the RootGraph in this method.
    private final int hideEdge(GraphPerspective source, int rootGraphEdgeInx)
    {
      final int returnThis = _hideEdge(rootGraphEdgeInx);
      if (returnThis != 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final Edge removedEdge = m_root.getEdge(rootGraphEdgeInx);
          listener.graphPerspectiveChanged
            (new GraphPerspectiveEdgesHiddenEvent
             (source, new Edge[] { removedEdge })); } }
      return returnThis;
    }

    // Don't call this method from outside this inner class.
    // Returns 0 if and only if hiding this edge was unsuccessful.
    // Otherwise returns the input parameter, the root edge index.
    private int _hideEdge(int rootGraphEdgeInx)
    {
      final int nativeEdgeIndex =
        m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);
      try {
        if (m_graph.removeEdge(nativeEdgeIndex)) {
          m_rootToNativeEdgeInxMap.put(~rootGraphEdgeInx, Integer.MAX_VALUE);
          m_nativeToRootEdgeInxMap.setIntAtIndex(0, nativeEdgeIndex);
          return rootGraphEdgeInx; } }
      catch (IllegalArgumentException e) { }
      return 0;
    }

    private final MinIntHeap m_heap = new MinIntHeap();

    private final int[] hideEdges(Object source, Edge[] edges)
    {
      m_heap.empty();
      final MinIntHeap successes = m_heap;
      final int[] returnThis = new int[edges.length];
      for (int i = 0; i < edges.length; i++) {
        returnThis[i] = _hideEdge(edges[i].getRootGraphIndex());
        if (returnThis[i] != 0) successes.toss(i); }
      if (successes.size() > 0) {
        final GraphPerspectiveChangeListener listener = m_lis[0];
        if (listener != null) {
          final Edge[] successArr = new Edge[successes.size()];
          final IntEnumerator enum = successes.elements();
          int index = -1;
          while (enum.numRemaining() > 0)
            successArr[++index] = edges[enum.nextInt()];
          listener.graphPerspectiveChanged
            (new GraphPerspectiveEdgesHiddenEvent(source, successArr)); } }
      return returnThis;
    }

  }

}
