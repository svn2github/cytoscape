package fing.model;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.util.intr.IntArray;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIntHash;

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
    m_lis = GraphPerspectiveChangeListenerChain.add(m_lis, listener);
  }

  public void removeGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener)
  { // This method is not thread safe; synchronize on an object to make it so.
    m_lis = GraphPerspectiveChangeListenerChain.remove(m_lis, listener);
  }

  // The object returned shares the same RootGraph with this object.
  public Object clone()
  {
    final int numNodes = m_numNodes;
    final IntEnumerator rootGraphNodeInx = new IntEnumerator() {
        private int index = 0;
        public int numRemaining() { return numNodes - index; }
        public int nextInt() {
          return m_perspToRootNodeInxMap.getIntAtIndex(index++); } };
    final int numEdges = m_numEdges;
    final IntEnumerator rootGraphEdgeInx = new IntEnumerator() {
        private int index = 0;
        public int numRemaining() { return numEdges - index; }
        public int nextInt() {
          return m_perspToRootEdgeInxMap.getIntAtIndex(index++); } };
    return new FGraphPerspective(m_root, rootGraphNodeInx, rootGraphEdgeInx);
  }

  public RootGraph getRootGraph()
  {
    return m_root;
  }

  public int getNodeCount()
  {
    throw new IllegalStateException("not implemented yet");
  }

  public int getEdgeCount()
  {
    throw new IllegalStateException("not implemented yet");
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
    throw new IllegalStateException("not implemented yet");
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

  private final FRootGraph m_root;
  private GraphPerspectiveChangeListener m_lis;
  private int m_numNodes;
  private int m_numEdges;
  private final IntArray m_perspToRootNodeInxMap;
  private final IntArray m_perspToRootEdgeInxMap;
  private final IntIntHash m_rootToPerspNodeInxMap;
  private final IntIntHash m_rootToPerspEdgeInxMap;

  // Package visible constructor.  rootGraphNodeInx
  // must contain all endpoint nodes corresponding to edges in
  // rootGraphEdgeInx.  All indices must correspond to existing nodes
  // and edges.  The indices lists must be non-repeating.
  FGraphPerspective(FRootGraph root,
                    IntEnumerator rootGraphNodeInx,
                    IntEnumerator rootGraphEdgeInx)
  {
    m_root = root;
    m_lis = null;
    m_numNodes = rootGraphNodeInx.numRemaining();
    m_numEdges = rootGraphEdgeInx.numRemaining();
    m_perspToRootNodeInxMap = new IntArray();
    m_rootToPerspNodeInxMap = new IntIntHash();
    for (int i = 0; i < m_numNodes; i++) {
      final int rootGraphInx = rootGraphNodeInx.nextInt();
      m_perspToRootNodeInxMap.setIntAtIndex(rootGraphInx, i);
      m_rootToPerspNodeInxMap.put(rootGraphInx, i); }
    m_perspToRootEdgeInxMap = new IntArray();
    m_rootToPerspEdgeInxMap = new IntIntHash();
    for (int i = 0; i < m_numEdges; i++) {
      final int rootGraphInx = rootGraphEdgeInx.nextInt();
      m_perspToRootEdgeInxMap.setIntAtIndex(rootGraphInx, i);
      m_rootToPerspEdgeInxMap.put(rootGraphInx, i); }
  }

  // Cannot have any recursize reference to a FGraphPerspective in this
  // object instance - we want to allow garbage collection of unused
  // GraphPerspective objects.
  private final static class RootGraphChangeSniffer
    implements RootGraphChangeListener
  {

    public final void rootGraphChanged(RootGraphChangeEvent evt)
    {
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

    private final DynamicGraph m_graph;

    // This is an array of length 0 - we need an array as an extra reference
    // to a reference because the surrounding GraphPerspective will be
    // modifying the entry at index 0 in this array.
    private final GraphPerspectiveChangeListener[] m_lis;

    private GraphWeeder(DynamicGraph graph,
                        GraphPerspectiveChangeListener[] listener)
    {
      m_graph = graph;
      m_lis = listener;
    }

    private final int hideNode(int rootGraphNodeInx)
    {
      return 0;
    }

    private final int[] hideNodes(int[] rootGraphNodeInx)
    {
      return null;
    }

    private final int hideEdge(int rootGraphNodeInx)
    {
      return 0;
    }

    private final int[] hideEdges(int[] rootGraphNodeInx)
    {
      return null;
    }

  }

}
