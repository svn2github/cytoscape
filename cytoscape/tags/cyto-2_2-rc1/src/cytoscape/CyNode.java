package cytoscape;

import giny.model.*;
import cytoscape.giny.CytoscapeFingRootGraph;

  // Package visible class.
public class CyNode implements cytoscape.giny.Node
{

  // Variables specific to public get/set methods.
  CytoscapeFingRootGraph m_rootGraph = null;
  int m_rootGraphIndex = 0;
  String m_identifier = null;

  public CyNode(RootGraph root,
         int rootGraphIndex) { 
    this.m_rootGraph = ( CytoscapeFingRootGraph )root;
    this.m_rootGraphIndex = rootGraphIndex;
    this.m_identifier = new Integer(m_rootGraphIndex).toString();
  }


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
    if (new_id == null) {
      m_rootGraph.setNodeIdentifier(m_identifier, 0); }
    else {
      m_rootGraph.setNodeIdentifier(new_id, m_rootGraphIndex); }
    m_identifier = new_id;
    return true;
  }

} 
