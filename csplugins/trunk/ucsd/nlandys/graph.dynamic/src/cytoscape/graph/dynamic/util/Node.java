package cytoscape.graph.dynamic.util;

// Package visible.
class Node
{

  int nodeId;
  Node nextNode;
  Node prevNode;
  Edge firstOutEdge;
  Edge firstInEdge;

  // The number of directed edges whose target is this node.
  int inDegree;

  // The number of directed edges whose source is this node.
  int outDegree;

  // The number of undirected edges which touch this node.
  int undDegree;

  Node() { }

}
