package fing.model;

import fing.util.IntEnumerator;

// Indices of edges and nodes are non-negative.
interface UnderlyingRootGraph
{
  IntEnumerator nodes();
  IntEnumerator edges();
  boolean removeNode(int node);
  int createNode();
  boolean removeEdge(int edge);
  // Returns -1 if node indices specified are invalid.
  int createEdge(int sourceNode, int targetNode, boolean directed);
  boolean containsNode(int node);
  boolean containsEdge(int edge);
  // Throws IllegalArgumentException
  IntEnumerator adjacentEdges(int node, boolean undirected,
                              boolean incoming, boolean outgoing);
  int sourceNode(int edge);
  int targetNode(int edge);
  boolean isDirectedEdge(int edge);
}
