package cytoscape.graph.dynamic.util;

// Package visible.
class Edge
{

  int m_edgeId;
  Edge m_nextAdjEdge;
  Edge m_prevAdjEdge;
  boolean m_directed;
  int m_sourceNode;
  int m_targetNode;

  Edge() { }

}
