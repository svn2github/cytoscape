package fing.model;

import fing.util.IntEnumerator;

// Indices of edges and nodes are non-negative.
interface UnderlyingRootGraph
{
  int nodeCount();
  int edgeCount();
  IntEnumerator nodeIndices();
  IntEnumerator edgeIndices();
  boolean removeNode(int nodeIndex);
  int createNode();
  boolean removeEdge(int edgeIndex);
  // Returns -1 if node indices specified are invalid.
  int createEdge(int sourceIndex, int targetIndex, boolean directed);
  boolean containsNode(int nodeIndex);
  boolean containsEdge(int edgeIndex);
  IntEnumerator adjacentEdgeIndices(int nodeIndex, boolean undirected,
                                    boolean incoming, boolean outgoing);

  Node _suckyGetNodeObject(int nodeIndex);
}
