package fing.model;

import giny.model.Node;
import giny.model.RootGraph;

// Package visible class.
class FEdge implements FingEdge
{

  // Variables specific to public get/set methods.
  private RootGraph m_rootGraph = null;
  private int m_rootGraphIndex = 0;
  private String m_identifier = null;

  // Package visible constructor.
  FEdge() { }

  public Node getSource()
  {
    return m_rootGraph.getNode
      (m_rootGraph.getEdgeSourceIndex(m_rootGraphIndex));
  }

  public Node getTarget()
  {
    return m_rootGraph.getNode
      (m_rootGraph.getEdgeTargetIndex(m_rootGraphIndex));
  }

  public boolean isDirected()
  {
    return m_rootGraph.isEdgeDirected(m_rootGraphIndex);
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

  public void _setRootGraph(RootGraph root)
  {
    m_rootGraph = root;
  }

  public void _setRootGraphIndex(int index)
  {
    m_rootGraphIndex = index;
  }

  public void _setIdentifier(String id)
  {
    m_identifier = id;
  }

}
