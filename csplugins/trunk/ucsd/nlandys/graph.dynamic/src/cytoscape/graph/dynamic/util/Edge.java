package cytoscape.graph.dynamic.util;

// Package visible.
class Edge
{

  int m_edgeId;
  Edge m_nextOutEdge;
  Edge m_prevOutEdge;
  Edge m_nextInEdge;
  Edge m_prevInEdge;
  boolean m_directed;
  int m_sourceNode;
  int m_targetNode;

  Edge() { }

}
