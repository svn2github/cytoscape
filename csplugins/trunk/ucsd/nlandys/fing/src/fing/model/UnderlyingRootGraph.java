package fing.model;

import cytoscape.util.intr.IntEnumerator;

// Edges and nodes are non-negative.
interface UnderlyingRootGraph
{
  IntEnumerator nodes();
  IntEnumerator edges();
  boolean removeNode(int node);
  int createNode();
  boolean removeEdge(int edge);
  // Returns -1 if nodes specified are invalid.
  int createEdge(int sourceNode, int targetNode, boolean directed);
  boolean containsNode(int node);
  boolean containsEdge(int edge);
  // Throws IllegalArgumentException.
  IntEnumerator adjacentEdges(int node, boolean undirected,
                              boolean incoming, boolean outgoing);
  // Returns -1 if edge specified is invalid.
  int sourceNode(int edge);
  int targetNode(int edge);
  // Throws IllegalArgumentException.
  boolean isDirectedEdge(int edge);
}
