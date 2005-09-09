package cytoscape.graph.dynamic.util;

// Package visible.
final class Edge implements java.io.Serializable
{

  int edgeId;
  Edge nextOutEdge;
  Edge prevOutEdge;
  Edge nextInEdge;
  Edge prevInEdge;
  boolean directed;
  int sourceNode;
  int targetNode;

  Edge() { edgeId = -1; }

}
