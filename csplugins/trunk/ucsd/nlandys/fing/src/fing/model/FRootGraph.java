package fing.model;

import fing.util.IntEnumerator;
import fing.util.MinIntHeap;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Package visible class.  Use factory to get instance.
class FRootGraph //implements RootGraph
{

  public GraphPerspective createGraphPerspective(Node[] nodes, Edge[] edges)
  {
    return null;
  }

  public GraphPerspective createGraphPerspective(int[] nodeInx, int[] edgeInx)
  {
    return null;
  }

  public void ensureCapacity(int nodes, int edges) { }

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
    final IntEnumerator nodes = m_graph.nodes();
    final FRootGraph rootGraph = this;
    return new Iterator() {
        public void remove() {
          throw new UnsupportedOperationException(); }
        public boolean hasNext() {
          return nodes.numRemaining() > 0; }
        public Object next() {
          if (!hasNext()) throw new NoSuchElementException();
          return rootGraph.getNode(~(nodes.nextInt())); } };
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List nodesList()
  {
    final int nodeCount = getNodeCount();
    final java.util.ArrayList returnThis = new java.util.ArrayList(nodeCount);
    Iterator iter = nodesIterator();
    for (int i = 0; i < nodeCount; i++) returnThis.add(iter.next());
    return returnThis;
  }

