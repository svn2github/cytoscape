package fing.model;

import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

// Package visible class.
class FNode implements Node
{

  // Variables specific to public get/set methods.
  RootGraph m_rootGraph = null;
  int m_rootGraphIndex = 0;
  String m_identifier = null;

  // Variables for internal data stucture.
  FEdge m_firstInEdge = null;
  FEdge m_firstOutEdge = null;

  FNode() {}

  public GraphPerspective getGraphPerspective()
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
  }

  public boolean setGraphPerspective(GraphPerspective gp)
  {
    throw new UnsupportedOperationException("meta nodes not yet supported");
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
