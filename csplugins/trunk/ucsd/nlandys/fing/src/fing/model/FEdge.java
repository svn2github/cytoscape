package fing.model;

import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;

// Package visible class.
class FEdge implements Edge
{

  // Variables specific to public get/set methods.
  Node m_sourceNode = null;
  Node m_targetNode = null;
  boolean m_directed = false;
  RootGraph m_rootGraph = null;
  int m_rootGraphIndex = 0;
  String m_identifier = null;

  // Variables for internal data structure.
  FEdge m_nextInEdge = null;
  FEdge m_nextOutEdge = null;

  FEdge() {}

  public Node getSource()
  {
    return m_sourceNode;
  }

  public Node getTarget()
  {
    return m_targetNode;
  }

  public boolean isDirected()
  {
    return m_directed;
  }

  public RootGraph getRootGraph()
  {
    return m_rootGraph;
  }

  public int getRootGraphIndex()
  {
    return m_rootGraphIndex;
  }

  public String getIdentifier()
  {
    return m_identifier;
  }

  public boolean setIdentifier(String new_id)
  {
    m_identifier = new_id;
    return true;
  }

}
