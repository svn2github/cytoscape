package fing.model;

import giny.model.GraphPerspective;
import giny.model.RootGraph;

// Package visible class.
class FNode implements FingNode
{

  // Variables specific to public get/set methods.
  private RootGraph m_rootGraph = null;
  private int m_rootGraphIndex = 0;
  private String m_identifier = null;

  // Package visible constructor.
  FNode() { }

  public GraphPerspective getGraphPerspective()
  {
    return m_rootGraph.createGraphPerspective
      (m_rootGraph.getNodeMetaChildIndicesArray(m_rootGraphIndex),
       m_rootGraph.getEdgeMetaChildIndicesArray(m_rootGraphIndex));
  }

  public boolean setGraphPerspective(GraphPerspective gp)
  {
    if (gp.getRootGraph() != m_rootGraph) return false;
    final int[] nodeInx = gp.getNodeIndicesArray();
    final int[] edgeInx = gp.getEdgeIndicesArray();
    for (int i = 0; i < nodeInx.length; i++)
      m_rootGraph.addNodeMetaChild(m_rootGraphIndex, nodeInx[i]);
    for (int i = 0; i < edgeInx.length; i++)
      m_rootGraph.addEdgeMetaChild(m_rootGraphIndex, edgeInx[i]);
    return true;
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
