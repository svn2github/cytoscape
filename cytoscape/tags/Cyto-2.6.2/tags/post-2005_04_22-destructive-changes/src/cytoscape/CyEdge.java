package cytoscape;

import giny.model.*;
import cytoscape.giny.Node;

public class CyEdge implements cytoscape.giny.Edge {


  // Variables specific to public get/set methods.
  RootGraph m_rootGraph = null;
  int m_rootGraphIndex = 0;
  String m_identifier = null;

  public CyEdge (RootGraph root,
          int rootGraphIndex ) {
    this.m_rootGraph = root;
    this.m_rootGraphIndex = rootGraphIndex;
    this.m_identifier = new Integer(m_rootGraphIndex).toString();
  }


  /**
   * @deprecated
   */
  public CyNode getSourceNode () {
    return (CyNode) getSource();
  }

  /**
   * @deprecated
   */
  public CyNode getTargetNode () {
    return (CyNode)getTarget();
  }

  public giny.model.Node getSource()
  {
    return m_rootGraph.getNode
      (m_rootGraph.getEdgeSourceIndex(m_rootGraphIndex));
  }

  public giny.model.Node getTarget()
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

 

}
