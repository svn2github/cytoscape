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
    return m_graph.nodeCount();
  }

  public int getEdgeCount()
  {
    return m_graph.edgeCount();
  }

  public Iterator nodesIterator()
  {
    final IntEnumerator nodes = m_graph.nodeIndices();
    return new Iterator() {
        public boolean hasNext() {
          return nodes.numRemaining() > 0; }
        public Object next() {
          if (!hasNext()) throw new NoSuchElementException();
          return getNode(~(nodes.nextInt())); } };
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
    IntEnumerator nodes = m_graph.nodeIndices();
    final int[] returnThis = new int[nodes.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = nodes.nextInt();
    return returnThis;
  }

  public Iterator edgesIterator()
  {
    final IntEnumerator edges = m_graph.edgeIndices();
    return new Iterator() {
        public boolean hasNext() {
          return edges.numRemaining() > 0; }
        public Object next() {
          if (!hasNext()) throw new NoSuchElementException();
          return getEdge(~(edges.nextInt())); } };
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
    IntEnumerator edges = m_graph.edgeIndices();
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
    try { edgeInxEnum = m_graph.adjacentEdgeIndices
            (positiveNodeIndex, true, true, true); }
    catch (IllegalArgumentException e) { return 0; }
    while (edgeInxEnum.numRemaining() > 0)
      // Does this iteration remain valid even while doing add/remove?
      removeEdge(~(edgeInxEnum.nextInt()));
    if (m_graph.removeNode(positiveNodeIndex)) {
      m_nodes.setNodeAtIndex(null, positiveNodeIndex);
      
      return nodeInx; }
    else { return 0; }
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
    if (positiveNodeIndex < 0) { return 0; }
    else {
      final int returnThis = ~positiveNodeIndex;
      // Theoretically I could postpone the creation of this object
      // and use a bit array to mark indices of nodes which aren't
      // instantiated yet.  This would complicate the code somewhat.
      FNode newNode = m_nodeDepo.getNode();
      newNode.m_rootGraph = this;
      newNode.m_rootGraphIndex = returnThis;
      newNode.m_identifier = null;
      m_nodes.setNodeAtIndex(newNode, positiveNodeIndex);
      return returnThis; }
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
      m_edges.setNodeAtIndex(null, positiveEdgeIndex);
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
    return 0;
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

  // The relationship between indices (both node and edge) in this
  // RootGraph and in the UnderlyingRootGraph is "flip the bits":
  // rootGraphIndex == ~(underlyingRootGraphIndex)
  final UnderlyingRootGraph m_graph;

  // This heap is re-used by many methods.  Make sure to empty() it before
  // using it.  You can use it as a bag of integers, to sort integers, or
  // to filter integer duplicates.
  final MinIntHeap m_heap = new MinIntHeap();

  // This is our "node factory" and "node recyclery".
  final NodeDepository m_nodeDepo = new NodeDepository();

  // This is our "edge factory" and "edge recyclery".
  final EdgeDepository m_edgeDepo = new EdgeDepository();

  // This is our index-to-node mapping.
  final NodeArray m_nodes = new NodeArray();

  // This is our index-to-edge mapping.
  final EdgeArray m_edges = new EdgeArray();

  // Package visible constructor.
  FRootGraph(UnderlyingRootGraph graph) { m_graph = graph; }

}