  // This method has been marked deprecated in the Giny API.
  public int[] getNodeIndicesArray()
  {
    IntEnumerator nodes = m_graph.nodes();
    final int[] returnThis = new int[nodes.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = nodes.nextInt();
    return returnThis;
  }

  public Iterator edgesIterator()
  {
    final IntEnumerator edges = m_graph.edges();
    final FRootGraph rootGraph = this;
    return new Iterator() {
        public void remove() {
          throw new UnsupportedOperationException(); }
        public boolean hasNext() {
          return edges.numRemaining() > 0; }
        public Object next() {
          if (!hasNext()) throw new NoSuchElementException();
          return rootGraph.getEdge(~(edges.nextInt())); } };
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List edgesList()
  {
    final int edgeCount = getEdgeCount();
    final java.util.ArrayList returnThis = new java.util.ArrayList(edgeCount);
    Iterator iter = edgesIterator();
    for (int i = 0; i < edgeCount; i++) returnThis.add(iter.next());
    return returnThis;
  }

  // This method has been marked deprecated in the Giny API.
  public int[] getEdgeIndicesArray()
  {
    IntEnumerator edges = m_graph.edges();
    final int[] returnThis = new int[edges.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = edges.nextInt();
    return returnThis;
  }

  public Node removeNode(Node node)
  {
    if (node.getRootGraph() == this &&
        removeNode(node.getRootGraphIndex()) < 0) return node;
    else return null;
  }

  public int removeNode(int nodeInx)
  {
    final int positiveNodeIndex = ~nodeInx;
    IntEnumerator edgeInxEnum;
    try { edgeInxEnum = m_graph.adjacentEdges
            (positiveNodeIndex, true, true, true); }
    catch (IllegalArgumentException e) { return 0; }
    m_heap.empty();
    while (edgeInxEnum.numRemaining() > 0)
      // Toss edges to be removed onto the heap; assume that the edge iteration
      // becomes invalid if we remove edges while iterating through.
      m_heap.toss(edgeInxEnum.nextInt());
    edgeInxEnum = m_heap.elements();
    // Remove adjacent edges using method defined on this instance.
    while (edgeInxEnum.numRemaining() > 0)
      removeEdge(~(edgeInxEnum.nextInt()));
    // Remove node from underlying graph.
    if (m_graph.removeNode(positiveNodeIndex)) {
      // Remove node from our node array.
      FNode garbage = m_nodes.getNodeAtIndex(positiveNodeIndex);
      m_nodes.setNodeAtIndex(null, positiveNodeIndex);
      m_nodeDepot.recycleNode(garbage);
      return nodeInx; }
    else return 0;
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List removeNodes(java.util.List nodes)
  {
    final java.util.ArrayList returnThis = new java.util.ArrayList();
    for (int i = 0; i < nodes.size(); i++)
      if (removeNode((Node) nodes.get(i)) != null)
        returnThis.add(nodes.get(i));
    return returnThis;
  }

  public int[] removeNodes(int[] nodeIndices)
  {
    // Instead of doing individual removes for every node, there may
    // be a way to optimize by removing a block of nodes.  Once this
    // implementation matures more, come back here and see if it's
    // possible to optimize.
    final int[] returnThis = new int[nodeIndices.length];
    for (int i = 0; i < nodeIndices.length; i++)
      returnThis[i] = removeNode(nodeIndices[i]);
    return returnThis;
  }

  public int createNode()
  {
    final int positiveNodeIndex = m_graph.createNode();
    final int returnThis = ~positiveNodeIndex;
    // Theoretically I could postpone the creation of this object
    // and use a bit array to mark indices of nodes which aren't
    // instantiated yet.  This would complicate the code somewhat.
    FNode newNode = m_nodeDepot.getNode();
    newNode.m_rootGraph = getThisRootGraph();
    newNode.m_rootGraphIndex = returnThis;
    newNode.m_identifier = null;
    m_nodes.setNodeAtIndex(newNode, positiveNodeIndex);
    return returnThis;
  }

  public int createNode(Node[] nodes, Edge[] edges)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int createNode(GraphPerspective persoective)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int createNode(int[] nodeIndices, int[] edgeindices)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public int[] createNodes(int numNewNodes)
  {
    // Instead of doing individual creations for every node, there may
    // be a way to optimize by creating a block of nodes.  Once this
    // implementation matures more, come back here and see if it's possible
    // to optimize.
    final int[] returnThis = new int[numNewNodes];
    for (int i = 0; i < returnThis.length; i++) returnThis[i] = createNode();
    return returnThis;
  }

  public Edge removeEdge(Edge edge)
  {
    if (edge.getRootGraph() == this &&
        removeEdge(edge.getRootGraphIndex()) < 0) return edge;
    else return null;
  }

  public int removeEdge(int edgeInx)
  {
    final int positiveEdgeIndex = ~edgeInx;
    if (m_graph.removeEdge(positiveEdgeIndex)) {
      FEdge garbage = m_edges.getEdgeAtIndex(positiveEdgeIndex);
      m_edges.setEdgeAtIndex(null, positiveEdgeIndex);
      m_edgeDepot.recycleEdge(garbage);
      return edgeInx; }
    else { return 0; }
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List removeEdges(java.util.List edges)
  {
    final java.util.ArrayList returnThis = new java.util.ArrayList();
    for (int i = 0; i < edges.size(); i++)
      if (removeEdge((Edge) edges.get(i)) != null)
        returnThis.add(edges.get(i));
    return returnThis;
  }

  public int[] removeEdges(int[] edgeIndices)
  {
    // Instead of doing individual removes for every edge, there may be a
    // way to optimize by removing a block of edges.  Once this implementation
    // matures more, come back here and see if it's possible to optimize.
    final int[] returnThis = new int[edgeIndices.length];
    for (int i = 0; i < edgeIndices.length; i++)
      returnThis[i] = removeEdge(edgeIndices[i]);
    return returnThis;
  }

  public int createEdge(Node source, Node target)
  {
    return
      createEdge(source, target,
                 source.getRootGraphIndex() != target.getRootGraphIndex());
  }

  public int createEdge(Node source, Node target, boolean directed)
  {
    if (source.getRootGraph() == this && target.getRootGraph() == this)
      return createEdge(source.getRootGraphIndex(),
                        target.getRootGraphIndex(),
                        directed);
    else return 0;
  }

  public int createEdge(int sourceNodeIndex, int targetNodeIndex)
  {
    return createEdge(sourceNodeIndex, targetNodeIndex,
                      sourceNodeIndex != targetNodeIndex);
  }

  public int createEdge(int sourceNodeIndex, int targetNodeIndex,
                        boolean directed)
  {
    final int positiveSourceNodeIndex = ~sourceNodeIndex;
    final int positiveTargetNodeIndex = ~targetNodeIndex;
    final int positiveEdgeIndex = m_graph.createEdge
      (positiveSourceNodeIndex, positiveTargetNodeIndex, directed);
    final int returnThis;
    if (positiveEdgeIndex < 0) return 0;
    else returnThis = ~positiveEdgeIndex;
    // Theoretically I could postpone the creation of this object
    // and use a bit array to mark indices of edges which aren't
    // instantiated yet.  This would complicate the code somewhat.
    FEdge newEdge = m_edgeDepot.getEdge();
    newEdge.m_rootGraph = getThisRootGraph();
    newEdge.m_rootGraphIndex = returnThis;
    newEdge.m_identifier = null;
    m_edges.setEdgeAtIndex(newEdge, positiveEdgeIndex);
    return returnThis;
  }

  public int[] createEdges(int[] sourceNodeIndices, int[] targetNodeIndices,
                           boolean directed)
  {
    // Instead of doing individual creations for every edge, there may be a
    // way to optimize by creating a block of edges.  Once this implementation
    // matures more, come back here and see if it's possible to optimize.
    int foo = targetNodeIndices[sourceNodeIndices.length - 1];
    foo = sourceNodeIndices[targetNodeIndices.length - 1];
    foo = 0;
    final int[] returnThis = new int[sourceNodeIndices.length];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = createEdge(sourceNodeIndices[i],
                                 targetNodeIndices[i],
                                 directed);
    return returnThis;
  }

  public boolean containsNode(Node node)
  {
    return node.getRootGraph() == this &&
      getNode(node.getRootGraphIndex()) != null;
  }

  public boolean containsEdge(Edge edge)
  {
    return edge.getRootGraph() == this
      && getEdge(edge.getRootGraphIndex()) != null;
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List neighborsList(Node node)
  {
    if (node.getRootGraph() == this)
    {
      final int nodeIndex = node.getRootGraphIndex();
      int[] adjacentEdgeIndices =
        getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);
      m_heap.empty();
      for (int i = 0; i < adjacentEdgeIndices.length; i++) {
        Edge e = getEdge(adjacentEdgeIndices[i]);
        int neighborIndex = (nodeIndex ^ e.getSource().getRootGraphIndex()) ^
          e.getTarget().getRootGraphIndex();
        m_heap.toss(neighborIndex); }
      IntEnumerator enum = m_heap.orderedElements(true);
      java.util.ArrayList list = new java.util.ArrayList(enum.numRemaining());
      while (enum.numRemaining() > 0)
        list.add(new Integer(enum.nextInt()));
      return list;
    }
    else
    {
      return new java.util.ArrayList();
    }
  }

  // This method has been marked deprecated in the Giny API.
  public boolean isNeighbor(Node a, Node b)
  {
    if (a.getRootGraph() == this && b.getRootGraph() == this)
    {
      return isNeighbor(a.getRootGraphIndex(), b.getRootGraphIndex());
    }
    else
    {
      return false;
    }
  }

  public boolean isNeighbor(int nodeInxA, int nodeInxB)
  {
    final int positiveNodeInxA = ~nodeInxA;
    final int positiveNodeInxB = ~nodeInxB;
    final IntEnumerator aAdj;
    final IntEnumerator bAdj;
    try {
      aAdj = m_graph.adjacentEdges(positiveNodeInxA, true, true, true);
      bAdj = m_graph.adjacentEdges(positiveNodeInxB, true, true, true); }
    catch (IllegalArgumentException e) { return false; }
    final IntEnumerator theAdj =
      ((aAdj.numRemaining() < bAdj.numRemaining()) ? aAdj : bAdj);
    final int adjPositiveNode =
      ((theAdj == aAdj) ? positiveNodeInxA : positiveNodeInxB);
    final int neighPositiveNode =
      ((theAdj == aAdj) ? positiveNodeInxB : positiveNodeInxA);
    while (theAdj.numRemaining() > 0) {
      final int adjEdge = theAdj.nextInt();
      if ((m_graph.sourceNode(adjEdge) ^ m_graph.targetNode(adjEdge) ^
           adjPositiveNode) == neighPositiveNode)
        return true; }
    return false;
  }

  public int[] getAdjacentEdgeIndicesArray(int nodeInx,
                                           boolean a,
                                           boolean b,
                                           boolean c)
  {
    return null;
  }

  public Node getNode(int nodeInx)
  {
    if (nodeInx < 0) return m_nodes.getNodeAtIndex(~nodeInx);
    else return null;
  }

  public Edge getEdge(int edgeInx)
  {
    if (edgeInx < 0) return m_edges.getEdgeAtIndex(~edgeInx);
    else return null;
  }

  // The relationship between indices (both node and edge) in this
  // RootGraph and in the UnderlyingRootGraph is "flip the bits":
  // rootGraphIndex == ~(underlyingRootGraphIndex)
  final UnderlyingRootGraph m_graph;

  // This heap is re-used by many methods.  Make sure to empty() it before
  // using it.  You can use it as a bag of integers, to sort integers, or
  // to filter integer duplicates.  You don't need to empty() it after usage.
  final MinIntHeap m_heap = new MinIntHeap();

  // This is our "node factory" and "node recyclery".
  final NodeDepository m_nodeDepot = new NodeDepository();

  // This is our "edge factory" and "edge recyclery".
  final EdgeDepository m_edgeDepot = new EdgeDepository();

  // This is our index-to-node mapping.
  final NodeArray m_nodes = new NodeArray();

  // This is our index-to-edge mapping.
  final EdgeArray m_edges = new EdgeArray();

  // Package visible constructor.
  FRootGraph(UnderlyingRootGraph graph) { m_graph = graph; }

  private RootGraph getThisRootGraph()
  {
    return null;
  }

}
