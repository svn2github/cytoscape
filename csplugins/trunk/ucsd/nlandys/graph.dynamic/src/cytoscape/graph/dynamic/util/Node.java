package cytoscape.graph.dynamic.util;

// Package visible.
class Node
{

  Node m_nextNode;
  Node m_prevNode;
  Edge m_firstOutEdge;
  Edge m_firstInEdge;

  // The number of directed edges whose target is this node.
  int m_inDegree;

  // The number of directed edges whose source is this node.
  int m_outDegree;

  // The number of undirected edges which touch this node.
  int m_undDegree;

  Node() { }

}
