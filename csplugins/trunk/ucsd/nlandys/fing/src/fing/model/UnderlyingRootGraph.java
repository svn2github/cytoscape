package fing.model;

import fing.util.IntEnumerator;

// Indices of edges and nodes are non-negative.
interface UnderlyingRootGraph
{
  IntEnumerator nodes();
  IntEnumerator edges();
  boolean removeNode(int nodeIndex);
  int createNode();
  boolean removeEdge(int edgeIndex);
  // Returns -1 if node indices specified are invalid.
  int createEdge(int sourceIndex, int targetIndex, boolean directed);
  boolean containsNode(int nodeIndex);
  boolean containsEdge(int edgeIndex);
  // Throws IllegalArgumentException
  IntEnumerator adjacentEdges(int nodeIndex, boolean undirected,
                              boolean incoming, boolean outgoing);
}
