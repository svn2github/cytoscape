package fing.model;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.Iterator;

// Package visible class.  Use factory to get instance.
class FRootGraph implements RootGraph
{

  // Package visible constructor.
  FRootGraph() {}

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
    return -1;
  }

  public int getEdgeCount()
  {
    return -1;
  }

  public Iterator nodesIterator()
  {
    return new NodesIterator();
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
    final int[] returnThis = new int[getNodeCount()];
    NodesIterator nIter = (NodesIterator) nodesIterator();
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = nIter.next().getRootGraphIndex();
    return returnThis;
  }

  public Iterator edgesIterator()
  {
    return new EdgesIterator();
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
    final int[] returnThis = new int[getEdgeCount()];
    EdgesIterator eIter = (EdgesIterator) edgesIterator();
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = eIter.next().getRootGraphIndex();
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
    return 0;
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List removeNodes(java.util.List nodes)
  {
    final java.util.ArrayList returnThis =
      new java.util.ArrayList(nodes.size());
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
    return 0;
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

  public int removeEdge(int edgeIndex)
  {
    return 0;
  }

  // This method has been marked deprecated in the Giny API.
  public java.util.List removeEdges(java.util.List edges)
  {
    final java.util.ArrayList returnThis =
      new java.util.ArrayList(edges.size());
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
    targetNodeIndices[sourceNodeIndices.length - 1];
    sourceNodeIndices[targetNodeIndices.length - 1];
    final int[] returnThis = new int[sourceNodeIndices.length];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = createEdge(sourceNodeIndices[i],
                                 targetNodeIndices[i],
                                 directed);
    return returnThis;
  }

  static class NodesIterator implements Iterator
  {
    public boolean hasNext() {
      return false; }
    public FNode next() {
      throw new NoSuchElementException(); }
    public void remove() {
      throw new UnsupportedOperationException(); }
  }

  static class EdgesIterator implements Iterator
  {
    public boolean hasNext() {
      return false; }
    public FEdge next() {
      throw new NoSuchElementException(); }
    public void remove() {
      throw new UnsupportedOperationException(); }
  }

}
